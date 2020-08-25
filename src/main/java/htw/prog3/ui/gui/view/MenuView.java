package htw.prog3.ui.gui.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.converter.IntegerStringConverter;

public class MenuView {
    final MenuViewModel viewModel;

    public MenuView(MenuViewModel menuVM) {
        this.viewModel = menuVM;
    }

    @FXML
    public TextField storagePositionInput;

    @FXML
    protected void initialize() {
        storagePositionInput.textProperty().bindBidirectional(viewModel.storagePositionInputProperty(), new IntegerStringConverter());
    }

    public MenuViewModel getViewModel() {
        return viewModel;
    }

    public void saveJos(ActionEvent actionEvent) {
        viewModel.saveJos();
    }

    public void saveJbp(ActionEvent actionEvent) {
        viewModel.saveJBP();
    }

    public void loadJos(ActionEvent actionEvent) {
        viewModel.loadJos();
    }

    public void loadJbp(ActionEvent actionEvent) {
        viewModel.loadJBP();
    }

    public void load(ActionEvent actionEvent) {
        viewModel.load();
    }
}
