package htw.prog3.routing.input.create.cargo;

import java.util.EventListener;

public interface AddCargoEventListener extends EventListener {
    void onAddCargoEvent(AddCargoEvent event);
}
