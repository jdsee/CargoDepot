package htw.prog3.storageContract.cargo;

import htw.prog3.sm.core.CargoType;
import javafx.beans.property.ReadOnlyBooleanProperty;

public interface LiquidBulkCargo extends Cargo {
    CargoType CARGO_TYPE = CargoType.LIQUID_BULK_CARGO;

    @Override
    default CargoType getCargoType() {
        return CARGO_TYPE;
    }

    boolean isPressurized();

    ReadOnlyBooleanProperty pressurizedProperty();
}
