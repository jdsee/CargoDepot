package htw.prog3.ui.gui.view;

import htw.prog3.sm.core.CustomerRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class CustomersView {
    private final CustomersViewModel customersViewModel;

    @FXML
    public Button addCustomerButton;
    @FXML
    public Button removeCustomerButton;
    @FXML
    public TableView<CustomerRecord> customersTable;
    @FXML
    private TextField nameInput;

    @FXML
    protected void initialize() {
        nameInput.textProperty().bindBidirectional(customersViewModel.nameSelectionProperty());
        customersViewModel.selectedCustomerProperty().bind(customersTable.getSelectionModel().selectedItemProperty());
    }

    public CustomersView() {
        this(new CustomersViewModel());
    }

    public CustomersView(CustomersViewModel customersViewModel) {
        this.customersViewModel = customersViewModel;
    }

    public CustomersViewModel getCustomersViewModel() {
        return customersViewModel;
    }

    public void addCustomer(ActionEvent actionEvent) {
        customersViewModel.addCustomer();
    }

    public void removeCustomer(ActionEvent actionEvent) {
        customersViewModel.removeCustomer();
    }
}
