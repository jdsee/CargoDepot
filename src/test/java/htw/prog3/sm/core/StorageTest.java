package htw.prog3.sm.core;

import htw.prog3.sm.core.*;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This test class is really bad written. There is too much setup going on and the tests are hard to understand.
 * This was one of the first test classes for this project. I tried to do better with the other tests.
 */
class StorageTest {
    private Storage storage;

    @Mock
    private Cargo mockCargo01;
    @Mock
    private Cargo mockCargo01_1;
    @Mock
    private Cargo mockCargo01_2;
    @Mock
    private Cargo mockCargo02;
    @Mock
    private Cargo mockCargo03;
    @Mock
    private Customer customerMock01;
    @Mock
    private Customer customerMock02;
    @Mock
    private Customer customerMock03;

    private Duration definedTestDurationOfStorage;
    private BigDecimal definedTestValue;

    private Map<Integer, StorageItem> actualStorageItems;

    private void setUpCustomerMocks() {
        when(customerMock01.getName()).thenReturn("c01");
        when(customerMock02.getName()).thenReturn("c02");
        when(customerMock03.getName()).thenReturn("c03");

        when(customerMock01.getMaxValue()).thenReturn(definedTestValue.pow(10));
        when(customerMock02.getMaxValue()).thenReturn(definedTestValue.pow(10));
        when(customerMock03.getMaxValue()).thenReturn(definedTestValue.pow(10));

        when(customerMock01.getMaxDurationOfStorage()).thenReturn(definedTestDurationOfStorage.multipliedBy(10));
        when(customerMock02.getMaxDurationOfStorage()).thenReturn(definedTestDurationOfStorage.multipliedBy(10));
        when(customerMock03.getMaxDurationOfStorage()).thenReturn(definedTestDurationOfStorage.multipliedBy(10));

    }

    private void setUpCargoMocks() {
        when(mockCargo01.getOwner()).thenReturn(customerMock01);
        when(mockCargo01_1.getOwner()).thenReturn(customerMock01);
        when(mockCargo01_2.getOwner()).thenReturn(customerMock01);
        when(mockCargo02.getOwner()).thenReturn(customerMock02);
        when(mockCargo03.getOwner()).thenReturn(customerMock03);

        when(mockCargo01.getDurationOfStorage()).thenReturn(definedTestDurationOfStorage);
        when(mockCargo01_1.getDurationOfStorage()).thenReturn(definedTestDurationOfStorage);
        when(mockCargo01_2.getDurationOfStorage()).thenReturn(definedTestDurationOfStorage);
        when(mockCargo02.getDurationOfStorage()).thenReturn(definedTestDurationOfStorage);
        when(mockCargo03.getDurationOfStorage()).thenReturn(definedTestDurationOfStorage);

        when(mockCargo01.getValue()).thenReturn(definedTestValue);
        when(mockCargo01_1.getValue()).thenReturn(definedTestValue);
        when(mockCargo01_2.getValue()).thenReturn(definedTestValue);
        when(mockCargo02.getValue()).thenReturn(definedTestValue);
        when(mockCargo03.getValue()).thenReturn(definedTestValue);

        ReadOnlySetProperty<Hazard> hazards = new SimpleSetProperty<>(FXCollections.observableSet());
        doReturn(hazards).when(mockCargo01).getHazards();
        doReturn(hazards).when(mockCargo01_1).getHazards();
        doReturn(hazards).when(mockCargo01_2).getHazards();
        doReturn(hazards).when(mockCargo02).getHazards();
        doReturn(hazards).when(mockCargo03).getHazards();
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.definedTestDurationOfStorage = Duration.ofDays(1);
        this.definedTestValue = new BigDecimal("2.0");
        setUpCustomerMocks();
        setUpCargoMocks();
        this.storage = Storage.ofCapacity(100);

        this.actualStorageItems = null;
    }

    @Test
    void create_shouldReturnNewInstance() {
        Storage storage = Storage.ofCapacity(100);
        Storage other = Storage.ofCapacity(100);

        assertThat(storage).isNotNull().isNotSameAs(other);
    }

    @Test
    void create_shouldReturnInstanceWithPassedCapacity() {
        Storage storage = Storage.ofCapacity(100);

        assertThat(storage.getCapacity()).isEqualTo(100);
    }

    @Test
    void constructorWithRevokedPositions_shouldUseTheRevokedPositionsForAddedCargos() {
        Cargo cargo = createTestCargo();
        Storage storage = new Storage(100, new HashSet<>(asList(5, 10)));

        StorageItem actualItem = storage.addCargo(cargo);

        assertThat(actualItem.getStoragePosition()).isIn(5, 10);
    }

