package htw.prog3.sm.core;

import htw.prog3.storageContract.administration.Customer;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.apache.commons.lang3.Validate;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class CustomerAdministration implements Serializable {
    private final MapProperty<String, CustomerRecord> customerRecords;

    private CustomerAdministration(List<CustomerRecord> recordList) {
        Validate.notNull(recordList);
        ObservableMap<String, CustomerRecord> customerRecords = FXCollections.observableHashMap();
        customerRecords.putAll(recordList.stream()
                .collect(Collectors.toMap(record -> record.getCustomer().getName(), Function.identity())));
        this.customerRecords = new SimpleMapProperty<>(customerRecords);
    }

    public static CustomerAdministration create() {
        return new CustomerAdministration(new LinkedList<>());
    }

    /**
     * Returns a Map containing all customers registered in this customer administration as keys
     * and a list of all the storage positions of their owned cargoes.
     *
     * @return A Map with all customer names as keys and the customer and property information as value.
     */
    public ReadOnlyMapProperty<String, CustomerRecord> getCustomerRecords() {
        return customerRecords;
    }

    /**
     * Adds the specified customer to the customer administration.
     * The customers are identified by their names, so their names are not allowed to exist twice in the system.
     *
     * @param customer The identifiable, unique name of the customer that is to be added.
     * @throws IllegalStateException If the specified customer name is already assigned.
     * @throws NullPointerException  If the specified customer object is not initialized.
     */
    public void addCustomer(Customer customer) {
        Validate.notNull(customer, "Customer " + FailureMessages.MUST_BE_NOT_NULL);
        String customerName = customer.getName();
        validateCustomerNameUnique(customerName);
        CustomerRecord customerRecord = new CustomerRecord(customer);
        customerRecords.put(customerName, customerRecord);
    }

    /**
     * Verifies that the specified name does not exist in the customer administration until now.
     *
     * @param name The customer to be verified.
     * @throws IllegalStateException If the specified customer name is already assigned.
     */
    private void validateCustomerNameUnique(String name) {
        if (isPresentCustomer(name))
            throw new IllegalStateException(FailureMessages.customerNameAmbiguous(name));
    }

    /**
     * Returns @code{true} if the customer associated to the specified name is assigned in this customer administration
     * or @code{false} if it's not present else.
     *
     * @param customerName The customer name that is to be checked.
     * @return @code{true} if the customer associated to the specified name is present, @code{false} else.
     */
    public boolean isPresentCustomer(String customerName) {
        return customerRecords.containsKey(customerName);
    }

    /**
     * Removes the specified customer. All storage items the customer owns will be
     * deleted in the appropriate storage by calling this method.
     *
     * @param customerName The customer that is to be removed.
     */
    public CustomerRecord removeCustomer(String customerName) {
        return customerRecords.remove(customerName);
    }

    /**
     * Returns a CustomerProperties object that contains the customer and all the storage position of his
     * proprietary cargos. The total value and duration of storage is also stored in there.
     *
     * @param customer The customer to whom the record is requested.
     * @return CustomerProperties object that contains the customer and all the storage position of his proprietary cargoes.
     * @throws IllegalArgumentException If the specified customer is not known.
     */
    CustomerRecord getCustomerRecord(Customer customer) {
        return getCustomerRecord(customer.getName());
    }

    CustomerRecord getCustomerRecord(String name) {
        if (!isPresentCustomer(name))
            throw new IllegalArgumentException(FailureMessages.unknownCustomer(name));
        return customerRecords.get(name);
    }

    /**
     * Adds the specified storage item to the customer records.
     *
     * @param storageItem The storage item that is to be added.
     * @throws IllegalArgumentException If the specified customer does not exist in the customer administration.
     */
    public void addStorageItemAsset(StorageItem storageItem) {
        CustomerRecord customerRecord = getCustomerRecord(storageItem.getOwner());
        customerRecord.addStorageItem(storageItem);
    }

    /**
     * Removes the specified storage item from the customer records.
     *
     * @param storageItem The storage item that is to be removed.
     * @throws IllegalArgumentException If the specified customer does not exist in the customer administration.
     */
    public void removeStorageItemAsset(StorageItem storageItem) {
        CustomerRecord customerRecord = getCustomerRecord(storageItem.getOwner());
        customerRecord.removeStorageItem(storageItem);
    }

    private static final class SerializationProxy implements Serializable {
        private final CustomerRecord[] customerRecordArray;

        private SerializationProxy(CustomerAdministration customerAdministration) {
            this.customerRecordArray = customerAdministration.getCustomerRecords().values().toArray(new CustomerRecord[0]);
        }

        private Object readResolve() {
            return new CustomerAdministration(asList(customerRecordArray));
        }

        private static final long serialVersionUID = 32478934L;
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