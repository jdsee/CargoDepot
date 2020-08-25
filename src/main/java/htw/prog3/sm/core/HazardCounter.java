package htw.prog3.sm.core;

import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.util.BindingUtils;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class provides methods to keep track of the present hazards in a specific
 * storage. To work properly it's necessary to inform one specific instance of this
 * class whenever an item is added to or removed from the storage.
 */
public class HazardCounter implements Serializable {
    private final MapProperty<Hazard, Integer> counters;

    public HazardCounter() {
        this(new HashMap<>());
    }

    public HazardCounter(Map<Hazard, Integer> counters) {
        this.counters = new SimpleMapProperty<>(FXCollections.observableMap(counters));
    }

    public static HazardCounter create() {
        return new HazardCounter();
    }

    /**
     * Returns a list containing all hazards acknowledged by this hazard counter.
     *
     * @return a list containing all hazards acknowledged by this hazard counter.
     */
    public ReadOnlySetProperty<Hazard> getPresentHazards() {
        return BindingUtils.createObservableKeySet(counters);
    }

    /**
     * Adds the specified hazards to this hazard counter or increments it if already present.
     * <p>
     * Nothing happens if specified hazards set is empty.
     */
    void addHazards(Set<Hazard> hazards) {
        hazards.forEach(hazard -> counters.merge(hazard, 1, Integer::sum));
    }

    /**
     * Decrements the counter for the specified hazards or removes the entry
     * if the counter reaches zero with the call of this method.
     * <p>
     * Nothing happens if specified hazards set is empty.
     */
    void removeHazards(Set<Hazard> hazards) {
        hazards.forEach(hazard -> counters.compute(hazard, (k, v) -> (v == null || v <= 1) ? null : --v));
    }

    private final static class SerializationProxy implements Serializable {
        private final int[] values;

        private SerializationProxy(HazardCounter hazardCounter) {
            Map<Hazard, Integer> counterMap = hazardCounter.counters;
            this.values = new int[]{
                    counterMap.getOrDefault(Hazard.EXPLOSIVE, 0),
                    counterMap.getOrDefault(Hazard.FLAMMABLE, 0),
                    counterMap.getOrDefault(Hazard.RADIOACTIVE, 0),
                    counterMap.getOrDefault(Hazard.TOXIC, 0)
            };
        }

        /**
         * [0] explosive
         * [1] flammable
         * [2] radioactive
         * [3] toxic
         */
        private Object readResolve() {
            Map<Hazard, Integer> readCounters = new HashMap<>();
            if (values[0] > 0) readCounters.put(Hazard.EXPLOSIVE, values[0]);
            if (values[1] > 0) readCounters.put(Hazard.FLAMMABLE, values[1]);
            if (values[2] > 0) readCounters.put(Hazard.RADIOACTIVE, values[2]);
            if (values[3] > 0) readCounters.put(Hazard.TOXIC, values[3]);
            return new HazardCounter(readCounters);
        }

        private static final long serialVersionUID = 890432784678423145L;
    }

    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * Not tested because method is supposed to be private and is not used anywhere
     * in the code.
     * It's just a security feature to prevent {@code NotSerializableException}
     */
    private Object readObject(ObjectInputStream in) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required.");
    }
}