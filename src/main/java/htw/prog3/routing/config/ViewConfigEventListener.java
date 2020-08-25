package htw.prog3.routing.config;

import java.util.EventListener;

public interface ViewConfigEventListener extends EventListener {
    void onViewConfigEvent(ViewConfigEvent event);
}
