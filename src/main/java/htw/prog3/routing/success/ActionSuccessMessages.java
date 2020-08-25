package htw.prog3.routing.success;

public class ActionSuccessMessages {
    public static final String STATE_CHANGE_SUCCEEDED = "The state was successfully changed.";
    public static final String REMOVE_CARGO_SUCCEEDED = "The cargo has been successfully removed";
    public static final String CARGO_INSPECTION_SUCCEEDED = "The cargo at the specified storage position was successfully inspected.";
    public static final String WAS_SUCCESSFULLY_ADDED = "was successfully added.";
    public static final String WAS_SUCCESSFULLY_REMOVED = "was successfully removed";
    public static final String STORAGE_SUCCESSFULLY_SAVED = "The storage has been successfully saved.";
    public static final String STORAGE_SUCCESSFULLY_LOAD = "The storage has been successfully load.";
    public static final String ITEM_SUCCESSFULLY_LOAD = "The item has been successfully load.";
    public static final String ITEM_SUCCESSFULLY_SAVED = "The storage has been successfully saved.";

    public static String customerSuccessfullyRemoved(String name) {
        return String.format("The customer '%s' %s", name, WAS_SUCCESSFULLY_REMOVED);
    }

    public static String cargoSuccessfullyAdded(int position) {
        return String.format("The cargo was successfully added to the storage. Storage position: '%s'.", position);
    }
}
