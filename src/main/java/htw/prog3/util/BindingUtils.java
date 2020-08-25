package htw.prog3.util;

import javafx.beans.property.*;
import javafx.collections.*;

import java.util.HashSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class BindingUtils {
    private BindingUtils() {
    }

    public static <K, V> ListProperty<V> createObservableValues(ObservableMap<K, V> source) {
        return createObservableValues(source, Function.identity());
    }

    public static <K, V, E> ListProperty<E> createObservableValues(ObservableMap<K, V> source,
                                                                   Function<V, E> valueConverter) {
        ObservableList<E> obsVal = source.values().stream()
                .map(valueConverter)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        ListProperty<E> target = new SimpleListProperty<>(obsVal);
        source.addListener((MapChangeListener<? super K, ? super V>) change -> {
            if (change.wasRemoved())
                target.remove(valueConverter.apply(change.getValueRemoved()));
            if (change.wasAdded())
                target.add(valueConverter.apply(change.getValueAdded()));
        });
        return target;
    }

    public static <K, V> MapProperty<K, V> createObservableMirrorMap(ObservableMap<K, V> source) {
        ObservableMap<K, V> obsMap = FXCollections.observableHashMap();
        MapProperty<K, V> target = new SimpleMapProperty<>(obsMap);
        target.putAll(source);
        source.addListener((MapChangeListener<K, V>) change -> {
            if (change.wasRemoved())
                target.remove(change.getKey());
            if (change.wasAdded())
                target.put(change.getKey(), change.getValueAdded());
        });
        return target;
    }

    public static <K, V> SetProperty<K> createObservableKeySet(ObservableMap<K, V> source) {
        ObservableSet<K> obsKeys = FXCollections.observableSet(new HashSet<>());
        SetProperty<K> target = new SimpleSetProperty<>(obsKeys);
        target.addAll(source.keySet());
        source.addListener((MapChangeListener<? super K, ? super V>) change -> {
            if (change.wasRemoved() && !change.wasAdded())
                target.remove(change.getKey());
            if (change.wasAdded() && !change.wasRemoved())
                target.add(change.getKey());
        });
        return target;
    }
}