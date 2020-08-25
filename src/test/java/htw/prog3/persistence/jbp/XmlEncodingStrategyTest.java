package htw.prog3.persistence.jbp;

import htw.prog3.persistence.jbp.XmlEncodingStrategy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class XmlEncodingStrategyTest {
    @Test
    void create_shouldReturnNewInstance() {
        XmlEncodingStrategy strategy = XmlEncodingStrategy.create();
        XmlEncodingStrategy other = XmlEncodingStrategy.create();

        assertThat(strategy).isNotSameAs(other);
    }
}