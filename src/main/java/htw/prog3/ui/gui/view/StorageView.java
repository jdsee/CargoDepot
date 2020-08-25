package htw.prog3.ui.gui.view;

import htw.prog3.sm.core.StorageItem;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.LiquidBulkCargo;
import htw.prog3.storageContract.cargo.UnitisedCargo;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

public class StorageView {
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    private final StorageViewModel storageViewModel;

    @FXML
    private TableView<StorageItem> storageItemsTable;

    @FXML
    protected void initialize() {
        storageViewModel.selectedItemProperty().bind(storageItemsTable.getSelectionModel().selectedItemProperty());

        enableStorageItemsDragAndDrop();
        addFragileColumn();
        addPressurizedColumn();
    }

    public StorageView() {
        this(new StorageViewModel());
    }

    public StorageView(StorageViewModel storageViewModel) {
        this.storageViewModel = storageViewModel;
    }

    public StorageViewModel getStorageViewModel() {
        return storageViewModel;
    }

    /**
     * guided by this so-answer:
     * https://stackoverflow.com/a/28606524
     */
    private void enableStorageItemsDragAndDrop() {
        storageItemsTable.setRowFactory(tableView -> {
            TableRow<StorageItem> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                StorageItem item = tableView.getSelectionModel().getSelectedItem();
                if (!row.isEmpty()) {
                    Dragboard dragboard = row.startDragAndDrop(TransferMode.MOVE);
                    dragboard.setDragView(row.snapshot(null, null));
                    ClipboardContent clipboardContent = new ClipboardContent();
                    Integer storagePosition = item.getStoragePosition();
                    clipboardContent.put(SERIALIZED_MIME_TYPE, storagePosition);
                    dragboard.setContent(clipboardContent);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (!row.isEmpty()) {
                        int actualPosition = row.getItem().getStoragePosition();
                        if (actualPosition != (Integer) dragboard.getContent(SERIALIZED_MIME_TYPE)) {
                            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                            event.consume();
                        }
                    }
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedPosition = (int) dragboard.getContent(SERIALIZED_MIME_TYPE);

                    int droppedPosition = row.isEmpty() ? Integer.MAX_VALUE : row.getItem().getStoragePosition();
                    relocateStorageItem(draggedPosition, droppedPosition);

                    event.setDropCompleted(true);
                    event.consume();

                    storageItemsTable.getSelectionModel().select(row.getItem());
                }
            });

            return row;
        });
    }

    private void addFragileColumn() {
        TableColumn<StorageItem, Boolean> fragileCol =
                initOptionalTableColumn("is fragile", cellDataFeatures -> {
                    Cargo cargo = cellDataFeatures.getValue().getCargo();
                    return (cargo instanceof UnitisedCargo) ? ((UnitisedCargo) cargo).fragileProperty() : null;
                });
        storageItemsTable.getColumns().add(fragileCol);
    }

    private void addPressurizedColumn() {
        TableColumn<StorageItem, Boolean> pressurizedCol =
                initOptionalTableColumn("is pressurized", cellDataFeatures -> {
                    Cargo cargo = cellDataFeatures.getValue().getCargo();
                    return (cargo instanceof LiquidBulkCargo) ? ((LiquidBulkCargo) cargo).pressurizedProperty() : null;
                });
        storageItemsTable.getColumns().add(pressurizedCol);
    }

    private <V, T> TableColumn<V, T> initOptionalTableColumn(
            String title, Callback<TableColumn.CellDataFeatures<V, T>, ObservableValue<T>> valueMapper) {
        TableColumn<V, T> col = new TableColumn<>();
        col.setText(title);
        col.setCellValueFactory(valueMapper);
        return col;
    }

    private void relocateStorageItem(int from, int to) {
        storageViewModel.relocateStorageItem(from, to);
    }
}