package htw.prog3.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.storageContract.administration.Customer;

import java.util.Locale;

public class ProcessLogDictionary {
    private final Locale locale;

    private final String itemRemoved;
    private final String itemAdded;
    private final String customerRemoved;
    private final String customerAdded;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ProcessLogDictionary(@JsonProperty("locale") Locale locale,
                                @JsonProperty("itemRemoved") String itemRemoved,
                                @JsonProperty("itemAdded") String itemAdded,
                                @JsonProperty("customerRemoved") String customerRemoved,
                                @JsonProperty("customerAdded") String customerAdded) {
        this.locale = locale;
        this.itemRemoved = itemRemoved;
        this.itemAdded = itemAdded;
        this.customerRemoved = customerRemoved;
        this.customerAdded = customerAdded;
    }

    public Locale getLocale() {
        return locale;
    }

    String itemRemovedMsg(StorageItem item) {
        return String.format(itemRemoved, item.getStoragePosition());
    }

    String itemAddedMsg(StorageItem item) {
        return String.format(itemAdded, item.getStoragePosition(), item.getOwner().getName());
    }

    String customerRemovedMsg(Customer customer) {
        return String.format(customerRemoved, customer.getName());
    }

    String customerAddedMsg(Customer customer) {
        return String.format(customerAdded, customer.getName());
    }
}
