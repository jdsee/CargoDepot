package htw.prog3.routing.success;

import htw.prog3.routing.EventHandler;

public class ActionSuccessEventHandler extends EventHandler<ActionSuccessEvent, ActionSuccessEventListener> {
    @Override
    public void handle(ActionSuccessEvent event) {
        getListeners().forEach(listener -> listener.onActionSuccessEvent(event));
    }
}
