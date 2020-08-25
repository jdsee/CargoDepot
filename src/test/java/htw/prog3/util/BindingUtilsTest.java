package htw.prog3.util;

import htw.prog3.util.BindingUtils;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.SetChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SuppressWarnings("ResultOfMethodCallIgnored")
class BindingUtilsTest {
    @Mock
    ListChangeListener<String> mockListChangeListener;
    @Mock
    SetChangeListener<Integer> mockSetChangeListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void createValuesProperty_returnsListWithAllTheValuesContainedInThePassedMap() {
        ObservableMap<Integer, String> obsMap = FXCollections.observableHashMap();
        obsMap.put(1, "x");
        obsMap.put(2, "y");

        ListProperty<String> obsVal = BindingUtils.createObservableValues(obsMap);

        assertThat(obsVal).containsExactly("x", "y");
    }

    @Test
    void createValuesProperty_shouldReturnListThatIsUpdatedWhenEntryIsAddedToBackingMap() {
        ObservableMap<Integer, String> obsMap = FXCollections.observableHashMap();

        ListProperty<String> obsVal = BindingUtils.createObservableValues(obsMap);

        obsMap.put(1, "x");
        assertThat(obsVal).containsExactly("x");
    }

    @Test
    void createValuesProperty_shouldReturnListThatIsUpdatedWhenEntryIsRemovedFromBackingMap() {
        ObservableMap<Integer, String> obsMap = FXCollections.observableHashMap();
        obsMap.put(1, "x");

        ListProperty<String> obsVal = BindingUtils.createObservableValues(obsMap);

        obsMap.remove(1);
        assertThat(obsVal).isEmpty();
    }

    @Test
    void createValuesProperty_shouldReturnListThatIsUpdatedWhenEntryIsReplacedInBackingMap() {
        ObservableMap<Integer, String> obsMap = FXCollections.observableHashMap();
        obsMap.put(1, "x");

        ListProperty<String> obsVal = BindingUtils.createObservableValues(obsMap);

        obsMap.replace(1, "y");
        assertThat(obsVal).containsExactly("y");
    }

    @Test
    void createValuesProperty_shouldReturnListThatNotifiesListenersWhenEntryIsAddedToBackingMap() {
        ObservableMap<Integer, String> obsMap = FXCollections.observableHashMap();

        ListProperty<String> obsVal = BindingUtils.createObservableValues(obsMap);

        obsVal.addListener(mockListChangeListener);
        obsMap.put(1, "x");
        verify(mockListChangeListener).onChanged(any());
    }

    @Test
    void createValuesProperty_shouldReturnListThatNotifiesListenersWhenEntryIsRemovedFromBackingMap() {
        ObservableMap<Integer, String> obsMap = FXCollections.observableHashMap();
        obsMap.put(1, "x");

        ListProperty<String> obsVal = BindingUtils.createObservableValues(obsMap);

        obsVal.addListener(mockListChangeListener);
        obsMap.remove(1);
        verify(mockListChangeListener).onChanged(any());
    }

    @Test
    void createValuesProperty_shouldReturnListThatNotifiesListenersWhenEntryIsReplacedInBackingMap() {
        ObservableMap<Integer, String> obsMap = FXCollections.observableHashMap();
        obsMap.put(1, "x");

        ListProperty<String> obsVal = BindingUtils.createObservableValues(obsMap);

        obsVal.addListener(mockListChangeListener);
        obsMap.replace(1, "y");
        verify(mockListChangeListener, times(2)).onChanged(any());
    }

    @Test
    void createObservableMirrorMap_shouldMapThatUpdatesWhenEntryIsAddedToSourceMap() {
        ObservableMap<Integer, String> source = FXCollections.observableHashMap();
        MapProperty<Integer, String> mirror = BindingUtils.createObservableMirrorMap(source);

        source.put(1, "x");

        assertThat(mirror).hasSize(1).containsAllEntriesOf(source);
    }

    @Test
    void createObservableMirrorMap_shouldMapThatUpdatesWhenEntryIsRemovedFromSourceMap() {
        ObservableMap<Integer, String> source = FXCollections.observableHashMap();
        MapProperty<Integer, String> mirror = BindingUtils.createObservableMirrorMap(source);

        source.remove(1);

        assertThat(mirror).isEmpty();
    }

    @Test
    void createObservableMirrorMap_shouldMapThatUpdatesWhenEntryIsReplacedInSourceMap() {
        ObservableMap<Integer, String> source = FXCollections.observableHashMap();
        MapProperty<Integer, String> mirror = BindingUtils.createObservableMirrorMap(source);
        source.put(1, "x");

        source.replace(1, "y");

        assertThat(mirror).hasSize(1).containsAllEntriesOf(source);
    }

    @Test
    void createObservableKeySet_shouldReturnSetThatNotifiesListenersWhenEntryIsAddedToBackingMap() {
        MapProperty<Integer, String> mapProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());

        SetProperty<Integer> obsKeys = BindingUtils.createObservableKeySet(mapProperty);

        obsKeys.addListener(mockSetChangeListener);
        mapProperty.put(100, "x");
        verify(mockSetChangeListener).onChanged(any());
        assertThat(obsKeys).containsExactly(100);
    }

    @Test
    void createObservableKeySet_shouldReturnSetThatNotifiesListenersWhenEntryIsRemovedFromBackingMap() {
        MapProperty<Integer, String> mapProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
        mapProperty.put(100, "x");

        SetProperty<Integer> obsKeys = BindingUtils.createObservableKeySet(mapProperty);

        obsKeys.addListener(mockSetChangeListener);
        mapProperty.remove(100, "x");
        verify(mockSetChangeListener).onChanged(any());
        assertThat(obsKeys).isEmpty();
    }

    @Test
    void createObservableKeySet_shouldReturnSetThatDoesNotNotifyListenersWhenEntryIsReplacedInBackingMap() {
        MapProperty<Integer, String> mapProperty = new SimpleMapProperty<>(FXCollections.observableHashMap());
        mapProperty.put(100, "x");

        SetProperty<Integer> obsKeys = BindingUtils.createObservableKeySet(mapProperty);

        obsKeys.addListener(mockSetChangeListener);
        mapProperty.replace(100, "x");
        verifyNoInteractions(mockSetChangeListener);
        assertThat(obsKeys).containsExactly(100);
    }
}