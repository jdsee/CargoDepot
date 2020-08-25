package htw.prog3.sm.core;

import htw.prog3.persistence.StorageItemPersistenceStrategy;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.*;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.*;

import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class StorageManagementImplTest {
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockCustomer.getName()).thenReturn(expectedName);
    }

    @Mock
    private Customer mockCustomer;

    //test values
    private final String expectedName = "c01";
    private final BigDecimal value = BigDecimal.valueOf(2);
    private final Duration durationOfStorage = Duration.ofDays(2);
    private final Set<Hazard> hazards = new HashSet<>();
    private final CargoType cargoType = CargoType.LIQUID_BULK_CARGO;

    @Mock
    private CustomerAdministration mockCustomerAdministration;
    @Mock
    private Storage mockStorage;
    @Captor
    ArgumentCaptor<Customer> customerCaptor;

    @Test
    void addCustomer_Test() {
        StorageManagement storageManagement =
                new StorageManagementImpl(mockStorage, mockCustomerAdministration);

        storageManagement.addCustomer(expectedName);

        verify(mockCustomerAdministration).addCustomer(customerCaptor.capture());
        assertThat(customerCaptor.getValue().getName()).isEqualTo(expectedName);
    }

    //Adding an already present customer name fails
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void addCustomer_ambiguousCustomerNameDoesNotOverridePresentCustomer_Test() {
        StorageManagement storageManagement =
                new StorageManagementImpl(mockStorage, mockCustomerAdministration);
        doThrow(IllegalStateException.class).when(mockCustomerAdministration).addCustomer(any());

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() ->
                storageManagement.addCustomer(expectedName));
    }

    @Mock
    StorageItemPersistenceStrategy mockItemPersistenceStrategy;
    @Mock
    StorageItem otherMockItem;
    @Mock
    StorageItem mockItem;

    //The total duration of storage must initially be 0 for every new customer.
    @Test
    void addCustomer_initiallySetsTotalDurationOfStorageToZero_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        StorageManagement storageManagement = new StorageManagementImpl(mockStorage, customerAdministration);

        storageManagement.addCustomer("x");

        assertThat(storageManagement.getCustomerRecords()).hasEntrySatisfying("x", record ->
                record.getTotalDurationOfStorage().isZero());
    }

    @Test
    void removeCustomer_removesAllProprietaryCargos_Test() {
        StorageManagement management = createStorageManagement();
        management.addCustomer("x");
        management.addCustomer("y");
        addCargoForCustomerWithName(management, "x");
        addCargoForCustomerWithName(management, "y");

        management.removeCustomer("x");

        assertThat(management.getStorageItems()).hasSize(1);
    }

    private void addCargoForCustomerWithName(StorageManagement management, String name) {
        management.addCargo(CargoType.LIQUID_BULK_CARGO,
                name,
                BigDecimal.TEN,
                Duration.ofDays(1),
                new HashSet<>(Collections.singletonList(Hazard.TOXIC)),
                true,
                false);
    }

    //The total value must initially be 0 for every new customer.
    @Test
    void addCustomer_initiallySetsTotalValueToZero_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        StorageManagement storageManagement = new StorageManagementImpl(mockStorage, customerAdministration);

        storageManagement.addCustomer("x");

        assertThat(storageManagement.getCustomerRecords()).hasEntrySatisfying("x", record ->
                record.getTotalValue().equals(BigDecimal.ZERO));
    }

    @Mock
    CustomerRecord mockRecord;
    @Captor
    ArgumentCaptor<StorageItem> itemCaptor;

    @Test
    void addCargo_createsNewCargoIfCustomerExists_Test() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        Storage storage = Storage.ofCapacity(100);
        StorageManagement storageManagement = new StorageManagementImpl(storage, customerAdministration);
        storageManagement.addCustomer("x");

        storageManagement.addCargo(CargoType.LIQUID_BULK_CARGO,
                "x",
                BigDecimal.TEN,
                Duration.ofDays(1),
                new HashSet<>(),
                true,
                false);

        assertThat(storageManagement.getStorageItems()).hasSize(1);
    }

    @Test
    void addCargo_toNonExistentCustomer_Fails_Test() {
        StorageManagement storageManagement =
                new StorageManagementImpl(Storage.ofCapacity(10), mockCustomerAdministration);
        when(mockCustomerAdministration.isPresentCustomer(mockCustomer.getName())).thenReturn(false);

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() ->
                storageManagement.addCargo(cargoType,
                        expectedName,
                        value,
                        durationOfStorage,
                        hazards,
                        true,
                        false)
        ).withMessage(FailureMessages.unknownCustomer(expectedName));
    }

    private StorageManagement createStorageManagement() {
        Storage storage = Storage.ofCapacity(100);
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        return new StorageManagementImpl(storage, customerAdministration);
    }

    @Test
    void addCargo_addsStorageItemToCustomerAdministration_Test() {
        Storage storage = Storage.ofCapacity(100);
        StorageManagement management = new StorageManagementImpl(storage, mockCustomerAdministration);
        doReturn(true).when(mockCustomerAdministration).isPresentCustomer("x");
        doReturn(mockRecord).when(mockCustomerAdministration).getCustomerRecord("x");
        doReturn(mockCustomer).when(mockRecord).getCustomer();
        doReturn("x").when(mockCustomer).getName();

        addCargoForCustomerWithName(management, "x");

        verify(mockCustomerAdministration).addStorageItemAsset(itemCaptor.capture());
        assertThat(itemCaptor.getValue())
                .extracting(item -> item.getOwner().getName())
                .containsExactly("x");
    }

    @Test
    void addCargo_throwsIllegalStateExceptionIfCapacityExceeds() {
        StorageManagement management = StorageManagement.ofCapacity(1);
        management.addCustomer("dummy");
        management.addCargo(createTestCargo("dummy"));

        Throwable throwable = catchThrowable(() -> management.addCargo(createTestCargo("dummy")));

        assertThat(throwable).isInstanceOf(IllegalStateException.class)
                .hasMessage(FailureMessages.STORAGE_CAPACITY_EXCESS);
    }

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Cargo mockCargo;

    @Test
    void addCargo_ThrowsNullPointerExceptionForNullArguments() {
        StorageManagement management = StorageManagement.create();
        management.addCustomer("test");

        Throwable t = catchThrowable(() -> management.addCargo(null, "test", BigDecimal.TEN,
                Duration.ofDays(1), null, true, false));

        assertThat(t).isInstanceOf(NullPointerException.class)
                .hasMessage(FailureMessages.notNull(CargoType.class));
    }

    @Test
    void addCargo_reactionOnNullArg() {
        StorageManagement management = StorageManagement.create();

        Throwable throwable = catchThrowable(() -> management.addCargo(null));

        assertThat(throwable).isInstanceOf(NullPointerException.class);
    }

    @Test
    void addCustomer_throwsNullPointerExceptionForNullArgument() {
        StorageManagement management = StorageManagement.create();

        Throwable t = catchThrowable(() -> management.addCustomer(null));

        assertThat(t).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void relocateStorageItem_shouldUpdateCustomerRecords() {
        Storage storage = Storage.ofCapacity(100);
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        StorageManagement management = new StorageManagementImpl(storage, customerAdministration);
        management.addCustomer("x");
        management.addCustomer("y");
        CustomerRecord customerX = customerAdministration.getCustomerRecord("x");
        CustomerRecord customerY = customerAdministration.getCustomerRecord("y");
        Cargo cargoX = createTestCargo("x");
        Cargo cargoY = createTestCargo("y");
        int posX = management.addCargo(cargoX);
        int posY = management.addCargo(cargoY);

        management.relocateStorageItem(posX, posY);

        assertThat(customerX.getStorageItems())
                .extracting(StorageItem::getStoragePosition)
                .containsExactly(posY);
        assertThat(customerY.getStorageItems())
                .extracting(StorageItem::getStoragePosition)
                .containsExactly(posX);
    }

    @Test
    void addCargo_addsCargoWhenCargoIsPassedAndCustomerExists() {
        CustomerAdministration customerAdministration = CustomerAdministration.create();
        Storage spyStorage = spy(Storage.ofCapacity(100));
        StorageManagement management = new StorageManagementImpl(spyStorage, customerAdministration);
        management.addCustomer("x");
        Customer owner = management.getCustomerRecords().get("x").getCustomer();
        doReturn(owner).when(mockCargo).getOwner();

        management.addCargo(mockCargo);

        verify(spyStorage).addCargo(mockCargo);
    }

    @Test
    void getStorageItems_shouldReturnDefensiveCopy() {
        StorageManagement management = StorageManagement.create();
        management.addCustomer("x");
        doReturn(new CustomerImpl("x")).when(mockCargo).getOwner();
        management.addCargo(mockCargo);

        MapProperty<Integer, StorageItem> items = management.getStorageItems();

        items.clear();
        assertThat(management.getStorageItems()).isNotEmpty();
    }

    @Test
    void getStorageItems_shouldReturnPropertyThatUpdatesWhenItemIsAdded() {
        StorageManagement management = StorageManagement.create();
        management.addCustomer("x");
        doReturn(new CustomerImpl("x")).when(mockCargo).getOwner();

        MapProperty<Integer, StorageItem> items = management.getStorageItems();

        management.addCargo(mockCargo);
        assertThat(items).isNotEmpty();
    }

    @Test
    void getCustomerRecords_shouldReturnDefensiveCopy() {
        StorageManagement management = StorageManagement.create();
        management.addCustomer("x");

        MapProperty<String, CustomerRecord> records = management.getCustomerRecords();

        records.clear();
        assertThat(management.getCustomerRecords()).isNotEmpty();
    }

    @Test
    void getCustomerRecords_shouldReturnPropertyThatUpdatesWhenRecordIsAdded() {
        StorageManagement management = StorageManagement.create();

        MapProperty<String, CustomerRecord> records = management.getCustomerRecords();

        management.addCustomer("x");
        assertThat(records).isNotEmpty();
    }

    @Test
    void getHazards_shouldReturnDefensiveCopy() {
        StorageManagement management = StorageManagement.create();
        management.addCustomer("x");
        management.addCargo(createTestCargo(Hazard.TOXIC));

        Set<Hazard> hazards = management.getHazards();

        hazards.clear();
        assertThat(management.getHazards()).containsExactly(Hazard.TOXIC);
    }

    @Test
    void getHazards_shouldReturnSetThatUpdatesWhenHazardsAdded() {
        StorageManagement management = StorageManagement.create();
        management.addCustomer("x");

        Set<Hazard> hazards = management.getHazards();

        management.addCargo(createTestCargo("x", Hazard.TOXIC));
        assertThat(hazards).containsExactly(Hazard.TOXIC);
    }

    @Test
    void getHazards_shouldReturnSetThatUpdatesWhenHazardsRemoved() {
        StorageManagement management = StorageManagement.create();
        management.addCustomer("x");
        int pos = management.addCargo(createTestCargo("x", Hazard.TOXIC));

        Set<Hazard> hazards = management.getHazards();

        management.removeCargo(pos);
        assertThat(hazards).isEmpty();
    }

    @Test
    void relocateStorageItem_shouldSwapPositionsIfItemIsPresentAtToIndex() {
        StorageManagement management = StorageManagement.ofCapacity(100);
        management.addCustomer("x");
        Cargo cargo1 = createTestCargo("x");
        Cargo cargo2 = createTestCargo("x");
        int pos1 = management.addCargo(cargo1);
        int pos2 = management.addCargo(cargo2);

        management.relocateStorageItem(pos1, pos2);

        assertThat(management.getStorageItems())
                .hasEntrySatisfying(pos1, item -> assertThat(item.getCargo()).isEqualTo(cargo2))
                .hasEntrySatisfying(pos2, item -> assertThat(item.getCargo()).isEqualTo(cargo1));
    }

    private Cargo createTestCargo(String customerName, Hazard... hazards) {
        return new UnitisedCargoImpl(new CustomerImpl(customerName), BigDecimal.TEN, Duration.ofDays(1),
                new HashSet<>(Arrays.asList(hazards)), true);
    }

    @Test
    void relocateStorageItem_shouldUpdatePositionsInStorageItemIfItemIsPresentAtToIndex() {
        StorageManagement management = StorageManagement.ofCapacity(100);
        management.addCustomer("x");
        Cargo cargo1 = createTestCargo("x");
        Cargo cargo2 = createTestCargo("x");
        int pos1 = management.addCargo(cargo1);
        int pos2 = management.addCargo(cargo2);

        management.relocateStorageItem(pos1, pos2);

        assertThat(management.getStorageItems())
                .hasEntrySatisfying(pos1, item -> assertThat(item.getStoragePosition()).isEqualTo(pos1))
                .hasEntrySatisfying(pos2, item -> assertThat(item.getStoragePosition()).isEqualTo(pos2));
    }

    @Test
    void storageManagement_shouldBeSerializable() throws IOException, ClassNotFoundException {
        StorageManagement initialManagement = StorageManagement.create();
        initialManagement.addCustomer("x");
        int expectedPos = initialManagement.addCargo(createTestCargo("x"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(new DataOutputStream(baos));

        oo.writeObject(initialManagement);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInput oi = new ObjectInputStream(new DataInputStream(bais));
        StorageManagement actualAdministration = (StorageManagement) oi.readObject();
        assertThat(actualAdministration.getCustomerRecords().get("x"))
                .extracting(record -> record.getCustomer().getName(),
                        record -> record.getStorageItems().get(0).getStoragePosition())
                .containsExactly("x", expectedPos);
        assertThat(actualAdministration.getStorageItems().values())
                .extracting(StorageItem::getStoragePosition)
                .containsExactly(expectedPos);
    }

    private Cargo createTestCargo(Hazard... hazards) {
        return createTestCargo("x", hazards);
    }

    @Test
    void addCustomer_shouldThrowIllegalArgumentExceptionForBlankNameString() {
        StorageManagement management = StorageManagement.ofCapacity(100);

        Throwable t = catchThrowable(() -> management.addCustomer("        "));

        assertThat(t).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(FailureMessages.CUSTOMER_NAME_EMPTY);
    }

    @Test
    void getStorageItemsByType_shouldDelegateToStorage() {
        StorageManagement management = new StorageManagementImpl(mockStorage, mockCustomerAdministration);

        management.getStorageItems(CargoType.UNITISED_CARGO);

        verify(mockStorage).getStorageItems(CargoType.UNITISED_CARGO);
    }

    @Test
    void getStorageItemsByType_shouldDelegateToStorageWithoutFilterForBaseType() {
        Storage spyStorage = spy(Storage.ofCapacity(100));
        StorageManagement management = new StorageManagementImpl(spyStorage, mockCustomerAdministration);

        management.getStorageItems(CargoType.CARGO_BASE_TYPE);

        verify(spyStorage).getStorageItems();
    }

    @Test
    void inspectCargo() {
        StorageManagement management = new StorageManagementImpl(mockStorage, mockCustomerAdministration);

        management.inspectCargo(999);

        verify(mockStorage).inspectCargo(999);
    }

    @Test
    void save_shouldDelegateToPersistenceStrategyIfStoragePositionIsKnown() {
        doReturn(true).when(mockStorage).isStoragePosition(999);
        StorageManagement management = new StorageManagementImpl(mockStorage, mockCustomerAdministration);
        doReturn(true).when(mockItemPersistenceStrategy).memoryFileExists();

        management.save(999, mockItemPersistenceStrategy);

        verify(mockItemPersistenceStrategy).save(nullable(StorageItem.class));
    }

    @Test
    void saveItem_shouldSaveAllItemsIfNoMemoryFileExists() {
        doReturn(true).when(mockStorage).isStoragePosition(999);
        StorageManagement management = new StorageManagementImpl(mockStorage, mockCustomerAdministration);
        MapProperty<Integer, StorageItem> itemMap = new SimpleMapProperty<>(FXCollections.observableHashMap());
        itemMap.put(0, mockItem);
        itemMap.put(999, otherMockItem);
        doReturn(itemMap).when(mockStorage).getStorageItems();
        doReturn(false).when(mockItemPersistenceStrategy).memoryFileExists();

        management.save(999, mockItemPersistenceStrategy);

        verify(mockItemPersistenceStrategy, times(2)).save(itemCaptor.capture());
        assertThat(itemCaptor.getAllValues()).containsExactly(mockItem, otherMockItem);
    }

    @Test
    void save_shouldThrowIllegalStateExceptionIfStoragePositionNotKnown() {
        doReturn(false).when(mockStorage).isStoragePosition(333);
        StorageManagement management = new StorageManagementImpl(mockStorage, mockCustomerAdministration);

        Throwable throwable = catchThrowable(() -> management.save(333, mockItemPersistenceStrategy));

        assertThat(throwable).isInstanceOf(IllegalStateException.class)
                .hasMessage(FailureMessages.UNALLOCATED_STORAGE_POSITION);
    }

    @Test
    void load_shouldDelegateToPersistenceStrategyAndAddItemIfPresent() {
        doReturn(Optional.of(mockItem)).when(mockItemPersistenceStrategy).load(3000);
        doReturn(new CustomerImpl("dummy")).when(mockItem).getOwner();
        doReturn(true).when(mockCustomerAdministration).isPresentCustomer("dummy");
        StorageManagement management = new StorageManagementImpl(mockStorage, mockCustomerAdministration);

        management.load(3000, mockItemPersistenceStrategy);

        verify(mockItemPersistenceStrategy).load(3000);
        verify(mockStorage).addItem(any(StorageItem.class));
    }

    @Test
    void getItemCount_shouldDelegateToStorage() {
        doReturn(333).when(mockStorage).getItemCount();
        StorageManagement management = new StorageManagementImpl(mockStorage, mockCustomerAdministration);

        int actualCount = management.getItemCount();

        assertThat(actualCount).isEqualTo(333);
    }

    @Test
    void getCargo_shouldDelegateToStorage() {
        StorageManagementImpl management = new StorageManagementImpl(mockStorage, mockCustomerAdministration);

        management.getCargo(123);

        verify(mockStorage).getCargo(123);
    }

    @Test
    void itemCountProperty_shouldDelegateToStorage() {
        StorageManagementImpl management = new StorageManagementImpl(mockStorage, mockCustomerAdministration);

        management.itemCountProperty();

        verify(mockStorage).itemCountProperty();
    }

    @Test
    void getCapacity_shouldDelegateToStorage() {
        StorageManagementImpl management = new StorageManagementImpl(mockStorage, mockCustomerAdministration);

        management.getCapacity();

        verify(mockStorage).getCapacity();
    }
}