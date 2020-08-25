package htw.prog3.ui.gui.view;

import htw.prog3.routing.error.IllegalInputEvent;
import htw.prog3.routing.error.IllegalInputEventListener;
import htw.prog3.routing.input.create.cargo.AddCargoEvent;
import htw.prog3.routing.input.create.cargo.AddCargoEventHandler;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEvent;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEventHandler;
import htw.prog3.routing.input.update.inspect.InspectCargoEvent;
import htw.prog3.routing.input.update.inspect.InspectCargoEventHandler;
import htw.prog3.routing.persistence.item.save.SaveItemEvent;
import htw.prog3.routing.persistence.item.save.SaveItemEventHandler;
import htw.prog3.routing.view.update.customers.UpdateCustomersViewEvent;
import htw.prog3.routing.view.update.customers.UpdateCustomersViewEventListener;
import htw.prog3.sm.core.CargoType;
import htw.prog3.sm.core.CustomerRecord;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.util.BindingUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class StorageManipulationViewModel implements UpdateCustomersViewEventListener, IllegalInputEventListener {
    private final ListProperty<Customer> customersRepresentation;
    private final ObjectProperty<CargoType> cargoTypeSelection = new SimpleObjectProperty<>();
    private final ObjectProperty<Customer> customerSelection = new SimpleObjectProperty<>();
    private final Set<Hazard> hazardsSelection = new HashSet<>();
    private final ObjectProperty<BigDecimal> valueSelection = new SimpleObjectProperty<>();
    private final IntegerProperty durationOfStorageSelection = new SimpleIntegerProperty();
    private final BooleanProperty fragileSelected = new SimpleBooleanProperty();
    private final BooleanProperty pressurizedSelected = new SimpleBooleanProperty();
    private final BooleanProperty addCargoButtonDisabled = new SimpleBooleanProperty(true);
    private final BooleanProperty noItemSelected = new SimpleBooleanProperty(true);
    private final StringProperty failureMessage = new SimpleStringProperty();
    private final ObjectProperty<StorageItem> selectedItem;

    private AddCargoEventHandler addCargoEventHandler;
    private RemoveCargoEventHandler removeCargoEventHandler;
    private SaveItemEventHandler saveItemEventHandler;
    private InspectCargoEventHandler inspectCargoEventHandler;

    public StorageManipulationViewModel(StorageViewModel storageVM) {
        this.customersRepresentation = new SimpleListProperty<>();
        bindAddCargoButtonToInputFields();

        this.selectedItem = storageVM.selectedItemProperty();
        noItemSelected.bind(selectedItem.isNull());
    }

    public StorageManipulationViewModel setAddCargoEventHandler(AddCargoEventHandler addCargoEventHandler) {
        this.addCargoEventHandler = addCargoEventHandler;
        return this;
    }

    public StorageManipulationViewModel setRemoveCargoEventHandler(RemoveCargoEventHandler removeCargoEventHandler) {
        this.removeCargoEventHandler = removeCargoEventHandler;
        return this;
    }

    public StorageManipulationViewModel setSaveItemEventHandler(SaveItemEventHandler saveItemEventHandler) {
        this.saveItemEventHandler = saveItemEventHandler;
        return this;
    }

    public StorageManipulationViewModel setInspectCargoEventHandler(InspectCargoEventHandler inspectCargoEventHandler) {
        this.inspectCargoEventHandler = inspectCargoEventHandler;
        return this;
    }

    @Override
    public void onUpdateCustomersViewEvent(UpdateCustomersViewEvent event) {
        ListProperty<Customer> customers =
                BindingUtils.createObservableValues(event.getRecords(), CustomerRecord::getCustomer);
        customersRepresentation.set(customers);
    }

    @Override
    public void onIllegalInputEvent(IllegalInputEvent event) {
        if (IllegalInputEvent.Trigger.STORAGE.equals(event.getTrigger())) {
            String message = event.getMessage();
            setFailureMessage(message);
        }
    }

    public void addCargo() {
        setFailureMessage("");

        String name = getCustomerSelection().getName();
        CargoType type = getCargoTypeSelection();
        BigDecimal value = getValueSelection();
        Duration duration = Duration.ofDays(getDurationOfStorageSelection());
        Set<Hazard> hazards = new HashSet<>(hazardsSelection);
        boolean fragile = isFragileSelected();
        boolean pressurized = isPressurizedSelected();

        fireAddCargoEvent(name, type, value, duration, hazards, fragile, pressurized);

        clearInputProperties();
    }

    public void removeCargo() {
        setFailureMessage("");
        int position = getSelectedPosition();

        fireRemoveCargoEvent(position);
    }

    public void saveCargo() {
        setFailureMessage("");
        int position = getSelectedPosition();

        fireSaveItemEvent(position);
    }

    public void inspectCargo() {
        setFailureMessage("");
        int position = getSelectedPosition();

        fireInspectCargoEvent(position);
    }

    private int getSelectedPosition() {
        return getSelectedItem().getStoragePosition();
    }

    private void fireAddCargoEvent(String name, CargoType type, BigDecimal value, Duration duration,
                                   Set<Hazard> hazards, boolean fragile, boolean pressurized) {
        if (null != addCargoEventHandler) {
            AddCargoEvent event = new AddCargoEvent(type, name, value, duration, hazards, pressurized, fragile, this);
            addCargoEventHandler.handle(event);
        }
    }

    private void clearInputProperties() {
        cargoTypeSelection.setValue(null);
        customerSelection.setValue(null);
        hazardsSelection.clear();
        valueSelection.setValue(null);
        setDurationOfStorageSelection(0);
        setFragileSelected(false);
        setPressurizedSelected(false);
    }

    private void fireRemoveCargoEvent(int position) {
        if (null != removeCargoEventHandler) {
            RemoveCargoEvent event = new RemoveCargoEvent(position, this);
            removeCargoEventHandler.handle(event);
        }
    }

    private void fireSaveItemEvent(int position) {
        if (null != saveItemEventHandler) {
            SaveItemEvent event = new SaveItemEvent(position, this);
            saveItemEventHandler.handle(event);
        }
    }

    private void fireInspectCargoEvent(int position) {
        if (null != inspectCargoEventHandler) {
            InspectCargoEvent event = new InspectCargoEvent(position, this);
            inspectCargoEventHandler.handle(event);
        }
    }

    private void bindAddCargoButtonToInputFields() {
        BooleanBinding isInvalidInput = new BooleanBinding() {
            {
                super.bind(cargoTypeSelection, customerSelection, valueSelection, durationOfStorageSelection);
            }

            @Override
            protected boolean computeValue() {
                return null == cargoTypeSelection.get() ||
                        null == customerSelection.get() ||
                        null == valueSelection.get();
            }
        };
        addCargoButtonDisabled.bind(isInvalidInput);
    }

    public boolean isFragileSelected() {
        return fragileSelected.get();
    }

    public BooleanProperty fragileSelectedProperty() {
        return fragileSelected;
    }

    public void setFragileSelected(boolean fragileSelected) {
        this.fragileSelected.set(fragileSelected);
    }

    public boolean isPressurizedSelected() {
        return pressurizedSelected.get();
    }

    public BooleanProperty pressurizedSelectedProperty() {
        return pressurizedSelected;
    }

    public void setPressurizedSelected(boolean pressurizedSelected) {
        this.pressurizedSelected.set(pressurizedSelected);
    }

    public CargoType getCargoTypeSelection() {
        return cargoTypeSelection.get();
    }

    public ObjectProperty<CargoType> cargoTypeSelectionProperty() {
        return cargoTypeSelection;
    }

    public ObservableList<Customer> getCustomers() {
        return customersRepresentation;
    }

    public Customer getCustomerSelection() {
        return customerSelection.get();
    }

    public ObjectProperty<Customer> customerSelectionProperty() {
        return customerSelection;
    }

    public Set<Hazard> getHazardsSelection() {
        return hazardsSelection;
    }

    public BigDecimal getValueSelection() {
        return valueSelection.get();
    }

    public ObjectProperty<BigDecimal> valueSelectionProperty() {
        return valueSelection;
    }

    public int getDurationOfStorageSelection() {
        return durationOfStorageSelection.get();
    }

    public IntegerProperty durationOfStorageSelectionProperty() {
        return durationOfStorageSelection;
    }

    public void setDurationOfStorageSelection(int durationOfStorageSelection) {
        this.durationOfStorageSelection.set(durationOfStorageSelection);
    }

    public String getFailureMessage() {
        return failureMessage.get();
    }

    public StringProperty failureMessageProperty() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage.set(failureMessage);
    }

    public boolean isAddCargoButtonDisabled() {

        return addCargoButtonDisabled.get();
    }

    public BooleanProperty addCargoButtonDisabledProperty() {
        return addCargoButtonDisabled;
    }

    public StorageItem getSelectedItem() {
        return selectedItem.get();
    }

    public void setSelectedItem(StorageItem selectedItem) {
        this.selectedItem.set(selectedItem);
    }

    public ObjectProperty<StorageItem> selectedItemProperty() {
        return selectedItem;
    }

    public boolean isNoItemSelected() {
        return noItemSelected.get();
    }

    public void setNoItemSelected(boolean noItemSelected) {
        this.noItemSelected.set(noItemSelected);
    }

    public BooleanProperty noItemSelectedProperty() {
        return noItemSelected;
    }
}