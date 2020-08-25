package htw.prog3.simulation;

import htw.prog3.simulation.CargoGenerator;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.storageContract.cargo.Cargo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CargoGeneratorTest {
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void addRandomCargo_shouldAlwaysReturnNewInstance() {
        StorageManagement management = StorageManagement.create();
        management.addCustomer(CargoGenerator.DEFAULT_OWNER_NAME);

        Cargo cargo = CargoGenerator.getRandomCargo();
        Cargo other = CargoGenerator.getRandomCargo();

        assertThat(cargo).isNotNull().isNotSameAs(other);
    }
}