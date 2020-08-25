package htw.prog3.sm.core;

import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.storageContract.cargo.LiquidBulkCargo;
import javafx.beans.property.ReadOnlyBooleanProperty;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class LiquidBulkCargoImpl extends AbstractCargo implements LiquidBulkCargo {
    private final LiquidBulkCargoBehavior liquidBulkCargoBehavior;

    public LiquidBulkCargoImpl(Customer owner,
                               BigDecimal value,
                               Duration durationOfStorage,
                               Set<Hazard> hazards,
                               boolean pressurized) {
        this(owner, value, durationOfStorage, hazards, pressurized, Calendar.getInstance().getTime());
    }

    public LiquidBulkCargoImpl(Customer owner,
                               BigDecimal value,
                               Duration durationOfStorage,
                               Set<Hazard> hazards,
                               boolean pressurized,
                               Date inspectionDate) {
        super(owner, value, durationOfStorage, hazards, inspectionDate);
        this.liquidBulkCargoBehavior = new LiquidBulkCargoBehavior(pressurized);
    }

    @Override
    public boolean isPressurized() {
        return liquidBulkCargoBehavior.isPressurized();
    }

    @Override
    public ReadOnlyBooleanProperty pressurizedProperty() {
        return liquidBulkCargoBehavior.pressurizedProperty();
    }

    private static final class SerializationProxy extends AbstractCargo.SerializationProxy<LiquidBulkCargoImpl> {
        private static final long serialVersionUID = 136782497389L;
        private final boolean pressurized;

        SerializationProxy(LiquidBulkCargoImpl cargo) {
            super(cargo);
            this.pressurized = cargo.isPressurized();
        }

        private Object readResolve() {
            return new LiquidBulkCargoImpl(owner(),
                    value(),
                    durationOfStorage(),
                    hazards(),
                    pressurized,
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
