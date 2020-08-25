package htw.prog3.storageContract.cargo;

import htw.prog3.sm.core.CargoType;
import htw.prog3.storageContract.administration.Customer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;

public interface Cargo extends Serializable {
    CargoType CARGO_TYPE = CargoType.CARGO_BASE_TYPE;

    default CargoType getCargoType() {
        return CARGO_TYPE;
    }

    Customer getOwner();

    ReadOnlyObjectProperty<Customer> ownerProperty();

    BigDecimal getValue();

    ReadOnlyObjectProperty<BigDecimal> valueProperty();

    Duration getDurationOfStorage();

    ReadOnlyObjectProperty<Duration> durationOfStorageProperty();

    ReadOnlySetProperty<Hazard> getHazards();

    Date getInspectionDate();

    ReadOnlyObjectProperty<Date> inspectionDateProperty();

    void inspect();
}
