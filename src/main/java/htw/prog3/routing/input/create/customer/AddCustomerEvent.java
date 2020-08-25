package htw.prog3.routing.input.create.customer;

import java.util.EventObject;

/**
 * This event represents commands to add a customer to the system.
 */
public class AddCustomerEvent extends EventObject {

    private final String customerName;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public AddCustomerEvent(String customerName, Object source) {
        super(source);
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }
}
