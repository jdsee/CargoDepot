package htw.prog3;

import htw.prog3.persistence.jbp.XmlEncodingStrategy;
import htw.prog3.persistence.jos.SerializationStrategy;
import htw.prog3.persistence.ra.RandomAccessPersistenceStrategy;
import htw.prog3.sm.api.StorageFacade;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.CargoType;
import htw.prog3.storageContract.cargo.Hazard;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("DuplicatedCode")
public class PersistenceExample {
    public static final BigDecimal DEFAULT_VALUE = BigDecimal.ONE;
    public static final Duration DEFAULT_DURATION = Duration.ofDays(1);
    public static final Set<Hazard> DEFAULT_HAZARDS = new HashSet<>();

    public static void main(String[] args) {
        PersistenceExample example = new PersistenceExample();
        System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++++++++ JOS ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        example.runJosExample();

        System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++++++++ JBP ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        example.runJbpExample();

        System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++ RANDOM ACCESS ++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        example.runRandomAccessExample();
    }

//    ++++++++++++++++++++++++++++++++++++++++++++++++++++ JOS +++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private void runJosExample() {
        StorageFacade storageFacade = new StorageFacade(StorageManagement.create());

        String customerNameX = "x";
        storageFacade.addCustomer(customerNameX);
        String customerNameY = "y";
        storageFacade.addCustomer(customerNameY);
        storageFacade.addCargo(CargoType.UNITISED_CARGO, customerNameX,
                DEFAULT_VALUE, DEFAULT_DURATION, DEFAULT_HAZARDS, true, false);
        storageFacade.addCargo(CargoType.LIQUID_BULK_CARGO, customerNameY,
                DEFAULT_VALUE, DEFAULT_DURATION, DEFAULT_HAZARDS, false, true);
        storageFacade.addCargo(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED, customerNameX,
                DEFAULT_VALUE, DEFAULT_DURATION, DEFAULT_HAZARDS, true, false);

        System.out.println("INITIAL STORAGE:");
        System.out.printf("customers: %s%n", storageFacade.getCustomerRecords());
        System.out.printf("cargos: %s%n", storageFacade.getStorageItems());

        storageFacade.save(SerializationStrategy.create());

        StorageFacade anotherStorageFacade = new StorageFacade(StorageManagement.create());

        anotherStorageFacade.load(SerializationStrategy.create());

        System.out.println("LOADED STORAGE:");
        System.out.printf("customers: %s%n", anotherStorageFacade.getCustomerRecords());
        System.out.printf("cargos: %s%n", anotherStorageFacade.getStorageItems());
    }

//    +++++++++++++++++++++++++++++++++++++++++++++ RANDOM ACCESS ++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private void runRandomAccessExample() {
        StorageFacade storageFacade = new StorageFacade(StorageManagement.create());

        String customerNameX = "x";
        storageFacade.addCustomer(customerNameX);
        String customerNameY = "y";
        storageFacade.addCustomer(customerNameY);
        int posX = storageFacade.addCargo(CargoType.UNITISED_CARGO, customerNameX,
                DEFAULT_VALUE, DEFAULT_DURATION, DEFAULT_HAZARDS, true, false);
        int posY = storageFacade.addCargo(CargoType.LIQUID_BULK_CARGO, customerNameY,
                DEFAULT_VALUE, DEFAULT_DURATION, DEFAULT_HAZARDS, false, true);
        int posZ = storageFacade.addCargo(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED, customerNameX,
                DEFAULT_VALUE, DEFAULT_DURATION, DEFAULT_HAZARDS, true, false);

        System.out.println("INITIAL STORAGE:");
        System.out.printf("customers: %s%n", storageFacade.getCustomerRecords());
        System.out.printf("cargos: %s%n", storageFacade.getStorageItems());

        storageFacade.save(posX, RandomAccessPersistenceStrategy.create());
        storageFacade.save(posY, RandomAccessPersistenceStrategy.create());
        storageFacade.save(posZ, RandomAccessPersistenceStrategy.create());

        StorageFacade anotherStorageFacade = new StorageFacade(StorageManagement.create());
        // owner must exist to load item. Item would be ignored otherwise
        anotherStorageFacade.addCustomer(customerNameY);

        anotherStorageFacade.load(posY, RandomAccessPersistenceStrategy.create());
        anotherStorageFacade.load(posX, RandomAccessPersistenceStrategy.create()); // should not be added since customer "x" ist not registered

        System.out.println("LOADED STORAGE:");
        System.out.printf("customers: %s%n", anotherStorageFacade.getCustomerRecords());
        System.out.printf("cargos: %s%n", anotherStorageFacade.getStorageItems());
    }

    //    ++++++++++++++++++++++++++++++++++++++++++++++++ JBP +++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private void runJbpExample() {
        StorageFacade storageFacade = new StorageFacade(StorageManagement.create());

        String customerNameX = "x";
        storageFacade.addCustomer(customerNameX);
        String customerNameY = "y";
        storageFacade.addCustomer(customerNameY);
        storageFacade.addCargo(CargoType.UNITISED_CARGO, customerNameX,
                DEFAULT_VALUE, DEFAULT_DURATION, DEFAULT_HAZARDS, true, false);
        storageFacade.addCargo(CargoType.LIQUID_BULK_CARGO, customerNameY,
                DEFAULT_VALUE, DEFAULT_DURATION, DEFAULT_HAZARDS, false, true);
        storageFacade.addCargo(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED, customerNameX,
                DEFAULT_VALUE, DEFAULT_DURATION, DEFAULT_HAZARDS, true, false);

        System.out.println("INITIAL STORAGE:");
        System.out.printf("customers: %s%n", storageFacade.getCustomerRecords());
        System.out.printf("cargos: %s%n", storageFacade.getStorageItems());

        storageFacade.save(XmlEncodingStrategy.create());

        StorageFacade anotherStorageFacade = new StorageFacade(StorageManagement.create());

        anotherStorageFacade.load(XmlEncodingStrategy.create());

        System.out.println("LOADED STORAGE:");
        System.out.printf("customers: %s%n", anotherStorageFacade.getCustomerRecords());
        System.out.printf("cargos: %s%n", anotherStorageFacade.getStorageItems());
    }
}
