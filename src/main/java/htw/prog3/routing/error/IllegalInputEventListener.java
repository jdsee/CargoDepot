package htw.prog3.routing.error;

import java.util.EventListener;

public interface IllegalInputEventListener extends EventListener {
    void onIllegalInputEvent(IllegalInputEvent event);
}
