package htw.prog3.routing.input.delete.cargo;

import htw.prog3.routing.EventHandler;

public class RemoveCargoEventHandler extends EventHandler<RemoveCargoEvent, RemoveCargoEventListener> {
    @Override
    public void handle(RemoveCargoEvent event) {
        getListeners().forEach(listener -> listener.onDeleteCargoEvent(event));
    }
}
