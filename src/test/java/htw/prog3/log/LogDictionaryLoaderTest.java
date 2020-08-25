package htw.prog3.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import htw.prog3.log.InteractionLogDictionary;
import htw.prog3.log.LogDictionaryLoader;
import htw.prog3.log.ProcessLogDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

class LogDictionaryLoaderTest {
    @Mock
    ObjectMapper mockObjMapper;
    @Mock
    URL mockURL;
    @Mock
    InteractionLogDictionary mockInteractionDictionary;
    @Mock
    private ProcessLogDictionary mockProcessDictionary;
    private String stringCapture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void from_shouldReturnNewInstance() {
        LogDictionaryLoader loader = LogDictionaryLoader.from(Locale.ENGLISH);
        LogDictionaryLoader other = LogDictionaryLoader.from(Locale.ENGLISH);

        assertThat(loader).isNotSameAs(other);
    }

    @Test
    void loadInteractionLogDictionary_shouldReturnInstanceCreatedByObjectMapper() throws IOException {
        doReturn(mockInteractionDictionary).when(mockObjMapper).readValue(mockURL, InteractionLogDictionary.class);
        LogDictionaryLoader loader = new LogDictionaryLoader(Locale.ENGLISH, mockObjMapper, (c, s) -> mockURL);

        InteractionLogDictionary dictionary = loader.loadInteractionLogDictionary();

        assertThat(dictionary).isEqualTo(mockInteractionDictionary);
    }

    /*
     The following tests know too much about the implementation of the tested class.
     The special constructor call also seams to be not reasonable.

     It took me a lot of time to find any solution at all to test this class without accessing the file system.
     This solution works, so I leave it at that.
     */

    @Test
    void loadProcessLogDictionary_shouldReturnInstanceCreatedByObjectMapper() throws IOException {
        doReturn(mockProcessDictionary).when(mockObjMapper).readValue(mockURL, ProcessLogDictionary.class);
        LogDictionaryLoader loader = new LogDictionaryLoader(Locale.ENGLISH, mockObjMapper, (c, s) -> mockURL);

        ProcessLogDictionary dictionary = loader.loadProcessLogDictionary();

        assertThat(dictionary).isEqualTo(mockProcessDictionary);
    }

    @Test
    void loadInteractionLogDictionary_shouldResolveEnglishLocale() throws IOException {
        LogDictionaryLoader loader = new LogDictionaryLoader(Locale.ENGLISH, mockObjMapper, (c, s) -> {
            stringCapture = s;
            return mockURL;
        });

        loader.loadInteractionLogDictionary();

        assertThat(stringCapture).contains("/log/dict/interaction_msg_formats_EN.json");
    }

    @Test
    void loadInteractionLogDictionary_shouldResolveGermanLocale() throws IOException {
        LogDictionaryLoader loader = new LogDictionaryLoader(Locale.GERMAN, mockObjMapper, (c, s) -> {
            stringCapture = s;
            return mockURL;
        });

        loader.loadProcessLogDictionary();

        assertThat(stringCapture).contains("/log/dict/process_msg_formats_DE.json");
    }
}