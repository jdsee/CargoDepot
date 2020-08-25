package htw.prog3.routing.input.listRequest.cargos;

import java.util.EventListener;

public interface ListCargosReqEventListener extends EventListener {
    void onListCargosReqEvent(ListCargosReqEvent event);
}
