package htw.prog3.routing.view.update.cargos;

import htw.prog3.routing.EventHandler;

public class UpdateCargoViewEventHandler extends EventHandler<UpdateCargoViewEvent, UpdateCargoViewEventListener> {
    @Override
    public void handle(UpdateCargoViewEvent event) {
        getListeners().forEach(listener -> listener.onUpdateCargoViewEvent(event));
    }
}
