package htw.prog3.routing.error;

import htw.prog3.routing.EventHandler;

public class IllegalInputEventHandler extends EventHandler<IllegalInputEvent, IllegalInputEventListener> {
    @Override
    public void handle(IllegalInputEvent event) {
        getListeners().forEach(listener -> listener.onIllegalInputEvent(event));
    }
}
