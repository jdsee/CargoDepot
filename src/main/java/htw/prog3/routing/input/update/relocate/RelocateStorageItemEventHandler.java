package htw.prog3.routing.input.update.relocate;

import htw.prog3.routing.EventHandler;

public class RelocateStorageItemEventHandler
        extends EventHandler<RelocateStorageItemEvent, RelocateStorageItemEventListener> {
    @Override
    public void handle(RelocateStorageItemEvent event) {
        getListeners().forEach(listener -> listener.onRelocateStorageItemEvent(event));
    }
}
