package htw.prog3.routing.input.listRequest.customers;

import java.util.EventObject;

public class ListCustomersReqEvent extends EventObject {
    /**
     * Constructs a ShowCustomerEvent.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ListCustomersReqEvent(Object source) {
        super(source);
    }
}
