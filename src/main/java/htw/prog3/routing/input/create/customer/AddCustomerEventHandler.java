package htw.prog3.routing.input.create.customer;

import htw.prog3.routing.EventHandler;

public class AddCustomerEventHandler extends EventHandler<AddCustomerEvent, AddCustomerEventListener> {
    @Override
    public void handle(AddCustomerEvent event) {
        getListeners().forEach(listener -> listener.onAddCustomerEvent(event));
    }
}
