package htw.prog3.sm.api;

import htw.prog3.persistence.StorageItemPersistenceStrategy;
import htw.prog3.persistence.StoragePersistenceStrategy;
import htw.prog3.sm.api.StorageFacade;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.StorageItem;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;

import static htw.prog3.sm.core.CargoType.LIQUID_BULK_CARGO;
import static htw.prog3.sm.core.CargoType.UNITISED_CARGO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class StorageFacadeTest {
    @Mock
    StorageManagement mockManagement;
    @Mock
    StoragePersistenceStrategy mockStorageStrategy;
    @Mock
    StorageItemPersistenceStrategy mockItemStrategy;
    @Mock
    MapProperty<Integer, StorageItem> mockItemMap;
    @Mock
    StorageItem mockItem;
    @Mock
    StorageManagement otherMockManagement;

    @BeforeEach
    void setUp_shouldDelegateToStorageManagement() {
        initMocks(this);
    }

    @Test
    void getStorageItems_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.getStorageItems();

        verify(mockManagement).getStorageItems();
    }

    @Test
    void getStorageItemsByType_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.getStorageItems(LIQUID_BULK_CARGO);

        verify(mockManagement).getStorageItems(LIQUID_BULK_CARGO);
    }

    @Test
    void isPresentCustomer_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.isPresentCustomer("dummy");

        verify(mockManagement).isPresentCustomer("dummy");
    }

    @Test
    void hasFreeCapacity_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.hasFreeCapacity();

        verify(mockManagement).hasFreeCapacity();
    }

    @Test
    void isStoragePosition_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.isStoragePosition(123);

        verify(mockManagement).isStoragePosition(123);
    }

    @Test
    void addCargo_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.addCargo(UNITISED_CARGO, "dummy", BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true, false);

        verify(mockManagement).addCargo(UNITISED_CARGO, "dummy", BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true, false);
    }

    @Test
    void removeCargo_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.removeCargo(123);

        verify(mockManagement).removeCargo(123);
    }

    @Test
    void inspectCargo_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.inspectCargo(123);

        verify(mockManagement).inspectCargo(123);
    }

    @Test
    void getCustomerRecords_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.getCustomerRecords();

        verify(mockManagement).getCustomerRecords();
    }

    @Test
    void addCustomer_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.addCustomer("dummy");

        verify(mockManagement).addCustomer("dummy");
    }

    @Test
    void removeCustomer_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.removeCustomer("dummt");

        verify(mockManagement).removeCustomer("dummt");
    }

    @Test
    void relocateStorageItem_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.relocateStorageItem(123, 321);

        verify(mockManagement).relocateStorageItem(123, 321);
    }

    @Test
    void getHazards_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.getHazards();

        verify(mockManagement).getHazards();
    }

    @Test
    void save_shouldDelegateToPersistenceStrategy() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.save(mockStorageStrategy);

        verify(mockStorageStrategy).save(mockManagement);
    }

    @Test
    void load_shouldSetLoadedStorageManagementWhenOptionalNotEmpty() {
        doReturn(Optional.of(mockManagement)).when(mockStorageStrategy).load();
        StorageFacade facade = spy(new StorageFacade(mockManagement));

        facade.load(mockStorageStrategy);

        verify(facade).setStorageManagement(mockManagement);
    }

    @Test
    void load_shouldReturnTrueIfSuccessfullyLoaded() {
        doReturn(Optional.of(mockManagement)).when(mockStorageStrategy).load();
        StorageFacade facade = spy(new StorageFacade(mockManagement));

        boolean result = facade.load(mockStorageStrategy);

        assertThat(result).isTrue();
    }

    @Test
    void load_shouldReturnFalseIfFailedToLoad() {
        doReturn(Optional.empty()).when(mockStorageStrategy).load();
        StorageFacade facade = spy(new StorageFacade(mockManagement));

        boolean result = facade.load(mockStorageStrategy);

        assertThat(result).isFalse();
    }

    @Test
    void load_shouldDelegateToPersistenceStrategy() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.load(mockStorageStrategy);

        verify(mockStorageStrategy).load();
    }

    @Test
    void saveItem_shouldDelegateToStorageManagement() {
        doReturn(mockItemMap).when(mockManagement).getStorageItems();
        doReturn(true).when(mockItemStrategy).memoryFileExists();
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.save(999, mockItemStrategy);

        verify(mockManagement).save(999, mockItemStrategy);
    }

    @Test
    void loadItem_shouldDelegateToStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.load(888, mockItemStrategy);

        verify(mockManagement).load(888, mockItemStrategy);
    }

    @Test
    void getStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        StorageManagement actual = facade.getStorageManagement();

        assertThat(actual).isEqualTo(mockManagement);
    }

    @Test
    void setStorageManagement() {
        StorageFacade facade = new StorageFacade(mockManagement);

        facade.setStorageManagement(otherMockManagement);

        StorageManagement actual = facade.getStorageManagement();
        assertThat(actual).isEqualTo(otherMockManagement);
    }

    @Test
    void storageManagementProperty() {
        StorageFacade facade = new StorageFacade(mockManagement);

        ObjectProperty<StorageManagement> managementProperty = facade.storageManagementProperty();

        assertThat(managementProperty.get()).isEqualTo(facade.getStorageManagement());
    }
}