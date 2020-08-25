package htw.prog3.sm.core;

import htw.prog3.sm.core.*;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import javafx.collections.ObservableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class CustomerAdministrationTest {

    @Mock
    private Customer mockCustomer01;
    @Mock
    private Customer mockCustomer02;
    @Mock
    private Customer mockCustomer03;
    @Mock
    private Cargo mockCargo01;
    @Mock
    private Cargo mockCargo02;

    private ObservableMap<String, CustomerRecord> resCustomerRecords;

    private void setUpCustomerMocks() {
        String customerName01 = "c01";
        when(mockCustomer01.getName()).thenReturn(customerName01);
        when(mockCustomer02.getName()).thenReturn("c02");
        when(mockCustomer03.getName()).thenReturn("c03");
    }

    private void setUpCargoMocks() {
        for (Cargo mockCargo : Arrays.asList(mockCargo01, mockCargo02)) {
            when(mockCargo.getValue()).thenReturn(BigDecimal.ONE);
            when(mockCargo.getDurationOfStorage()).thenReturn(Duration.ofDays(1));
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        setUpCustomerMocks();
        setUpCargoMocks();

        this.resCustomerRecords = null;
    }

    private void addTestCustomers(CustomerAdministration administration) {
        administration.addCustomer(mockCustomer01);
        administration.addCustomer(mockCustomer02);
        administration.addCustomer(mockCustomer03);
    }

    @Test
    void getCustomers_initiallyReturnsEmptyMap_succeeds_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        resCustomerRecords = customerAdministration.getCustomerRecords();

        assertTrue(resCustomerRecords.isEmpty());
    }

    @Test
    void addCustomerInitially_succeeds_Test01() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        customerAdministration.addCustomer(mockCustomer01);

        List<Customer> actualCustomers = customerAdministration.getCustomerRecords().values().stream()
                .map(CustomerRecord::getCustomer).collect(Collectors.toList());
        assertTrue(actualCustomers.contains(mockCustomer01));
    }

    @Test
    void addCustomerInitially_succeeds_Test02() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        customerAdministration.addCustomer(mockCustomer01);

        resCustomerRecords = customerAdministration.getCustomerRecords();
        assertEquals(1, resCustomerRecords.size());
    }

    @Test
    void addCustomerInitially_succeeds_Test03() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        customerAdministration.addCustomer(mockCustomer01);

        CustomerRecord actualCustomerRecord = customerAdministration.getCustomerRecord(mockCustomer01.getName());
        assertNotNull(actualCustomerRecord.getCustomer());
    }

    @Test
    void addVariousValidCustomers_succeeds_Test01() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        customerAdministration.addCustomer(mockCustomer01);
        customerAdministration.addCustomer(mockCustomer02);
        customerAdministration.addCustomer(mockCustomer03);

        resCustomerRecords = customerAdministration.getCustomerRecords();
        assertEquals(3, resCustomerRecords.size());
    }

    @Test
    void addVariousValidCustomers_succeeds_Test02() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        customerAdministration.addCustomer(mockCustomer01);
        customerAdministration.addCustomer(mockCustomer02);
        customerAdministration.addCustomer(mockCustomer03);

        List<Customer> actualCustomers = customerAdministration.getCustomerRecords().values().stream()
                .map(CustomerRecord::getCustomer).collect(Collectors.toList());
        assertTrue(actualCustomers.contains(mockCustomer02));
    }

    @Test
    void addCustomerPassingNull_Fails_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        assertThrows(NullPointerException.class, () -> customerAdministration.addCustomer(null));
    }

    @Test
    void addSameCustomerTwice_Fails_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        Customer customer = new CustomerImpl("test");
        customerAdministration.addCustomer(customer);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> customerAdministration.addCustomer(customer))
                .withMessage(FailureMessages.customerNameAmbiguous(customer.getName()))
                .withNoCause();
    }

    //Adding an already present customer name fails
    @Test
    void addCustomer_addingAmbiguousNameFails_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        String ambiguousName = "22";
        when(mockCustomer01.getName()).thenReturn(ambiguousName);
        when(mockCustomer02.getName()).thenReturn(ambiguousName);
        customerAdministration.addCustomer(mockCustomer01);

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() ->
                customerAdministration.addCustomer(mockCustomer01))
                .withMessage(FailureMessages.customerNameAmbiguous(ambiguousName))
                .withNoCause();
    }

    @Test
    void addCustomer_addingAmbiguousNameHasNoSideEffects_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        String ambiguousName = "22";
        when(mockCustomer01.getName()).thenReturn(ambiguousName);
        when(mockCustomer02.getName()).thenReturn(ambiguousName);
        customerAdministration.addCustomer(mockCustomer01);

        try {
            customerAdministration.addCustomer(mockCustomer01);
        } catch (Exception ignored) {
        }

        assertThat(customerAdministration.getCustomerRecords()).hasSize(1);
        CustomerRecord actualRecord = customerAdministration.getCustomerRecords().get(ambiguousName);
        assertThat(actualRecord).matches(record -> ambiguousName.equals(record.getCustomer().getName()));
    }

    @Test
    void getCustomer_ValidName_succeeds_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        Customer customer = new CustomerImpl("1");

        customerAdministration.addCustomer(customer);

        CustomerRecord actualRecord = customerAdministration.getCustomerRecord(customer.getName());
        assertThat(actualRecord.getCustomer()).isEqualTo(customer);
    }

    @Test
    void getCustomer_NonValidName_Fails_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        customerAdministration.addCustomer(mockCustomer01);

        assertThrows(IllegalArgumentException.class,
                () -> customerAdministration.getCustomerRecord("UNKNOWN"));
    }

    @Test
    void removeExistentCustomer_succeeds_Test01() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        addTestCustomers(customerAdministration);

        customerAdministration.removeCustomer(mockCustomer02.getName());

        List<Customer> actualCustomers = customerAdministration.getCustomerRecords().values().stream()
                .map(CustomerRecord::getCustomer).collect(Collectors.toList());
        assertFalse(actualCustomers.contains(mockCustomer02));
    }

    @Test
    void removeExistentCustomer_keepsRemainingCustomers_Test01() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        addTestCustomers(customerAdministration);

        customerAdministration.removeCustomer(mockCustomer02.getName());

        resCustomerRecords = customerAdministration.getCustomerRecords();
        assertEquals(2, resCustomerRecords.size());
    }

    @Test
    void removeExistentCustomer_keepsRemainingCustomers_Test02() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        addTestCustomers(customerAdministration);

        customerAdministration.removeCustomer(mockCustomer02.getName());

        List<Customer> actualCustomers = customerAdministration.getCustomerRecords().values().stream()
                .map(CustomerRecord::getCustomer).collect(Collectors.toList());
        assertTrue(actualCustomers.contains(mockCustomer01));
    }

    @Test
    void remove_NonExistentCustomer_doesNotManipulate_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        addTestCustomers(customerAdministration);
        Map<String, CustomerRecord> expectedCustomerRecords = customerAdministration.getCustomerRecords();

        customerAdministration.removeCustomer("NON EXISTENT");

        Map<String, CustomerRecord> actualCustomerRecords = customerAdministration.getCustomerRecords();
        assertEquals(expectedCustomerRecords, actualCustomerRecords);
    }

    @Test
    void addCustomerRecord_addsAppropriateValueToTotal_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        addTestCustomers(customerAdministration);
        when(mockCargo01.getOwner()).thenReturn(mockCustomer01);
        final BigDecimal value01 = new BigDecimal(2);
        when(mockCargo01.getValue()).thenReturn(value01);
        when(mockCargo02.getOwner()).thenReturn(mockCustomer01);
        final BigDecimal value02 = new BigDecimal(4);
        when(mockCargo02.getValue()).thenReturn(value02);
        StorageItem storageItem01 = new StorageItem(mockCargo01, 0);
        StorageItem storageItem02 = new StorageItem(mockCargo02, 1);

        customerAdministration.addStorageItemAsset(storageItem01);
        customerAdministration.addStorageItemAsset(storageItem02);

        BigDecimal expectedValue = value01.add(value02);
        BigDecimal actualValue = customerAdministration.getCustomerRecord(mockCustomer01).getTotalValue();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    void addCustomerRecord_addsAppropriateDurationOfStorageToTotal_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        addTestCustomers(customerAdministration);
        when(mockCargo01.getOwner()).thenReturn(mockCustomer01);
        final Duration duration01 = Duration.ofDays(1);
        when(mockCargo01.getDurationOfStorage()).thenReturn(duration01);
        when(mockCargo02.getOwner()).thenReturn(mockCustomer01);
        final Duration duration02 = Duration.ofDays(1);
        when(mockCargo02.getDurationOfStorage()).thenReturn(duration02);
        StorageItem storageItem01 = new StorageItem(mockCargo01, 0);
        StorageItem storageItem02 = new StorageItem(mockCargo02, 1);

        customerAdministration.addStorageItemAsset(storageItem01);
        customerAdministration.addStorageItemAsset(storageItem02);

        Duration expectedTotalDuration = duration01.plus(duration02);
        Duration actualTotalDuration = customerAdministration.getCustomerRecord(mockCustomer01).getTotalDurationOfStorage();
        assertEquals(expectedTotalDuration, actualTotalDuration);
    }

    @Test
    void getCustomerRecord_succeeds_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        customerAdministration.addCustomer(mockCustomer01);

        Customer actual = customerAdministration.getCustomerRecord(mockCustomer01.getName()).getCustomer();
        assertEquals(mockCustomer01, actual);
    }

    @Test
    void getCustomerRecord_unknownCustomerNamePassed_Fails_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        Customer customer = new CustomerImpl("test");
        customerAdministration.addCustomer(customer);
        String nonExistentName = "x";

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(
                        () -> customerAdministration.getCustomerRecord(nonExistentName)
                ).withMessage(FailureMessages.unknownCustomer(nonExistentName))
                .withNoCause();
    }

    @Test
    void removeCustomerRecord_succeeds_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        Customer customer = new CustomerImpl("test");
        customerAdministration.addCustomer(customer);
        doReturn(customer).when(mockCargo01).getOwner();
        StorageItem storageItem = new StorageItem(mockCargo01, 0);
        customerAdministration.addStorageItemAsset(storageItem);

        customerAdministration.removeStorageItemAsset(storageItem);

        CustomerRecord customerRecord = customerAdministration.getCustomerRecord(customer.getName());
        assertThat(customerRecord.getStorageItems()).isEmpty();
    }

    @Test
    void removeCustomerRecord_addsAppropriateDurationOfStorageToTotal_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        Customer customer = new CustomerImpl("test");
        customerAdministration.addCustomer(customer);
        doReturn(customer).when(mockCargo01).getOwner();
        doReturn(Duration.ofDays(1)).when(mockCargo01).getDurationOfStorage();
        StorageItem storageItem = new StorageItem(mockCargo01, 0);

        customerAdministration.addStorageItemAsset(storageItem);

        CustomerRecord record = customerAdministration.getCustomerRecord(customer.getName());
        assertThat(record.getTotalDurationOfStorage()).isEqualTo(Duration.ofDays(1));
    }

    @Test
    void removeCustomerRecord_removesAppropriateDurationOfStorageFromTotal_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        Customer customer = new CustomerImpl("test");
        customerAdministration.addCustomer(customer);
        doReturn(customer).when(mockCargo01).getOwner();
        doReturn(Duration.ofDays(1)).when(mockCargo01).getDurationOfStorage();
        StorageItem storageItem = new StorageItem(mockCargo01, 0);
        customerAdministration.addStorageItemAsset(storageItem);

        customerAdministration.removeStorageItemAsset(storageItem);

        CustomerRecord record = customerAdministration.getCustomerRecord(customer.getName());
        assertThat(record.getTotalDurationOfStorage()).isEqualTo(Duration.ofDays(0));
    }

    @Test
    void removeCustomerRecord_addsAppropriateValueToTotal_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        Customer customer = new CustomerImpl("test");
        customerAdministration.addCustomer(customer);
        doReturn(customer).when(mockCargo01).getOwner();
        doReturn(BigDecimal.TEN).when(mockCargo01).getValue();
        StorageItem storageItem = new StorageItem(mockCargo01, 0);

        customerAdministration.addStorageItemAsset(storageItem);

        CustomerRecord record = customerAdministration.getCustomerRecord(customer.getName());
        assertThat(record.getTotalValue()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void removeCustomerRecord_removesAppropriateValueFromTotal_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        Customer customer = new CustomerImpl("test");
        customerAdministration.addCustomer(customer);
        doReturn(customer).when(mockCargo01).getOwner();
        doReturn(BigDecimal.TEN).when(mockCargo01).getValue();
        StorageItem storageItem = new StorageItem(mockCargo01, 0);
        customerAdministration.addStorageItemAsset(storageItem);

        customerAdministration.removeStorageItemAsset(storageItem);

        CustomerRecord record = customerAdministration.getCustomerRecord(customer.getName());
        assertThat(record.getTotalValue()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void customerAdministrationSerializationClonesInstance_Test() throws IOException, ClassNotFoundException {
        CustomerAdministration initialAdministration = CustomerAdministration.create();
        String customerName = "test";
        Customer initialCustomer = new CustomerImpl(customerName);
        initialAdministration.addCustomer(initialCustomer);
        CustomerRecord initialRecord = initialAdministration.getCustomerRecord(customerName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(new DataOutputStream(baos));

        oo.writeObject(initialAdministration);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInput oi = new ObjectInputStream(new DataInputStream(bais));
        CustomerAdministration actualAdministration = (CustomerAdministration) oi.readObject();
        assertThat(actualAdministration.getCustomerRecords()).hasSize(1);
        assertThat(actualAdministration.getCustomerRecord(customerName))
                .isNotNull()
                .extracting(record -> record.getCustomer().getName(),
                        CustomerRecord::getTotalDurationOfStorage,
                        CustomerRecord::getTotalValue,
                        CustomerRecord::getAssetCount)
                .containsExactly(customerName,
                        initialRecord.getTotalDurationOfStorage(),
                        initialRecord.getTotalValue(),
                        initialRecord.getAssetCount());
    }
}