    @Test
    void getCargo_returnsCargoIfPresent() {
        Storage storage = Storage.ofCapacity(100);
        int pos = storage.addCargo(mockCargo01).getStoragePosition();

        Cargo actualCargo = storage.getCargo(pos);

        assertThat(actualCargo).isEqualTo(mockCargo01);
    }

    @Test
    void getCargoInitiallyReturnsEmptyList_Succeeds_Test() {
        actualStorageItems = storage.getStorageItems();

        assertTrue(actualStorageItems.isEmpty());
    }

    @Test
    void getStorageItems_filteredByType_ReturnsOnlyFilteredResults_Test01() {
        Cargo liquidBulkCargo = createTestCargo(CargoType.LIQUID_BULK_CARGO);
        storage.addCargo(liquidBulkCargo);
        Cargo unitisedCargo = createTestCargo(CargoType.UNITISED_CARGO);
        storage.addCargo(unitisedCargo);
        Cargo mixedCargo = createTestCargo(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED);
        storage.addCargo(mixedCargo);

        Map<Integer, StorageItem> filteredStorageItems = storage.getStorageItems(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED);

        assertThat(filteredStorageItems.values())
                .extracting(StorageItem::getCargo)
                .containsExactly(mixedCargo);
    }

    @Test
    void getStorageItems_filteredByType_ReturnsOnlyFilteredResults_Test02() {
        Cargo liquidBulkCargo = createTestCargo(CargoType.LIQUID_BULK_CARGO);
        storage.addCargo(liquidBulkCargo);
        Cargo unitisedCargo = createTestCargo(CargoType.UNITISED_CARGO);
        storage.addCargo(unitisedCargo);
        Cargo mixedCargo = createTestCargo(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED);
        storage.addCargo(mixedCargo);

        Map<Integer, StorageItem> filteredStorageItems = storage.getStorageItems(CargoType.LIQUID_BULK_CARGO);

        assertThat(filteredStorageItems.values())
                .extracting(StorageItem::getCargo)
                .containsExactly(liquidBulkCargo);
    }

    @Test
    void getStorageItems_filteredByType_shouldReturnEmptyListForBaseType() {
        Cargo liquidBulkCargo = createTestCargo(CargoType.LIQUID_BULK_CARGO);
        storage.addCargo(liquidBulkCargo);
        Cargo unitisedCargo = createTestCargo(CargoType.UNITISED_CARGO);
        storage.addCargo(unitisedCargo);
        Cargo mixedCargo = createTestCargo(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED);
        storage.addCargo(mixedCargo);

        Map<Integer, StorageItem> filteredStorageItems = storage.getStorageItems(CargoType.CARGO_BASE_TYPE);

        assertThat(filteredStorageItems.values()).isEmpty();
    }

    @Test
    void getStorageItems_filteredByType_ReturnsNoWrongResults_Test01() {
        Cargo liquidBulkCargo = createTestCargo(CargoType.LIQUID_BULK_CARGO);
        storage.addCargo(liquidBulkCargo);
        Cargo unitisedCargo = createTestCargo(CargoType.UNITISED_CARGO);
        storage.addCargo(unitisedCargo);
        Cargo mixedCargo = createTestCargo(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED);
        storage.addCargo(mixedCargo);

        Map<Integer, StorageItem> filteredStorageItems = storage.getStorageItems(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED);

        assertThat(filteredStorageItems.values())
                .extracting(StorageItem::getCargo)
                .doesNotContain(liquidBulkCargo, unitisedCargo);
    }

    @Test
    void getStorageItems_filteredByType_ReturnsNoWrongResults_Test02() {
        Cargo liquidBulkCargo = createTestCargo(CargoType.LIQUID_BULK_CARGO);
        storage.addCargo(liquidBulkCargo);
        Cargo unitisedCargo = createTestCargo(CargoType.UNITISED_CARGO);
        storage.addCargo(unitisedCargo);
        Cargo mixedCargo = createTestCargo(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED);
        storage.addCargo(mixedCargo);

        Map<Integer, StorageItem> filteredStorageItems = storage.getStorageItems(CargoType.LIQUID_BULK_CARGO);

        assertThat(filteredStorageItems.values())
                .extracting(StorageItem::getCargo)
                .doesNotContain(unitisedCargo);
    }

