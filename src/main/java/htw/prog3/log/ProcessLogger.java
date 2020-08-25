package htw.prog3.log;

import htw.prog3.routing.view.update.cargos.UpdateCargoViewEvent;
import htw.prog3.routing.view.update.cargos.UpdateCargoViewEventListener;
import htw.prog3.sm.api.StorageFacade;
import htw.prog3.sm.core.CustomerRecord;
import htw.prog3.sm.core.StorageItem;
import javafx.collections.MapChangeListener;

import java.io.Closeable;
import java.io.PrintWriter;

public class ProcessLogger implements Closeable, UpdateCargoViewEventListener {
    private final StorageFacade facade;
    private final PrintWriter writer;
    private final ProcessLogDictionary dictionary;

    public ProcessLogger(StorageFacade facade, PrintWriter out, ProcessLogDictionary dictionary) {
        this.facade = facade;
        this.writer = out;
        this.dictionary = dictionary;
        initObservers();
    }

    @Override
    public void onUpdateCargoViewEvent(UpdateCargoViewEvent event) {
        initObservers();
    }

    private void initObservers() {
        initItemObserver();
        initCustomerObserver();
    }

    private void initItemObserver() {
        facade.getStorageItems().addListener((MapChangeListener<Integer, StorageItem>) change -> {
            if (change.wasRemoved())
                writer.println(dictionary.itemRemovedMsg(change.getValueRemoved()));
            if (change.wasAdded())
                writer.println(dictionary.itemAddedMsg(change.getValueAdded()));
        });
    }

    private void initCustomerObserver() {
        facade.getCustomerRecords().addListener((MapChangeListener<String, CustomerRecord>) change -> {
            if (change.wasRemoved())
                writer.println(dictionary.customerRemovedMsg(change.getValueRemoved().getCustomer()));
            if (change.wasAdded())
                writer.println(dictionary.customerAddedMsg(change.getValueAdded().getCustomer()));
        });
    }

    @Override
    public void close() {
        writer.close();
    }
}
