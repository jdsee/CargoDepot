package htw.prog3.ui.cli.view.listener;

import htw.prog3.routing.config.ViewConfigEvent;
import htw.prog3.routing.config.ViewConfigEventListener;
import htw.prog3.sm.api.StorageFacade;
import htw.prog3.storageContract.cargo.Hazard;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.SetChangeListener;

public class HazardChangeListener implements ViewConfigEventListener {
    private final SetProperty<Hazard> presentHazards;
    private final SetChangeListener<? super Hazard> listener = this::onHazardsChanged;

    public HazardChangeListener(StorageFacade storageFacade) {
        presentHazards = new SimpleSetProperty<>();
        presentHazards.set(storageFacade.getHazards());
        presentHazards.addListener(listener);
    }

    @Override
    public void onViewConfigEvent(ViewConfigEvent event) {
        if (this.getClass().equals(event.getType())) {
            if (event.isActivation())
                presentHazards.addListener(listener);
            else
                presentHazards.removeListener(listener);
        }
    }

    private void onHazardsChanged(SetChangeListener.Change<? extends Hazard> change) {
        System.out.printf("<> Hazards changed --- Actually present: %s%n", presentHazards.get());
    }
}