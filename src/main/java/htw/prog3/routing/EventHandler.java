package htw.prog3.routing;

import java.util.EventListener;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

/**
 * Generic class for EventHandlers.
 *
 * @param <T> The supported type of event.
 * @param <U> The supported type of event listener.
 */
public abstract class EventHandler<T extends EventObject, U extends EventListener> {
    private final List<U> listeners = new LinkedList<>();

    /**
     * Registers the specified listener, so that it will be informed about incoming events.
     *
     * @param listener The listener that is to be added.
     */
    public void addListener(U listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters the specified listener, so that it won't be informed about incoming events anymore.
     *
     * @param listener The listener that is to be removed.
     */
    public void removeListener(U listener) {
        listeners.remove(listener);
    }

    protected List<U> getListeners() {
        return listeners;
    }

    /**
     * Informs all the registered listeners about the specified Event.
     *
     * @param event The event that is to be handled.
     */
    public abstract void handle(T event);
}
