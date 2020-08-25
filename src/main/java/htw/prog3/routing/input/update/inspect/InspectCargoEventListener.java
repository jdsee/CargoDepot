package htw.prog3.routing.input.update.inspect;

import java.util.EventListener;

public interface InspectCargoEventListener extends EventListener {
    void onInspectCargoEvent(InspectCargoEvent event);
}
