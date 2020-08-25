package htw.prog3.ui.cli.control;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.routing.input.update.inspect.InspectCargoEvent;

class UpdateState extends CommandLineReaderState {
    public static final String PROMPT_NAME = "update";

    private UpdateState() {
        super(PROMPT_NAME);
    }

    public static UpdateState getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    protected void utilizeCmd(String input, CommandLineReader context) {
        try {
            int storagePosition = Integer.parseInt(input);
            InspectCargoEvent event = new InspectCargoEvent(storagePosition, context);
            context.fireInspectCargoEvent(event);
        } catch (NumberFormatException e) {
            context.fireIllegalInputEvent(InputFailureMessages.UNKNOWN_STORAGE_POSITION);
        }
    }

    @Override
    protected boolean isValidCmd(String input) {
        return ValidationPattern.DIGIT.matcher(input).matches();
    }

    private static final class InstanceHolder {
        static final UpdateState INSTANCE = new UpdateState();
    }
}