    @Test
    void addCargoToExistingCustomer_Succeeds_Test() throws Exception {

        storage.addCargo(mockCargo02);

        actualStorageItems = storage.getStorageItems();

        StorageItem actualItem = actualStorageItems.values().stream().findAny().orElseThrow(Exception::new);
        Assertions.assertEquals(mockCargo02, actualItem.getCargo());
    }

    @Test
    void addCargo_ReturnsCorrectStoragePosition_Succeeds_Test() throws Exception {
        storage.addCargo(mockCargo01);
        storage.addCargo(mockCargo03);

        StorageItem storageItemCargo02 = storage.addCargo(mockCargo02);

        actualStorageItems = storage.getStorageItems();
        Cargo resCargo = actualStorageItems.values().stream()
                .filter(item -> item.getStoragePosition() == storageItemCargo02.getStoragePosition())
                .findAny().orElseThrow(Exception::new).getCargo();
        assertEquals(mockCargo02, resCargo);
    }

    @Test
    void addCargo_ExceedsMaxStorageCapacity_Test() {
        Storage storage = Storage.ofCapacity(3);
        storage.addCargo(mockCargo01);
        storage.addCargo(mockCargo02);
        storage.addCargo(mockCargo03);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> storage.addCargo(mockCargo03))
                .withMessage(FailureMessages.STORAGE_CAPACITY_EXCESS)
                .withNoCause();
    }

    @Test
    void removeCargo_Succeeds_Test01() {
        storage.addCargo(mockCargo01);
        storage.addCargo(mockCargo02);
        StorageItem storageItem02 = storage.addCargo(mockCargo03);

        storage.removeCargo(storageItem02.getStoragePosition());

        actualStorageItems = storage.getStorageItems();
        List<Cargo> resultCargos = actualStorageItems.values().stream().map(StorageItem::getCargo).collect(Collectors.toList());
        assertFalse(resultCargos.contains(mockCargo03));
    }

    @Test
    void removeCargo_Succeeds_Test03() {
        storage.addCargo(mockCargo01);
        StorageItem storageItem02 = storage.addCargo(mockCargo02);
        storage.addCargo(mockCargo03);

        storage.removeCargo(storageItem02.getStoragePosition());

        actualStorageItems = storage.getStorageItems();
        assertEquals(2, actualStorageItems.size());
    }

    @Test
    void removeCargo_PassingUnallocatedIdentifier_Fails_Test() {
        storage.addCargo(mockCargo01);
        storage.addCargo(mockCargo01_1);
        StorageItem storageItem02 = storage.addCargo(mockCargo01_2);
        storage.addCargo(mockCargo02);
        storage.addCargo(mockCargo03);

        storage.removeCargo(storageItem02.getStoragePosition());
        assertThrows(IndexOutOfBoundsException.class, () -> storage.removeCargo(storageItem02.getStoragePosition()));
    }

    @Test
    void removeCargo_PassingUnallocatedIdentifierDoesNotRemoveAnyItem_Test() {
        storage.addCargo(mockCargo01);
        storage.addCargo(mockCargo01_1);
        storage.addCargo(mockCargo01_2);
        storage.addCargo(mockCargo02);
        storage.addCargo(mockCargo03);

        try {
            storage.removeCargo(1234);
        } catch (IndexOutOfBoundsException e) {
            actualStorageItems = storage.getStorageItems();
            assertEquals(5, actualStorageItems.size());
            return;
        }
        fail();
    }

    @Test
    void inspectCargo_shouldDelegateCallToCargo() {
        Cargo cargo = spy(createTestCargo());
        StorageItem storageItem = storage.addCargo(cargo);

        storage.inspectCargo(storageItem.getStoragePosition());

        verify(cargo, times(1)).inspect();
    }

    @Test
    void getHazards_ReturnsAnyExistentHazard_Succeeds_Test() {
        when(mockCargo01.getHazards()).thenReturn(createHazardSetOf(Hazard.EXPLOSIVE));
        when(mockCargo02.getHazards()).thenReturn(createHazardSetOf(Hazard.RADIOACTIVE));
        when(mockCargo03.getHazards()).thenReturn(createHazardSetOf(Hazard.TOXIC));
        storage.addCargo(mockCargo01);
        storage.addCargo(mockCargo02);
        storage.addCargo(mockCargo03);

        Collection<Hazard> actualHazards = storage.getHazards();

        boolean storageReturnsCorrectHazards = actualHazards.containsAll(asList(Hazard.EXPLOSIVE, Hazard.RADIOACTIVE, Hazard.TOXIC));
        assertTrue(storageReturnsCorrectHazards);
    }

    private ReadOnlySetProperty<Hazard> createHazardSetOf(Hazard... hazards) {
        return new SimpleSetProperty<>(FXCollections.observableSet(hazards));
    }

    @Test
    void getHazards_ReturnsNoNonExistentHazards_Succeeds_Test() {
        when(mockCargo01.getHazards()).thenReturn(createHazardSetOf(Hazard.EXPLOSIVE));
        when(mockCargo02.getHazards()).thenReturn(createHazardSetOf(Hazard.RADIOACTIVE));
        when(mockCargo03.getHazards()).thenReturn(createHazardSetOf(Hazard.TOXIC));
        storage.addCargo(mockCargo01);
        storage.addCargo(mockCargo02);
        storage.addCargo(mockCargo03);

        Collection<Hazard> hazards = storage.getHazards();

        boolean storageReturnsNoWrongHazards = !hazards.contains(Hazard.FLAMMABLE);
        assertTrue(storageReturnsNoWrongHazards);
    }

    @Test
    void getHazards_ReturnsUpdatedHazardsAfterRemovingCargo_Succeeds_Test() {
        when(mockCargo01.getHazards()).thenReturn(createHazardSetOf(Hazard.EXPLOSIVE));
        when(mockCargo02.getHazards()).thenReturn(createHazardSetOf(Hazard.RADIOACTIVE));
        when(mockCargo03.getHazards()).thenReturn(createHazardSetOf(Hazard.TOXIC));
        storage.addCargo(mockCargo01);
        StorageItem storageItemCargo02 = storage.addCargo(mockCargo02);
        storage.addCargo(mockCargo03);
        storage.removeCargo(storageItemCargo02.getStoragePosition());

        Collection<Hazard> hazards = storage.getHazards();

        boolean storageReturnsCorrectHazards = hazards.containsAll(asList(Hazard.EXPLOSIVE, Hazard.TOXIC));
        assertTrue(storageReturnsCorrectHazards);
    }

    @Test
    void capacityProperty() {
        Storage storage = Storage.ofCapacity(333);

        IntegerProperty capacityProperty = storage.capacityProperty();

        assertThat(capacityProperty.get()).isEqualTo(storage.getCapacity());
    }

    @Test
    void getItemCount() {
        Storage storage = Storage.ofCapacity(100);
        storage.addCargo(mockCargo01);

        int actualItemCount = storage.getItemCount();

        assertThat(actualItemCount).isEqualTo(1);
    }

    @Test
    void itemCountProperty() {
        Storage storage = Storage.ofCapacity(100);
        storage.addCargo(mockCargo01);

        ReadOnlyIntegerProperty itemCountProperty = storage.itemCountProperty();

        assertThat(itemCountProperty.get()).isEqualTo(1);
    }

    @Test
    void storageIsCloneableBySerialization() throws Exception {
        // GIVEN
        Cargo cargo = new UnitisedCargoImpl(new CustomerImpl("exp"),
                BigDecimal.ONE,
                Duration.ofDays(1),
                new HashSet<>(),
                true);
        StorageItem initialItem = storage.addCargo(cargo);

        // WHEN
        Storage deserializedStorage;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(storage);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            deserializedStorage = (Storage) ois.readObject();
        }

        // THEN
        Map<Integer, StorageItem> actualItems = deserializedStorage.getStorageItems();
        assertThat(actualItems).hasSize(1);
        assertThat(actualItems.get(initialItem.getStoragePosition()))
                .extracting(
                        item -> item.getOwner().getName(),
                        StorageItem::getValue,
                        StorageItem::getDurationOfStorage,
                        StorageItem::getStoragePosition,
                        StorageItem::getStorageDate)
                .containsExactly(
                        initialItem.getOwner().getName(),
                        initialItem.getValue(),
                        initialItem.getDurationOfStorage(),
                        initialItem.getStoragePosition(),
                        initialItem.getStorageDate());
    }

    private Cargo createTestCargo() {
        return new UnitisedCargoImpl(new CustomerImpl("x"), BigDecimal.TEN, Duration.ofDays(1),
                new HashSet<>(), true);
    }

    private Cargo createTestCargo(CargoType ofType) {
        Customer customer = new CustomerImpl("x");
        switch (ofType) {
            case UNITISED_CARGO:
                return new UnitisedCargoImpl(customer, BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true);
            case LIQUID_BULK_CARGO:
                return new LiquidBulkCargoImpl(customer, BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true);
            case MIXED_CARGO_LIQUID_BULK_AND_UNITISED:
                return new MixedCargoLiquidBulkAndUnitisedImpl(customer, BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true, false);
            default:
                throw new IllegalArgumentException("CargoType unknown");
        }
    }
}