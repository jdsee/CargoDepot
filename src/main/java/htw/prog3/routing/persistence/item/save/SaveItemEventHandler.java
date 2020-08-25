package htw.prog3.routing.persistence.item.save;

import htw.prog3.routing.EventHandler;

public class SaveItemEventHandler extends EventHandler<SaveItemEvent, SaveItemEventListener> {
    @Override
    public void handle(SaveItemEvent event) {
        getListeners().forEach(listener -> listener.onSaveItemEvent(event));
    }
}
