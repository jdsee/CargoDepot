package htw.prog3.sm.util;

import org.apache.commons.lang3.Validate;

public final class ValidationHelper {
    private ValidationHelper() {
    }

    public static void requireNonNullConstructorArgs(Class<?> context, Object... args) {
        for (Object arg : args)
            Validate.notNull(arg, "Null Values not permitted for instantiation of '%s'.", context);
    }
}
