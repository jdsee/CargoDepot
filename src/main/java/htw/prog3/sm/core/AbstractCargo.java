package htw.prog3.sm.core;

import htw.prog3.sm.util.ValidationHelper;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractCargo implements Cargo {
    private final ReadOnlyObjectProperty<Customer> owner;
    private final ReadOnlyObjectProperty<BigDecimal> value;
    private final ReadOnlyObjectProperty<Duration> durationOfStorage;
    private final ReadOnlySetProperty<Hazard> hazards;
    private final ObjectProperty<Date> inspectionDate;

    AbstractCargo(Customer owner, BigDecimal value, Duration durationOfStorage, Set<Hazard> hazards, Date inspectionDate) {
        ValidationHelper.requireNonNullConstructorArgs(
                this.getClass(), owner, value, durationOfStorage, hazards, inspectionDate);

        this.owner = new SimpleObjectProperty<>(owner);
        this.value = new SimpleObjectProperty<>(value);
        this.durationOfStorage = new SimpleObjectProperty<>(durationOfStorage);
        HashSet<Hazard> safeHazards = new HashSet<>(hazards);
        this.hazards = new SimpleSetProperty<>(FXCollections.observableSet(safeHazards));
        this.inspectionDate = new SimpleObjectProperty<>(inspectionDate);
    }

    @Override
    public Customer getOwner() {
        return owner.get();
    }

    @Override
    public ReadOnlyObjectProperty<Customer> ownerProperty() {
        return owner;
    }

    @Override
    public BigDecimal getValue() {
        return value.get();
    }

    @Override
    public ReadOnlyObjectProperty<BigDecimal> valueProperty() {
        return value;
    }

    @Override
    public Duration getDurationOfStorage() {
        return durationOfStorage.get();
    }

    @Override
    public ReadOnlyObjectProperty<Duration> durationOfStorageProperty() {
        return durationOfStorage;
    }

    @Override
    public ReadOnlySetProperty<Hazard> getHazards() {
        return hazards;
    }

    public Set<Hazard> getSimpleHazards() {
        return new HashSet<>(hazards);
    }

    @Override
    public Date getInspectionDate() {
        return inspectionDate.get();
    }

    @Override
    public ReadOnlyObjectProperty<Date> inspectionDateProperty() {
        return inspectionDate;
    }

    /**
     * Not testable: usage of system time
     */
    @Override
    public void inspect() {
        inspectionDate.setValue(Date.from(Instant.now()));
    }

    abstract static class SerializationProxy<T extends Cargo> implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Customer owner;
        private final String valueString;
        private final long durationOfStorageInDays;
        private final Hazard[] hazardsArray;
        private final Date inspectionDate;

        SerializationProxy(T cargo) {
            this.owner = cargo.getOwner();
            this.valueString = cargo.getValue().toString();
            this.durationOfStorageInDays = cargo.getDurationOfStorage().toDays();
            this.hazardsArray = cargo.getHazards().toArray(new Hazard[0]);
            this.inspectionDate = cargo.getInspectionDate();
        }

        Customer owner() {
            return owner;
        }

        BigDecimal value() {
            return new BigDecimal(valueString);
        }

        Duration durationOfStorage() {
            return Duration.ofDays(durationOfStorageInDays);
        }

        Set<Hazard> hazards() {
            return new HashSet<>(Arrays.asList(hazardsArray));
        }

        Date inspectionDate() {
            return inspectionDate;
        }
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
