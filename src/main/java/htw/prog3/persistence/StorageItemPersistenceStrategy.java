package htw.prog3.persistence;

import htw.prog3.sm.core.StorageItem;

import java.util.Optional;

public interface StorageItemPersistenceStrategy {
    boolean memoryFileExists();

    void save(StorageItem item);

    Optional<StorageItem> load(int position);
}
