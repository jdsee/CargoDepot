package htw.prog3.routing.view.listResponse.customers;

import htw.prog3.sm.core.CustomerRecord;

import java.util.EventObject;
import java.util.List;

public class ListCustomersResEvent extends EventObject {
    private final List<CustomerRecord> customerRecords;

    /**
     * Constructs a  CustomerItemsViewEvent.
     *
     * @param source object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ListCustomersResEvent(List<CustomerRecord> customerRecords, Object source) {
        super(source);
        this.customerRecords = customerRecords;
    }

    public List<CustomerRecord> getCustomerRecords() {
        return customerRecords;
    }
}
