package htw.prog3.sm.api;

import htw.prog3.persistence.StorageItemPersistenceStrategy;
import htw.prog3.sm.core.*;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlySetProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

public interface StorageManagement extends Serializable {
    static StorageManagement ofCapacity(int capacity) {
        return new StorageManagementImpl(capacity);
    }

    static StorageManagement create() {
        return ofCapacity(Storage.DEFAULT_CAPACITY);
    }

    boolean isPresentCustomer(String customerName);

    MapProperty<String, CustomerRecord> getCustomerRecords();

    void addCustomer(String name);

    boolean removeCustomer(String name);

    boolean hasFreeCapacity();

    boolean isStoragePosition(int position);

    MapProperty<Integer, StorageItem> getStorageItems();

    Map<Integer, StorageItem> getStorageItems(CargoType type);

    ReadOnlySetProperty<Hazard> getHazards();

    Cargo getCargo(int position);

    int addCargo(Cargo cargo);

    int addCargo(CargoType type,
                 String owner,
                 BigDecimal value,
                 Duration durationOfStorage,
                 Set<Hazard> hazards,
                 boolean pressurized,
                 boolean fragile);

    void removeCargo(int storagePosition);

    /**
     * Updates the inspection date for the cargo at the specified storage position.
     *
     * @param storagePosition The position to find the targeted cargo.
     * @throws IndexOutOfBoundsException If the there is no cargo stored at the specified storage position.
     */
    void inspectCargo(int storagePosition);

    int getItemCount();

    ReadOnlyIntegerProperty itemCountProperty();

    int getCapacity();

    void relocateStorageItem(int from, int to);

    void save(int position, StorageItemPersistenceStrategy strategy);

    boolean load(int position, StorageItemPersistenceStrategy strategy);
}
