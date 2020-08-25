package htw.prog3.log;

import java.io.Closeable;
import java.io.IOException;

public class SmLogManager implements Closeable {
    private final InteractionLogger interactionLogger;
    private final ProcessLogger processLogger;

    private SmLogManager(InteractionLogger interactionLogger, ProcessLogger processLogger) {
        this.interactionLogger = interactionLogger;
        this.processLogger = processLogger;
    }

    public static SmLogManager from(InteractionLogger interactionLogger, ProcessLogger processLogger) {
        return new SmLogManager(interactionLogger, processLogger);
    }

    @Override
    public void close() throws IOException {
        interactionLogger.close();
        processLogger.close();
    }
}
