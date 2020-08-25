package htw.prog3.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import htw.prog3.log.ProcessLogDictionary;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.storageContract.administration.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.MockitoAnnotations.initMocks;

class ProcessLogDictionaryTest {
    @Mock
    StorageItem mockItem;
    @Mock
    Customer mockCustomer;

    @Test
    void shouldBeDeserializableWithJackson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String serialized = "{\n" +
                "  \"customerAdded\": \"Customer '%s' has been added to the customer administration.\",\n" +
                "  \"customerRemoved\": \"Customer '%s' has been removed from the customer administration.\",\n" +
                "  \"itemAdded\": \"New cargo has been added to position '%d'. Owner: '%s'.\",\n" +
                "  \"itemRemoved\": \"Cargo at position '%d' has been removed.\"\n" +
                "}";

        ProcessLogDictionary dictionary = mapper.readValue(serialized, ProcessLogDictionary.class);

        assertThat(dictionary).isNotNull();
    }

    private ProcessLogDictionary dictionary = new ProcessLogDictionary(Locale.ENGLISH,
            "position: %d",
            "position: %d, owner: %s",
            "name: %s",
            "name: %s"
    );

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void getLocale() {
        Locale actual = dictionary.getLocale();

        assertThat(actual).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void itemRemovedMsg() {
        doReturn(123).when(mockItem).getStoragePosition();

        String actual = dictionary.itemRemovedMsg(mockItem);

        assertThat(actual).isEqualTo("position: 123");
    }

    @Test
    void itemAddedMsg() {
        doReturn(123).when(mockItem).getStoragePosition();
        Customer owner = new CustomerImpl("dummy");
        doReturn(owner).when(mockItem).getOwner();

        String actual = dictionary.itemAddedMsg(mockItem);

        assertThat(actual).isEqualTo("position: 123, owner: dummy");
    }

    @Test
    void customerRemovedMsg() {
        doReturn("dummy").when(mockCustomer).getName();

        String actual = dictionary.customerRemovedMsg(mockCustomer);

        assertThat(actual).isEqualTo("name: dummy");
    }

    @Test
    void customerAddedMsg() {
        doReturn("dummy").when(mockCustomer).getName();

        String actual = dictionary.customerAddedMsg(mockCustomer);

        assertThat(actual).isEqualTo("name: dummy");
    }
}