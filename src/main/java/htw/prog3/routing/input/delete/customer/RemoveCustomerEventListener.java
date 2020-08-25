package htw.prog3.routing.input.delete.customer;

import java.util.EventListener;

public interface RemoveCustomerEventListener extends EventListener {
    void onDeleteCustomerEvent(RemoveCustomerEvent event);
}
