package htw.prog3.routing.persistence.item.load;

import htw.prog3.routing.EventHandler;

public class LoadItemEventHandler extends EventHandler<LoadItemEvent, LoadItemEventListener> {
    @Override
    public void handle(LoadItemEvent event) {
        getListeners().forEach(listener -> listener.onLoadItemEvent(event));
    }
}
