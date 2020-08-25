package htw.prog3.routing.error;

import htw.prog3.routing.error.IllegalInputEvent;
import htw.prog3.routing.error.IllegalInputEvent.Trigger;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IllegalInputEventTest {

    @Test
    void constructor_shouldReturnNewInstance() {
        IllegalInputEvent event = new IllegalInputEvent("abc", Trigger.ANY, this);
        IllegalInputEvent other = new IllegalInputEvent("abc", Trigger.ANY, this);

        assertThat(event).isNotNull().isNotSameAs(other);
    }

    @Test
    void getMessage() {
        IllegalInputEvent event = new IllegalInputEvent("abc", Trigger.ANY, this);

        String actualMessage = event.getMessage();

        assertThat(actualMessage).isEqualTo("abc");
    }

    @Test
    void getTrigger() {
        IllegalInputEvent event = new IllegalInputEvent("abc", Trigger.ANY, this);

        Trigger actualTrigger = event.getTrigger();

        assertThat(actualTrigger).isEqualTo(Trigger.ANY);
    }
}