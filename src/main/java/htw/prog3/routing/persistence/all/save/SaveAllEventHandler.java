package htw.prog3.routing.persistence.all.save;

import htw.prog3.routing.EventHandler;

public class SaveAllEventHandler extends EventHandler<SaveAllEvent, SaveAllEventListener> {
    @Override
    public void handle(SaveAllEvent event) {
        getListeners().forEach(listener -> listener.onSaveAllEvent(event));
    }
}
