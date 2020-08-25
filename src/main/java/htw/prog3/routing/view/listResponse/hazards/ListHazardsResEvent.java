package htw.prog3.routing.view.listResponse.hazards;

import htw.prog3.storageContract.cargo.Hazard;

import java.util.EventObject;
import java.util.Set;

public class ListHazardsResEvent extends EventObject {
    private final Set<Hazard> viewItems;

    /**
     * Constructs a  CargoViewEvent.
     *
     * @param source Hazards object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ListHazardsResEvent(Set<Hazard> hazardItems, Object source) {
        super(source);
        this.viewItems = hazardItems;
    }

    public Set<Hazard> getHazards() {
        return viewItems;
    }
}
