package htw.prog3.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import htw.prog3.log.InteractionLogDictionary;
import htw.prog3.routing.input.create.cargo.AddCargoEvent;
import htw.prog3.routing.input.create.customer.AddCustomerEvent;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEvent;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEvent;
import htw.prog3.routing.input.listRequest.cargos.ListCargosReqEvent;
import htw.prog3.routing.input.listRequest.hazards.ListHazardsReqEvent;
import htw.prog3.routing.input.update.inspect.InspectCargoEvent;
import htw.prog3.routing.input.update.relocate.RelocateStorageItemEvent;
import htw.prog3.routing.persistence.item.load.LoadItemEvent;
import htw.prog3.routing.persistence.item.save.SaveItemEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;
import java.util.Locale;

import static htw.prog3.sm.core.CargoType.CARGO_BASE_TYPE;
import static htw.prog3.sm.core.CargoType.UNITISED_CARGO;
import static htw.prog3.storageContract.cargo.Hazard.RADIOACTIVE;
import static htw.prog3.storageContract.cargo.Hazard.TOXIC;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@SuppressWarnings("ResultOfMethodCallIgnored")
class InteractionLogDictionaryTest {
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    InteractionLogDictionary dictionary = new InteractionLogDictionary(Locale.ENGLISH,
            "type: %s, owner: %s, value: %s, durationofstorage: %s, hazards: %s, pressurized: %s, fragile: %s",
            "name: %s",
            "position: %d",
            "name: %s",
            "from: %d, to: %d",
            "position: %d",
            "position: %d",
            "position: %d",
            "type: %s",
            "list customers attempt",
            "inclusive: %s");
    @Mock
    AddCustomerEvent mockAddCustomerEvent;
    @Mock
    RemoveCargoEvent mockRemoveCargoAttempt;

    @Test
    void shouldBeDeserializableWithJackson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String serialized = "{\"addCustomerAttempt\":\"x\",\"addItemAttempt\":\"y\"}";

        InteractionLogDictionary dictionary = mapper.readValue(serialized, InteractionLogDictionary.class);

        assertThat(dictionary).isNotNull();
    }

    @Test
    void getLocale() {
        Locale locale = dictionary.getLocale();

        assertThat(locale).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void addCargoAttemptMsg_shouldReturnMessageContainingCargoTypeAndOwner() {
        AddCargoEvent addCargoEvent = new AddCargoEvent(UNITISED_CARGO, "x", BigDecimal.TEN, Duration.ofDays(365),
                new HashSet<>(asList(TOXIC, RADIOACTIVE)), true, false, this);

        String actual = dictionary.addItemAttemptMsg(addCargoEvent);

        assertThat(actual).matches("type: Unitised Cargo, owner: x, value: 10, durationofstorage: PT8760H, hazards: \\[(Toxic|Radioactive), (Toxic|Radioactive)], pressurized: true, fragile: false");
    }

    @Test
    void addCustomerAttemptMsg() {
        doReturn("x").when(mockAddCustomerEvent).getCustomerName();

        String actual = dictionary.addCustomerAttemptMsg(mockAddCustomerEvent);

        assertThat(actual).isEqualTo("name: x");
    }

    @Test
    void removeCargoAttempt() {
        doReturn(123).when(mockRemoveCargoAttempt).getStoragePosition();

        String actual = dictionary.removeItemAttemptMsg(mockRemoveCargoAttempt);

        assertThat(actual).isEqualTo("position: 123");
    }

    @Test
    void removeCustomerAttempt() {
        RemoveCustomerEvent event = new RemoveCustomerEvent("dummy", this);

        String actual = dictionary.removeCustomerAttemptMsg(event);

        assertThat(actual).isEqualTo("name: dummy");
    }

    @Test
    void relocateItemAttempt() {
        RelocateStorageItemEvent event = new RelocateStorageItemEvent(123, 321, this);

        String actual = dictionary.relocateItemAttemptMsg(event);

        assertThat(actual).isEqualTo("from: 123, to: 321");
    }

    @Test
    void inspectCargoAttempt() {
        InspectCargoEvent event = new InspectCargoEvent(123, this);

        String actual = dictionary.inspectCargoAttemptMsg(event);

        assertThat(actual).isEqualTo("position: 123");
    }

    @Test
    void saveItemAttempt() {
        SaveItemEvent event = new SaveItemEvent(999, this);

        String actual = dictionary.saveItemAttemptMsg(event);

        assertThat(actual).isEqualTo("position: 999");
    }

    @Test
    void loadItemAttempt() {
        LoadItemEvent event = new LoadItemEvent(999, this);

        String actual = dictionary.loadItemAttemptMsg(event);

        assertThat(actual).isEqualTo("position: 999");
    }

    @Test
    void listCargosAttempt() {
        ListCargosReqEvent event = new ListCargosReqEvent(CARGO_BASE_TYPE, this);

        String actual = dictionary.listCargosAttemptMsg(event);

        assertThat(actual).isEqualTo("type: Cargo Base Type");
    }

    @Test
    void listCustomersAttempt() {
        String actual = dictionary.listCustomersAttemptMsg();

        assertThat(actual).isEqualTo("list customers attempt");
    }

    @Test
    void listHazardsAttempt() {
        ListHazardsReqEvent event = new ListHazardsReqEvent(true, this);

        String actual = dictionary.listHazardsAttemptMsg(event);

        assertThat(actual).isEqualTo("inclusive: true");
    }
}