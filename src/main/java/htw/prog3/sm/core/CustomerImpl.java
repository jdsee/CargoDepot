package htw.prog3.sm.core;

import htw.prog3.storageContract.administration.Customer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;

public class CustomerImpl implements Customer {

    private final transient ReadOnlyStringProperty name;
    private final transient ReadOnlyObjectProperty<BigDecimal> maxValue;
    private final transient ReadOnlyObjectProperty<Duration> maxDurationOfStorage;

    public CustomerImpl(String name) {
        this(name, DEFAULT_MAX_VALUE, DEFAULT_MAX_DURATION_OF_STORAGE);
    }

    public CustomerImpl(String name, BigDecimal maxValue, Duration maxDurationOfStorage) {
        this.name = new SimpleStringProperty(name);
        this.maxValue = new SimpleObjectProperty<>(maxValue);
        this.maxDurationOfStorage = new SimpleObjectProperty<>(maxDurationOfStorage);
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    @Override
    public BigDecimal getMaxValue() {
        return maxValue.get();
    }

    @Override
    public ReadOnlyObjectProperty<BigDecimal> maxValueProperty() {
        return maxValue;
    }

    @Override
    public Duration getMaxDurationOfStorage() {
        return maxDurationOfStorage.get();
    }

    @Override
    public ReadOnlyObjectProperty<Duration> maxDurationOfStorageProperty() {
        return maxDurationOfStorage;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static final class SerializationProxy implements Serializable {
        private final String name;
        private final String maxValueString;
        private final Long maxDurationInDays;

        public SerializationProxy(Customer customer) {
            this.name = customer.getName();
            this.maxValueString = customer.getMaxValue().toString();
            this.maxDurationInDays = customer.getMaxDurationOfStorage().toDays();
        }

        private Object readResolve() {
            BigDecimal value = new BigDecimal(maxValueString);
            Duration duration = Duration.ofDays(maxDurationInDays);
            return new CustomerImpl(name, value, duration);
        }

        private static final long serialVersionUID = 123456L;
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
