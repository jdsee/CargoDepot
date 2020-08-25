package htw.prog3.sm.core;

import htw.prog3.sm.core.AbstractCargo;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.CustomerRecord;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerRecordTest {
    private CustomerRecord customerRecord;

    @Mock
    private AbstractCargo cargoMock01;
    @Mock
    private AbstractCargo cargoMock02;
    @Mock
    private AbstractCargo cargoMock03;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        Customer customerMock = mock(Customer.class);
        this.customerRecord = new CustomerRecord(customerMock);
        setUpCargoMocks();
    }

    private void setUpCargoMocks() {
        for (Cargo cargoMock : Arrays.asList(cargoMock01, cargoMock02, cargoMock03)) {
            when(cargoMock.getValue()).thenReturn(new BigDecimal(2));
            when(cargoMock.getDurationOfStorage()).thenReturn(Duration.ofDays(1));
        }
    }

    @Test
    void getStorageItems_InitiallyReturnsEmptyCollection_Test() {
        List<StorageItem> storagePositions = customerRecord.getStorageItems();

        assertTrue(storagePositions.isEmpty());
    }

    @Test
    void getStorageItems_filteredByStorage_expectedPositionsPresent_Test() {
        StorageItem storageItem01 = new StorageItem(cargoMock01, 1);
        customerRecord.addStorageItem(storageItem01);
        StorageItem storageItem02 = new StorageItem(cargoMock02, 2);
        customerRecord.addStorageItem(storageItem02);
        StorageItem storageItem03 = new StorageItem(cargoMock03, 3);
        customerRecord.addStorageItem(storageItem03);

        List<StorageItem> actualStorageItems = customerRecord.getStorageItems();

        List<StorageItem> expectedStorageItems = Arrays.asList(storageItem01, storageItem02, storageItem03);
        assertThat(actualStorageItems).containsExactlyElementsOf(expectedStorageItems);
    }

    @Test
    void addStorageItem_addsStoragePosition_Test() {
        int expectedPosition = 1;
        StorageItem storageItem = new StorageItem(cargoMock01, expectedPosition);

        customerRecord.addStorageItem(storageItem);

        List<StorageItem> actualStorageItems = customerRecord.getStorageItems();
        assertTrue(actualStorageItems.contains(storageItem));
    }

    @Test
    void addStorageItem_addsOnlyOneStoragePosition_Test() {
        StorageItem storageItem01 = new StorageItem(cargoMock01, 1);
        customerRecord.addStorageItem(storageItem01);

        List<StorageItem> actualStorageItems = customerRecord.getStorageItems();
        assertEquals(1, actualStorageItems.size());
    }

    @Mock
    Customer dummyCustomer;

    @Test
    void addStorageItem_hasNotSideEffectsOnOtherItems_Test() {
        CustomerRecord record = new CustomerRecord(dummyCustomer);
        StorageItem item01 = new StorageItem(cargoMock01, 1);
        StorageItem item02 = new StorageItem(cargoMock02, 2);

        record.addStorageItem(item01);
        record.addStorageItem(item02);

        List<StorageItem> actualItems = record.getStorageItems();
        assertThat(actualItems).containsExactly(item01, item02);
    }

    @Test
    void addStorageItem_updatesTotalValue_Test() {
        BigDecimal value01 = new BigDecimal(2);
        when(cargoMock01.getValue()).thenReturn(value01);
        BigDecimal value02 = new BigDecimal(4);
        when(cargoMock02.getValue()).thenReturn(value02);
        StorageItem storageItem01 = new StorageItem(cargoMock01, 1);
        StorageItem storageItem02 = new StorageItem(cargoMock02, 2);

        customerRecord.addStorageItem(storageItem01);
        customerRecord.addStorageItem(storageItem02);

        BigDecimal expectedTotalValue = value01.add(value02);
        BigDecimal actualTotalValue = customerRecord.getTotalValue();
        assertEquals(expectedTotalValue, actualTotalValue);
    }

    @Test
    void addStorageItem_updatesTotalDurationOfStorage_Test() {
        Duration duration01 = Duration.ofDays(1);
        when(cargoMock01.getDurationOfStorage()).thenReturn(duration01);
        Duration duration02 = Duration.ofDays(2);
        when(cargoMock02.getDurationOfStorage()).thenReturn(duration02);
        StorageItem storageItem01 = new StorageItem(cargoMock01, 1);
        StorageItem storageItem02 = new StorageItem(cargoMock02, 2);

        customerRecord.addStorageItem(storageItem01);
        customerRecord.addStorageItem(storageItem02);

        Duration expectedTotalDuration = duration01.plus(duration02);
        Duration actualTotalDuration = customerRecord.getTotalDurationOfStorage();
        assertEquals(expectedTotalDuration, actualTotalDuration);
    }

    @Test
    void removeProperty_removesStoragePosition_Test() {
        int storagePosition = 1;
        StorageItem storageItem01 = new StorageItem(cargoMock01, storagePosition);
        customerRecord.addStorageItem(storageItem01);
        StorageItem storageItem02 = new StorageItem(cargoMock02, 2);
        customerRecord.addStorageItem(storageItem02);

        customerRecord.removeStorageItem(storageItem01);

        List<StorageItem> actualStorageItems = customerRecord.getStorageItems();
        assertFalse(actualStorageItems.contains(storageItem01));
    }

    @Test
    void removeProperty_retainsRemainingStoragePosition_Test() {
        StorageItem storageItem01 = new StorageItem(cargoMock01, 1);
        customerRecord.addStorageItem(storageItem01);
        StorageItem storageItem02 = new StorageItem(cargoMock02, 2);
        customerRecord.addStorageItem(storageItem02);

        customerRecord.removeStorageItem(storageItem01);

        List<StorageItem> actualStorageItems = customerRecord.getStorageItems();
        assertTrue(actualStorageItems.contains(storageItem02));
    }

    @Test
    void removeProperty_updatesTotalValue_Test() {
        BigDecimal value01 = new BigDecimal(2);
        when(cargoMock01.getValue()).thenReturn(value01);
        BigDecimal value02 = new BigDecimal(4);
        when(cargoMock02.getValue()).thenReturn(value02);
        StorageItem storageItem01 = new StorageItem(cargoMock01, 1);
        customerRecord.addStorageItem(storageItem01);
        StorageItem storageItem02 = new StorageItem(cargoMock02, 2);
        customerRecord.addStorageItem(storageItem02);

        customerRecord.removeStorageItem(storageItem02);

        BigDecimal actualTotalValue = customerRecord.getTotalValue();
        assertEquals(value01, actualTotalValue);
    }

    @Test
    void removeProperty_updatesTotalDurationOfStorage_Test() {
        Duration duration01 = Duration.ofDays(1);
        when(cargoMock01.getDurationOfStorage()).thenReturn(duration01);
        Duration duration02 = Duration.ofDays(2);
        when(cargoMock02.getDurationOfStorage()).thenReturn(duration02);
        StorageItem storageItem01 = new StorageItem(cargoMock01, 1);
        customerRecord.addStorageItem(storageItem01);
        StorageItem storageItem02 = new StorageItem(cargoMock02, 2);
        customerRecord.addStorageItem(storageItem02);

        customerRecord.removeStorageItem(storageItem02);

        Duration actualTotalDuration = customerRecord.getTotalDurationOfStorage();
        assertEquals(duration01, actualTotalDuration);
    }

    @Test
    void getTotalValue_NoNullResult_Test() {
        BigDecimal actualTotalValue = customerRecord.getTotalValue();

        assertNotNull(actualTotalValue);
    }

    @Test
    void getTotalDurationOfStorage_NoNullResult_Test() {
        Duration actualTotalDuration = customerRecord.getTotalDurationOfStorage();

        assertNotNull(actualTotalDuration);
    }

    @Test
    void toString_returnsAppropriateOutput_Test() {
        String customerName = customerRecord.getCustomer().getName();
        int assetCount = customerRecord.getAssetCount();
        String expected = "customer: " + customerName + " -- owns: " + assetCount + " items.";
        assertThat(customerRecord.toString()).isEqualTo(expected);
    }

    @Test
    void customerProperty() {
        CustomerRecord record = new CustomerRecord(new CustomerImpl("xyz"));

        ReadOnlyObjectProperty<Customer> customerProperty = record.customerProperty();

        assertThat(customerProperty.get()).isEqualTo(record.getCustomer());
    }

    @Test
    void totalValueProperty() {
        CustomerRecord record = new CustomerRecord(new CustomerImpl("xyz"));

        ReadOnlyObjectProperty<BigDecimal> totalValueProperty = record.totalValueProperty();

        assertThat(totalValueProperty.get()).isEqualTo(record.getTotalValue());
    }

    @Test
    void totalDurationOfStorageProperty() {
        CustomerRecord record = new CustomerRecord(new CustomerImpl("xyz"));

        ReadOnlyObjectProperty<Duration> totalDurationOfStorageProperty = record.totalDurationOfStorageProperty();

        assertThat(totalDurationOfStorageProperty.get()).isEqualTo(record.getTotalDurationOfStorage());
    }

    @Test
    void assetCountProperty() {
        CustomerRecord record = new CustomerRecord(new CustomerImpl("xyz"));

        ReadOnlyIntegerProperty assetCountProperty = record.assetCountProperty();

        assertThat(assetCountProperty.get()).isEqualTo(record.getAssetCount());
    }

    @Mock(serializable = true)
    Cargo mockCargo;

    @Test
    void serializationClonesInstance_Test() throws IOException, ClassNotFoundException {
        Customer initialCustomer = new CustomerImpl("test");
        CustomerRecord initialRecord = new CustomerRecord(initialCustomer);
        doReturn(BigDecimal.ONE).when(mockCargo).getValue();
        doReturn(Duration.ofDays(1)).when(mockCargo).getDurationOfStorage();
        StorageItem initialItem = new StorageItem(mockCargo, 1);
        initialRecord.addStorageItem(initialItem);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(new DataOutputStream(baos));

        oo.writeObject(initialRecord);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInput oi = new ObjectInputStream(new DataInputStream(bais));
        CustomerRecord actualRecord = (CustomerRecord) oi.readObject();
        assertThat(actualRecord.getStorageItems()).hasSize(1);
        assertThat(actualRecord.getCustomer())
                .extracting(Customer::getName)
                .containsExactly(initialCustomer.getName());
    }
}