package htw.prog3.routing.view.update.customers;

import htw.prog3.routing.EventHandler;

public class UpdateCustomersViewEventHandler
        extends EventHandler<UpdateCustomersViewEvent, UpdateCustomersViewEventListener> {
    @Override
    public void handle(UpdateCustomersViewEvent event) {
        getListeners().forEach(listener -> listener.onUpdateCustomersViewEvent(event));
    }
}
