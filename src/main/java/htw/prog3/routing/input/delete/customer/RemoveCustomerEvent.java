package htw.prog3.routing.input.delete.customer;

import java.util.EventObject;

public class RemoveCustomerEvent extends EventObject {
    private final String customerName;

    /**
     * Constructs a DeleteCustomerEvent.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public RemoveCustomerEvent(String customerName, Object source) {
        super(source);
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }
}
