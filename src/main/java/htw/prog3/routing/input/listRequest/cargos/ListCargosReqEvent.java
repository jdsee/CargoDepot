package htw.prog3.routing.input.listRequest.cargos;

import htw.prog3.sm.core.CargoType;

import java.util.EventObject;

public class ListCargosReqEvent extends EventObject {
    private final CargoType type;

    /**
     * Constructs a ShowCargosEvent.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public ListCargosReqEvent(CargoType type, Object source) {
        super(source);
        this.type = type;
    }

    public CargoType getCargoType() {
        return type;
    }
}
