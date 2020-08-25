package htw.prog3.sm.core;

import htw.prog3.sm.core.AbstractCargo;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.MixedCargoLiquidBulkAndUnitisedImpl;
import htw.prog3.sm.core.UnitisedCargoImpl;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import javafx.beans.property.ReadOnlyObjectProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class AbstractCargoTest {

    private Cargo cargo;

    private Customer expectedOwner;
    private BigDecimal expectedValue;
    private Duration expectedDuration;
    private Set<Hazard> expectedHazards;
    private boolean isFragileExpected;
    private boolean isPressurizedExpected;

    @BeforeEach
    void setUp() {
        expectedOwner = mock(Customer.class);
        expectedValue = BigDecimal.valueOf(1);
        expectedDuration = Duration.ofDays(1);
        expectedHazards = new HashSet<>();
        isFragileExpected = true;
        isPressurizedExpected = true;
        cargo = new MixedCargoLiquidBulkAndUnitisedImpl(expectedOwner, expectedValue, expectedDuration, expectedHazards, isPressurizedExpected, isFragileExpected);
    }

    @Test
    void getOwner() {
        Customer actualOwner = cargo.getOwner();

        assertEquals(expectedOwner, actualOwner);
    }

    @Test
    void getValue() {
        BigDecimal actualValue = cargo.getValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    void getDurationOfStorage() {
        Duration actualDuration = cargo.getDurationOfStorage();

        assertEquals(actualDuration, expectedDuration);
    }

    @Test
    void getHazards() {
        Collection<Hazard> actualHazards = cargo.getHazards();

        assertEquals(expectedHazards, actualHazards);
    }

    /**
     * Not sure if this is a reasonable unit test
     */
    @Test
    void inspect() {
        Date now = Date.from(Instant.now());
        cargo.inspect();

        assertThat(cargo.getInspectionDate()).isCloseTo(now, 100);
    }

    @Test
    void ownerProperty() {
        Cargo cargo = createTestCargo("dummy");

        ReadOnlyObjectProperty<Customer> owner = cargo.ownerProperty();

        assertThat(owner.get()).isEqualTo(cargo.getOwner());
    }

    @Test
    void valueProperty() {
        Cargo cargo = createTestCargo("dummy");

        ReadOnlyObjectProperty<BigDecimal> value = cargo.valueProperty();

        assertThat(value.get()).isEqualTo(cargo.getValue());
    }

    @Test
    void durationOfStorageProperty() {
        Cargo cargo = createTestCargo("dummy");

        ReadOnlyObjectProperty<Duration> duration = cargo.durationOfStorageProperty();

        assertThat(duration.get()).isEqualTo(cargo.getDurationOfStorage());
    }

    @Test
    void getSimpleHazards() {
        AbstractCargo cargo = (AbstractCargo) createTestCargo("dummy");

        Set<Hazard> hazards = cargo.getSimpleHazards();

        assertThat(hazards).isEmpty();
    }

    // this is probably not reasonable for a unit test
    @Test
    void getInspectionDate() {
        Cargo cargo = createTestCargo("dummy");

        Date inspectionDate = cargo.getInspectionDate();

        assertThat(inspectionDate).isCloseTo(Date.from(Instant.now()), 1000);
    }

    @Test
    void inspectionDateProperty() {
        Cargo cargo = createTestCargo("dummy");

        ReadOnlyObjectProperty<Date> inspectionDate = cargo.inspectionDateProperty();

        assertThat(inspectionDate.get()).isEqualTo(cargo.getInspectionDate());
    }

    @SuppressWarnings("SameParameterValue")
    private Cargo createTestCargo(String owner) {
        return new UnitisedCargoImpl(new CustomerImpl(owner), BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true);
    }
}