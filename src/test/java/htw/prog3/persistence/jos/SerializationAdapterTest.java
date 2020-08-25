package htw.prog3.persistence.jos;

import htw.prog3.persistence.jos.SerializationAdapter;
import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.CustomerAdministration;
import htw.prog3.sm.core.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

class SerializationAdapterTest {
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void create_returnsNewInstance() {
        SerializationAdapter adapter = SerializationAdapter.create();
        SerializationAdapter other = SerializationAdapter.create();

        assertThat(adapter).isNotNull().isNotSameAs(other);
    }

    @Mock
    ObjectOutput mockOut;
    @Mock(serializable = true)
    CustomerAdministration mockCustomerAdministration;

    @Test
    void serialize_serializationOfCustomerAdministrationExecutes() throws IOException {
        SerializationAdapter adapter = new SerializationAdapter();

        adapter.serialize(mockOut, mockCustomerAdministration);

        verify(mockOut).writeObject(mockCustomerAdministration);
    }

    @Mock(serializable = true)
    Storage mockStorage;

    @Test
    void serialize_serializationOfStorageExecutes() throws IOException {
        SerializationAdapter adapter = new SerializationAdapter();

        adapter.serialize(mockOut, mockStorage);

        verify(mockOut).writeObject(mockStorage);
    }

    @Mock
    ObjectInput mockIn;
    @Mock
    StorageManagement mockStorageManagement;

    @Test
    void deserialize_returnsNewStorageManagementInstance() throws IOException, ClassNotFoundException {
        SerializationAdapter adapter = new SerializationAdapter();
        doReturn(mockStorageManagement, mockStorage).when(mockIn).readObject();

        StorageManagement actual = adapter.deserialize(mockIn);

        assertThat(actual).isNotNull();
    }


    @Test
    void deserialize_throwsIllegalArgumentExceptionOnClassNotFoundException() throws IOException, ClassNotFoundException {
        SerializationAdapter adapter = SerializationAdapter.create();
        given(mockIn.readObject()).willThrow(ClassNotFoundException.class);

        assertThatThrownBy(() ->
                adapter.deserialize(mockIn)
        ).isExactlyInstanceOf(IllegalArgumentException.class)
                .hasCause(new ClassNotFoundException());
    }
}