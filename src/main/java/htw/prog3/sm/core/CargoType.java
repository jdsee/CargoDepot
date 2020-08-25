package htw.prog3.sm.core;

import htw.prog3.routing.error.InputFailureMessages;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum CargoType {
    CARGO_BASE_TYPE(0, "Cargo Base Type"),
    UNITISED_CARGO(1, "Unitised Cargo"),
    LIQUID_BULK_CARGO(2, "Liquid Bulk Cargo"),
    MIXED_CARGO_LIQUID_BULK_AND_UNITISED(3, "Mixed Cargo");

    private final int value;
    private final String description;

    CargoType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static CargoType from(int value) {
        return Arrays.stream(values())
                .filter(type -> Objects.equals(type.intValue(), value))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * @param type String that is to be converted into CargoType.
     * @return The matching CargoType
     * @throws IllegalArgumentException If the specified string can't be mapped on a CargoType.
     */
    public static CargoType parseCargoType(String type) {
        switch (type.toLowerCase()) {
            case "uc":
            case "unitisedcargo":
            case "unitised_cargo":
                return UNITISED_CARGO;
            case "lbc":
            case "liquidbulkcargo":
            case "liquid_bulk_cargo":
                return LIQUID_BULK_CARGO;
            case "lbuc":
            case "mclbau":
            case "mixedcargoliquidbulkandunitised":
            case "mixed_cargo_liquid_bulk_and_unitised":
                return MIXED_CARGO_LIQUID_BULK_AND_UNITISED;
            default:
                throw new IllegalArgumentException(InputFailureMessages.UNKNOWN_CARGO_TYPE);
        }
    }

    public static List<CargoType> validValues() {
        return Arrays.stream(values())
                .filter(type -> !CARGO_BASE_TYPE.equals(type))
                .collect(Collectors.toList());
    }

    public int intValue() {
        return value;
    }

    @Override
    public String toString() {
        return description;
    }
}
