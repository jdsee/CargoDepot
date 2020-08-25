package htw.prog3.util;

import htw.prog3.sm.core.Storage;
import htw.prog3.ui.cli.control.ValidationPattern;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public final class SetupHelper {
    public static final String LOG_LANG_BLN = "bln";

    private SetupHelper() {
    }

    public static int readCapacity(String param) {
        return (param != null && ValidationPattern.DIGIT.matcher(param).matches()) ?
                Integer.parseInt(param) :
                Storage.DEFAULT_CAPACITY;
    }

    // not testable: access on file system
    public static FileOutputStream readLogPath(String param) {
        if (param != null) {
            File logFile = new File(param);
            try {
                if (logFile.exists() || logFile.createNewFile()) {
                    System.out.printf("The session will be logged in file '%s'", logFile.getAbsolutePath());
                    return new FileOutputStream(logFile);
                }
            } catch (IOException e) {
                System.out.printf("The file '%s' does not exist and can't be created.", param);
            }
        }
        return null;
    }

    public static Locale readLogLang(String param) {
        return param.equals(LOG_LANG_BLN) ? Locale.GERMAN : new Locale(param);
    }
}
