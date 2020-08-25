package htw.prog3.ui.cli.control;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.routing.persistence.all.PersistenceType;

import java.util.function.Consumer;

public class PersistenceState extends CommandLineReaderState {
    public static final String SAVE_JOS_CMD = "saveJOS";
    public static final String SAVE_JBP_CMD = "saveJBP";
    public static final String SAVE_ITEM_CMD = "save";
    private static final String PROMPT_NAME = "persistence";
    public static final String LOAD_JOS_CMD = "loadJOS";
    public static final String LOAD_JBP_CMD = "loadJBP";
    public static final String LOAD_ITEM_CMD = "load";

    private PersistenceState() {
        super(PROMPT_NAME);
    }

    public static PersistenceState getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    protected void utilizeCmd(String input, CommandLineReader context) {
        String[] split = ValidationPattern.WHITESPACE_SEQUENCE.split(input.trim());
        switch (split[0]) {
            case SAVE_JOS_CMD:
                context.fireSaveAllEvent(PersistenceType.JOS);
                break;
            case SAVE_JBP_CMD:
                context.fireSaveAllEvent(PersistenceType.JBP);
                break;
            case SAVE_ITEM_CMD:
                handleItemCmd(split, context, context::fireSaveItemEvent);
                break;
            case LOAD_JOS_CMD:
                context.fireLoadAllEvent(PersistenceType.JOS);
                break;
            case LOAD_JBP_CMD:
                context.fireLoadAllEvent(PersistenceType.JBP);
                break;
            case LOAD_ITEM_CMD:
                handleItemCmd(split, context, context::fireLoadItemEvent);
                break;
            default:
                fireBadArgsEvent(context);
        }
    }

    private void handleItemCmd(String[] args, CommandLineReader context, Consumer<Integer> callback) {
        if (args.length > 1 && ValidationPattern.DIGIT.matcher(args[1]).matches()) {
            int position = Integer.parseInt(args[1]);
            callback.accept(position);
        } else
            fireBadArgsEvent(context);
    }

    private void fireBadArgsEvent(CommandLineReader context) {
        context.fireIllegalInputEvent(InputFailureMessages.BAD_ARGUMENTS);
    }

    @Override
    protected boolean isValidCmd(String input) {
        return ValidationPattern.SINGLE_WORD.matcher(input).matches() ||
                ValidationPattern.SINGLE_WORD_FOLLOWED_BY_DIGIT.matcher(input).matches();
    }

    private static final class InstanceHolder {
        static final PersistenceState INSTANCE = new PersistenceState();
    }
}
