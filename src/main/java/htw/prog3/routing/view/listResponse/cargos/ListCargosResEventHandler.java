package htw.prog3.routing.view.listResponse.cargos;

import htw.prog3.routing.EventHandler;

public class ListCargosResEventHandler extends EventHandler<ListCargosResEvent, ListCargosResEventListener> {
    @Override
    public void handle(ListCargosResEvent event) {
        getListeners().forEach(listener -> listener.onListCargosResEvent(event));
    }
}
