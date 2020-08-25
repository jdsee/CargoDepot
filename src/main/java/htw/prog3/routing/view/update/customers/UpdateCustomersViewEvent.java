package htw.prog3.routing.view.update.customers;

import htw.prog3.sm.core.CustomerRecord;
import javafx.beans.property.MapProperty;
import javafx.collections.ObservableMap;

import java.util.EventObject;

public class UpdateCustomersViewEvent extends EventObject {
    private final MapProperty<String, CustomerRecord> records;

    /**
     * Constructs an UpdateCustomersViewEvent.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public UpdateCustomersViewEvent(MapProperty<String, CustomerRecord> records, Object source) {
        super(source);
        this.records = records;
    }

    public ObservableMap<String, CustomerRecord> getRecords() {
        return records.get();
    }

    public MapProperty<String, CustomerRecord> recordsProperty() {
        return records;
    }
}
