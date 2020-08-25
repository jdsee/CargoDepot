package htw.prog3.ui.cli.view;

import htw.prog3.routing.error.IllegalInputEvent;
import htw.prog3.routing.success.ActionSuccessEvent;
import htw.prog3.routing.view.listResponse.cargos.ListCargosResEvent;
import htw.prog3.routing.view.listResponse.customers.ListCustomersResEvent;
import htw.prog3.routing.view.listResponse.hazards.ListHazardsResEvent;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.CustomerRecord;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.sm.core.UnitisedCargoImpl;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.ui.cli.view.CommandLineWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.mockito.Mockito.*;


@SuppressWarnings("ResultOfMethodCallIgnored")
class CommandLineWriterTest {
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    ActionSuccessEvent mockSuccessEvent;

    @Test
    void onActionSuccessEvent_valueIsAccessed_Test() {
        CommandLineWriter writer = new CommandLineWriter();
        String expectedMessage = "msg";
        doReturn(expectedMessage).when(mockSuccessEvent).getMessage();

        writer.onActionSuccessEvent(mockSuccessEvent);

        verify(mockSuccessEvent, atLeastOnce()).getMessage();
        verifyNoMoreInteractions(mockSuccessEvent);
    }

    @Mock
    PrintStream mockOut;

    @Test
    void onActionSuccessEvent_noReactionOnNullValue_Test() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            CommandLineWriter writer = new CommandLineWriter();

            writer.onActionSuccessEvent(null);

            verifyNoInteractions(System.out);
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void onActionSuccessEvent_printsAppropriateOutput_Test() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            CommandLineWriter writer = new CommandLineWriter();
            String message = "msg";
            doReturn(message).when(mockSuccessEvent).getMessage();

            writer.onActionSuccessEvent(mockSuccessEvent);

            verify(System.out, atLeastOnce()).printf(">>> %s%n", message);
        } finally {
            System.setOut(ogOut);
        }
    }

    @Mock
    IllegalInputEvent mockIllegalInputEvent;

    @Test
    void onActionSuccessEvent_printsAppropriateOutputWhenMessageEmpty_Test() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            CommandLineWriter writer = new CommandLineWriter();
            doReturn("").when(mockSuccessEvent).getMessage();

            writer.onActionSuccessEvent(mockSuccessEvent);

            verify(System.out).println(">>> OPERATION SUCCEEDED");
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void onIllegalInputEvent_shouldPrintMessageOfEvent() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            CommandLineWriter writer = new CommandLineWriter();
            doReturn("xyz").when(mockIllegalInputEvent).getMessage();

            writer.onIllegalInputEvent(mockIllegalInputEvent);

            verify(System.out).println("<!> AN ERROR OCCURRED");
            verify(System.out).printf("<!> %s%n", mockIllegalInputEvent.getMessage());
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void onListCargosResEvent_shouldListAllCargos() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            CommandLineWriter writer = new CommandLineWriter();
            StorageItem item1 = createTestItem(0);
            StorageItem item2 = createTestItem(1);
            ListCargosResEvent event = new ListCargosResEvent(asList(item1, item2), this);

            writer.onListCargosResEvent(event);

            verify(System.out).println(item1);
            verify(System.out).println(item2);
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void onListCargosResEvent_shouldPrintMessageIfStorageIsEmpty() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            CommandLineWriter writer = new CommandLineWriter();
            ListCargosResEvent event = new ListCargosResEvent(emptyList(), this);

            writer.onListCargosResEvent(event);

            verify(System.out).println(">>> The storage seems to be empty at the moment.");
        } finally {
            System.setOut(ogOut);
        }
    }

    private StorageItem createTestItem(int position) {
        Cargo cargo = new UnitisedCargoImpl(new CustomerImpl("x"), BigDecimal.TEN,
                Duration.ofDays(1), new HashSet<>(), true);
        return new StorageItem(cargo, position);
    }

    @Test
    void onListCustomerResEvent_shouldListAllCustomers() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            CommandLineWriter writer = new CommandLineWriter();
            CustomerRecord record1 = new CustomerRecord(new CustomerImpl("x"));
            CustomerRecord record2 = new CustomerRecord(new CustomerImpl("y"));
            ListCustomersResEvent event = new ListCustomersResEvent(asList(record1, record2), this);

            writer.onListCustomerResEvent(event);

            verify(System.out).println(record1);
            verify(System.out).println(record2);

        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void onListCustomerResEvent_shouldPrintMessageIfCustomersEmpty() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            CommandLineWriter writer = new CommandLineWriter();
            ListCustomersResEvent event = new ListCustomersResEvent(emptyList(), this);

            writer.onListCustomerResEvent(event);

            verify(System.out).println(">>> The customer administration seems to be empty at the moment.");

        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void onListHazardsResEvent_shouldListAllHazards() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            CommandLineWriter writer = new CommandLineWriter();
            Set<Hazard> hazards = new HashSet<>(asList(Hazard.TOXIC, Hazard.RADIOACTIVE));
            ListHazardsResEvent event = new ListHazardsResEvent(hazards, this);

            writer.onListHazardsResEvent(event);

            verify(System.out).println(hazards);
        } finally {
            System.setOut(ogOut);
        }
    }

    @Test
    void onListHazardsResEvent_shouldPrintMessageIfNoHazardsStored() {
        PrintStream ogOut = System.out;
        try {
            System.setOut(mockOut);
            CommandLineWriter writer = new CommandLineWriter();
            ListHazardsResEvent event = new ListHazardsResEvent(emptySet(), this);

            writer.onListHazardsResEvent(event);

            verify(System.out).println(">>> There are no hazards in the storage at the moment.");
        } finally {
            System.setOut(ogOut);
        }
    }
}