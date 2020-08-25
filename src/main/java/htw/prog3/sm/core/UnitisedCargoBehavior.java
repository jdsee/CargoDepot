package htw.prog3.sm.core;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

class UnitisedCargoBehavior {
    private final ReadOnlyBooleanProperty fragile;

    public UnitisedCargoBehavior(boolean isFragile) {
        this.fragile = new SimpleBooleanProperty(isFragile);
    }

    public boolean isFragile() {
        return fragile.get();
    }

    public ReadOnlyBooleanProperty fragileProperty() {
        return fragile;
    }
}
