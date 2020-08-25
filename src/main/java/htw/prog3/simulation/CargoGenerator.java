package htw.prog3.simulation;

import htw.prog3.sm.core.CargoFactory;
import htw.prog3.sm.core.CargoType;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;

public final class CargoGenerator {
    public static final BigDecimal DEFAULT_VALUE = BigDecimal.valueOf(1);
    public static final Duration DEFAULT_DURATION = Duration.ofDays(1);
    public static final Set<Hazard> DEFAULT_HAZARDS = new HashSet<>();
    public static final String DEFAULT_OWNER_NAME = "dummy";
    public static final Customer DEFAULT_OWNER = new CustomerImpl(DEFAULT_OWNER_NAME);

    private static final Random random = new Random();
    private static final List<Supplier<Cargo>> generators = new ArrayList<>();


    static {
        generators.add(() -> CargoFactory.create(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED, DEFAULT_OWNER,
                DEFAULT_VALUE, DEFAULT_DURATION, DEFAULT_HAZARDS, true, false));

        generators.add(() -> CargoFactory.create(CargoType.LIQUID_BULK_CARGO, DEFAULT_OWNER,
                DEFAULT_VALUE, DEFAULT_DURATION, DEFAULT_HAZARDS, true, false));

        generators.add(() -> CargoFactory.create(CargoType.UNITISED_CARGO, DEFAULT_OWNER,
                DEFAULT_VALUE, DEFAULT_DURATION, DEFAULT_HAZARDS, true, false));

    }

    private CargoGenerator() {
    }

    public static Cargo getRandomCargo() {
        int size = generators.size();
        int choice = random.nextInt(size - 1);
        return generators.get(choice).get();
    }
}
