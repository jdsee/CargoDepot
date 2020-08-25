package htw.prog3.sm.api;

import htw.prog3.persistence.StorageItemPersistenceStrategy;
import htw.prog3.persistence.StoragePersistenceStrategy;
import htw.prog3.sm.core.CargoType;
import htw.prog3.sm.core.CustomerRecord;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.storageContract.cargo.Hazard;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class StorageFacade {
    private final ObjectProperty<StorageManagement> storageManagement;

    public StorageFacade(StorageManagement storageManagement) {
        this.storageManagement = new SimpleObjectProperty<>(storageManagement);
    }

    public MapProperty<Integer, StorageItem> getStorageItems() {
        return getStorageManagement().getStorageItems();
    }

    public Map<Integer, StorageItem> getStorageItems(CargoType type) {
        return getStorageManagement().getStorageItems(type);
    }

    public boolean isPresentCustomer(String name) {
        return getStorageManagement().isPresentCustomer(name);
    }

    public boolean hasFreeCapacity() {
        return getStorageManagement().hasFreeCapacity();
    }

    public boolean isStoragePosition(int position) {
        return getStorageManagement().isStoragePosition(position);
    }

    public int addCargo(CargoType type,
                        String owner,
                        BigDecimal value,
                        Duration durationOfStorage,
                        Set<Hazard> hazards,
                        boolean pressurized,
                        boolean fragile) {
        return getStorageManagement().addCargo(type, owner, value, durationOfStorage, hazards, pressurized, fragile);
    }

    public void removeCargo(int storagePosition) {
        getStorageManagement().removeCargo(storagePosition);
    }

    public void inspectCargo(int storagePosition) {
        getStorageManagement().inspectCargo(storagePosition);
    }

    public MapProperty<String, CustomerRecord> getCustomerRecords() {
        return getStorageManagement().getCustomerRecords();
    }

    public void addCustomer(String name) {
        getStorageManagement().addCustomer(name);
    }

    public boolean removeCustomer(String name) {
        return getStorageManagement().removeCustomer(name);
    }

    public void relocateStorageItem(int from, int to) {
        getStorageManagement().relocateStorageItem(from, to);
    }

    public ReadOnlySetProperty<Hazard> getHazards() {
        return getStorageManagement().getHazards();
    }

    public void save(StoragePersistenceStrategy strategy) {
        strategy.save(getStorageManagement());
    }

    public boolean load(StoragePersistenceStrategy strategy) {
        Optional<StorageManagement> replacement = strategy.load();
        replacement.ifPresent(this::setStorageManagement);

        return replacement.isPresent();
    }

    public void save(int position, StorageItemPersistenceStrategy strategy) {
        getStorageManagement().save(position, strategy);
    }

    public boolean load(int position, StorageItemPersistenceStrategy strategy) {
        return getStorageManagement().load(position, strategy);
    }

    public StorageManagement getStorageManagement() {
        return storageManagement.get();
    }

    public void setStorageManagement(StorageManagement storageManagement) {
        this.storageManagement.set(storageManagement);
    }

    public ObjectProperty<StorageManagement> storageManagementProperty() {
        return storageManagement;
    }
}
