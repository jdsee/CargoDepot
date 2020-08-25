package htw.prog3.sm.core;

import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.storageContract.administration.Customer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerImplTest {

    private Customer customer;

    private String expectedName;
    private BigDecimal expectedMaxValue;
    private Duration expectedMaxDuration;

    @BeforeEach
    void setUp() {
        expectedName = "expected";
        expectedMaxValue = BigDecimal.valueOf(1);
        expectedMaxDuration = Duration.ofDays(1);
        customer = new CustomerImpl(expectedName, expectedMaxValue, expectedMaxDuration);
    }

    @Test
    void from_factoryCreatesInstanceFromName() {
        Customer customer = Customer.from("x");

        assertThat(customer.getName()).isEqualTo("x");
    }

    @Test
    void getName_Test() {
        String actualName = customer.getName();

        assertEquals(expectedName, actualName);
    }

    @Test
    void getMaxValue_Test() {
        BigDecimal actualMaxValue = customer.getMaxValue();

        assertEquals(expectedMaxValue, actualMaxValue);
    }

    @Test
    void getMaxDurationOfStorage_Test() {
        Duration actualMaxDuration = customer.getMaxDurationOfStorage();

        assertEquals(expectedMaxDuration, actualMaxDuration);
    }

    @Test
    void toString_returnsAppropriateOutput_Test() {
        String expected = customer.getName();

        assertThat(customer.toString()).isEqualTo(expected);
    }

    @Test
    void nameProperty() {
        Customer customer = new CustomerImpl("dummy");

        ReadOnlyStringProperty nameProperty = customer.nameProperty();

        assertThat(nameProperty.get()).isEqualTo(customer.getName());
    }

    @Test
    void maxValueProperty() {
        Customer customer = new CustomerImpl("dummy");

        ReadOnlyObjectProperty<BigDecimal> valueProperty = customer.maxValueProperty();

        assertThat(valueProperty.get()).isEqualTo(customer.getMaxValue());
    }

    @Test
    void maxDurationOfStorageProperty() {
        Customer customer = new CustomerImpl("dummy");

        ReadOnlyObjectProperty<Duration> durationProperty = customer.maxDurationOfStorageProperty();

        assertThat(durationProperty.get()).isEqualTo(customer.getMaxDurationOfStorage());
    }

    @Test
    void isCloneableBySerialization() throws Exception {
        Customer customer = new CustomerImpl("custoemr");
        Customer deserializedCustomer;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(customer);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            deserializedCustomer = (Customer) ois.readObject();
        }

        assertThat(deserializedCustomer).isEqualToComparingFieldByField(customer);
    }
}