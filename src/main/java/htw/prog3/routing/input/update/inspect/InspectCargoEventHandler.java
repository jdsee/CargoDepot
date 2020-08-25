package htw.prog3.routing.input.update.inspect;

import htw.prog3.routing.EventHandler;

public class InspectCargoEventHandler extends EventHandler<InspectCargoEvent, InspectCargoEventListener> {
    @Override
    public void handle(InspectCargoEvent event) {
        getListeners().forEach(listener -> listener.onInspectCargoEvent(event));
    }
}
