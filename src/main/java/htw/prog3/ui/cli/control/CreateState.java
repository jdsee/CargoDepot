package htw.prog3.ui.cli.control;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.routing.input.create.cargo.AddCargoEvent;
import htw.prog3.routing.input.create.customer.AddCustomerEvent;
import htw.prog3.sm.core.CargoType;
import htw.prog3.storageContract.cargo.Hazard;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

class CreateState extends CommandLineReaderState {
    public static final String PROMPT_NAME = "create";

    private CreateState() {
        super(PROMPT_NAME);
    }

    /**
     * @throws IllegalArgumentException If any input could not been parsed.
     */
    private AddCargoEvent createAddCargoEvent(String[] input, CommandLineReader context) {
        CargoType type = CargoType.parseCargoType(input[0]);
        String owner = input[1];
        BigDecimal value = parseValue(input[2]);
        Duration durationOfStorage = parseDurationOfStorage(input[3]);
        Set<Hazard> hazards = parseHazards(input[4]);
        boolean isFragile = parseBoolean(input[5], "y", "n", InputFailureMessages.PARSING_IS_PRESSURIZED_FAILED);
        boolean isPressurized = parseBoolean(input[6], "y", "n", InputFailureMessages.PARSING_IS_FRAGILE_FAILED);
        return new AddCargoEvent(type, owner, value, durationOfStorage, hazards, isPressurized, isFragile, context);
    }

    public static CreateState getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    protected void utilizeCmd(String input, CommandLineReader context) {
        String[] split = splitUserInput(input);
        int argLength = split.length;
        try {
            if (argLength == 1) {
                AddCustomerEvent event = createAddCustomerEvent(split, context);
                context.fireAddCustomerEvent(event);
            } else if (argLength > 5 && argLength < 9) {
                AddCargoEvent event = createAddCargoEvent(split, context);
                context.fireAddCargoEvent(event);
            } else {
                context.fireIllegalInputEvent(InputFailureMessages.BAD_ARGUMENTS);
            }
        } catch (IllegalArgumentException e) {
            context.fireIllegalInputEvent(e.getMessage());
        }
    }

    /**
     * This methods validates the user input with a regex.
     * The validation accepts following patterns with any number of whitespace in between:
     * ADD CUSTOMER   - 'word'
     * ADD CARGO      - 'word word decimal decimal ([,] OR [cs hazards](max. 4)) boolean boolean'
     *
     * @param input The user input that is to be validated.
     * @return @code{true} if the user input is valid or @code{false} else.
     */
    @Override
    protected boolean isValidCmd(String input) {
        Matcher addCustomer = ValidationPattern.SINGLE_WORD.matcher(input);
        Matcher addCargo = ValidationPattern.ADD_CARGO_SYNTAX.matcher(input);
        return addCustomer.matches() || addCargo.matches();
    }

    /**
     * Deletes all space between the comma separated hazards if they are present.
     *
     * @param input The input that is to be split.
     * @return An array with the split user input.
     */
    private String[] splitUserInput(String input) {
        Matcher matcher = ValidationPattern.MULTIPLE_HAZARDS_CONTAINED.matcher(input);
        if (matcher.find()) {
            Matcher wordFollowedByComma = ValidationPattern.GROUP1_WORD_FOLLOWED_BY_COMMA.matcher(input);
            input = wordFollowedByComma.replaceAll("$1,");
        }
        return ValidationPattern.WHITESPACE_SEQUENCE.split(input.trim());
    }

    private AddCustomerEvent createAddCustomerEvent(String[] input, CommandLineReader context) {
        return new AddCustomerEvent(input[0], context);
    }

    private static final class InstanceHolder {
        public static final CreateState instance = new CreateState();
    }

    private BigDecimal parseValue(String input) {
        BigDecimal value;
        try {
            value = new BigDecimal(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(InputFailureMessages.PARSING_VALUE_FAILED);
        }
        return value;
    }

    private Duration parseDurationOfStorage(String input) {
        try {
            long duration = Long.parseLong(input);
            return Duration.ofSeconds(duration);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(InputFailureMessages.PARSING_DURATION_FAILED);
        }
    }

    private Set<Hazard> parseHazards(String input) {
        Set<Hazard> hazards = new HashSet<>();
        String[] splitInput = input.toUpperCase().split(",");
        for (String split : splitInput) {
            try {
                hazards.add(Hazard.valueOf(split));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(InputFailureMessages.UNKNOWN_HAZARD_TYPE);
            }
        }
        return hazards;
    }
}