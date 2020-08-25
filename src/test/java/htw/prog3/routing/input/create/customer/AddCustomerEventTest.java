package htw.prog3.routing.input.create.customer;

import htw.prog3.routing.input.create.customer.AddCustomerEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddCustomerEventTest {

    @Test
    void constructor_shouldAssignArgsToInstance() {
        AddCustomerEvent event = new AddCustomerEvent("test", this);

        String name = event.getCustomerName();
        Object source = event.getSource();
        assertThat(name).isEqualTo("test");
        assertThat(source).isEqualTo(this);
    }

    @Test
    void getCustomerName() {
        AddCustomerEvent event = new AddCustomerEvent("test", this);

        String name = event.getCustomerName();

        assertThat(name).isEqualTo("test");
    }
}