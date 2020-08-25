package htw.prog3.routing.input.listRequest.cargos;

import htw.prog3.routing.EventHandler;

public class ListCargosReqEventHandler extends EventHandler<ListCargosReqEvent, ListCargosReqEventListener> {
    @Override
    public void handle(ListCargosReqEvent event) {
        getListeners().forEach(listener -> listener.onListCargosReqEvent(event));
    }
}
