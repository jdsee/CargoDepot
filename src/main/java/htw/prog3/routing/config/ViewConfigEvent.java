package htw.prog3.routing.config;

import java.util.EventObject;

public class ViewConfigEvent extends EventObject {
    private final Class<? extends ViewConfigEventListener> type;
    private final boolean activation;

    public ViewConfigEvent(Class<? extends ViewConfigEventListener> target, boolean activation, Object source) {
        super(source);
        this.type = target;
        this.activation = activation;
    }

    public Class<? extends ViewConfigEventListener> getType() {
        return type;
    }

    public boolean isActivation() {
        return activation;
    }
}
