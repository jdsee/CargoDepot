package htw.prog3.sm.core;

import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.MixedCargoLiquidBulkAndUnitisedImpl;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.*;
import javafx.beans.property.ReadOnlyBooleanProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class MixedCargoLiquidBulkAndUnitisedImplImplTest {

    private MixedCargoLiquidBulkAndUnitisedImpl cargo;

    private boolean isFragileExpected;
    private boolean isPressurizedExpected;

    @BeforeEach
    void setUp() {
        Customer owner = mock(Customer.class);
        BigDecimal value = BigDecimal.valueOf(1);
        Duration duration = Duration.ofDays(1);
        Set<Hazard> hazards = new HashSet<>();
        isFragileExpected = true;
        isPressurizedExpected = true;
        cargo = new MixedCargoLiquidBulkAndUnitisedImpl(owner, value, duration, hazards, isPressurizedExpected, isFragileExpected);
    }

    @Test
    void isFragile_Test() {
        boolean isFragileActual = cargo.isFragile();

        assertEquals(isFragileExpected, isFragileActual);

    }

    @Test
    void isPressurized_Test() {
        boolean isPressurizedActual = cargo.isPressurized();

        assertEquals(isPressurizedExpected, isPressurizedActual);
    }

    @Test
    void isCloneableBySerialization() throws Exception {
        MixedCargoLiquidBulkAndUnitised cargo = new MixedCargoLiquidBulkAndUnitisedImpl(new CustomerImpl("exp"),
                BigDecimal.ONE,
                Duration.ofDays(1),
                new HashSet<>(),
                true,
                true);

        MixedCargoLiquidBulkAndUnitised deserializedCargo;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(cargo);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            deserializedCargo = (MixedCargoLiquidBulkAndUnitised) ois.readObject();
        }

        assertThat(deserializedCargo).extracting(
                c -> c.getOwner().getName(),
                Cargo::getValue,
                Cargo::getDurationOfStorage,
                UnitisedCargo::isFragile,
                LiquidBulkCargo::isPressurized)
                .containsExactly(
                        cargo.getOwner().getName(),
                        cargo.getValue(),
                        cargo.getDurationOfStorage(),
                        cargo.isFragile(),
                        cargo.isPressurized());
    }

    @Test
    void pressurizedProperty() {
        MixedCargoLiquidBulkAndUnitised cargo = createTestCargo(true, false);

        ReadOnlyBooleanProperty pressurizedProperty = cargo.pressurizedProperty();

        assertThat(pressurizedProperty.get()).isTrue();
    }

    @Test
    void fragileProperty() {
        MixedCargoLiquidBulkAndUnitised cargo = createTestCargo(true, false);

        ReadOnlyBooleanProperty pressurizedProperty = cargo.fragileProperty();

        assertThat(pressurizedProperty.get()).isFalse();
    }

    @SuppressWarnings("SameParameterValue")
    private MixedCargoLiquidBulkAndUnitised createTestCargo(boolean pressurized, boolean fragile) {
        Customer owner = mock(Customer.class);
        BigDecimal value = BigDecimal.valueOf(1);
        Duration duration = Duration.ofDays(1);
        Set<Hazard> hazards = new HashSet<>();
        return new MixedCargoLiquidBulkAndUnitisedImpl(owner, value, duration, hazards, pressurized, fragile);
    }
}