package htw.prog3.ui.cli.control;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.routing.input.listRequest.cargos.ListCargosReqEvent;
import htw.prog3.routing.input.listRequest.customers.ListCustomersReqEvent;
import htw.prog3.routing.input.listRequest.hazards.ListHazardsReqEvent;
import htw.prog3.sm.core.CargoType;

import java.util.regex.Matcher;

class PresentationState extends CommandLineReaderState {
    public static final String PROMPT_NAME = "read";
    private static final String CUSTOMER_STRING = "customer";
    private static final String CARGO_STRING = "cargo";
    private static final String HAZARD_STRING = "hazard";
    private static final String INCLUSIVE_FLAG = "i";
    private static final String EXCLUSIVE_FLAG = "e";

    private PresentationState() {
        super(PROMPT_NAME);
    }

    public static PresentationState getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    protected void utilizeCmd(String input, CommandLineReader context) {
        String[] splitUserInput = ValidationPattern.WHITESPACE_SEQUENCE.split(input.toLowerCase().trim());
        try {
            switch (splitUserInput[0]) {
                case CUSTOMER_STRING:
                    ListCustomersReqEvent customersReqEvent = new ListCustomersReqEvent(context);
                    context.fireListCustomersReqEvent(customersReqEvent);
                    break;
                case CARGO_STRING:
                    ListCargosReqEvent cargosReqEvent = createRequestCargoViewEvent(splitUserInput, context);
                    context.fireListCargosReqEvent(cargosReqEvent);
                    break;
                case HAZARD_STRING:
                    ListHazardsReqEvent hazardsReqEvent = createRequestHazardViewEvent(splitUserInput, context);
                    context.fireListHazardsReqEvent(hazardsReqEvent);
                    break;
                default:
                    context.fireIllegalInputEvent(InputFailureMessages.BAD_ARGUMENTS);
            }
        } catch (IllegalArgumentException e) {
            context.fireIllegalInputEvent(e.getMessage());
        }
    }

    @Override
    protected boolean isValidCmd(String input) {
        Matcher matchSingleWord = ValidationPattern.SINGLE_WORD.matcher(input);
        Matcher matchTwoWords = ValidationPattern.TWO_WORDS.matcher(input);
        return matchSingleWord.matches() || matchTwoWords.matches();
    }

    private ListCargosReqEvent createRequestCargoViewEvent(String[] input, CommandLineReader context) {
        CargoType type = input.length > 1 ? CargoType.parseCargoType(input[1]) : CargoType.CARGO_BASE_TYPE;
        return new ListCargosReqEvent(type, context);
    }

    private ListHazardsReqEvent createRequestHazardViewEvent(String[] input, CommandLineReader context) {
        boolean inclusiveRequested = input.length <= 1 || parseBoolean(input[1], INCLUSIVE_FLAG, EXCLUSIVE_FLAG,
                InputFailureMessages.PARSING_INCLUSIVE_HAZARDS_REQUESTED_FAILED);
        return new ListHazardsReqEvent(inclusiveRequested, context);
    }

    private static final class InstanceHolder {
        static final PresentationState INSTANCE = new PresentationState();
    }
}