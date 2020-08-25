package htw.prog3.routing.view.listResponse.customers;

import java.util.EventListener;

public interface ListCustomersResEventListener extends EventListener {
    void onListCustomerResEvent(ListCustomersResEvent event);
}
