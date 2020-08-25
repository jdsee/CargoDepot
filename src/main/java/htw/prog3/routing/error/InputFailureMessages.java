package htw.prog3.routing.error;

public class InputFailureMessages {
    public static final String UNKNOWN_CARGO_TYPE = "The specified cargo type is either not known or syntactically incorrect.";
    public static final String UNKNOWN_HAZARD_TYPE = "The specified hazard is either not known or syntactically incorrect.";
    public static final String UNKNOWN_STORAGE_POSITION = "The specified storage position is not known.";
    public static final String UNKNOWN_STATE = "The specified state is either not known or syntactically incorrect.";

    public static final String PARSING_VALUE_FAILED = "The specified value is syntactically incorrect.";
    public static final String PARSING_DURATION_FAILED = "The specified duration of storage is syntactically incorrect.";
    public static final String PARSING_IS_PRESSURIZED_FAILED = "The specified pressurized boolean is syntactically incorrect.";
    public static final String PARSING_IS_FRAGILE_FAILED = "The specified fragile boolean is syntactically incorrect.";
    public static final String PARSING_INCLUSIVE_HAZARDS_REQUESTED_FAILED = "The specified included-hazards flag is syntactically incorrect.";

    public static final String BAD_ARGUMENTS = "The specified command does not match the correct input format";
    public static final String IO_FAIL = "Problem with reading input.";
    public static final String UNKNOWN_LISTENER = "The specified listener is not known.";
}
