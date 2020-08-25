package htw.prog3.routing.input.update.relocate;

import java.util.EventObject;

public class RelocateStorageItemEvent extends EventObject {
    private final int from;
    private final int to;

    public RelocateStorageItemEvent(int from, int to, Object source) {
        super(source);
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}
