package htw.prog3.ui.cli.control;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.routing.success.ActionSuccessMessages;

import java.util.regex.Matcher;

abstract class CommandLineReaderState {
    private static final char COLON_CHAR = ':';
    public static final String PERSISTENCE_STATE_CMD = "p";
    public static final String CONFIG_STATE_CMD = "config";
    private static final String CREATE_STATE_CMD = "c";
    private static final String PRESENTATION_STATE_CMD = "r";
    private static final String UPDATE_STATE_CMD = "u";
    private static final String DELETE_STATE_CMD = "d";
    private final String promptName;

    public CommandLineReaderState(String promptName) {
        this.promptName = promptName;
    }

    final void processCmd(String input, CommandLineReader context) {
        input = input.trim();
        if (isStateChangeRequest(input))
            tryStateChange(input, context);
        else if (!isValidCmd(input))
            context.fireIllegalInputEvent(InputFailureMessages.BAD_ARGUMENTS);
        else
            utilizeCmd(input, context);
    }

    private boolean isStateChangeRequest(String input) {
        return input.length() > 0 && input.charAt(0) == COLON_CHAR;
    }

    private void tryStateChange(String input, CommandLineReader context) {
        CommandLineReaderState newState = null;
        Matcher matcher = ValidationPattern.STATE_CHANGE_COMMAND.matcher(input);
        if (matcher.find()) {
            String requested = matcher.group(1);
            newState = parseState(requested);
        }
        if (null == newState)
            context.fireIllegalInputEvent(InputFailureMessages.UNKNOWN_STATE);
        else if (!context.getActualState().equals(newState)) {
            context.setActualState(newState);
            context.fireActionSuccessEvent(ActionSuccessMessages.STATE_CHANGE_SUCCEEDED);
        }
    }

    private CommandLineReaderState parseState(String s) {
        switch (s) {
            case CREATE_STATE_CMD:
                return CreateState.getInstance();
            case PRESENTATION_STATE_CMD:
                return PresentationState.getInstance();
            case UPDATE_STATE_CMD:
                return UpdateState.getInstance();
            case DELETE_STATE_CMD:
                return DeleteState.getInstance();
            case PERSISTENCE_STATE_CMD:
                return PersistenceState.getInstance();
            case CONFIG_STATE_CMD:
                return ConfigState.getInstance();
            default:
                // not testable: ValidationPattern in tryStateChange(input, context) catches any left cases
                return null;
        }
    }

    /**
     * This method must be implemented to syntactically validate the user input
     * so that the parsing can be omitted if it's not valid.
     * A precompiled regex is suggested for this use case.
     *
     * @param input The user input.
     * @return {@code true} if the input is valid, {@code false} otherwise.
     */
    protected abstract boolean isValidCmd(String input);

    protected abstract void utilizeCmd(String input, CommandLineReader context);

    final String getPromptName() {
        return promptName;
    }

    protected boolean parseBoolean(String input, String trueSymbol, String falseSymbol, String errorMessage) {
        input = input.toLowerCase();
        if (input.equals(trueSymbol))
            return true;
        else if (input.equals(falseSymbol))
            return false;
        else throw new IllegalArgumentException(errorMessage);
    }
}
