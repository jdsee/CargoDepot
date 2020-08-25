package htw.prog3.ui.cli.view.listener;

import htw.prog3.routing.config.ViewConfigEvent;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.UnitisedCargoImpl;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.ui.cli.view.listener.CriticalCapacityListener;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CriticalCapacityListenerTest {
    @Mock
    StorageManagement mockStorageManagement;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    IntegerProperty mockCountProperty;

    @Test
    void appropriateInitialization_Test() {
        doReturn(mockCountProperty).when(mockStorageManagement).itemCountProperty();

        new CriticalCapacityListener(mockStorageManagement);

        verify(mockStorageManagement).getCapacity();
        verify(mockStorageManagement).itemCountProperty();
        verify(mockCountProperty).addListener(ArgumentMatchers.<InvalidationListener>any());
    }

    @Mock
    PrintStream mockOut;

    @Test
    void update_triggersOutputWhenOnlyOneSpaceLeft_Test() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            doReturn(10).when(mockStorageManagement).getCapacity();
            IntegerProperty itemCount = new SimpleIntegerProperty(8);
            doReturn(itemCount).when(mockStorageManagement).itemCountProperty();
            CriticalCapacityListener listener = new CriticalCapacityListener(mockStorageManagement);

            itemCount.set(9);

            verify(System.out).printf("<> Storage capacity critical --- " +
                    "%s space left. The actual maximum is: %d%n", "Only one", 10);
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void update_triggersAppropriateOutputWhenOnlyOneSpaceLeft_Test01() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            StorageManagement management = StorageManagement.ofCapacity(3);
            management.addCustomer("x");
            CriticalCapacityListener listener = new CriticalCapacityListener(management);
            management.addCargo(createTestCargo("x"));

            management.addCargo(createTestCargo("x"));

            verify(System.out, times(1)).printf("<> Storage capacity critical --- " +
                    "%s space left. The actual maximum is: %d%n", "Only one", 3);
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void update_triggersAppropriateOutputWhenNoMoreSpaceLeft_Test() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            StorageManagement management = StorageManagement.ofCapacity(3);
            management.addCustomer("x");
            CriticalCapacityListener listener = new CriticalCapacityListener(management);
            management.addCargo(createTestCargo("x"));
            management.addCargo(createTestCargo("x"));

            management.addCargo(createTestCargo("x"));

            verify(System.out, times(1)).printf("<> Storage capacity critical --- " +
                    "%s space left. The actual maximum is: %d%n", "No more", 3);
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void update_triggersNoOutputIfEnoughCapacityFree_Test() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            StorageManagement management = StorageManagement.ofCapacity(100);
            management.addCustomer("x");
            CriticalCapacityListener listener = new CriticalCapacityListener(management);
            management.addCargo(createTestCargo("x"));
            management.addCargo(createTestCargo("x"));

            management.addCargo(createTestCargo("x"));

            verifyNoInteractions(System.out);
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void onViewConfigurationEvent_shouldAddListenerOnActivationViewConfigEvent() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            StorageManagement storageManagement = StorageManagement.ofCapacity(2);
            storageManagement.addCustomer("dummy");
            CriticalCapacityListener listener = new CriticalCapacityListener(storageManagement);
            ViewConfigEvent event = new ViewConfigEvent(CriticalCapacityListener.class, true, this);

            listener.onViewConfigEvent(event);

            storageManagement.addCargo(createTestCargo("dummy"));
            verify(System.out, times(2)).printf(any(), any());
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void onViewConfigurationEvent_shouldRemoveListenerOnDeactivationViewConfigEvent() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            StorageManagement storageManagement = StorageManagement.ofCapacity(2);
            storageManagement.addCustomer("dummy");
            CriticalCapacityListener listener = new CriticalCapacityListener(storageManagement);
            ViewConfigEvent event = new ViewConfigEvent(CriticalCapacityListener.class, false, this);

            listener.onViewConfigEvent(event);

            storageManagement.addCargo(createTestCargo("dummy"));
            verifyNoInteractions(mockOut);
        } finally {
            System.setOut(ogOut);
        }
    }

    private Cargo createTestCargo(String customerName) {
        return new UnitisedCargoImpl(new CustomerImpl(customerName), BigDecimal.TEN, Duration.ofDays(1),
                new HashSet<>(), true);
    }
}