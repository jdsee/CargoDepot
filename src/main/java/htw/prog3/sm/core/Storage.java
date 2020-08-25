package htw.prog3.sm.core;

import htw.prog3.sm.util.ValidationHelper;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.apache.commons.lang3.Validate;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Storage implements Serializable {
    public static final int DEFAULT_CAPACITY = 10_000;

    private final HazardCounter hazardCounter;
    private final MapProperty<Integer, StorageItem> storageItems;
    private final IntegerProperty capacity;
    private final StoragePositionProvider positionProvider;

    public Storage(int capacity, Set<Integer> revokedPositions) {
        this(capacity, FXCollections.observableHashMap(), new HazardCounter(), revokedPositions);
    }

    private Storage(int capacity, ObservableMap<Integer, StorageItem> storageItems,
                    HazardCounter hazardCounter, Set<Integer> revokedPositions) {
        Validate.inclusiveBetween(0, Integer.MAX_VALUE, capacity);
        ValidationHelper.requireNonNullConstructorArgs(this.getClass(), storageItems, hazardCounter);

        this.storageItems = new SimpleMapProperty<>(storageItems);
        this.capacity = new SimpleIntegerProperty(capacity);
        this.hazardCounter = hazardCounter;
        this.positionProvider = new StoragePositionProvider(revokedPositions);
    }

    public static Storage ofCapacity(int capacity) {
        return new Storage(capacity, FXCollections.observableHashMap(), new HazardCounter(), new HashSet<>());
    }

    public boolean hasFreeCapacity() {
        return getItemCount() < getCapacity();
    }

    /**
     * Returns a Map with all cargos that apply to the specified CargoType.
     *
     * @return A Map with all cargos that apply to the specified CargoType.
     */
    public Map<Integer, StorageItem> getStorageItems(CargoType type) {
        return new HashMap<>(storageItems.values().stream()
                .filter(cargo -> type.equals(cargo.getCargo().getCargoType()))
                .collect(Collectors.toMap(StorageItem::getStoragePosition, Function.identity())));
    }

    public ReadOnlyMapProperty<Integer, StorageItem> getStorageItems() {
        return storageItems;
    }

    /**
     * Stores the specified cargo in the storage.
     * The owner of the cargo must already exist in the control system when the cargo is stored.
     *
     * @param cargo The cargo that is to be stored.
     * @return the storage position to find the stored cargo.
     * @throws IllegalArgumentException if the specified customer is not known.
     * @throws IllegalStateException    if this storage capacity exceeded.
     */
    public StorageItem addCargo(Cargo cargo) {
        Validate.notNull(cargo);
        checkMaxCapacityExcess();

        int nextPosition = positionProvider.acquire();
        StorageItem newItem = new StorageItem(cargo, nextPosition);
        addItem(newItem);

        return newItem;
    }


    public Cargo getCargo(int storagePosition) {
        return storageItems.get(storagePosition).getCargo();
    }

    /**
     * This method will likely cause an inconsistent state in the storage.
     * It should be used only to present the random access persistence.
     * <p>
     * If there is no more capacity left the specified item will be ignored.
     * If there is already an item stored at the same position as the specified item it will be overridden.
     *
     * @return The item that was overridden or null if the position was unoccupied
     */
    public Optional<StorageItem> addItem(StorageItem item) {
        Validate.notNull(item);
        StorageItem ancestor = null;
        int position = item.getStoragePosition();
        if (isStoragePosition(position))
            ancestor = removeCargo(position);
        if (hasFreeCapacity()) {
            positionProvider.preserve(position);
            storageItems.put(position, item);
            addPotentialHazards(item.getCargo());
        }
        return (null != ancestor) ? Optional.of(ancestor) : Optional.empty();
    }

    /**
     * Removes the cargo at the specified position if it exists.
     *
     * @param storagePosition The storage position of the cargo that is to be deleted.
     * @return The cargo that has been deleted.
     * @throws IndexOutOfBoundsException If there is no cargo stored at the specified storage position.
     */
    public StorageItem removeCargo(int storagePosition) {
        validateIsStoragePosition(storagePosition);
        StorageItem item = storageItems.remove(storagePosition);
        removePotentialHazards(item.getCargo());
        positionProvider.release(item.getStoragePosition());

        return item;
    }

    private void addPotentialHazards(Cargo cargo) {
        hazardCounter.addHazards(cargo.getHazards());
    }

    private void checkMaxCapacityExcess() {
        if (!hasFreeCapacity())
            throw new IllegalStateException(FailureMessages.STORAGE_CAPACITY_EXCESS);
    }

    private void removePotentialHazards(Cargo cargo) {
        hazardCounter.removeHazards(cargo.getHazards());
    }

    /**
     * Updates the inspection date for the cargo at the specified storage position.
     *
     * @param storagePosition The position to find the targeted cargo.
     * @throws IndexOutOfBoundsException If the there is no cargo stored at the specified storage position.
     */
    void inspectCargo(int storagePosition) {
        validateIsStoragePosition(storagePosition);
        Cargo cargo = storageItems.get(storagePosition).getCargo();
        cargo.inspect();
    }

    /**
     * Returns a list containing all existent hazards in this storage.
     *
     * @return A list containing all existent hazards in this storage.
     */
    public ReadOnlySetProperty<Hazard> getHazards() {
        return hazardCounter.getPresentHazards();
    }

    public int getItemCount() {
        return storageItems.size();
    }

    public ReadOnlyIntegerProperty itemCountProperty() {
        return storageItems.sizeProperty();
    }

    public int getCapacity() {
        return capacity.get();
    }

    public IntegerProperty capacityProperty() {
        return capacity;
    }

    public boolean isStoragePosition(int position) {
        return storageItems.containsKey(position);
    }

    private void validateIsStoragePosition(int storagePosition) {
        if (!storageItems.containsKey(storagePosition)) {
            throw new IndexOutOfBoundsException(FailureMessages.UNALLOCATED_STORAGE_POSITION);
        }
    }

    StorageItem getStorageItem(int position) {
        return storageItems.get(position);
    }

    private static final class SerializationProxy implements Serializable {
        private final StorageItem[] storageItemArray;
        private final int capacity;
        private final HazardCounter hazardCounter;
        private final Set<Integer> revokedPositions;

        private SerializationProxy(Storage storage) {
            this.storageItemArray = storage.getStorageItems().values().toArray(new StorageItem[0]);
            this.capacity = storage.getCapacity();
            this.hazardCounter = storage.hazardCounter;
            this.revokedPositions = storage.positionProvider.revokedPositions;
        }

        private Object readResolve() {
            ObservableMap<Integer, StorageItem> storageItems = FXCollections.observableHashMap();
            for (StorageItem item : storageItemArray)
                storageItems.put(item.getStoragePosition(), item);
            return new Storage(capacity, storageItems, hazardCounter, revokedPositions);
        }

        private static final long serialVersionUID = 1263478094678423145L;
    }

    private final class StoragePositionProvider {
        private final Set<Integer> revokedPositions;

        private StoragePositionProvider(Set<Integer> revokedPositions) {
            this.revokedPositions = revokedPositions;
        }

        private int acquire() {
            return revokedPositions.stream().findAny().orElseGet(storageItems::size);
        }

        private void release(int position) {
            revokedPositions.add(position);
        }

        private void preserve(int position) {
            if (position > getItemCount())
                revokedPositions.addAll(
                        IntStream.range(getMaxPosition() + 1, position).boxed().collect(Collectors.toSet()));
            revokedPositions.remove(position);
        }

        private int getMaxPosition() {
            return getStorageItems().keySet().stream().max(Comparator.naturalOrder()).orElse(0);
        }
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
