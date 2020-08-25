package htw.prog3.routing.input.delete.customer;

import htw.prog3.routing.EventHandler;

public class RemoveCustomerEventHandler extends EventHandler<RemoveCustomerEvent, RemoveCustomerEventListener> {
    @Override
    public void handle(RemoveCustomerEvent event) {
        getListeners().forEach(listener -> listener.onDeleteCustomerEvent(event));
    }
}
