package htw.prog3.persistence.jos;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

class SerializationAdapter {
    public static SerializationAdapter create() {
        return new SerializationAdapter();
    }

    public <T extends Serializable> void serialize(ObjectOutput out, T o) throws IOException {
        out.writeObject(o);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(ObjectInput in) throws IOException {
        try {
            return (T) in.readObject();
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new IllegalArgumentException("Method must be called with parameters in same order as serialized", e);
        }
    }
}