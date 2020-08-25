package htw.prog3.routing.config;

import htw.prog3.routing.EventHandler;

public class ViewConfigEventHandler
        extends EventHandler<ViewConfigEvent, ViewConfigEventListener> {
    @Override
    public void handle(ViewConfigEvent event) {
        getListeners().forEach(listener -> listener.onViewConfigEvent(event));
    }
}
