package htw.prog3.storageContract.cargo;

import htw.prog3.storageContract.cargo.Hazard;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HazardTest {

    @Test
    void toString_returnsDescriptionForExplosive() {
        String actual = Hazard.EXPLOSIVE.toString();

        assertThat(actual).isEqualTo("Explosive");
    }

    @Test
    void toString_returnsDescriptionForFlammable() {
        String actual = Hazard.FLAMMABLE.toString();

        assertThat(actual).isEqualTo("Flammable");
    }

    @Test
    void toString_returnsDescriptionForRadioactive() {
        String actual = Hazard.RADIOACTIVE.toString();

        assertThat(actual).isEqualTo("Radioactive");
    }

    @Test
    void toString_returnsDescriptionForToxic() {
        String actual = Hazard.TOXIC.toString();

        assertThat(actual).isEqualTo("Toxic");
    }
}