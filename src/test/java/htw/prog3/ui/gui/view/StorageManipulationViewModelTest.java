package htw.prog3.ui.gui.view;

import htw.prog3.routing.input.delete.cargo.RemoveCargoEvent;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEventHandler;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.ui.gui.view.StorageManipulationViewModel;
import htw.prog3.ui.gui.view.StorageViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

class StorageManipulationViewModelTest {
    @Mock
    StorageItem mockItem;
    @Mock
    RemoveCargoEventHandler mockRemoveCargoHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void removeCargo_shouldFireRemoveCargoEvent() {
        StorageViewModel storageVM = new StorageViewModel();
        StorageManipulationViewModel viewModel = new StorageManipulationViewModel(storageVM);
        viewModel.setRemoveCargoEventHandler(mockRemoveCargoHandler);
        doReturn(1).when(mockItem).getStoragePosition();
        viewModel.setSelectedItem(mockItem);

        viewModel.removeCargo();

        verify(mockRemoveCargoHandler).handle(any(RemoveCargoEvent.class));
    }
}