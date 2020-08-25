package htw.prog3.routing.input.create.customer;

import java.util.EventListener;

public interface AddCustomerEventListener extends EventListener {
    void onAddCustomerEvent(AddCustomerEvent event);
}
