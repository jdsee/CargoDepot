package htw.prog3.ui.cli.view;

import htw.prog3.routing.error.IllegalInputEvent;
import htw.prog3.routing.error.IllegalInputEventListener;
import htw.prog3.routing.success.ActionSuccessEvent;
import htw.prog3.routing.success.ActionSuccessEventListener;
import htw.prog3.routing.view.listResponse.cargos.ListCargosResEvent;
import htw.prog3.routing.view.listResponse.cargos.ListCargosResEventListener;
import htw.prog3.routing.view.listResponse.customers.ListCustomersResEvent;
import htw.prog3.routing.view.listResponse.customers.ListCustomersResEventListener;
import htw.prog3.routing.view.listResponse.hazards.ListHazardsResEvent;
import htw.prog3.routing.view.listResponse.hazards.listHazardsResEventListener;
import htw.prog3.sm.core.CustomerRecord;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.storageContract.cargo.Hazard;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;

public class CommandLineWriter implements ActionSuccessEventListener, IllegalInputEventListener,
        ListCargosResEventListener, ListCustomersResEventListener, listHazardsResEventListener {

    final PrintStream console = System.out;

    @Override
    public void onActionSuccessEvent(ActionSuccessEvent event) {
        if (null != event) {
            console.println(">>> OPERATION SUCCEEDED");
            String message = event.getMessage();
            if (null != message && !message.isEmpty())
                console.printf(">>> %s%n", message);
        }
    }

    @Override
    public void onIllegalInputEvent(IllegalInputEvent event) {
        console.println("<!> AN ERROR OCCURRED");
        console.printf("<!> %s%n", event.getMessage());
    }

    @Override
    public void onListCargosResEvent(ListCargosResEvent event) {
        List<StorageItem> storageItems = event.getStorageItems();
        if (storageItems.isEmpty()) {
            console.println(">>> The storage seems to be empty at the moment.");
        } else for (StorageItem item : storageItems) {
            System.out.println(item);
        }
    }

    @Override
    public void onListCustomerResEvent(ListCustomersResEvent event) {
        List<CustomerRecord> records = event.getCustomerRecords();
        if (records.isEmpty()) {
            console.println(">>> The customer administration seems to be empty at the moment.");
        } else for (CustomerRecord record : records) {
            System.out.println(record);
        }
    }

    @Override
    public void onListHazardsResEvent(ListHazardsResEvent event) {
        Set<Hazard> hazards = event.getHazards();
        if (hazards.isEmpty())
            console.println(">>> There are no hazards in the storage at the moment.");
        else
            console.println(hazards);
    }
}