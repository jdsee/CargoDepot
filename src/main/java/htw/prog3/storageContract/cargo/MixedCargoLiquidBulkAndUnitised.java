package htw.prog3.storageContract.cargo;

import htw.prog3.sm.core.CargoType;

public interface MixedCargoLiquidBulkAndUnitised extends LiquidBulkCargo, UnitisedCargo {
    CargoType CARGO_TYPE = CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED;

    @Override
    default CargoType getCargoType() {
        return CARGO_TYPE;
    }
}
