package htw.prog3.ui.gui.view;

import htw.prog3.routing.persistence.all.PersistenceType;
import htw.prog3.routing.persistence.all.load.LoadAllEvent;
import htw.prog3.routing.persistence.all.load.LoadAllEventHandler;
import htw.prog3.routing.persistence.all.save.SaveAllEvent;
import htw.prog3.routing.persistence.all.save.SaveAllEventHandler;
import htw.prog3.routing.persistence.item.load.LoadItemEvent;
import htw.prog3.routing.persistence.item.load.LoadItemEventHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class MenuViewModel {
    private final ObjectProperty<Integer> storagePositionInput = new SimpleObjectProperty<>();
    private final BooleanProperty saveByPositionButtonDisabled = new SimpleBooleanProperty(true);
    private SaveAllEventHandler saveAllEventHandler;
    private LoadItemEventHandler loadItemEventHandler;
    private LoadAllEventHandler loadAllEventHandler;

    public MenuViewModel() {
        saveByPositionButtonDisabled.bind(storagePositionInput.isNull());
    }

    public MenuViewModel setSaveAllEventHandler(SaveAllEventHandler saveAllEventHandler) {
        this.saveAllEventHandler = saveAllEventHandler;
        return this;
    }

    public MenuViewModel setLoadItemEventHandler(LoadItemEventHandler loadItemEventHandler) {
        this.loadItemEventHandler = loadItemEventHandler;
        return this;
    }

    public MenuViewModel setLoadAllEventHandler(LoadAllEventHandler loadAllEventHandler) {
        this.loadAllEventHandler = loadAllEventHandler;
        return this;
    }

    public void saveJos() {
        fireSaveAllEvent(PersistenceType.JOS);
    }

    public void saveJBP() {
        fireSaveAllEvent(PersistenceType.JBP);
    }

    private void fireSaveAllEvent(PersistenceType type) {
        if (null != saveAllEventHandler) {
            SaveAllEvent event = new SaveAllEvent(type, this);
            saveAllEventHandler.handle(event);
        }
    }

    public void load() {
        Integer position = storagePositionInput.get();
        if (null != position && 0 <= position)
            fireLoadItemEvent(position);
    }

    public void loadJos() {
        fireLoadAllEvent(PersistenceType.JOS);
    }

    public void loadJBP() {
        fireLoadAllEvent(PersistenceType.JBP);

    }

    private void fireLoadItemEvent(int position) {
        if (null != loadItemEventHandler) {
            LoadItemEvent event = new LoadItemEvent(position, this);
            loadItemEventHandler.handle(event);
        }
    }

    private void fireLoadAllEvent(PersistenceType type) {
        if (null != loadAllEventHandler) {
            LoadAllEvent event = new LoadAllEvent(type, this);
            loadAllEventHandler.handle(event);
        }
    }

    public int getStoragePositionInput() {
        return storagePositionInput.get();
    }

    public ObjectProperty<Integer> storagePositionInputProperty() {
        return storagePositionInput;
    }

    public void setStoragePositionInput(int storagePositionInput) {
        this.storagePositionInput.set(storagePositionInput);
    }

    public boolean isSaveByPositionButtonDisabled() {
        return saveByPositionButtonDisabled.get();
    }

    public BooleanProperty saveByPositionButtonDisabledProperty() {
        return saveByPositionButtonDisabled;
    }

    public void setSaveByPositionButtonDisabled(boolean saveByPositionButtonDisabled) {
        this.saveByPositionButtonDisabled.set(saveByPositionButtonDisabled);
    }
}