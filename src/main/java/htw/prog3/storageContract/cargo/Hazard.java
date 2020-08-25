package htw.prog3.storageContract.cargo;

public enum Hazard {
    EXPLOSIVE("Explosive"),
    FLAMMABLE("Flammable"),
    TOXIC("Toxic"),
    RADIOACTIVE("Radioactive");

    private final String description;

    Hazard(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return description;
    }
}
