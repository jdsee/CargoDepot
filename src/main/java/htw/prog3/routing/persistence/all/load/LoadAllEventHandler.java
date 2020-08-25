package htw.prog3.routing.persistence.all.load;

import htw.prog3.routing.EventHandler;

public class LoadAllEventHandler extends EventHandler<LoadAllEvent, LoadAllEventListener> {
    @Override
    public void handle(LoadAllEvent event) {
        getListeners().forEach(listener -> listener.onLoadAllEvent(event));
    }
}
