package htw.prog3.routing.success;

import java.util.EventListener;

public interface ActionSuccessEventListener extends EventListener {
    void onActionSuccessEvent(ActionSuccessEvent event);
}
