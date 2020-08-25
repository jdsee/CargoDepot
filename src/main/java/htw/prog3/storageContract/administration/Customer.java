package htw.prog3.storageContract.administration;

import htw.prog3.sm.core.CustomerImpl;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;

public interface Customer extends Serializable {
    BigDecimal DEFAULT_MAX_VALUE = BigDecimal.valueOf(10_000);
    Duration DEFAULT_MAX_DURATION_OF_STORAGE = Duration.ofDays(356);

    static Customer from(String name) {
        return new CustomerImpl(name);
    }

    String getName();

    ReadOnlyStringProperty nameProperty();

    BigDecimal getMaxValue();

    ReadOnlyObjectProperty<BigDecimal> maxValueProperty();

    Duration getMaxDurationOfStorage();

    ReadOnlyObjectProperty<Duration> maxDurationOfStorageProperty();
}
