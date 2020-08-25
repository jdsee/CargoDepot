package htw.prog3.routing.input.create.cargo;

import htw.prog3.routing.EventHandler;

public class AddCargoEventHandler extends EventHandler<AddCargoEvent, AddCargoEventListener> {
    @Override
    public void handle(AddCargoEvent event) {
        getListeners().forEach(listener -> listener.onAddCargoEvent(event));
    }
}
