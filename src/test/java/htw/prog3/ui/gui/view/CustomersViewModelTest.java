package htw.prog3.ui.gui.view;

import htw.prog3.routing.input.create.customer.AddCustomerEvent;
import htw.prog3.routing.input.create.customer.AddCustomerEventHandler;
import htw.prog3.ui.gui.view.CustomersViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class CustomersViewModelTest {
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    AddCustomerEventHandler mockAddCustomerHandler;
    @Captor
    private ArgumentCaptor<AddCustomerEvent> addCustomerEventCaptor;

    @Test
    void addCustomer_shouldFireAddCustomerEventOnAddCustomer() {
        CustomersViewModel customersVM = new CustomersViewModel();
        customersVM.setAddCustomerEventHandler(mockAddCustomerHandler);
        String expected = "name";
        customersVM.setNameSelection(expected);

        customersVM.addCustomer();

        verify(mockAddCustomerHandler).handle(addCustomerEventCaptor.capture());
        assertThat(addCustomerEventCaptor.getValue().getCustomerName()).isEqualTo(expected);
    }
}