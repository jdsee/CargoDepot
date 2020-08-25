package htw.prog3.routing.persistence.item.load;

import java.util.EventObject;

public class LoadItemEvent extends EventObject {
    private final int position;

    public LoadItemEvent(int position, Object source) {
        super(source);
        this.position = position;
    }

    public int getStoragePosition() {
        return position;
    }
}
