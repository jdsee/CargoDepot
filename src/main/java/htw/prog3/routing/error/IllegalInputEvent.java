package htw.prog3.routing.error;

import java.util.EventObject;

public class IllegalInputEvent extends EventObject {

    private final String message;
    private final Trigger trigger;

    public IllegalInputEvent(String failureMessage, Trigger trigger, Object source) {
        super(source);
        this.message = failureMessage;
        this.trigger = trigger;
    }

    public String getMessage() {
        return message;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public enum Trigger {
        CUSTOMERS,
        STORAGE,
        ANY
    }
}
