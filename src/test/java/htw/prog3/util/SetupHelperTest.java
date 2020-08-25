package htw.prog3.util;

import htw.prog3.sm.core.Storage;
import htw.prog3.util.SetupHelper;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class SetupHelperTest {

    @Test
    void readCapacity_shouldReturnDefaultForBadInput() {
        int actual = SetupHelper.readCapacity("abc");

        assertThat(actual).isEqualTo(Storage.DEFAULT_CAPACITY);
    }

    @Test
    void readCapacity_shouldReturnIntCapacityForValidDigit() {
        int actual = SetupHelper.readCapacity("123456");

        assertThat(actual).isEqualTo(123456);
    }

    @Test
    void readLogLang_shouldReturnGermanLocaleForBln() {
        Locale actual = SetupHelper.readLogLang("bln");

        assertThat(actual).isEqualTo(Locale.GERMAN);
    }
}