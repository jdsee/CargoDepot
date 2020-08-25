package htw.prog3.sm.core;

import htw.prog3.routing.persistence.all.PersistenceType;

public class FailureMessages {
    public static final String MUST_BE_NOT_NULL = "must not be null.";
    public static final String UNKNOWN_CUSTOMER = "The specified customer is not known to the CustomerAdministration.";
    public static final String UNALLOCATED_STORAGE_POSITION = "The specified storage position is not allocated.";
    public static final String UNKNOWN_CARGO_TYPE = "The specified cargo type is not known.";
    public static final String STORAGE_CAPACITY_EXCESS = "The storage capacity reached the limit. Remove cargos to make space for new ones.";
    public static final String CUSTOMER_NAME_EMPTY = "Customer name must be not empty or null.";
    public static final String NO_DATA_PERSISTED = "There is no file to load the storage from.";
    public static final String PERSISTED_ITEM_NOT_FOUND = "The item at the requested position could not be load.";

    public static String unknownCustomer(String name) {
        return String.format("Customer '%s' not known", name);
    }

    public static String customerNameAmbiguous(String name) {
        return String.format("Customer '%s' already present. Customer names must be unique", name);
    }

    public static String unknownPersistenceType(PersistenceType type) {
        return String.format("Persistence Type '%s' is not known.", type);
    }

    public static String notNull(Object o) {
        return String.format("'%s' is not allowed to be null.", o);
    }
}
