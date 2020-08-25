package htw.prog3.log;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import htw.prog3.routing.input.create.cargo.AddCargoEvent;
import htw.prog3.routing.input.create.customer.AddCustomerEvent;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEvent;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEvent;
import htw.prog3.routing.input.listRequest.cargos.ListCargosReqEvent;
import htw.prog3.routing.input.listRequest.hazards.ListHazardsReqEvent;
import htw.prog3.routing.input.update.inspect.InspectCargoEvent;
import htw.prog3.routing.input.update.relocate.RelocateStorageItemEvent;
import htw.prog3.routing.persistence.item.load.LoadItemEvent;
import htw.prog3.routing.persistence.item.save.SaveItemEvent;

import java.util.Locale;

public class InteractionLogDictionary {
    private final Locale locale;

    private final String addCargoAttempt;
    private final String addCustomerAttempt;
    private final String removeCargoAttempt;
    private final String removeCustomerAttempt;
    private final String relocateItemAttempt;
    private final String inspectCargoAttempt;
    private final String saveItemAttempt;
    private final String loadItemAttempt;
    private final String listCargosAttempt;
    private final String listCustomersAttempt;
    private final String listHazardsAttempt;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public InteractionLogDictionary(@JsonProperty("locale") Locale locale,
                                    @JsonProperty("addItemAttempt") String addItemAttempt,
                                    @JsonProperty("addCustomerAttempt") String addCustomerAttempt,
                                    @JsonProperty("removeItemAttempt") String removeItemAttempt,
                                    @JsonProperty("removeCustomerAttempt") String removeCustomerAttempt,
                                    @JsonProperty("relocateItemAttempt") String relocateItemAttempt,
                                    @JsonProperty("inspectCargoAttempt") String inspectCargoAttempt,
                                    @JsonProperty("saveItemAttempt") String saveItemAttempt,
                                    @JsonProperty("loadItemAttempt") String loadItemAttempt,
                                    @JsonProperty("listCargosAttempt") String listCargosAttempt,
                                    @JsonProperty("listCustomersAttempt") String listCustomersAttempt,
                                    @JsonProperty("listHazardsAttempt") String listHazardsAttempt) {
        this.locale = locale;
        this.addCargoAttempt = addItemAttempt;
        this.addCustomerAttempt = addCustomerAttempt;
        this.removeCargoAttempt = removeItemAttempt;
        this.removeCustomerAttempt = removeCustomerAttempt;
        this.relocateItemAttempt = relocateItemAttempt;
        this.inspectCargoAttempt = inspectCargoAttempt;
        this.saveItemAttempt = saveItemAttempt;
        this.loadItemAttempt = loadItemAttempt;
        this.listCargosAttempt = listCargosAttempt;
        this.listCustomersAttempt = listCustomersAttempt;
        this.listHazardsAttempt = listHazardsAttempt;
    }

    public Locale getLocale() {
        return locale;
    }

    public String addItemAttemptMsg(AddCargoEvent event) {
        return String.format(addCargoAttempt, event.getCargoType(), event.getOwnerName(), event.getValue(), event.getDurationOfStorage(), event.getHazards(), event.isPressurized(), event.isFragile());
    }

    public String addCustomerAttemptMsg(AddCustomerEvent event) {
        return String.format(addCustomerAttempt, event.getCustomerName());
    }

    public String removeItemAttemptMsg(RemoveCargoEvent event) {
        return String.format(removeCargoAttempt, event.getStoragePosition());
    }

    public String removeCustomerAttemptMsg(RemoveCustomerEvent event) {
        return String.format(removeCustomerAttempt, event.getCustomerName());
    }

    public String relocateItemAttemptMsg(RelocateStorageItemEvent event) {
        return String.format(relocateItemAttempt, event.getFrom(), event.getTo());
    }

    public String inspectCargoAttemptMsg(InspectCargoEvent event) {
        return String.format(inspectCargoAttempt, event.getStoragePosition());
    }

    public String saveItemAttemptMsg(SaveItemEvent event) {
        return String.format(saveItemAttempt, event.getStoragePosition());
    }

    public String loadItemAttemptMsg(LoadItemEvent event) {
        return String.format(loadItemAttempt, event.getStoragePosition());
    }

    public String listCargosAttemptMsg(ListCargosReqEvent event) {
        return String.format(listCargosAttempt, event.getCargoType());
    }

    public String listCustomersAttemptMsg() {
        return listCustomersAttempt;
    }

    public String listHazardsAttemptMsg(ListHazardsReqEvent event) {
        return String.format(listHazardsAttempt, event.isInclusive());
    }
}
