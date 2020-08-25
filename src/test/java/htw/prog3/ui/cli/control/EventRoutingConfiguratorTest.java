package htw.prog3.ui.cli.control;

import htw.prog3.routing.EventHandler;
import htw.prog3.routing.EventRoutingConfigurator;
import htw.prog3.routing.UiEventController;
import htw.prog3.routing.config.ViewConfigEvent;
import htw.prog3.routing.config.ViewConfigEventHandler;
import htw.prog3.routing.config.ViewConfigEventListener;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.CargoType;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.ui.cli.view.listener.HazardChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventRoutingConfiguratorTest {
    @Mock
    UiEventController mockController;
    @Mock
    EventHandler<EventObject, EventListener> mockHandler;
    @Mock
    ViewConfigEventHandler mockViewConfigHandler;
    @Captor
    ArgumentCaptor<ViewConfigEvent> viewConfigEventCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void activateEventListener_shouldAddListener() {
        EventRoutingConfigurator config = new EventRoutingConfigurator(mockController);

        config.activateEventListener(mockHandler);

        verify(mockHandler).addListener(mockController);
    }


    @Test
    void activateEventListener_shouldDoNothingIfPassedHandlerIsNull() {
        EventRoutingConfigurator config = new EventRoutingConfigurator(mockController);
        config.activateEventListener(mockHandler);

        config.activateEventListener(null);

        verify(mockHandler).addListener(mockController);
        verifyNoMoreInteractions(mockHandler);
    }

    @Test
    void deactivateEventListener_shouldRemoveListener() {
        EventRoutingConfigurator config = new EventRoutingConfigurator(mockController);

        config.deactivateEventListener(mockHandler);

        verify(mockHandler).removeListener(mockController);
        verifyNoMoreInteractions(mockHandler);
    }

    @Test
    void fireViewConfigurationEvent_shouldFireEventWhenHandlerSet() {
        EventRoutingConfigurator config = new EventRoutingConfigurator(mockController);
        config.setViewConfigurationEventHandler(mockViewConfigHandler);

        config.fireViewConfigurationEvent(HazardChangeListener.class, true);

        verify(mockViewConfigHandler).handle(viewConfigEventCaptor.capture());
        assertThat(viewConfigEventCaptor.getValue())
                .extracting(
                        ViewConfigEvent::getType,
                        ViewConfigEvent::isActivation)
                .containsExactly(
                        HazardChangeListener.class,
                        true
                );
    }

    @Test
    void setViewConfigurationEventHandler_shouldSetHandler() {
        EventRoutingConfigurator config = new EventRoutingConfigurator(mockController);

        config.setViewConfigurationEventHandler(mockViewConfigHandler);

        config.fireViewConfigurationEvent(ViewConfigEventListener.class, true);
        verify(mockViewConfigHandler).handle(any(ViewConfigEvent.class));
    }

    // ------------------

    private void addCargoWithHazardsToManagement(StorageManagement management) {
        Set<Hazard> hazards = new HashSet<>(singletonList(Hazard.TOXIC));
        management.addCargo(CargoType.UNITISED_CARGO, "x", BigDecimal.TEN, Duration.ofDays(1),
                hazards, true, true);
    }
}