package htw.prog3.log;

import htw.prog3.log.ProcessLogDictionary;
import htw.prog3.log.ProcessLogger;
import htw.prog3.routing.view.update.cargos.UpdateCargoViewEvent;
import htw.prog3.sm.api.StorageFacade;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.CustomerRecord;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.sm.core.UnitisedCargoImpl;
import htw.prog3.storageContract.cargo.Cargo;
import javafx.beans.property.MapProperty;
import javafx.collections.MapChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProcessLoggerTest {
    @Mock
    PrintWriter mockOut;
    @Mock
    ProcessLogDictionary mockDictionary;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    StorageFacade mockFacade;
    @Mock
    MapProperty<Integer, StorageItem> mockItems;
    @Mock
    MapProperty<String, CustomerRecord> mockRecords;
    @Mock
    UpdateCargoViewEvent mockUpdateCargoViewEvent;

    @Test
    void constructor_shouldReturnNonNullInstance() {
        StorageManagement management = StorageManagement.create();
        StorageFacade facade = new StorageFacade(management);

        ProcessLogger logger = new ProcessLogger(facade, mockOut, mockDictionary);

        assertThat(logger).isNotNull();
    }

    @Test
    void close() {
        StorageManagement management = StorageManagement.create();
        StorageFacade facade = new StorageFacade(management);
        ProcessLogger logger = new ProcessLogger(facade, mockOut, mockDictionary);

        logger.close();

        verify(mockOut).close();
    }

    @Test
    void shouldLogWhenItemWasRemoved() {
        StorageManagement management = StorageManagement.create();
        StorageFacade facade = new StorageFacade(management);
        management.addCustomer("x");
        int pos = management.addCargo(createTestCargo("x"));
        doReturn("remove cargo").when(mockDictionary).itemRemovedMsg(any());

        ProcessLogger logger = new ProcessLogger(facade, mockOut, mockDictionary);

        management.removeCargo(pos);
        verify(mockOut).println("remove cargo");
        logger.close();
    }

    @Test
    void shouldLogWhenItemWasAdded() {
        StorageManagement management = StorageManagement.create();
        StorageFacade facade = new StorageFacade(management);
        management.addCustomer("x");
        doReturn("add cargo").when(mockDictionary).itemAddedMsg(any());

        ProcessLogger logger = new ProcessLogger(facade, mockOut, mockDictionary);

        management.addCargo(createTestCargo("x"));
        verify(mockOut).println("add cargo");
        logger.close();
    }

    @Test
    void shouldLogWhenCustomerWasRemoved() {
        StorageManagement management = StorageManagement.create();
        StorageFacade facade = new StorageFacade(management);
        management.addCustomer("x");
        doReturn("remove customer").when(mockDictionary).customerRemovedMsg(any());

        ProcessLogger logger = new ProcessLogger(facade, mockOut, mockDictionary);

        management.removeCustomer("x");
        verify(mockOut).println("remove customer");
        logger.close();
    }

    @Test
    void shouldLogWhenCustomerWasAdded() {
        StorageManagement management = StorageManagement.create();
        StorageFacade facade = new StorageFacade(management);
        doReturn("add customer").when(mockDictionary).customerAddedMsg(any());

        ProcessLogger logger = new ProcessLogger(facade, mockOut, mockDictionary);

        management.addCustomer("x");
        verify(mockOut).println("add customer");
        logger.close();
    }

    @Test
    void onUpdateCargoViewEvent_shouldInitListenerAgain() {
        doReturn(mockItems).when(mockFacade).getStorageItems();
        doReturn(mockRecords).when(mockFacade).getCustomerRecords();
        ProcessLogger logger = new ProcessLogger(mockFacade, mockOut, mockDictionary);

        logger.onUpdateCargoViewEvent(mockUpdateCargoViewEvent);

        verify(mockItems, times(2)).addListener(ArgumentMatchers.<MapChangeListener<Integer, StorageItem>>any());
        verify(mockRecords, times(2)).addListener(ArgumentMatchers.<MapChangeListener<String, CustomerRecord>>any());
    }

    @SuppressWarnings("SameParameterValue")
    private Cargo createTestCargo(String owner) {
        return new UnitisedCargoImpl(new CustomerImpl(owner), BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true);
    }
}