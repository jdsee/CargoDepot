package htw.prog3.persistence.ra;

import htw.prog3.persistence.ra.RandomAccessPersistenceStrategy;
import htw.prog3.persistence.ra.RandomAccessSerializer;
import htw.prog3.sm.core.CargoType;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.MixedCargoLiquidBulkAndUnitisedImpl;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;
import java.util.HashSet;

import static org.mockito.Mockito.*;

class RandomAccessSerializerTest {
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock()
    DataOutputStream mockOut;

    @Mock
    RandomAccessFile mockRandomAccessFile;

    @Test
    void serializeStorageItem_shouldWriteAttributesInProperOrder() throws IOException {
        RandomAccessSerializer serializer = new RandomAccessSerializer();
        Customer owner = new CustomerImpl("test");
        Cargo cargo = new MixedCargoLiquidBulkAndUnitisedImpl(owner, BigDecimal.ONE, Duration.ofDays(1), new HashSet<>(), true, false);
        Date expectedDate = new Date(1234567890L);
        StorageItem item = new StorageItem(cargo, 1, expectedDate);

        serializer.serializeStorageItem(mockOut, item);

        InOrder inOrder = inOrder(mockOut);
        inOrder.verify(mockOut).writeInt(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED.intValue());
        inOrder.verify(mockOut).writeLong(expectedDate.toInstant().toEpochMilli());
        inOrder.verify(mockOut).writeInt(owner.getName().length());
        inOrder.verify(mockOut).writeChars(owner.getName());
        inOrder.verify(mockOut).writeInt(BigDecimal.ONE.toString().length());
        inOrder.verify(mockOut).writeChars(BigDecimal.ONE.toString());
        inOrder.verify(mockOut).writeLong(Duration.ofDays(1).toDays());
        inOrder.verify(mockOut, times(4)).writeBoolean(false);
        inOrder.verify(mockOut).writeBoolean(true);
        inOrder.verify(mockOut).writeBoolean(false);
    }

    @Test
    void deserializeStorageItem_shouldReadAttributesInProperOrder() throws IOException {
        doReturn('1').when(mockRandomAccessFile).readChar();
        doReturn(1, 22, 11).when(mockRandomAccessFile).readInt();
        doReturn(true).when(mockRandomAccessFile).readBoolean();
        RandomAccessSerializer serializer = new RandomAccessSerializer();
        RandomAccessPersistenceStrategy.RecordInfo info = new RandomAccessPersistenceStrategy.RecordInfo(100L);

        serializer.deserializeStorageItem(mockRandomAccessFile, info, 1);

        InOrder inOrder = inOrder(mockRandomAccessFile);
        inOrder.verify(mockRandomAccessFile).seek(100);
        inOrder.verify(mockRandomAccessFile).readInt();
        inOrder.verify(mockRandomAccessFile).readLong();
        inOrder.verify(mockRandomAccessFile).readInt();
        inOrder.verify(mockRandomAccessFile, times(22)).readChar();
        inOrder.verify(mockRandomAccessFile).readInt();
        inOrder.verify(mockRandomAccessFile, times(11)).readChar();
        inOrder.verify(mockRandomAccessFile).readLong();
        inOrder.verify(mockRandomAccessFile, times(6)).readBoolean();
    }
}
