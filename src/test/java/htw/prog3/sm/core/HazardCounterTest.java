package htw.prog3.sm.core;

import htw.prog3.storageContract.cargo.Hazard;
import javafx.beans.property.ReadOnlySetProperty;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class HazardCounterTest {
    @Test
    void create_shouldReturnNewInstance() {
        HazardCounter counter = HazardCounter.create();
        HazardCounter other = HazardCounter.create();

        assertThat(counter).isNotNull().isNotSameAs(other);
    }

    @Test
    void getPresentHazards_shouldReturnSetThatUpdatesWhenHazardIsAdded() {
        HazardCounter counter = HazardCounter.create();
        Set<Hazard> hazards = new HashSet<>(singletonList(Hazard.TOXIC));

        ReadOnlySetProperty<Hazard> actualHazards = counter.getPresentHazards();

        counter.addHazards(hazards);
        assertThat(actualHazards).containsExactly(Hazard.TOXIC);
    }

    @Test
    void getPresentHazards_shouldReturnSetThatUpdatesWhenHazardIsRemoved() {
        HazardCounter counter = HazardCounter.create();
        Set<Hazard> hazards = new HashSet<>(singletonList(Hazard.TOXIC));
        counter.addHazards(hazards);

        ReadOnlySetProperty<Hazard> actualHazards = counter.getPresentHazards();

        counter.removeHazards(hazards);
        assertThat(actualHazards).isEmpty();
    }

    @Test
    void getPresentHazards_shouldReturnSetThatCanNotMutateTheBackingData() {
        HazardCounter counter = HazardCounter.create();
        Set<Hazard> hazards = new HashSet<>(singletonList(Hazard.TOXIC));
        counter.addHazards(hazards);

        ReadOnlySetProperty<Hazard> actualHazards = counter.getPresentHazards();

        actualHazards.clear();
        assertThat(counter.getPresentHazards()).isNotEmpty();
    }

    @Test
    void addPotentialHazards_shouldAddSpecifiedHazards() {
        HazardCounter counter = HazardCounter.create();
        Set<Hazard> hazards = new HashSet<>(asList(Hazard.TOXIC, Hazard.EXPLOSIVE));

        counter.addHazards(hazards);

        assertThat(counter.getPresentHazards()).containsExactlyInAnyOrder(Hazard.TOXIC, Hazard.EXPLOSIVE);
    }

    @Test
    void removePotentialHazards_shouldRemoveSpecifiedHazardsOnlyIfNoMoreInstancesWhereCounted() {
        HazardCounter counter = HazardCounter.create();
        Set<Hazard> hazards = new HashSet<>(asList(Hazard.TOXIC, Hazard.EXPLOSIVE));
        counter.addHazards(hazards);
        Set<Hazard> toxic = new HashSet<>(singletonList(Hazard.TOXIC));
        counter.addHazards(toxic);

        counter.removeHazards(hazards);

        assertThat(counter.getPresentHazards()).containsExactly(Hazard.TOXIC);
    }
}