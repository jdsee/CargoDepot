package htw.prog3.routing.input.listRequest.hazards;

import htw.prog3.routing.EventHandler;

public class ListHazardsReqEventHandler extends EventHandler<ListHazardsReqEvent, ListHazardsReqEventListener> {
    @Override
    public void handle(ListHazardsReqEvent event) {
        getListeners().forEach(listener -> listener.onListHazardsReqEvent(event));
    }
}
