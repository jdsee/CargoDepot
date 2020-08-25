package htw.prog3.routing.persistence.item.save;

import java.util.EventListener;

public interface SaveItemEventListener extends EventListener {
    void onSaveItemEvent(SaveItemEvent event);
}
