package htw.prog3.routing.persistence.all.save;

import htw.prog3.routing.persistence.all.PersistenceType;

import java.util.EventObject;

public class SaveAllEvent extends EventObject {
    private final PersistenceType type;

    public SaveAllEvent(PersistenceType type, Object source) {
        super(source);
        this.type = type;
    }

    public PersistenceType getType() {
        return type;
    }
}
