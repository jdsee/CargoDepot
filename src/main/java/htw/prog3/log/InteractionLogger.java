package htw.prog3.log;

import htw.prog3.routing.input.create.cargo.AddCargoEventHandler;
import htw.prog3.routing.input.create.customer.AddCustomerEventHandler;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEventHandler;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEventHandler;
import htw.prog3.routing.input.listRequest.cargos.ListCargosReqEventHandler;
import htw.prog3.routing.input.listRequest.customers.ListCustomersReqEventHandler;
import htw.prog3.routing.input.listRequest.hazards.ListHazardsReqEventHandler;
import htw.prog3.routing.input.update.inspect.InspectCargoEventHandler;
import htw.prog3.routing.input.update.relocate.RelocateStorageItemEventHandler;
import htw.prog3.routing.persistence.item.load.LoadItemEventHandler;
import htw.prog3.routing.persistence.item.save.SaveItemEventHandler;

import java.io.Closeable;
import java.io.PrintWriter;
import java.util.EventObject;
import java.util.function.Function;

public class InteractionLogger implements Closeable {
    private final InteractionLogDictionary dictionary;
    private final PrintWriter writer;

    public InteractionLogger(PrintWriter out, InteractionLogDictionary dictionary) {
        this.writer = out;
        this.dictionary = dictionary;
    }

    public void registerAddCargoEventListener(AddCargoEventHandler handler) {
        handler.addListener(event -> log(dictionary::addItemAttemptMsg, event));
    }

    public void registerAddCustomerEventListener(AddCustomerEventHandler handler) {
        handler.addListener(event -> log(dictionary::addCustomerAttemptMsg, event));
    }

    public void registerRemoveCargoEventListener(RemoveCargoEventHandler handler) {
        handler.addListener(event -> log(dictionary::removeItemAttemptMsg, event));
    }

    public void registerRemoveCustomerEventListener(RemoveCustomerEventHandler handler) {
        handler.addListener(event -> log(dictionary::removeCustomerAttemptMsg, event));
    }

    public void registerInspectCargoEventListener(InspectCargoEventHandler handler) {
        handler.addListener(event -> log(dictionary::inspectCargoAttemptMsg, event));
    }

    public void registerRelocateStorageItemEventListener(RelocateStorageItemEventHandler handler) {
        handler.addListener(event -> log(dictionary::relocateItemAttemptMsg, event));
    }

    public void registerSaveItemEventListener(SaveItemEventHandler handler) {
        handler.addListener(event -> log(dictionary::saveItemAttemptMsg, event));
    }

    public void registerLoadItemEventListener(LoadItemEventHandler handler) {
        handler.addListener(event -> log(dictionary::loadItemAttemptMsg, event));
    }

    public void registerListCargosReqEventListener(ListCargosReqEventHandler handler) {
        handler.addListener(event -> log(dictionary::listCargosAttemptMsg, event));
    }

    public void registerListCustomersReqEventListener(ListCustomersReqEventHandler handler) {
        handler.addListener(() -> log(dictionary.listCustomersAttemptMsg()));
    }

    public void registerListHazardsReqEventListener(ListHazardsReqEventHandler handler) {
        handler.addListener(event -> log(dictionary::listHazardsAttemptMsg, event));
    }

    @Override
    public void close() {
        writer.close();
    }

    private <T extends EventObject> void log(Function<T, String> msgSupplier, T event) {
        writer.println(msgSupplier.apply(event));
    }

    private void log(String msg) {
        writer.println(msg);
    }
}
