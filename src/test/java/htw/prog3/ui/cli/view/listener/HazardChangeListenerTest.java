package htw.prog3.ui.cli.view.listener;

import htw.prog3.routing.config.ViewConfigEvent;
import htw.prog3.sm.api.StorageFacade;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.CustomerAdministration;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.UnitisedCargoImpl;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.ui.cli.view.listener.HazardChangeListener;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;

import static htw.prog3.storageContract.cargo.Hazard.TOXIC;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unused")
class HazardChangeListenerTest {
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    StorageFacade mockStorageFacade;
    @Mock
    SetProperty<Hazard> mockHazards;

    @SuppressWarnings("unchecked")
    @Test
    void appropriateInitialization_Test() {
        doReturn(mockHazards).when(mockStorageFacade).getHazards();

        HazardChangeListener listener = new HazardChangeListener(mockStorageFacade);

        verify(mockStorageFacade).getHazards();
        verify(mockHazards).addListener(any(SetChangeListener.class));
    }

    @Mock
    PrintStream mockOut;

    @Test
    void onViewConfigurationEvent_shouldAddListenerOnActivationViewConfigEvent() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            SetProperty<Hazard> hazards = getHazardSetBinding();
            doReturn(hazards).when(mockStorageFacade).getHazards();
            HazardChangeListener listener = new HazardChangeListener(mockStorageFacade);
            ViewConfigEvent event = new ViewConfigEvent(HazardChangeListener.class, true, this);

            listener.onViewConfigEvent(event);

            hazards.add(TOXIC);
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
            SetProperty<Hazard> hazards = getHazardSetBinding();
            doReturn(hazards).when(mockStorageFacade).getHazards();
            HazardChangeListener listener = new HazardChangeListener(mockStorageFacade);
            ViewConfigEvent event = new ViewConfigEvent(HazardChangeListener.class, false, this);

            listener.onViewConfigEvent(event);

            hazards.add(TOXIC);
            verifyNoInteractions(System.out);
        } finally {
            System.setOut(ogOut);
        }
    }

    private SetProperty<Hazard> getHazardSetBinding() {
        return new SimpleSetProperty<>(FXCollections.observableSet(new HashSet<>()));
    }

    @Test
    void update_triggersAppropriateOutputWhenHazardsChange_Test() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            SetProperty<Hazard> hazards = getHazardSetBinding();
            doReturn(hazards).when(mockStorageFacade).getHazards();
            hazards.add(Hazard.EXPLOSIVE);
            HazardChangeListener listener = new HazardChangeListener(mockStorageFacade);

            hazards.add(TOXIC);

            verify(System.out).printf("<> Hazards changed --- Actually present: %s%n", hazards.get());
        } finally {
            System.setOut(ogOut);
        }
    }

    @Mock
    Customer mockCustomer;
    @Mock
    CustomerAdministration mockCustomerAdministration;

    @Test
    void update_shouldTriggerAppropriateOutputWhenHazardWasRemovedFromStorage() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            StorageManagement management = StorageManagement.create();
            StorageFacade facade = new StorageFacade(management);
            management.addCustomer("x");
            Cargo cargo = createTestCargo("x", TOXIC, Hazard.EXPLOSIVE);
            int position = management.addCargo(cargo);
            HazardChangeListener listener = new HazardChangeListener(facade);

            management.removeCargo(position);

            verify(System.out, atLeastOnce()).printf("<> Hazards changed --- Actually present: %s%n", new HashSet<>());
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void update_shouldTriggerAppropriateOutputWhenHazardWasAddedToStorage() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            StorageManagement management = StorageManagement.create();
            StorageFacade facade = new StorageFacade(management);
            management.addCustomer("x");
            Cargo cargo = createTestCargo("x", TOXIC, Hazard.EXPLOSIVE);
            HazardChangeListener listener = new HazardChangeListener(facade);

            management.addCargo(cargo);

            verify(System.out, times(2)).printf("<> Hazards changed --- Actually present: %s%n",
                    new HashSet<>(asList(TOXIC, Hazard.EXPLOSIVE)));
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void update_shouldNotTriggerAnyOutputIfHazardsDidNotChange() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            StorageManagement management = StorageManagement.create();
            StorageFacade facade = new StorageFacade(management);
            management.addCustomer("x");
            Cargo cargo = createTestCargo("x"); // cargo without hazards
            HazardChangeListener listener = new HazardChangeListener(facade);

            management.addCargo(cargo);

            verifyNoInteractions(System.out);
        } finally {
            System.setOut(ogOut);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private Cargo createTestCargo(String customerName, Hazard... hazards) {
        return new UnitisedCargoImpl(new CustomerImpl(customerName), BigDecimal.TEN, Duration.ofDays(1),
                new HashSet<>(asList(hazards)), true);
    }
}