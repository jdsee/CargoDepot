package htw.prog3.sm.core;

import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.storageContract.cargo.UnitisedCargo;
import javafx.beans.property.ReadOnlyBooleanProperty;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class UnitisedCargoImpl extends AbstractCargo implements UnitisedCargo, Serializable {
    private final UnitisedCargoBehavior unitisedCargoBehavior;

    public UnitisedCargoImpl(Customer owner,
                             BigDecimal value,
                             Duration durationOfStorage,
                             Set<Hazard> hazards,
                             boolean fragile) {
        this(owner, value, durationOfStorage, hazards, fragile, Calendar.getInstance().getTime());
    }

    public UnitisedCargoImpl(Customer owner,
                             BigDecimal value,
                             Duration durationOfStorage,
                             Set<Hazard> hazards,
                             boolean fragile,
                             Date inspectionDate) {
        super(owner, value, durationOfStorage, hazards, inspectionDate);
        unitisedCargoBehavior = new UnitisedCargoBehavior(fragile);
    }

    @Override
    public boolean isFragile() {
        return unitisedCargoBehavior.isFragile();
    }

    @Override
    public ReadOnlyBooleanProperty fragileProperty() {
        return unitisedCargoBehavior.fragileProperty();
    }


    private static final class SerializationProxy extends AbstractCargo.SerializationProxy<UnitisedCargoImpl> {
        private static final long serialVersionUID = 780348932L;
        private final boolean fragile;

        SerializationProxy(UnitisedCargoImpl cargo) {
            super(cargo);
            this.fragile = cargo.isFragile();
        }

        private Object readResolve() {
            return new UnitisedCargoImpl(owner(),
                    value(),
                    durationOfStorage(),
                    hazards(),
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
