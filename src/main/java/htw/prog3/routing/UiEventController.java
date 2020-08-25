package htw.prog3.routing;

import htw.prog3.persistence.StorageItemPersistenceStrategy;
import htw.prog3.persistence.StoragePersistenceStrategy;
import htw.prog3.persistence.jbp.XmlEncodingStrategy;
import htw.prog3.persistence.jos.SerializationStrategy;
import htw.prog3.persistence.ra.RandomAccessPersistenceStrategy;
import htw.prog3.routing.error.IllegalInputEvent;
import htw.prog3.routing.error.IllegalInputEvent.Trigger;
import htw.prog3.routing.error.IllegalInputEventHandler;
import htw.prog3.routing.input.create.cargo.AddCargoEvent;
import htw.prog3.routing.input.create.cargo.AddCargoEventListener;
import htw.prog3.routing.input.create.customer.AddCustomerEvent;
import htw.prog3.routing.input.create.customer.AddCustomerEventListener;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEvent;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEventListener;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEvent;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEventListener;
import htw.prog3.routing.input.listRequest.cargos.ListCargosReqEvent;
import htw.prog3.routing.input.listRequest.cargos.ListCargosReqEventListener;
import htw.prog3.routing.input.listRequest.customers.ListCustomersReqEventListener;
import htw.prog3.routing.input.listRequest.hazards.ListHazardsReqEvent;
import htw.prog3.routing.input.listRequest.hazards.ListHazardsReqEventListener;
import htw.prog3.routing.input.update.inspect.InspectCargoEvent;
import htw.prog3.routing.input.update.inspect.InspectCargoEventListener;
import htw.prog3.routing.input.update.relocate.RelocateStorageItemEvent;
import htw.prog3.routing.input.update.relocate.RelocateStorageItemEventListener;
import htw.prog3.routing.persistence.all.PersistenceType;
import htw.prog3.routing.persistence.all.load.LoadAllEvent;
import htw.prog3.routing.persistence.all.load.LoadAllEventListener;
import htw.prog3.routing.persistence.all.save.SaveAllEvent;
import htw.prog3.routing.persistence.all.save.SaveAllEventListener;
import htw.prog3.routing.persistence.item.load.LoadItemEvent;
import htw.prog3.routing.persistence.item.load.LoadItemEventListener;
import htw.prog3.routing.persistence.item.save.SaveItemEvent;
import htw.prog3.routing.persistence.item.save.SaveItemEventListener;
import htw.prog3.routing.success.ActionSuccessEvent;
import htw.prog3.routing.success.ActionSuccessEventHandler;
import htw.prog3.routing.success.ActionSuccessMessages;
import htw.prog3.routing.view.listResponse.cargos.ListCargosResEvent;
import htw.prog3.routing.view.listResponse.cargos.ListCargosResEventHandler;
import htw.prog3.routing.view.listResponse.customers.ListCustomersResEvent;
import htw.prog3.routing.view.listResponse.customers.ListCustomersResEventHandler;
import htw.prog3.routing.view.listResponse.hazards.ListHazardsResEvent;
import htw.prog3.routing.view.listResponse.hazards.ListHazardsResEventHandler;
import htw.prog3.routing.view.update.cargos.UpdateCargoViewEvent;
import htw.prog3.routing.view.update.cargos.UpdateCargoViewEventHandler;
import htw.prog3.routing.view.update.customers.UpdateCustomersViewEvent;
import htw.prog3.routing.view.update.customers.UpdateCustomersViewEventHandler;
import htw.prog3.sm.api.StorageFacade;
import htw.prog3.sm.core.CargoType;
import htw.prog3.sm.core.CustomerRecord;
import htw.prog3.sm.core.FailureMessages;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.storageContract.cargo.Hazard;
import javafx.beans.property.MapProperty;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

