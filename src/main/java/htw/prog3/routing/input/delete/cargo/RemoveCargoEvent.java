package htw.prog3.routing.input.delete.cargo;

import java.util.EventObject;

public class RemoveCargoEvent extends EventObject {
    private final int storagePosition;

    /**
     * Constructs a DeleteCargoEvent.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public RemoveCargoEvent(int storagePosition, Object source) {
        super(source);
        this.storagePosition = storagePosition;
    }

    public int getStoragePosition() {
        return storagePosition;
    }
}
