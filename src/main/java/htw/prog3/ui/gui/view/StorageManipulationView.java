package htw.prog3.ui.gui.view;

import htw.prog3.sm.core.CargoType;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.ui.gui.PromptButtonCell;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import org.controlsfx.control.CheckComboBox;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class StorageManipulationView {
    public static final String CARGO_TYPE_PROMPT_TEXT = "Cargo Type";
    public static final String OWNER_PROMPT_TEXT = "Owner";

    private final StorageManipulationViewModel storageManipulationViewModel;

    public StorageManipulationView(StorageManipulationViewModel storageManipulationVM) {
        this.storageManipulationViewModel = storageManipulationVM;
    }

    @FXML
    public ComboBox<CargoType> cargoTypeInput;
    @FXML
    public ComboBox<Customer> ownerInput;
    @FXML
    public Spinner<Integer> durationOfStorageSpinner;
    @FXML
    public CheckBox fragileSelection;
    @FXML
    public CheckBox pressurizedSelection;
    @FXML
    CheckComboBox<Hazard> hazardsInput;
    @FXML
    private TextField valueInput;

    @FXML
    protected void initialize() {
        cargoTypeInput.valueProperty().bindBidirectional(storageManipulationViewModel.cargoTypeSelectionProperty());
        ownerInput.valueProperty().bindBidirectional(storageManipulationViewModel.customerSelectionProperty());
        cargoTypeInput.setButtonCell(new PromptButtonCell<>(CARGO_TYPE_PROMPT_TEXT));
        ownerInput.setButtonCell(new PromptButtonCell<>(OWNER_PROMPT_TEXT));
        storageManipulationViewModel.fragileSelectedProperty().bindBidirectional(fragileSelection.selectedProperty());
        storageManipulationViewModel.pressurizedSelectedProperty().bindBidirectional(pressurizedSelection.selectedProperty());
        initHazardsInput();
        initValueInput();
    }

    private void initHazardsInput() {
        hazardsInput.getItems().addAll(Arrays.asList(Hazard.values()));
        Set<Hazard> hazardsSelection = storageManipulationViewModel.getHazardsSelection();
        hazardsInput.getCheckModel().getCheckedItems().addListener(
                (ListChangeListener<? super Hazard>) change -> {
                    hazardsInput.setTitle(String.format("%d/%d %s",
                            hazardsInput.getCheckModel().getCheckedItems().size(),
                            hazardsInput.getItems().size(), "Hazards"));
                    while (change.next()) {
                        if (change.wasRemoved())
                            hazardsSelection.removeAll(change.getRemoved());
                        if (change.wasAdded())
                            hazardsSelection.addAll(change.getAddedSubList());
                    }
                });
    }

    private void initValueInput() {
        BigDecimalStringConverter converter = new BigDecimalStringConverter() {
            @Override
            public BigDecimal fromString(String s) {
                if (null == s || s.isEmpty()) {
                    valueInput.setStyle("");
                    return null;
                }
                try {
                    BigDecimal value = new BigDecimal(s);
                    valueInput.setStyle("");
                    return value;
                } catch (NumberFormatException e) {
                    valueInput.setStyle("-fx-border-color:orangered;");
                }
                return null;
            }
        };
        valueInput.textProperty().bindBidirectional(storageManipulationViewModel.valueSelectionProperty(), converter);
    }

    public void addCargo(ActionEvent actionEvent) {
        storageManipulationViewModel.addCargo();
        hazardsInput.getCheckModel().clearChecks();
    }

    public void removeCargo(ActionEvent actionEvent) {
        storageManipulationViewModel.removeCargo();
    }

    public StorageManipulationViewModel getStorageManipulationViewModel() {
        return storageManipulationViewModel;
    }

    public ObservableList<CargoType> getCargoTypes() {
        return FXCollections.observableList(CargoType.validValues());
    }

    private <T> TextFormatter<T> getTextFormatter(Pattern validEditingState, StringConverter<T> converter, T value) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String changeText = change.getControlNewText();
            return validEditingState.matcher(changeText).matches() ? change : null;
        };
        return new TextFormatter<>(converter, value, filter);
    }

    public void saveCargo(ActionEvent actionEvent) {
        storageManipulationViewModel.saveCargo();
    }

    public void inspectCargo(ActionEvent actionEvent) {
        storageManipulationViewModel.inspectCargo();
    }
}
