package htw.prog3.ui.cli.control;

import htw.prog3.routing.error.InputFailureMessages;
import htw.prog3.ui.cli.view.listener.CriticalCapacityListener;
import htw.prog3.ui.cli.view.listener.HazardChangeListener;

public class ConfigState extends CommandLineReaderState {
    private static final String PROMPT_NAME = "config";
    private static final String ADD_CMD = "add";
    private static final String REMOVE_CMD = "remove";
    public static final String CRITICAL_CAPACITY_LISTENER_CLASS_NAME = "criticalcapacitylistener";
    private static final String ADD_CARGO_EVENT_LISTENER_CLASS_NAME = "addcargoeventlistener";
    private static final String HAZARD_CHANGE_LISTENER_CLASS_NAME = "hazardchangelistener";

    private ConfigState() {
        super(PROMPT_NAME);
    }

    public static ConfigState getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected void utilizeCmd(String input, CommandLineReader context) {
        String[] split = ValidationPattern.WHITESPACE_SEQUENCE.split(input.trim());
        switch (split[0]) {
            case ADD_CMD:
                handleCmd(split[1], true, context);
                break;
            case REMOVE_CMD:
                handleCmd(split[1], false, context);
                break;
            default:
                context.fireIllegalInputEvent(InputFailureMessages.BAD_ARGUMENTS);
        }
    }

    @Override
    protected boolean isValidCmd(String input) {
        return ValidationPattern.TWO_WORDS.matcher(input).matches();
    }

    private void handleCmd(String arg, boolean activation, CommandLineReader context) {
        switch (arg.toLowerCase()) {
            case ADD_CARGO_EVENT_LISTENER_CLASS_NAME:
                if (activation)
                    context.activateAddCargoEventListener();
                else context.deactivateAddCargoEventListener();
                break;
            case HAZARD_CHANGE_LISTENER_CLASS_NAME:
                context.fireViewConfigEvent(HazardChangeListener.class, activation);
                break;
            case CRITICAL_CAPACITY_LISTENER_CLASS_NAME:
                context.fireViewConfigEvent(CriticalCapacityListener.class, activation);
                break;
            default:
                context.fireIllegalInputEvent(InputFailureMessages.UNKNOWN_LISTENER);
        }
    }

    private static final class SingletonHolder {
        static final ConfigState INSTANCE = new ConfigState();
    }
}
