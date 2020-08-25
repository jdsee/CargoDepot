package htw.prog3.routing.persistence.item.load;

import java.util.EventListener;

public interface LoadItemEventListener extends EventListener {
    void onLoadItemEvent(LoadItemEvent event);
}
