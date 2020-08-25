package htw.prog3.sm.core;

import htw.prog3.persistence.StorageItemPersistenceStrategy;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.ui.cli.control.ValidationPattern;
import htw.prog3.util.BindingUtils;
import javafx.beans.property.*;
import org.apache.commons.lang3.Validate;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class StorageManagementImpl implements StorageManagement {
    private final ObjectProperty<CustomerAdministration> customerAdministration;
    private final ObjectProperty<Storage> storage;

    public StorageManagementImpl(Storage storage, CustomerAdministration customerAdministration) {
        this.storage = new SimpleObjectProperty<>(storage);
        this.customerAdministration = new SimpleObjectProperty<>(customerAdministration);
    }

    public StorageManagementImpl(int capacity) {
        this(Storage.ofCapacity(capacity), CustomerAdministration.create());
    }

    @Override
    public synchronized boolean isPresentCustomer(String customerName) {
        return getCustomerAdministration().isPresentCustomer(customerName);
    }

    @Override
    public synchronized MapProperty<String, CustomerRecord> getCustomerRecords() {
        return BindingUtils.createObservableMirrorMap(getCustomerAdministration().getCustomerRecords());
    }

    /**
     * @param name Name of the new customer.
     * @throws IllegalStateException if the specified name is already used in the customer administration.
     */
    @Override
    public synchronized void addCustomer(String name) {
        if (null == name || name.isEmpty() || ValidationPattern.BLANK.matcher(name).matches())
            throw new IllegalArgumentException(FailureMessages.CUSTOMER_NAME_EMPTY);
        Customer customer = new CustomerImpl(name);
        getCustomerAdministration().addCustomer(customer);
    }

    @Override
    public synchronized boolean removeCustomer(String customerName) {
        boolean succeeded = isPresentCustomer(customerName);
        if (succeeded) {
            CustomerRecord record = getCustomerAdministration().removeCustomer(customerName);
            removeRemainingCargos(record);
        }
        return succeeded;
    }

    @Override
    public synchronized boolean isStoragePosition(int position) {
        return getStorage().isStoragePosition(position);
    }

    @Override
    public synchronized boolean hasFreeCapacity() {
        return getStorage().hasFreeCapacity();
    }

    @Override
    synchronized public MapProperty<Integer, StorageItem> getStorageItems() {
        return BindingUtils.createObservableMirrorMap(getStorage().getStorageItems());
    }

    @Override
    synchronized public Map<Integer, StorageItem> getStorageItems(CargoType type) {
        if (CargoType.CARGO_BASE_TYPE.equals(type))
            return getStorageItems();
        return getStorage().getStorageItems(type);
    }

    /**
     * @return The position where the new cargo has been stored.
     * @throws IllegalStateException If the specified customer is not known.
     */
    @Override
    public synchronized int addCargo(CargoType type,
                                     String owner,
                                     BigDecimal value,
                                     Duration durationOfStorage,
                                     Set<Hazard> hazards,
                                     boolean pressurized,
                                     boolean fragile) {
        return addCargo(owner, () -> {
            CustomerRecord customerRecord = getCustomerAdministration().getCustomerRecord(owner);
            return CargoFactory.create(
                    type, customerRecord.getCustomer(), value, durationOfStorage, hazards, pressurized, fragile);
        });
    }

    /**
     * @return The position where the new cargo has been stored.
     * @throws IllegalStateException If the specified customer is not known.
     */
    @Override
    public synchronized int addCargo(Cargo cargo) {
        Validate.notNull(cargo, FailureMessages.notNull(cargo.getClass()));
        String owner = cargo.getOwner().getName();
        return addCargo(owner, () -> cargo);
    }

    private int addCargo(String owner, Supplier<Cargo> cargoSupplier) {
        if (!hasFreeCapacity())
            throw new IllegalStateException(FailureMessages.STORAGE_CAPACITY_EXCESS);

        validateCustomerIsPresent(owner);

        Cargo cargo = cargoSupplier.get();
        StorageItem newItem = getStorage().addCargo(cargo);
        getCustomerAdministration().addStorageItemAsset(newItem);

        return newItem.getStoragePosition();
    }

    /**
     * Updates the inspection date for the cargo at the specified storage position.
     *
     * @param storagePosition The position to find the targeted cargo.
     * @throws IndexOutOfBoundsException If the there is no cargo stored at the specified storage position.
     */
    @Override
    public synchronized void inspectCargo(int storagePosition) {
        getStorage().inspectCargo(storagePosition);
    }

    @Override
    public Cargo getCargo(int position) {
        return storage.get().getCargo(position);
    }

    /**
     * @param storagePosition The position that is to be removed.
     */
    @Override
    public synchronized void removeCargo(int storagePosition) {
        if (getStorage().isStoragePosition(storagePosition)) {
            StorageItem storageItem = getStorage().removeCargo(storagePosition);
            getCustomerAdministration().removeStorageItemAsset(storageItem);
        }
    }

    @Override
    public synchronized ReadOnlySetProperty<Hazard> getHazards() {
        return getStorage().getHazards();
    }

    /**
     * Relocates the storage at the specified from index to the specified to index.
     * The items are replaced if the the to position is already allocated.
     *
     * @param from The position of the item that is to be relocated.
     * @param to   The target position.
     */
    @Override
    public synchronized void relocateStorageItem(int from, int to) {
        if (isStoragePosition(from)) {
            StorageItem fromItem = getStorageItem(from);
            Optional<StorageItem> toItem = rearrangeStorageItem(fromItem, to);

            toItem.ifPresent(item -> rearrangeStorageItem(item, from));
        }
    }

    private Optional<StorageItem> rearrangeStorageItem(StorageItem item, int newPosition) {
        StorageItem replacement = StorageItem.create(item.getCargo(), newPosition, item.getStorageDate());
        replaceStorageItemAssets(item, replacement);
        return getStorage().addItem(replacement);
    }

    private void replaceStorageItemAssets(StorageItem oldItem, StorageItem newItem) {
        getCustomerAdministration().removeStorageItemAsset(oldItem);
        getCustomerAdministration().addStorageItemAsset(newItem);
    }

    @Override
    public synchronized void save(int position, StorageItemPersistenceStrategy strategy) {
        if (!isStoragePosition(position))
            throw new IllegalStateException(FailureMessages.UNALLOCATED_STORAGE_POSITION);

        if (!strategy.memoryFileExists()) {
            getStorageItems().values().forEach(strategy::save);
        } else {
            StorageItem item = getStorageItem(position);
            strategy.save(item);
        }
    }

    @Override
    public synchronized boolean load(int position, StorageItemPersistenceStrategy strategy) {
        Optional<StorageItem> optItem = strategy.load(position);

        return optItem.isPresent() && addStorageItem(optItem.get());
    }

    @Override
    public int getItemCount() {
        return getStorage().getItemCount();
    }

    @Override
    public ReadOnlyIntegerProperty itemCountProperty() {
        return getStorage().itemCountProperty();
    }

    @Override
    public int getCapacity() {
        return getStorage().getCapacity();
    }

    /**
     * If the owner is unknown the specified item will be ignored.
     *
     * @param item The item that is to be added.
     */
    private boolean addStorageItem(StorageItem item) {
        Customer owner = item.getOwner();
        boolean customerPresent = isPresentCustomer(owner.getName());
        if (customerPresent) {
            Optional<StorageItem> ancestor = getStorage().addItem(item);
            ancestor.ifPresent(getCustomerAdministration()::removeStorageItemAsset);
            getCustomerAdministration().addStorageItemAsset(item);
        }
        return customerPresent;
    }

    private void validateCustomerIsPresent(String name) {
        if (!isPresentCustomer(name))
            throw new IllegalStateException(FailureMessages.unknownCustomer(name));
    }

    private synchronized CustomerAdministration getCustomerAdministration() {
        return customerAdministration.get();
    }

    private synchronized Storage getStorage() {
        return storage.get();
    }

    private void removeRemainingCargos(CustomerRecord record) {
        record.getStorageItems().stream()
                .map(StorageItem::getStoragePosition)
                .forEach(getStorage()::removeCargo);
    }

    private synchronized StorageItem getStorageItem(int position) {
        return getStorage().getStorageItem(position);
    }

    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * Not tested because method is supposed to be private and is not used anywhere in the code.
     * It's just a security feature to prevent {@code NotSerializableException}
     */
    private Object readObject(ObjectInputStream in) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required.");
    }

    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 85734957456245L;
        private final CustomerAdministration customerAdministration;
        private final Storage storage;

        private SerializationProxy(StorageManagementImpl management) {
            this.customerAdministration = management.getCustomerAdministration();
            this.storage = management.getStorage();
        }

        private Object readResolve() {
            return new StorageManagementImpl(storage, customerAdministration);
        }

    }
}