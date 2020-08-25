package htw.prog3.routing.view.listResponse.customers;

import htw.prog3.routing.EventHandler;

public class ListCustomersResEventHandler extends EventHandler<ListCustomersResEvent, ListCustomersResEventListener> {
    @Override
    public void handle(ListCustomersResEvent event) {
        getListeners().forEach(listener -> listener.onListCustomerResEvent(event));
    }
}
