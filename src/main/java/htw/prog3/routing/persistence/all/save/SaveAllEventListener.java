package htw.prog3.routing.persistence.all.save;

import java.util.EventListener;

public interface SaveAllEventListener extends EventListener {
    void onSaveAllEvent(SaveAllEvent event);
}