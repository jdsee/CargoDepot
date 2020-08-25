package htw.prog3.routing.input.update.inspect;

import java.util.EventObject;

public class InspectCargoEvent extends EventObject {
    private final int storagePosition;

    /**
     * Constructs a InspectCargoEvent.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public InspectCargoEvent(int storagePosition, Object source) {
        super(source);
        this.storagePosition = storagePosition;
    }

    public int getStoragePosition() {
        return storagePosition;
    }
}
