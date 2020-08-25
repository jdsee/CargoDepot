package htw.prog3.routing.input.delete.cargo;

import java.util.EventListener;

public interface RemoveCargoEventListener extends EventListener {
    void onDeleteCargoEvent(RemoveCargoEvent event);
}
