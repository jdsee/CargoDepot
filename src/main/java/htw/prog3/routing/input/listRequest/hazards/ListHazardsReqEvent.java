package htw.prog3.routing.input.listRequest.hazards;

import java.util.EventObject;

public class ListHazardsReqEvent extends EventObject {
    private final boolean inclusive;

    /**
     * Constructs a RequestHazardViewEvent.
     *
     * @param source    The object on which the Event initially occurred.
     * @param inclusive Defines if the included hazards are to be shown.
     * @throws IllegalArgumentException if source is null.
     */
    public ListHazardsReqEvent(boolean inclusive, Object source) {
        super(source);
        this.inclusive = inclusive;
    }

    public boolean isInclusive() {
        return inclusive;
    }
}
