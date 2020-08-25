package htw.prog3.sm.core;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

class LiquidBulkCargoBehavior {
    private final ReadOnlyBooleanProperty pressurized;

    public LiquidBulkCargoBehavior(boolean isPressurized) {
        this.pressurized = new SimpleBooleanProperty(isPressurized);
    }

    public boolean isPressurized() {
        return pressurized.get();
    }

    public ReadOnlyBooleanProperty pressurizedProperty() {
        return pressurized;
    }
}
