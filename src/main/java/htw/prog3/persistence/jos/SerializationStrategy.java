package htw.prog3.persistence.jos;

import htw.prog3.persistence.StoragePersistenceStrategy;
import htw.prog3.sm.api.StorageManagement;

import java.io.*;
import java.util.Optional;

public class SerializationStrategy implements StoragePersistenceStrategy {
    public static final String JOS_FILE_NAME = "./src/resources/persistence/jos_storage_snapshot.bin";

    public SerializationStrategy(SerializationAdapter adapter) {
        this.adapter = adapter;
    }

    private final SerializationAdapter adapter;

    public static SerializationStrategy create() {
        return new SerializationStrategy(new SerializationAdapter());
    }

    /**
     * Not testable: access on file system
     */
    @Override
    public void save(StorageManagement source) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(JOS_FILE_NAME))) {
            adapter.serialize(out, source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Not testable: access on file system
     */
    @Override
    public Optional<StorageManagement> load() {
        File memory = new File(JOS_FILE_NAME);
        if (memory.exists()) {
            try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(memory))) {
                return Optional.of(adapter.deserialize(input));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
