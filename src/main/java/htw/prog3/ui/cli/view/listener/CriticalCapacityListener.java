package htw.prog3.ui.cli.view.listener;

import htw.prog3.routing.config.ViewConfigEvent;
import htw.prog3.routing.config.ViewConfigEventListener;
import htw.prog3.sm.api.StorageManagement;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class CriticalCapacityListener implements ViewConfigEventListener {
    private final int maxCapacity;
    private final IntegerProperty itemCount;
    private final ChangeListener<Number> listener = this::capacityChanged;

    public CriticalCapacityListener(StorageManagement management) {
        maxCapacity = management.getCapacity();
        itemCount = new SimpleIntegerProperty();
        itemCount.bind(management.itemCountProperty());

        itemCount.addListener(listener);
    }

    private void update() {
        String message = (maxCapacity == (itemCount).get()) ? "No more" : "Only one";
        System.out.printf("<> Storage capacity critical --- " +
                "%s space left. The actual maximum is: %d%n", message, maxCapacity);
    }

    @Override
    public void onViewConfigEvent(ViewConfigEvent event) {
        if (this.getClass().equals(event.getType())) {
            if (event.isActivation())
                itemCount.addListener(listener);
            else
                itemCount.removeListener(listener);
        }
    }

    private void capacityChanged(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (1 >= maxCapacity - newValue.intValue())
            update();
    }
}
