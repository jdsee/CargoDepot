package htw.prog3.routing;

import htw.prog3.routing.config.ViewConfigEvent;
import htw.prog3.routing.config.ViewConfigEventHandler;
import htw.prog3.routing.config.ViewConfigEventListener;

import java.util.EventListener;
import java.util.EventObject;

/**
 * This class uses unchecked typecasts! The use of this class requires great caution.
 * The corresponding listeners to the passed handlers MUST be implemented
 * by the {@code UiEventController} passed in the constructor.
 */
@SuppressWarnings("unchecked")
public class EventRoutingConfigurator {
    private final UiEventController controller;
    private ViewConfigEventHandler viewConfigEventHandler;

    public EventRoutingConfigurator(UiEventController controller) {
        this.controller = controller;
    }

    public <T extends EventHandler<? extends EventObject, U>, U extends EventListener>
    void activateEventListener(T handler) {
        if (null != handler)
            handler.addListener((U) controller);
    }

    public <T extends EventHandler<? extends EventObject, U>, U extends EventListener>
    void deactivateEventListener(T handler) {
        if (null != handler)
            handler.removeListener((U) controller);
    }

    public void fireViewConfigurationEvent(Class<? extends ViewConfigEventListener> type, boolean activate) {
        if (null != viewConfigEventHandler) {
            ViewConfigEvent event = new ViewConfigEvent(type, activate, this);
            viewConfigEventHandler.handle(event);
        }
    }

    public void setViewConfigurationEventHandler(ViewConfigEventHandler handler) {
        this.viewConfigEventHandler = handler;
    }
}