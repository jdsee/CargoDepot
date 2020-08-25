package htw.prog3.persistence.jos;

import htw.prog3.persistence.jos.SerializationStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SerializationStrategyTest {
    @Test
    void create_shouldReturnNewInstance() {
        SerializationStrategy strategy = SerializationStrategy.create();
        SerializationStrategy other = SerializationStrategy.create();

        assertThat(strategy).isNotSameAs(other);
    }
}