package htw.prog3.sm.core;

import htw.prog3.storageContract.administration.Customer;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import org.apache.commons.lang3.Validate;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class CustomerRecord implements Serializable {
    private final ReadOnlyObjectProperty<Customer> customer;
    private final MapProperty<Integer, StorageItem> storageItems;
    private final ObjectProperty<BigDecimal> totalValue;
    private final ObjectProperty<Duration> totalDurationOfStorage;

    public CustomerRecord(Customer customer) {
        this(customer, new LinkedList<>(), BigDecimal.ZERO, Duration.ofDays(0));
    }

    public CustomerRecord(Customer customer,
                          List<StorageItem> storageItems,
                          BigDecimal totalValue,
                          Duration totalDurationOfStorage) {
        this.customer = new SimpleObjectProperty<>(customer);
        this.storageItems = new SimpleMapProperty<>(FXCollections.observableHashMap());
        this.storageItems.putAll(storageItems.stream()
                .collect(Collectors.toMap(StorageItem::getStoragePosition, Function.identity())));
        this.totalValue = new SimpleObjectProperty<>(totalValue);
        this.totalDurationOfStorage = new SimpleObjectProperty<>(totalDurationOfStorage);
    }

    public List<StorageItem> getStorageItems() {
        return new ArrayList<>(storageItems.values());
    }

    /**
     * Adds the specified storage item to the customer records.
     *
     * @param storageItem The storage item that is to be added.
     * @throws IllegalArgumentException If one or more arguments are null.
     */
    void addStorageItem(StorageItem storageItem) {
        Validate.notNull(storageItem);

        int position = storageItem.getStoragePosition();
        storageItems.put(position, storageItem);
        setTotalValue(getTotalValue().add(storageItem.getCargo().getValue()));
        setTotalDurationOfStorage(getTotalDurationOfStorage().plus(storageItem.getCargo().getDurationOfStorage()));
    }

    /**
     * Removes the specified storage item from the customer records.
     *
     * @param storageItem The storage item that is to be removed.
     * @throws IllegalArgumentException If storageItem is null.
     */
    void removeStorageItem(StorageItem storageItem) {
        Validate.notNull(storageItem);
        int position = storageItem.getStoragePosition();
        if (null != storageItems.remove(position)) {
            setTotalValue(getTotalValue().subtract(storageItem.getCargo().getValue()));
            setTotalDurationOfStorage(getTotalDurationOfStorage().minus(storageItem.getCargo().getDurationOfStorage()));
        }
    }

    @Override
    public String toString() {
        int assetCount = getAssetCount();
        return String.format("customer: %s -- owns: %d item%s.", getCustomer().getName(),
                assetCount, (assetCount == 1) ? "" : "s");
    }

    public Customer getCustomer() {
        return customer.get();
    }

    public ReadOnlyObjectProperty<Customer> customerProperty() {
        return customer;
    }

    public BigDecimal getTotalValue() {
        return totalValue.get();
    }

    public ReadOnlyObjectProperty<BigDecimal> totalValueProperty() {
        return totalValue;
    }

    private void setTotalValue(BigDecimal totalValue) {
        this.totalValue.set(totalValue);
    }

    public Duration getTotalDurationOfStorage() {
        return totalDurationOfStorage.get();
    }

    public ReadOnlyObjectProperty<Duration> totalDurationOfStorageProperty() {
        return totalDurationOfStorage;
    }

    private void setTotalDurationOfStorage(Duration totalDurationOfStorage) {
        this.totalDurationOfStorage.set(totalDurationOfStorage);
    }

    public int getAssetCount() {
        return storageItems.size();
    }

    public ReadOnlyIntegerProperty assetCountProperty() {
        return storageItems.sizeProperty();
    }

    private static final class SerializationProxy implements Serializable {
        private final Customer customer;
        private final StorageItem[] storageItemArray;
        private final String totalValueString;
        private final long totalDurationOfStorageInDays;

        private SerializationProxy(CustomerRecord record) {
            this.customer = record.getCustomer();
            this.storageItemArray = record.getStorageItems().toArray(new StorageItem[0]);
            this.totalValueString = record.getTotalValue().toString();
            this.totalDurationOfStorageInDays = record.getTotalDurationOfStorage().toDays();
        }

        private Object readResolve() {
            BigDecimal totalValue = new BigDecimal(totalValueString);
            Duration totalDurationOfStorage = Duration.ofDays(totalDurationOfStorageInDays);
            return new CustomerRecord(customer, asList(storageItemArray), totalValue, totalDurationOfStorage);
        }

        private static final long serialVersionUID = 857349547207L;
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
