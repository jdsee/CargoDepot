package htw.prog3.persistence.ra;

import htw.prog3.persistence.ra.FileAccessSupplier;
import htw.prog3.persistence.ra.RandomAccessPersistenceStrategy;
import htw.prog3.persistence.ra.RandomAccessSerializer;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.LiquidBulkCargoImpl;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RandomAccessPersistenceStrategyTest {
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    RandomAccessSerializer mockSerializer;
    @Mock
    FileAccessSupplier mockFileAccessor;
    @Mock
    ObjectInputStream mockObjIn;
    @Mock
    ObjectOutputStream mockObjOut;
    @Mock
    DataOutputStream mockDataOut;
    @Captor
    ArgumentCaptor<StorageItem> itemCaptor;
    @Mock
    StorageItem mockItem;
    @Mock
    RandomAccessFile mockRandomAccessFile;
    @Captor
    ArgumentCaptor<RandomAccessPersistenceStrategy.RecordInfo> recordInfoCaptor;
    @Spy
    ObjectInputStream spyObjIn;

    @Disabled // access on file system
    @Test
    void create_shouldCreateNewInstance() {
//        RandomAccessPersistenceStrategy strategy = RandomAccessPersistenceStrategy.create();
//        RandomAccessPersistenceStrategy other = RandomAccessPersistenceStrategy.create();
//
//        assertThat(strategy).isNotSameAs(other);
    }

    @Test
    void save_shouldCallSerializeStorageItemOnSerializer() throws IOException {
        doReturn(mockObjIn).when(mockFileAccessor).createObjectInputStream(anyString());
        doReturn(mockObjOut).when(mockFileAccessor).createObjectOutputStream(anyString(), anyBoolean());
        doReturn(mockDataOut).when(mockFileAccessor).createDataOutputStream(anyString(), anyBoolean());
        StorageItem item = createTestStorageItem();
        RandomAccessPersistenceStrategy strategy =
                new RandomAccessPersistenceStrategy(mockFileAccessor, mockSerializer, new HashMap<>());

        strategy.save(item);

        verify(mockSerializer).serializeStorageItem(any(DataOutputStream.class), itemCaptor.capture());
        assertThat(itemCaptor.getValue()).isEqualTo(item);
    }

    @Test
    void save_shouldSkipSerializationOnIOException() throws IOException {
        doReturn(mockObjIn).when(mockFileAccessor).createObjectInputStream(anyString());
        RandomAccessPersistenceStrategy strategy = new RandomAccessPersistenceStrategy(mockFileAccessor, mockSerializer, new HashMap<>());
        doThrow(FileNotFoundException.class).when(mockFileAccessor).createDataOutputStream(anyString(), anyBoolean());

        strategy.save(mockItem);

        verifyNoInteractions(mockSerializer);
    }

    @Test
    void load_shouldCallDeserializeStorageItemOnSerializer() throws IOException {
        doReturn(mockObjIn).when(mockFileAccessor).createObjectInputStream(anyString());
        doReturn(mockObjOut).when(mockFileAccessor).createObjectOutputStream(anyString(), anyBoolean());
        doReturn(mockRandomAccessFile).when(mockFileAccessor).createRandomAccessFile(anyString(), anyString());
        doReturn(mockItem).when(mockSerializer).deserializeStorageItem(any(), any(), anyInt());
        Map<Integer, RandomAccessPersistenceStrategy.RecordInfo> indexMapper = new HashMap<>();
        indexMapper.put(66, new RandomAccessPersistenceStrategy.RecordInfo(100L));
        RandomAccessPersistenceStrategy strategy =
                new RandomAccessPersistenceStrategy(mockFileAccessor, mockSerializer, indexMapper);

        strategy.load(66);

        verify(mockSerializer).deserializeStorageItem(
                any(RandomAccessFile.class), recordInfoCaptor.capture(), anyInt());
        assertThat(recordInfoCaptor.getValue())
                .extracting(info -> info.filePointer).containsExactly(100L);
    }

    @Test
    void load_shouldCatchIoExceptionAndReturnEmptyOptional() throws IOException {
        Map<Integer, RandomAccessPersistenceStrategy.RecordInfo> indexMapper = new HashMap<>();
        indexMapper.put(66, new RandomAccessPersistenceStrategy.RecordInfo(100L));
        doReturn(mockObjIn).when(mockFileAccessor).createObjectInputStream(anyString());
        doReturn(mockRandomAccessFile).when(mockFileAccessor).createRandomAccessFile(anyString(), anyString());
        doThrow(IOException.class).when(mockSerializer).deserializeStorageItem(any(), any(), anyInt());
        RandomAccessPersistenceStrategy strategy =
                new RandomAccessPersistenceStrategy(mockFileAccessor, mockSerializer, indexMapper);

        Optional<StorageItem> actual = strategy.load(66);

        assertThat(actual).isEmpty();
    }

    @Test
    void constructor_shouldCatchIoExceptionAndCreateNewInstance() throws IOException {
        doThrow(FileNotFoundException.class).when(mockFileAccessor).createObjectInputStream(anyString());

        RandomAccessPersistenceStrategy strategy = new RandomAccessPersistenceStrategy(mockFileAccessor, mockSerializer, new HashMap<>());

        assertThat(strategy).isNotNull();
    }

    @Test
    void constructor_shouldThrowAssertionErrorOnClassNotFoundException() throws IOException, ClassNotFoundException {
        doReturn(spyObjIn).when(mockFileAccessor).createObjectInputStream(anyString());
        doThrow(ClassNotFoundException.class).when(spyObjIn).readObject();

        Throwable t = catchThrowable(() -> new RandomAccessPersistenceStrategy(mockFileAccessor, mockSerializer, new HashMap<>()));

        assertThat(t).isInstanceOf(AssertionError.class)
                .hasCauseExactlyInstanceOf(ClassNotFoundException.class)
                .hasMessage("Index mapper file contains malicious data.");
    }

    @Test
    void memoryFileExists_returnsResultOfFileAccessor() {
        doReturn(true).when(mockFileAccessor).isExistentFile(anyString());
        RandomAccessPersistenceStrategy strategy = new RandomAccessPersistenceStrategy(mockFileAccessor, mockSerializer, new HashMap<>());

        boolean actual = strategy.memoryFileExists();

        assertThat(actual).isTrue();
    }

    private StorageItem createTestStorageItem() {
        Customer owner = new CustomerImpl("x");
        Cargo cargo = new LiquidBulkCargoImpl(owner, BigDecimal.TEN, Duration.ofDays(1), new HashSet<>(), true);
        return new StorageItem(cargo, 1);
    }
}