package htw.prog3.ui.gui.view;

import htw.prog3.routing.input.update.relocate.RelocateStorageItemEvent;
import htw.prog3.routing.input.update.relocate.RelocateStorageItemEventHandler;
import htw.prog3.routing.view.update.cargos.UpdateCargoViewEvent;
import htw.prog3.routing.view.update.cargos.UpdateCargoViewEventListener;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.util.BindingUtils;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class StorageViewModel implements UpdateCargoViewEventListener {
    private final ListProperty<StorageItem> storageItemsRepresentation;
    private final BooleanProperty removeCargoButtonDisabled = new SimpleBooleanProperty(true);
    private final ObjectProperty<StorageItem> selectedItem = new SimpleObjectProperty<>();

    private RelocateStorageItemEventHandler relocateStorageItemHandler;

    public StorageViewModel() {
        this.storageItemsRepresentation = new SimpleListProperty<>();
        removeCargoButtonDisabled.bind(selectedItem.isNull());
    }

    @Override
    public void onUpdateCargoViewEvent(UpdateCargoViewEvent event) {
        ListProperty<StorageItem> items = BindingUtils.createObservableValues(event.itemsProperty());
        storageItemsRepresentation.set(items);
    }

    public void setRelocateStorageItemHandler(RelocateStorageItemEventHandler relocateStorageItemHandler) {
        this.relocateStorageItemHandler = relocateStorageItemHandler;
    }

    public void relocateStorageItem(int from, int to) {
        to = (Integer.MAX_VALUE == to) ? storageItemsProperty().size() : to;
        fireRelocateStorageItemEvent(from, to);
    }

    private void fireRelocateStorageItemEvent(int from, int to) {
        RelocateStorageItemEvent event = new RelocateStorageItemEvent(from, to, this);
        relocateStorageItemHandler.handle(event);
    }

    public ListProperty<StorageItem> storageItemsProperty() {
        return storageItemsRepresentation;
    }

    public ObservableList<StorageItem> getStorageItems() {
        return storageItemsRepresentation.get();
    }

    public StorageItem getSelectedItem() {
        return selectedItem.get();
    }

    public ObjectProperty<StorageItem> selectedItemProperty() {
        return selectedItem;
    }

    public void setSelectedItem(StorageItem selectedItem) {
        this.selectedItem.set(selectedItem);
    }

    public boolean getRemoveCargoButtonDisabled() {
        return removeCargoButtonDisabled.get();
    }

    public BooleanProperty removeCargoButtonDisabledProperty() {
        return removeCargoButtonDisabled;
    }

    public void setRemoveCargoButtonDisabled(boolean removeCargoButtonDisabled) {

        this.removeCargoButtonDisabled.set(removeCargoButtonDisabled);
    }
}
