package htw.prog3.ui.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ListCell;

/**
 * This class is copied from this so-answer:
 * https://stackoverflow.com/questions/50569330/how-to-reset-combobox-and-display-prompttext
 * <p>
 * I did not find a solution provided by jfx directly and also couldn't do better than this by myself.
 *
 * @param <T>
 */
public class PromptButtonCell<T> extends ListCell<T> {

    private final StringProperty promptText = new SimpleStringProperty();

    public PromptButtonCell(String promptText) {
        this.promptText.addListener((obs, oldText, newText) -> {
            if (isEmpty() || getItem() == null) {
                setText(newText);
            }
        });
        setPromptText(promptText);
    }

    public StringProperty promptTextProperty() {
        return promptText;
    }

    public final String getPromptText() {
        return promptTextProperty().get();
    }

    public final void setPromptText(String promptText) {
        promptTextProperty().set(promptText);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(getPromptText());
        } else {
            setText(item.toString());
        }
    }
}