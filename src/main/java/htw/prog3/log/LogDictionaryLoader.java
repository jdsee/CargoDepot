package htw.prog3.log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.function.BiFunction;

public final class LogDictionaryLoader {
    public static final String INTERACTION_LOG = "interaction";
    public static final String PROCESS_LOG = "process";

    private final Locale locale;
    private final ObjectMapper mapper;
    private final BiFunction<Class<?>, String, URL> resourceSupplier;

    LogDictionaryLoader(Locale locale, ObjectMapper mapper,
                        BiFunction<Class<?>, String, URL> resourceSupplier) {
        this.locale = locale;
        this.mapper = mapper;
        this.resourceSupplier = resourceSupplier;
    }

    public static LogDictionaryLoader from(Locale locale) {
        return new LogDictionaryLoader(locale, new ObjectMapper(), Class::getResource);
    }

    public InteractionLogDictionary loadInteractionLogDictionary() throws IOException {
        return loadLogDictionary(INTERACTION_LOG, InteractionLogDictionary.class);
    }

    public ProcessLogDictionary loadProcessLogDictionary() throws IOException {
        return loadLogDictionary(PROCESS_LOG, ProcessLogDictionary.class);
    }

    private <T> T loadLogDictionary(String logType, Class<T> logTypeClass) throws IOException {
        URL resource = resolveLocalResource(logType);
        return mapper.readValue(resource, logTypeClass);
    }

    /**
     * @return Matching log dictionary for this locale or default english dictionary if no resource matches.
     */
    private URL resolveLocalResource(String type) {
        URL resource = resolveLocalResource(type, locale);
        return (resource != null) ? resource : resolveLocalResource(type, Locale.ENGLISH);
    }

    private URL resolveLocalResource(String type, Locale locale) {
        String lang = locale.getLanguage();
        String name = String.format("/log/dict/%s_msg_formats_%s.json", type, lang.toUpperCase());
        return resourceSupplier.apply(this.getClass(), name);
    }
}