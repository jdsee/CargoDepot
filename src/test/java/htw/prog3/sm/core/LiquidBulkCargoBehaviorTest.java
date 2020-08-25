package htw.prog3.sm.core;

import javafx.beans.property.ReadOnlyBooleanProperty;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LiquidBulkCargoBehaviorTest {

    @Test
    void isPressurized_Test() {
        LiquidBulkCargoBehavior behavior = new LiquidBulkCargoBehavior(true);

        boolean actual = behavior.isPressurized();

        assertTrue(actual);
    }

    @Test
    void pressurizedProperty() {
        LiquidBulkCargoBehavior behavior = new LiquidBulkCargoBehavior(true);

        ReadOnlyBooleanProperty pressurizedProperty = behavior.pressurizedProperty();

        assertThat(pressurizedProperty.get()).isTrue();
    }
}