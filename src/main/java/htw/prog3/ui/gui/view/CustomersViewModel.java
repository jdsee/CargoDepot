package htw.prog3.ui.gui.view;

import htw.prog3.routing.error.IllegalInputEvent;
import htw.prog3.routing.error.IllegalInputEventListener;
import htw.prog3.routing.input.create.customer.AddCustomerEvent;
import htw.prog3.routing.input.create.customer.AddCustomerEventHandler;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEvent;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEventHandler;
import htw.prog3.routing.view.update.customers.UpdateCustomersViewEvent;
import htw.prog3.routing.view.update.customers.UpdateCustomersViewEventListener;
import htw.prog3.sm.core.CustomerRecord;
import htw.prog3.util.BindingUtils;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class CustomersViewModel implements UpdateCustomersViewEventListener, IllegalInputEventListener {
    private final StringProperty nameSelection = new SimpleStringProperty();
    private final BooleanProperty addCustomerButtonDisabled = new SimpleBooleanProperty(true);
    private final BooleanProperty removeCustomerButtonDisabled = new SimpleBooleanProperty();
    private final StringProperty failureMessage = new SimpleStringProperty();
    private final ObjectProperty<CustomerRecord> selectedCustomer = new SimpleObjectProperty<>();
    private final ListProperty<CustomerRecord> customerRecordsRepresentation;

    private AddCustomerEventHandler addCustomerEventHandler;
    private RemoveCustomerEventHandler removeCustomerEventHandler;

    public CustomersViewModel() {
        this.customerRecordsRepresentation = new SimpleListProperty<>();

        this.addCustomerButtonDisabled.bind(nameSelection.isEmpty().or(nameSelection.isNull()));
        this.removeCustomerButtonDisabled.bind(selectedCustomer.isNull());
    }

    public CustomersViewModel setAddCustomerEventHandler(AddCustomerEventHandler addCustomerEventHandler) {
        this.addCustomerEventHandler = addCustomerEventHandler;
        return this;
    }

    public CustomersViewModel setRemoveCustomerEventHandler(RemoveCustomerEventHandler removeCustomerEventHandler) {
        this.removeCustomerEventHandler = removeCustomerEventHandler;
        return this;
    }

    @Override
    public void onUpdateCustomersViewEvent(UpdateCustomersViewEvent event) {
        ListProperty<CustomerRecord> records = BindingUtils.createObservableValues(event.getRecords());
        customerRecordsRepresentation.set(records);
    }

    @Override
    public void onIllegalInputEvent(IllegalInputEvent event) {
        if (IllegalInputEvent.Trigger.CUSTOMERS.equals(event.getTrigger())) {
            String message = event.getMessage();
            setFailureMessage(message);
        }
    }

    public void addCustomer() {
        setFailureMessage("");

        String name = getNameSelection(); //possible validation here
        fireAddCustomerEvent(name);

        setNameSelection("");
    }

    public void removeCustomer() {
        setFailureMessage("");

        String name = getSelectedCustomer().getCustomer().getName();
        fireRemoveCustomerEvent(name);
    }

    private void fireAddCustomerEvent(String name) {
        AddCustomerEvent event = new AddCustomerEvent(name, this);
        addCustomerEventHandler.handle(event);
    }

    private void fireRemoveCustomerEvent(String name) {
        RemoveCustomerEvent event = new RemoveCustomerEvent(name, this);
        removeCustomerEventHandler.handle(event);
    }

    public CustomerRecord getSelectedCustomer() {
        return selectedCustomer.get();
    }

    public ObjectProperty<CustomerRecord> selectedCustomerProperty() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(CustomerRecord selectedCustomer) {
        this.selectedCustomer.set(selectedCustomer);
    }

    public boolean isRemoveCustomerButtonDisabled() {
        return removeCustomerButtonDisabled.get();
    }

    public BooleanProperty removeCustomerButtonDisabledProperty() {
        return removeCustomerButtonDisabled;
    }

    public void setRemoveCustomerButtonDisabled(boolean removeCustomerButtonDisabled) {
        this.removeCustomerButtonDisabled.set(removeCustomerButtonDisabled);
    }

    public boolean getAddCustomerButtonDisabled() {
        return addCustomerButtonDisabled.get();
    }

    public BooleanProperty addCustomerButtonDisabledProperty() {
        return addCustomerButtonDisabled;
    }

    public void setAddCustomerButtonDisabled(boolean addCustomerButtonDisabled) {
        this.addCustomerButtonDisabled.set(addCustomerButtonDisabled);
    }

    public String getNameSelection() {
        return nameSelection.get();
    }

    public StringProperty nameSelectionProperty() {
        return nameSelection;
    }


    public void setNameSelection(String nameSelection) {
        this.nameSelection.set(nameSelection);
    }

    public final String getFailureMessage() {
        return failureMessage.get();
    }

    public final void setFailureMessage(String message) {
        failureMessage.setValue(message);
    }

    public StringProperty failureMessageProperty() {
        return failureMessage;
    }

    public ObservableList<CustomerRecord> getCustomerRecords() {
        return customerRecordsRepresentation;
    }
}
