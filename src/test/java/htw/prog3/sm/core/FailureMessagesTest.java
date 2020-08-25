package htw.prog3.sm.core;

import htw.prog3.routing.persistence.all.PersistenceType;
import htw.prog3.sm.core.FailureMessages;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FailureMessagesTest {

    @Test
    void unknownCustomer() {
        String actual = FailureMessages.unknownCustomer("x");

        assertThat(actual).isEqualTo("Customer 'x' not known");
    }

    @Test
    void customerNameAmbiguous() {
        String actual = FailureMessages.customerNameAmbiguous("x");

        assertThat(actual).isEqualTo("Customer 'x' already present. Customer names must be unique");
    }

    @Test
    void unknownPersistenceType() {
        String actual = FailureMessages.unknownPersistenceType(PersistenceType.JOS);

        assertThat(actual).isEqualTo("Persistence Type '" + PersistenceType.JOS + "' is not known.");
    }

    @Test
    void notNull() {
        String actual = FailureMessages.notNull("x");

        assertThat(actual).isEqualTo("'x' is not allowed to be null.");
    }
}