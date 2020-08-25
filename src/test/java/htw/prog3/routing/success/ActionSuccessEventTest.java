package htw.prog3.routing.success;

import htw.prog3.routing.success.ActionSuccessEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActionSuccessEventTest {
    @Test
    void constructor_shouldReturnInstanceWithProperMessageAndSource() {
        ActionSuccessEvent event = new ActionSuccessEvent("abc", this);

        assertThat(event).extracting(
                ActionSuccessEvent::getMessage,
                ActionSuccessEvent::getSource)
                .containsExactly(
                        "abc",
                        this
                );
    }

    @Test
    void getMessage() {
        ActionSuccessEvent event = new ActionSuccessEvent("abc", this);

        assertThat(event.getMessage()).isEqualTo("abc");
    }
}