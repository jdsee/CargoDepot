package htw.prog3.sm.core;

import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.LiquidBulkCargoImpl;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.storageContract.cargo.LiquidBulkCargo;
import javafx.beans.property.ReadOnlyBooleanProperty;
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
import static org.mockito.Mockito.mock;

class LiquidBulkCargoImplTest {

    @Test
    void isPressurized() {
        LiquidBulkCargo cargo = createTestCargo(true);

        boolean actual = cargo.isPressurized();

        assertThat(actual).isTrue();
    }

    @Test
    void pressurizedProperty() {
        LiquidBulkCargo cargo = createTestCargo(true);

        ReadOnlyBooleanProperty pressurizedProperty = cargo.pressurizedProperty();

        assertThat(pressurizedProperty.get()).isTrue();
    }

    @Test
    void isCloneableBySerialization() throws Exception {
        LiquidBulkCargo cargo = new LiquidBulkCargoImpl(new CustomerImpl("exp"),
                BigDecimal.ONE,
                Duration.ofDays(1),
                new HashSet<>(),
                true);

        LiquidBulkCargo deserializedCargo;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(cargo);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            deserializedCargo = (LiquidBulkCargo) ois.readObject();
        }

        assertThat(deserializedCargo)
                .extracting(
                        c -> c.getOwner().getName(),
                        Cargo::getValue,
                        Cargo::getDurationOfStorage,
                        LiquidBulkCargo::isPressurized)
                .containsExactly(
                        cargo.getOwner().getName(),
                        cargo.getValue(),
                        cargo.getDurationOfStorage(),
                        cargo.isPressurized());
    }

    @SuppressWarnings("SameParameterValue")
    private LiquidBulkCargo createTestCargo(boolean pressurized) {
        Customer owner = mock(Customer.class);
        BigDecimal value = BigDecimal.valueOf(1);
        Duration duration = Duration.ofDays(1);
        Set<Hazard> hazards = new HashSet<>();
        return new LiquidBulkCargoImpl(owner, value, duration, hazards, pressurized);
    }
}