package htw.prog3.routing.success;


import java.util.EventObject;

public class ActionSuccessEvent extends EventObject {

    private final String message;

    public ActionSuccessEvent(String message, Object source) {
        super(source);
        this.message = message;
    }

    /**
     * Returns the attached success message to this event.
     *
     * @return The success message stored in this event.
     */
    public String getMessage() {
        return message;
    }
}
