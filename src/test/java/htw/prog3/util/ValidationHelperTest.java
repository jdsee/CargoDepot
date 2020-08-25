package htw.prog3.util;

import htw.prog3.sm.util.ValidationHelper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ValidationHelperTest {

    @Test
    void requireNonNullConstructorArgs_shouldThrowNullPointerExceptionForNullArg() {
        Throwable t = catchThrowable(() -> ValidationHelper.requireNonNullConstructorArgs(this.getClass(), "valid", null));

        assertThat(t).isInstanceOf(NullPointerException.class)
                .hasMessage(String.format("Null Values not permitted for instantiation of '%s'.", this.getClass()));
    }
}