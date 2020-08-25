package htw.prog3.sm.core;

import htw.prog3.sm.core.UnitisedCargoBehavior;
import javafx.beans.property.ReadOnlyBooleanProperty;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitisedCargoBehaviorTest {
    @Test
    void ifFragile_Test() {
        UnitisedCargoBehavior behavior = new UnitisedCargoBehavior(true);

        boolean actual = behavior.isFragile();

        assertTrue(actual);
    }

    @Test
    void fragileProperty() {
        UnitisedCargoBehavior behavior = new UnitisedCargoBehavior(false);

        ReadOnlyBooleanProperty pressurizedProperty = behavior.fragileProperty();

        assertThat(pressurizedProperty.get()).isFalse();
    }
}