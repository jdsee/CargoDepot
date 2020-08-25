package htw.prog3.routing.input.listRequest.customers;

import htw.prog3.routing.EventHandler;

public class ListCustomersReqEventHandler extends EventHandler<ListCustomersReqEvent, ListCustomersReqEventListener> {
    @Override
    public void handle(ListCustomersReqEvent event) {
        getListeners().forEach(ListCustomersReqEventListener::onListCustomersReqEvent);
    }
}
