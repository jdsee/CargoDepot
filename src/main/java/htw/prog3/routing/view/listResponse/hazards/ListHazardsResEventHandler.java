package htw.prog3.routing.view.listResponse.hazards;

import htw.prog3.routing.EventHandler;

public class ListHazardsResEventHandler extends EventHandler<ListHazardsResEvent, listHazardsResEventListener> {
    @Override
    public void handle(ListHazardsResEvent event) {
        getListeners().forEach(listener -> listener.onListHazardsResEvent(event));
    }
}
