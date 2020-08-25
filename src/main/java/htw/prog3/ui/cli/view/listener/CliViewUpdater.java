package htw.prog3.ui.cli.view.listener;

import htw.prog3.routing.config.ViewConfigEventHandler;
import htw.prog3.routing.config.ViewConfigEventListener;
import htw.prog3.routing.view.update.cargos.UpdateCargoViewEvent;
import htw.prog3.routing.view.update.cargos.UpdateCargoViewEventListener;
import htw.prog3.sm.api.StorageFacade;

public class CliViewUpdater implements UpdateCargoViewEventListener {
    private final StorageFacade storageFacade;
    private ViewConfigEventHandler viewConfigEventHandler;
    private HazardChangeListener hazardChangeListener;
    private CriticalCapacityListener criticalCapacityListener;

    public CliViewUpdater(StorageFacade storageFacade) {
        this.storageFacade = storageFacade;
//        updateListener();
    }

    public void setViewConfigEventHandler(ViewConfigEventHandler viewConfigEventHandler) {
        this.viewConfigEventHandler = viewConfigEventHandler;
    }

    @Override
    public void onUpdateCargoViewEvent(UpdateCargoViewEvent event) {
        updateListener();
    }

    private void updateListener() {
        final HazardChangeListener prevHazardChangeListener = hazardChangeListener;
        hazardChangeListener = new HazardChangeListener(storageFacade);

        final CriticalCapacityListener prevCriticalCapacityListener = criticalCapacityListener;
        criticalCapacityListener = new CriticalCapacityListener(storageFacade.getStorageManagement());

        updateViewConfigHandler(prevHazardChangeListener, hazardChangeListener);
        updateViewConfigHandler(prevCriticalCapacityListener, criticalCapacityListener);
    }

    private void updateViewConfigHandler(ViewConfigEventListener prev, ViewConfigEventListener actual) {
        if (viewConfigEventHandler != null) {
            viewConfigEventHandler.removeListener(prev);
            viewConfigEventHandler.addListener(actual);
        }

    }
}
