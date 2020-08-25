package htw.prog3.ui.cli.control;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.routing.input.delete.cargo.RemoveCargoEvent;
import htw.prog3.routing.input.delete.customer.RemoveCustomerEvent;

class DeleteState extends CommandLineReaderState {
    public static final String PROMPT_NAME = "delete";

    private DeleteState() {
        super(PROMPT_NAME);
    }

    public static DeleteState getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    protected void utilizeCmd(String input, CommandLineReader context) {
        if (isDigit(input)) {
            try {
                int storagePosition = Integer.parseInt(input);
                RemoveCargoEvent event = new RemoveCargoEvent(storagePosition, context);
                context.fireDeleteCargoEvent(event);
            } catch (NumberFormatException e) {
                context.fireIllegalInputEvent(InputFailureMessages.UNKNOWN_STORAGE_POSITION);
            }
        } else {
            RemoveCustomerEvent event = new RemoveCustomerEvent(input, context);
            context.fireDeleteCustomerEvent(event);
        }
    }

    @Override
    protected boolean isValidCmd(String input) {
        return ValidationPattern.SINGLE_WORD.matcher(input).matches();
    }

    private static final class InstanceHolder {
        static final DeleteState INSTANCE = new DeleteState();
    }

    private boolean isDigit(String input) {
        return ValidationPattern.DIGIT.matcher(input).matches();
    }
}
