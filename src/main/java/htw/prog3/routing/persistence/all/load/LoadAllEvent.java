package htw.prog3.routing.persistence.all.load;

import htw.prog3.routing.persistence.all.PersistenceType;

import java.util.EventObject;

public class LoadAllEvent extends EventObject {
    private final PersistenceType type;

    public LoadAllEvent(PersistenceType type, Object source) {
        super(source);
        this.type = type;
    }

    public PersistenceType getType() {
        return type;
    }
}
