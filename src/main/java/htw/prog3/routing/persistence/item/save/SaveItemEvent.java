package htw.prog3.routing.persistence.item.save;

import java.util.EventObject;

public class SaveItemEvent extends EventObject {
    private final int storagePosition;

    public SaveItemEvent(int storagePosition, Object source) {
        super(source);
        this.storagePosition = storagePosition;
    }

    public int getStoragePosition() {
        return storagePosition;
    }
}
