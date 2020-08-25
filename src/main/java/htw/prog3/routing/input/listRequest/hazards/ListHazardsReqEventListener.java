package htw.prog3.routing.input.listRequest.hazards;

import java.util.EventListener;

public interface ListHazardsReqEventListener extends EventListener {
    void onListHazardsReqEvent(ListHazardsReqEvent event);
}
