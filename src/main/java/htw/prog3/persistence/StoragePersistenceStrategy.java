package htw.prog3.persistence;

import htw.prog3.sm.api.StorageManagement;

import java.util.Optional;

public interface StoragePersistenceStrategy {
    void save(StorageManagement source);

    Optional<StorageManagement> load();
}
