package htw.prog3.routing.view.update.customers;

import java.util.EventListener;

public interface UpdateCustomersViewEventListener extends EventListener {
    void onUpdateCustomersViewEvent(UpdateCustomersViewEvent event);
}
