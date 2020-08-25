package htw.prog3.routing.input.update.relocate;

import java.util.EventListener;

public interface RelocateStorageItemEventListener extends EventListener {
    void onRelocateStorageItemEvent(RelocateStorageItemEvent event);
}
