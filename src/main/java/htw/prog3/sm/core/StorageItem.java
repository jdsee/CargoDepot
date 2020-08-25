package htw.prog3.sm.core;

import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

public class StorageItem implements Serializable {
    private final ReadOnlyObjectProperty<Cargo> cargo;
    private final ReadOnlyObjectProperty<Date> storageDate;
    private final ReadOnlyIntegerProperty storagePosition;

    public StorageItem(Cargo cargo, int storagePosition) {
        this(cargo, storagePosition, Calendar.getInstance().getTime());
    }

    public StorageItem(Cargo cargo, int storagePosition, Date storageDate) {
        this.cargo = new SimpleObjectProperty<>(cargo);
        this.storagePosition = new SimpleIntegerProperty(storagePosition);
        this.storageDate = new SimpleObjectProperty<>(storageDate);
    }

    public static StorageItem create(Cargo cargo, int to, Date storageDate) {
        return new StorageItem(cargo, to, storageDate);
    }

    public Cargo getCargo() {
        return cargo.get();
    }

    public ReadOnlyObjectProperty<Cargo> cargoProperty() {
        return cargo;
    }

    public Date getStorageDate() {
        return storageDate.get();
    }

    public ReadOnlyObjectProperty<Date> storageDateProperty() {
        return storageDate;
    }

    public int getStoragePosition() {
        return storagePosition.get();
    }

    public ReadOnlyIntegerProperty storagePositionProperty() {
        return storagePosition;
    }

    public Customer getOwner() {
        return getCargo().getOwner();
    }

    public ReadOnlyObjectProperty<Customer> ownerProperty() {
        return getCargo().ownerProperty();
    }

    public ReadOnlyObjectProperty<Date> inspectionDateProperty() {
        return getCargo().inspectionDateProperty();
    }

    public BigDecimal getValue() {
        return getCargo().getValue();
    }

    public Duration getDurationOfStorage() {
        return getCargo().getDurationOfStorage();
    }

    public ObservableSet<Hazard> getHazards() {
        return getCargo().getHazards();
    }

    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedStorageDate = simpleDateFormat.format(getStorageDate());
        String formattedInspectionDate = simpleDateFormat.format(getCargo().getInspectionDate());
        return String.format("[%d] type: %s | owner: %s | storage date: %s | last inspection: %s",
                getStoragePosition(), getCargo().getCargoType(),
                getCargo().getOwner(), formattedStorageDate, formattedInspectionDate);
    }

    private static final class SerializationProxy implements Serializable {
        private final Cargo cargo;
        private final Date storageDate;
        private final int storagePosition;

        private SerializationProxy(StorageItem item) {
            this.cargo = item.getCargo();
            this.storageDate = item.getStorageDate();
            storagePosition = item.getStoragePosition();
        }

        private Object readResolve() {
            return new StorageItem(cargo, storagePosition, storageDate);
        }

        private static final long serialVersionUID = 1L;
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
