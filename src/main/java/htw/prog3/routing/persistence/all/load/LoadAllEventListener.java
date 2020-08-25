package htw.prog3.routing.persistence.all.load;

import java.util.EventListener;

public interface LoadAllEventListener extends EventListener {
    void onLoadAllEvent(LoadAllEvent event);
}
