package htw.prog3.routing.success;

import htw.prog3.routing.success.ActionSuccessMessages;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActionSuccessMessagesTest {
    @Test
    void customerSuccessfullyRemoved() {
        String actual = ActionSuccessMessages.customerSuccessfullyRemoved("x");

        assertThat(actual).isEqualTo("The customer 'x' was successfully removed");
    }

    @Test
    void addCargoSucceeded() {
        String actual = ActionSuccessMessages.cargoSuccessfullyAdded(1);

        assertThat(actual).isEqualTo("The cargo was successfully added to the storage. Storage position: '1'.");
    }
}