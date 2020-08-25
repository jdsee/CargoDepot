package htw.prog3.persistence.ra;

import htw.prog3.persistence.StorageItemPersistenceStrategy;
import htw.prog3.sm.core.StorageItem;
import org.apache.commons.lang3.Validate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RandomAccessPersistenceStrategy implements StorageItemPersistenceStrategy {
    public static final String RA_STORAGE_ITEMS_PATH = "./src/resources/persistence/ra_storage_items.bin";
    public static final String RA_INDEX_MAPPER_PATH = "./src/resources/persistence/ra_index_mapper.bin";
    private final FileAccessSupplier fileAccessor;
    private final RandomAccessSerializer serializer;
    private Map<Integer, RecordInfo> indexMapper;

    RandomAccessPersistenceStrategy(FileAccessSupplier fileAccessor,
                                    RandomAccessSerializer serializer, Map<Integer, RecordInfo> indexMapper) {
        this.fileAccessor = fileAccessor;
        this.serializer = serializer;
        this.indexMapper = indexMapper;
        updateIndexMapper();
    }

    // not testable: access on file system
    public static RandomAccessPersistenceStrategy create() {
        return new RandomAccessPersistenceStrategy(new FileAccessSupplier(),
                new RandomAccessSerializer(), new HashMap<>());
    }

    @Override
    public boolean memoryFileExists() {
        return fileAccessor.isExistentFile(RA_STORAGE_ITEMS_PATH);
    }

    @Override
    public void save(StorageItem item) {
        Validate.notNull(item);
        long itemPointer = fileAccessor.getActualFileSize(RA_STORAGE_ITEMS_PATH);
        try (DataOutputStream dataOut = fileAccessor.createDataOutputStream(RA_STORAGE_ITEMS_PATH, true)) {
            handleItemSerialization(item, itemPointer, dataOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleItemSerialization(StorageItem item, long itemPointer, DataOutputStream out) throws IOException {
        serializer.serializeStorageItem(out, item);
        RecordInfo info = RecordInfo.from(itemPointer);
        addToIndexMapper(item, info);
    }

    @Override
    public Optional<StorageItem> load(int position) {
        try (RandomAccessFile raf = fileAccessor.createRandomAccessFile(RA_STORAGE_ITEMS_PATH, "r")) {
            if (raf != null) {
                Optional<StorageItem> optItem = handleItemDeserialization(raf, position);
                saveIndexMapper();
                return optItem;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Optional<StorageItem> handleItemDeserialization(RandomAccessFile file, int position) throws IOException {
        updateIndexMapper();
        RecordInfo info = indexMapper.remove(position);
        return (null != info) ? Optional.of(serializer.deserializeStorageItem(file, info, position)) : Optional.empty();
    }

    private void addToIndexMapper(StorageItem item, RecordInfo info) throws IOException {
        int storagePosition = item.getStoragePosition();
        indexMapper.put(storagePosition, info);
        saveIndexMapper();
    }

    private void saveIndexMapper() throws IOException {
        try (ObjectOutputStream objOut = fileAccessor.createObjectOutputStream(RA_INDEX_MAPPER_PATH, false)) {
            objOut.writeObject(indexMapper);
        }
    }

    @SuppressWarnings("unchecked")
    private void updateIndexMapper() {
        try {
            ObjectInputStream input = fileAccessor.createObjectInputStream(RA_INDEX_MAPPER_PATH);
            if (input != null) {
                Map<Integer, RecordInfo> load = (Map<Integer, RecordInfo>) input.readObject();
                if (null != load) this.indexMapper = load;
            }
        } catch (ClassNotFoundException e) {
            // hard to test -> see disabled test in test suite
            throw new AssertionError("Index mapper file contains malicious data.", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static final class RecordInfo implements Serializable {
        final long filePointer;

        RecordInfo(long filePointer) {
            this.filePointer = filePointer;
        }

        static RecordInfo from(long itemPointer) {
            return new RecordInfo(itemPointer);
        }
    }
}
