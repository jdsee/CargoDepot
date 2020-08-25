package htw.prog3.sm.core;

import htw.prog3.sm.core.*;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.UnitisedCargo;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
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
import java.util.Date;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class StorageItemTest {
    @Mock
    private AbstractCargo mockCargo;

    @BeforeEach
    void setUp_Test() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void create_shouldReturnNewInstance() {
        StorageItem item = StorageItem.create(mockCargo, 0, new Date(123456L));
        StorageItem other = StorageItem.create(mockCargo, 0, new Date(123456L));

        assertThat(item).isNotNull().isNotSameAs(other);
    }

    @Test
    void constructor_shouldReturnNewInstance() {
        StorageItem item = new StorageItem(mockCargo, 0, new Date(123456L));
        StorageItem other = new StorageItem(mockCargo, 0, new Date(123456L));

        assertThat(item).isNotNull().isNotSameAs(other);
    }

    @Test
    void getCargo_Test() {
        StorageItem item = new StorageItem(mockCargo, 1);

        Cargo actualCargo = item.getCargo();

        assertEquals(mockCargo, actualCargo);
    }

    @Test
    void cargoProperty() {
        StorageItem item = new StorageItem(mockCargo, 1);

        ReadOnlyObjectProperty<Cargo> cargo = item.cargoProperty();

        assertThat(cargo.get()).isEqualTo(item.getCargo());
    }

    @Test
    void getStoragePosition_Test() {
        int expectedPosition = 1;
        StorageItem item = new StorageItem(mockCargo, expectedPosition);

        int actualPosition = item.getStoragePosition();

        assertEquals(expectedPosition, actualPosition);
    }

    @Test
    void storagePositionProperty() {
        int expectedPosition = 1;
        StorageItem item = new StorageItem(mockCargo, expectedPosition);

        ReadOnlyIntegerProperty actualPosition = item.storagePositionProperty();

        assertThat(actualPosition.get()).isEqualTo(expectedPosition);
    }

    @Test
    void getStorageDate() {
        StorageItem item = new StorageItem(mockCargo, 1);

        Date storageDate = item.getStorageDate();

        assertThat(storageDate).isToday();
    }

    @Test
    void storageDateProperty() {
        StorageItem item = new StorageItem(mockCargo, 1);

        ReadOnlyObjectProperty<Date> storageDate = item.storageDateProperty();

        assertThat(storageDate.get()).isEqualTo(item.getStorageDate());
    }

    @Test
    void getOwner_shouldDelegateToCargo() {
        StorageItem item = new StorageItem(mockCargo, 1);

        item.getOwner();

        verify(mockCargo).getOwner();
    }

    @Test
    void ownerProperty_shouldDelegateToCargo() {
        StorageItem item = new StorageItem(mockCargo, 1);

        item.ownerProperty();

        verify(mockCargo).ownerProperty();
    }

    @Test
    void getValue_shouldDelegateToCargo() {
        StorageItem item = new StorageItem(mockCargo, 1);

        item.getValue();

        verify(mockCargo).getValue();
    }

    @Test
    void getDurationOfStorage_shouldDelegateToCargo() {
        StorageItem item = new StorageItem(mockCargo, 1);

        item.getDurationOfStorage();

        verify(mockCargo).getDurationOfStorage();
    }

    @Test
    void getHazards_shouldDelegateToCargo() {
        StorageItem item = new StorageItem(mockCargo, 1);

        item.getHazards();

        verify(mockCargo).getHazards();
    }

    @Test
    void inspectionDateProperty_shouldDelegateToCargo() {
        StorageItem item = new StorageItem(mockCargo, 1);

        item.inspectionDateProperty();

        verify(mockCargo).inspectionDateProperty();
    }

    @Test
    void toString_shouldReturnProperRepresentation() {
        Cargo cargo = new LiquidBulkCargoImpl(
                new CustomerImpl("x"), BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true);
        Date date = new Date(123456789L);
        StorageItem item = new StorageItem(cargo, 1, date);

        String actual = item.toString();

        assertThat(actual).satisfies(s -> {
            assertThat(s).contains("[1]");
            assertThat(s).contains("type: Liquid Bulk Cargo");
            assertThat(s).contains("owner: x");
        });
    }

    @Test
    void isCloneableBySerialization() throws Exception {
        // cargo needs to be real to serialized correctly
        UnitisedCargo cargo = new UnitisedCargoImpl(new CustomerImpl("exp"),
                BigDecimal.ONE,
                Duration.ofDays(1),
                new HashSet<>(),
                true);
        StorageItem initalItem = new StorageItem(cargo, 1);

        StorageItem deserializedItem;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(initalItem);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            deserializedItem = (StorageItem) ois.readObject();
        }

        assertThat(deserializedItem).extracting(
                item -> item.getOwner().getName(),
                StorageItem::getValue,
                StorageItem::getDurationOfStorage,
                StorageItem::getStoragePosition,
                StorageItem::getStorageDate)
                .containsExactly(
                        initalItem.getOwner().getName(),
                        initalItem.getValue(),
                        initalItem.getDurationOfStorage(),
                        initalItem.getStoragePosition(),
                        initalItem.getStorageDate());
    }
}