public class UiEventController
        implements AddCargoEventListener, AddCustomerEventListener, ListCustomersReqEventListener,
        ListCargosReqEventListener, ListHazardsReqEventListener, RemoveCustomerEventListener,
        RemoveCargoEventListener, InspectCargoEventListener, SaveAllEventListener, SaveItemEventListener,
        LoadAllEventListener, LoadItemEventListener, RelocateStorageItemEventListener {

    private final StorageFacade storageFacade;

    private IllegalInputEventHandler illegalInputEventHandler;
    private ActionSuccessEventHandler actionSuccessEventHandler;
    private ListCargosResEventHandler listCargosResEventHandler;
    private ListCustomersResEventHandler listCustomersResEventHandler;
    private ListHazardsResEventHandler listHazardsResEventHandler;
    private UpdateCargoViewEventHandler updateCargoViewEventHandler;
    private UpdateCustomersViewEventHandler updateCustomersViewEventHandler;
    private RandomAccessPersistenceStrategy itemPersistenceStrategy;

    public UiEventController(StorageFacade storageFacade) {
        this.storageFacade = storageFacade;
    }

    public UiEventController setIllegalInputEventHandler(IllegalInputEventHandler illegalInputEventHandler) {
        this.illegalInputEventHandler = illegalInputEventHandler;
        return this;
    }

    public UiEventController setActionSuccessEventHandler(ActionSuccessEventHandler actionSuccessEventHandler) {
        this.actionSuccessEventHandler = actionSuccessEventHandler;
        return this;
    }

    public UiEventController setListCargosResEventHandler(ListCargosResEventHandler listCargosResEventHandler) {
        this.listCargosResEventHandler = listCargosResEventHandler;
        return this;
    }

    public UiEventController setListCustomersResEventHandler(ListCustomersResEventHandler listCustomersResEventHandler) {
        this.listCustomersResEventHandler = listCustomersResEventHandler;
        return this;
    }

    public UiEventController setListHazardsResEventHandler(ListHazardsResEventHandler listHazardsResEventHandler) {
        this.listHazardsResEventHandler = listHazardsResEventHandler;
        return this;
    }

    public UiEventController setUpdateCargoViewEventHandler(UpdateCargoViewEventHandler handler) {
        this.updateCargoViewEventHandler = handler;
        return this;
    }

    public UiEventController setUpdateCustomersViewEventHandler(UpdateCustomersViewEventHandler handler) {
        this.updateCustomersViewEventHandler = handler;
        return this;
    }

    @Override
    public void onAddCargoEvent(AddCargoEvent event) {
        if (null != event) {
            String name = event.getOwnerName();
            if (!storageFacade.isPresentCustomer(name))
                fireIllegalInputEvent(FailureMessages.UNKNOWN_CUSTOMER, Trigger.STORAGE);
            else if (!storageFacade.hasFreeCapacity())
                fireIllegalInputEvent(FailureMessages.STORAGE_CAPACITY_EXCESS, Trigger.STORAGE);
            else {
                CargoType type = event.getCargoType();
                BigDecimal value = event.getValue();
                Duration duration = event.getDurationOfStorage();
                Set<Hazard> hazards = event.getHazards();
                boolean isPressurized = event.isPressurized();
                boolean isFragile = event.isFragile();

                int position = storageFacade.addCargo(
                        type, name, value, duration, hazards, isPressurized, isFragile);

                fireActionSuccessEvent(ActionSuccessMessages.cargoSuccessfullyAdded(position));
            }
        }
    }

    @Override
    public void onAddCustomerEvent(AddCustomerEvent event) {
        if (null != event) {
            String name = event.getCustomerName();
            if (storageFacade.isPresentCustomer(name))
                fireIllegalInputEvent(FailureMessages.customerNameAmbiguous(name), Trigger.CUSTOMERS);
            else {
                storageFacade.addCustomer(name);

                fireActionSuccessEvent(String.format("%s %s", name, ActionSuccessMessages.WAS_SUCCESSFULLY_ADDED));
            }
        }
    }

    @Override
    public void onListCargosReqEvent(ListCargosReqEvent event) {
        if (null != event) {
            CargoType type = event.getCargoType();
            List<StorageItem> storageItems = new ArrayList<>(storageFacade.getStorageItems(type).values());
            storageItems.sort(Comparator.comparingInt(StorageItem::getStoragePosition));
            ListCargosResEvent ListCargosResEvent = new ListCargosResEvent(storageItems, this);

            fireListCargosResEvent(ListCargosResEvent);
        }
    }

    @Override
    public void onListCustomersReqEvent() {
        List<CustomerRecord> customerRecords = new ArrayList<>(storageFacade.getCustomerRecords().values());
        customerRecords.sort(Comparator.comparing(t -> t.getCustomer().getName()));
        ListCustomersResEvent cargoItemsViewEvent = new ListCustomersResEvent(customerRecords, this);

        fireListCustomersResEvent(cargoItemsViewEvent);
    }

    @Override
    public void onListHazardsReqEvent(ListHazardsReqEvent event) {
        if (null != event) {
            boolean inclusive = event.isInclusive();
            Set<Hazard> actual = storageFacade.getHazards().get();

            fireListHazardsResEvent((inclusive) ? actual : invertHazards(actual));
        }
    }

    @Override
    public void onDeleteCargoEvent(RemoveCargoEvent event) {
        if (null != event) {
            int position = event.getStoragePosition();
            if (!storageFacade.isStoragePosition(position))
                fireIllegalInputEvent(FailureMessages.UNALLOCATED_STORAGE_POSITION, Trigger.STORAGE);
            else {
                storageFacade.removeCargo(position);

                fireActionSuccessEvent(ActionSuccessMessages.REMOVE_CARGO_SUCCEEDED);
            }
        }
    }

    @Override
    public void onDeleteCustomerEvent(RemoveCustomerEvent event) {
        if (null != event) {
            String name = event.getCustomerName();
            try {
                boolean succeeded = storageFacade.removeCustomer(name);

                if (succeeded)
                    fireActionSuccessEvent(ActionSuccessMessages.customerSuccessfullyRemoved(name));
                else {
                    fireUnknownCustomerEvent();
                }
            } catch (IllegalArgumentException e) {
                fireUnknownCustomerEvent();
            }
        }
    }

    private void fireUnknownCustomerEvent() {
        fireIllegalInputEvent(FailureMessages.UNKNOWN_CUSTOMER, Trigger.CUSTOMERS);
    }

    @Override
    public void onInspectCargoEvent(InspectCargoEvent event) {
        if (null != event) {
            int storagePosition = event.getStoragePosition();
            try {
                storageFacade.inspectCargo(storagePosition);

                fireActionSuccessEvent(ActionSuccessMessages.CARGO_INSPECTION_SUCCEEDED);
            } catch (IndexOutOfBoundsException e) {
                fireIllegalInputEvent(e.getMessage(), Trigger.STORAGE);
            }
        }
    }

    @Override
    public void onSaveAllEvent(SaveAllEvent event) {
        StoragePersistenceStrategy strategy = getStoragePersistenceStrategy(event.getType());
        storageFacade.save(strategy);
        fireActionSuccessEvent(ActionSuccessMessages.STORAGE_SUCCESSFULLY_SAVED);
    }

    @Override
    public void onLoadAllEvent(LoadAllEvent event) {
        StoragePersistenceStrategy strategy = getStoragePersistenceStrategy(event.getType());
        boolean succeeded = storageFacade.load(strategy);
        if (succeeded) {
            fireActionSuccessEvent(ActionSuccessMessages.STORAGE_SUCCESSFULLY_LOAD);
            updateView();
        } else
            fireIllegalInputEvent(FailureMessages.NO_DATA_PERSISTED, Trigger.STORAGE);
    }

    @Override
    public void onSaveItemEvent(SaveItemEvent event) {
        int position = event.getStoragePosition();
        if (storageFacade.isStoragePosition(position)) {
            storageFacade.save(position, getItemPersistenceStrategy());
            fireActionSuccessEvent(ActionSuccessMessages.ITEM_SUCCESSFULLY_SAVED);
        } else
            fireIllegalInputEvent(FailureMessages.UNALLOCATED_STORAGE_POSITION, Trigger.STORAGE);
    }

    @Override
    public void onLoadItemEvent(LoadItemEvent event) {
        int position = event.getStoragePosition();
        boolean succeeded = storageFacade.load(position, getItemPersistenceStrategy());

        if (succeeded)
            fireActionSuccessEvent(ActionSuccessMessages.ITEM_SUCCESSFULLY_LOAD);
        else
            fireIllegalInputEvent(FailureMessages.PERSISTED_ITEM_NOT_FOUND, Trigger.STORAGE);
    }

    @Override
    public void onRelocateStorageItemEvent(RelocateStorageItemEvent event) {
        int from = event.getFrom();
        int to = event.getTo();

        storageFacade.relocateStorageItem(from, to);
    }

    public void updateView() {
        fireUpdateCargoViewEvent();
        fireUpdateCustomersViewEvent();
    }

    private StorageItemPersistenceStrategy getItemPersistenceStrategy() {
        if (null == itemPersistenceStrategy)
            itemPersistenceStrategy = RandomAccessPersistenceStrategy.create();
        return itemPersistenceStrategy;
    }

    private StoragePersistenceStrategy getStoragePersistenceStrategy(PersistenceType type) {
        switch (type) {
            case JOS:
                return SerializationStrategy.create();
            case JBP:
                return XmlEncodingStrategy.create();
            default:
                throw new AssertionError(FailureMessages.unknownPersistenceType(type));
        }
    }

    private Set<Hazard> invertHazards(Set<Hazard> hazards) {
        Set<Hazard> inverted = new HashSet<>(Arrays.asList(Hazard.values()));
        inverted.removeAll(hazards);
        return inverted;
    }

    private void fireListCargosResEvent(ListCargosResEvent event) {
        if (null != listCargosResEventHandler)
            listCargosResEventHandler.handle(event);
    }

    private void fireListCustomersResEvent(ListCustomersResEvent event) {
        if (null != listCustomersResEventHandler)
            listCustomersResEventHandler.handle(event);
    }

    private void fireListHazardsResEvent(Set<Hazard> hazards) {
        ListHazardsResEvent cargoItemsViewEvent = new ListHazardsResEvent(hazards, this);
        if (null != listHazardsResEventHandler)
            listHazardsResEventHandler.handle(cargoItemsViewEvent);
    }

    private void fireActionSuccessEvent(String message) {
        if (null != actionSuccessEventHandler) {
            ActionSuccessEvent event = new ActionSuccessEvent(message, this);
            actionSuccessEventHandler.handle(event);
        }
    }

    private void fireIllegalInputEvent(String failureMessage, Trigger trigger) {
        if (null != illegalInputEventHandler) {
            IllegalInputEvent event = new IllegalInputEvent(failureMessage, trigger, this);
            illegalInputEventHandler.handle(event);
        }
    }

    private void fireUpdateCargoViewEvent() {
        if (null != updateCargoViewEventHandler) {
            MapProperty<Integer, StorageItem> items = storageFacade.getStorageItems();
            UpdateCargoViewEvent event = new UpdateCargoViewEvent(items, this);
            updateCargoViewEventHandler.handle(event);
        }
    }

    private void fireUpdateCustomersViewEvent() {
        if (null != updateCustomersViewEventHandler) {
            MapProperty<String, CustomerRecord> records = storageFacade.getCustomerRecords();
            UpdateCustomersViewEvent event = new UpdateCustomersViewEvent(records, this);
            updateCustomersViewEventHandler.handle(event);
        }
    }
}