package htw.prog3.routing.view.listResponse.cargos;

import htw.prog3.sm.core.StorageItem;

import java.util.EventObject;
import java.util.List;

public class ListCargosResEvent extends EventObject {
    private final List<StorageItem> storageItems;

    /**
     * Constructs a  CargoViewEvent.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ListCargosResEvent(List<StorageItem> storageItems, Object source) {
        super(source);
        this.storageItems = storageItems;
    }

    public List<StorageItem> getStorageItems() {
        return storageItems;
    }
}