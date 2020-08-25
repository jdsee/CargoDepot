package htw.prog3.sm.core;

import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.UnitisedCargoImpl;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.storageContract.cargo.UnitisedCargo;
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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@SuppressWarnings("ConstantConditions")
class UnitisedCargoImplTest {
    @Test
    void create_shouldNotAcceptNullValue() {
        Throwable t = catchThrowable(() -> UnitisedCargo.create(null, null, null, null, true));

        assertThat(t).isInstanceOf(NullPointerException.class);
    }

    @Test
    void isFragile_Test() {
        Customer owner = mock(Customer.class);
        BigDecimal value = BigDecimal.valueOf(1);
        Duration duration = Duration.ofDays(1);
        Set<Hazard> hazards = new HashSet<>();
        boolean fragile = true;
        UnitisedCargo cargo = new UnitisedCargoImpl(owner, value, duration, hazards, fragile);

        boolean actual = cargo.isFragile();

        assertEquals(fragile, actual);
    }

    @Test
    void fragileProperty() {
        UnitisedCargo cargo = createTestCargo(true);

        ReadOnlyBooleanProperty pressurizedProperty = cargo.fragileProperty();

        assertThat(pressurizedProperty.get()).isTrue();
    }

    @Test
    void isCloneableBySerialization() throws Exception {
        UnitisedCargo cargo = new UnitisedCargoImpl(new CustomerImpl("exp"),
                BigDecimal.ONE,
                Duration.ofDays(1),
                new HashSet<>(),
                true);

        UnitisedCargo deserializedCargo;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(cargo);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            deserializedCargo = (UnitisedCargo) ois.readObject();
        }

        assertThat(deserializedCargo).extracting(
                c -> c.getOwner().getName(),
                Cargo::getValue,
                Cargo::getDurationOfStorage,
                UnitisedCargo::isFragile)
                .containsExactly(
                        cargo.getOwner().getName(),
                        cargo.getValue(),
                        cargo.getDurationOfStorage(),
                        cargo.isFragile());
    }

    private UnitisedCargo createTestCargo(boolean pressurized) {
        Customer owner = mock(Customer.class);
        BigDecimal value = BigDecimal.valueOf(1);
        Duration duration = Duration.ofDays(1);
        Set<Hazard> hazards = new HashSet<>();
        return new UnitisedCargoImpl(owner, value, duration, hazards, pressurized);
    }
}