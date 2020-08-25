package htw.prog3.persistence.jbp;

import htw.prog3.persistence.jbp.XmlEncodingHandler;
import htw.prog3.persistence.jbp.XmlEncodingHelper;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.CustomerRecord;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.sm.core.StorageManagementImpl;
import javafx.beans.property.MapProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.beans.IntrospectionException;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

class XmlEncodingHandlerTest {
    @Mock
    XmlEncodingHelper mockEncodingHelper;
    @Mock
    MapProperty<String, CustomerRecord> mockRecords;
    @Mock
    MapProperty<Integer, StorageItem> mockItems;

    @Mock
    StorageManagementImpl mockStorageManagement;
    @Mock
    XMLEncoder mockEncoder;
    @Captor
    ArgumentCaptor<Object> objectCaptor;
    @Mock
    XMLDecoder mockDecoder;
    @Mock(answer = Answers.RETURNS_MOCKS)
    StorageItem mockItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void constructor_returnsNewNonNullInstance() {
        XmlEncodingHandler adapter = new XmlEncodingHandler(mockEncodingHelper);
        XmlEncodingHandler other = new XmlEncodingHandler(mockEncodingHelper);

        assertThat(adapter).isNotNull().isNotSameAs(other);
    }

    /**
     * no mockito inOrder test because i can not find a solution for an argument
     * matcher that checks the generic type of a class
     */
    @Test
    void saveFrom_readsStorageItemsAndCustomerRecordsInProperOrder() {
        XmlEncodingHelper encodingHelper = XmlEncodingHelper.create();
        XmlEncodingHandler handler = new XmlEncodingHandler(encodingHelper);
        doReturn(mockRecords).when(mockStorageManagement).getCustomerRecords();
        doReturn(mockItems).when(mockStorageManagement).getStorageItems();

        handler.writeStorageManagement(mockEncoder, mockStorageManagement);

        verify(mockEncoder, times(3)).writeObject(objectCaptor.capture());
        assertThat(objectCaptor.getAllValues().get(0)).asList().hasOnlyElementsOfType(CustomerRecord.class);
        assertThat(objectCaptor.getAllValues().get(1)).isInstanceOf(Integer.class);
        assertThat(objectCaptor.getAllValues().get(2)).asList().hasOnlyElementsOfType(StorageItem.class);
    }

    @Test
    void writeStorageManagement_shouldThrowAssertionErrorWhenPersistenceDelegatesNotSet() throws IntrospectionException, NoSuchMethodException {
        XmlEncodingHandler handler = new XmlEncodingHandler(mockEncodingHelper);
        doThrow(IntrospectionException.class).when(mockEncodingHelper).setLiquidBulkCargoPersistenceDelegate(mockEncoder);

        Throwable t = catchThrowable(() -> handler.writeStorageManagement(mockEncoder, mockStorageManagement));

        assertThat(t).isInstanceOf(AssertionError.class)
                .hasMessage("Persistence Delegates not properly set.")
                .hasCauseInstanceOf(IntrospectionException.class);
    }

    @Test
    void load_readsStorageAndCustomerAdministrationInProperOrder() {
        XmlEncodingHelper encodingHelper = XmlEncodingHelper.create();
        XmlEncodingHandler handler = new XmlEncodingHandler(encodingHelper);
        CustomerImpl owner = new CustomerImpl("x");
        doReturn(singletonList(owner), 1, singletonList(mockItem)).when(mockDecoder).readObject();
        doReturn(owner).when(mockItem).getOwner();

        handler.readStorageManagement(mockDecoder);

        verify(mockDecoder, times(3)).readObject();
    }
}