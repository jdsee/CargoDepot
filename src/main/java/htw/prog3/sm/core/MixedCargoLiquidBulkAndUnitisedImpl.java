package htw.prog3.sm.core;

import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.storageContract.cargo.MixedCargoLiquidBulkAndUnitised;
import javafx.beans.property.ReadOnlyBooleanProperty;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class MixedCargoLiquidBulkAndUnitisedImpl extends AbstractCargo implements MixedCargoLiquidBulkAndUnitised {
    private final LiquidBulkCargoBehavior liquidBulkCargoBehavior;
    private final UnitisedCargoBehavior unitisedCargoBehavior;

    public MixedCargoLiquidBulkAndUnitisedImpl(Customer owner,
                                               BigDecimal value,
                                               Duration durationOfStorage,
                                               Set<Hazard> hazards,
                                               boolean pressurized,
                                               boolean fragile) {
        this(owner, value, durationOfStorage, hazards, pressurized, fragile, Calendar.getInstance().getTime());
    }

    public MixedCargoLiquidBulkAndUnitisedImpl(Customer owner,
                                               BigDecimal value,
                                               Duration durationOfStorage,
                                               Set<Hazard> hazards,
                                               boolean pressurized,
                                               boolean fragile,
                                               Date inspectionDate) {
        super(owner, value, durationOfStorage, hazards, inspectionDate);
        liquidBulkCargoBehavior = new LiquidBulkCargoBehavior(pressurized);
        unitisedCargoBehavior = new UnitisedCargoBehavior(fragile);
    }

    @Override
    public boolean isPressurized() {
        return liquidBulkCargoBehavior.isPressurized();
    }

    @Override
    public ReadOnlyBooleanProperty pressurizedProperty() {
        return liquidBulkCargoBehavior.pressurizedProperty();
    }

    @Override
    public boolean isFragile() {
        return unitisedCargoBehavior.isFragile();
    }

    @Override
    public ReadOnlyBooleanProperty fragileProperty() {
        return unitisedCargoBehavior.fragileProperty();
    }

    private static final class SerializationProxy
            extends AbstractCargo.SerializationProxy<MixedCargoLiquidBulkAndUnitisedImpl> {
        private static final long serialVersionUID = 742098339L;
        private final boolean pressurized;
        private final boolean fragile;


        SerializationProxy(MixedCargoLiquidBulkAndUnitisedImpl cargo) {
            super(cargo);
            this.pressurized = cargo.isPressurized();
            this.fragile = cargo.isFragile();
        }

        private Object readResolve() {
            return new MixedCargoLiquidBulkAndUnitisedImpl(owner(),
                    value(),
                    durationOfStorage(),
                    hazards(),
                    pressurized,
                    fragile,
                    inspectionDate());
        }
    }

    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * Not tested because method is supposed to be private and is not used anywhere
     * in the code.
     * It's just a security feature to prevent {@code NotSerializableException}
     */
    private Object readObject(ObjectInputStream in) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required.");
    }
}
