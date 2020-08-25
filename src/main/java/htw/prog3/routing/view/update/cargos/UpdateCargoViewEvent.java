package htw.prog3.routing.view.update.cargos;

import htw.prog3.sm.core.StorageItem;
import javafx.beans.property.MapProperty;
import javafx.collections.ObservableMap;

import java.util.EventObject;

public class UpdateCargoViewEvent extends EventObject {
    private final MapProperty<Integer, StorageItem> items;

    /**
     * Constructs an UpdateCargoViewEvent.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public UpdateCargoViewEvent(MapProperty<Integer, StorageItem> items, Object source) {
        super(source);
        this.items = items;
    }

    public ObservableMap<Integer, StorageItem> getItems() {
        return items.get();
    }

    public MapProperty<Integer, StorageItem> itemsProperty() {
        return items;
    }
}
