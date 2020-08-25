package htw.prog3.persistence.ra;

import htw.prog3.sm.core.CargoFactory;
import htw.prog3.sm.core.CargoType;
import htw.prog3.sm.core.CustomerImpl;
import htw.prog3.sm.core.StorageItem;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.Cargo;
import htw.prog3.storageContract.cargo.Hazard;
import htw.prog3.storageContract.cargo.LiquidBulkCargo;
import htw.prog3.storageContract.cargo.UnitisedCargo;

import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

class RandomAccessSerializer {
    public void serializeStorageItem(DataOutputStream out, StorageItem item) throws IOException {
        Cargo cargo = item.getCargo();
        CargoType type = cargo.getCargoType();
        out.writeInt(type.intValue());                                      // type as int
        out.writeLong(item.getStorageDate().toInstant().toEpochMilli());    // storageDate in epoch millis
        String name = cargo.getOwner().getName();
        out.writeInt(name.length());                                        // length of customer name as int
        out.writeChars(name);                                               // customer name as char seq
        String value = cargo.getValue().toString();
        out.writeInt(value.length());                                       // length of value string as int
        out.writeChars(value);                                              // value as char seq
        out.writeLong(cargo.getDurationOfStorage().toDays());               // duration of storage in days
        writeHazards(out, cargo.getHazards());                              // hazards - each as boolean{isPresent}
        writeTypeSpecificBooleans(out, type, cargo);                         // 1. pressurized - 2. fragile
    }

    private void writeTypeSpecificBooleans(DataOutput out, CargoType type, Cargo cargo) throws IOException {
        out.writeBoolean((type.equals(CargoType.LIQUID_BULK_CARGO) ||
                type.equals(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED)) &&
                ((LiquidBulkCargo) cargo).isPressurized());
        out.writeBoolean((type.equals(CargoType.UNITISED_CARGO) ||
                type.equals(CargoType.MIXED_CARGO_LIQUID_BULK_AND_UNITISED)) &&
                ((UnitisedCargo) cargo).isFragile());
    }

    private void writeHazards(DataOutput out, Set<Hazard> hazards) throws IOException {
        out.writeBoolean(hazards.contains(Hazard.FLAMMABLE));
        out.writeBoolean(hazards.contains(Hazard.EXPLOSIVE));
        out.writeBoolean(hazards.contains(Hazard.RADIOACTIVE));
        out.writeBoolean(hazards.contains(Hazard.TOXIC));
    }

    public StorageItem deserializeStorageItem(RandomAccessFile file,
                                              RandomAccessPersistenceStrategy.RecordInfo info,
                                              int storagePosition) throws IOException {
        file.seek(info.filePointer);
        CargoType type = CargoType.from(file.readInt());
        Date storageDate = readDate(file);
        int nameLen = file.readInt();
        String name = readString(file, nameLen);
        int valueLen = file.readInt();
        String valueString = readString(file, valueLen);
        BigDecimal value = new BigDecimal(valueString);
        Duration duration = Duration.ofDays(file.readLong());
        Set<Hazard> hazards = readHazards(file);
        boolean pressurized = file.readBoolean();
        boolean fragile = file.readBoolean();

        Customer owner = new CustomerImpl(name);
        Cargo cargo = CargoFactory.create(type, owner, value, duration, hazards, pressurized, fragile);
        return StorageItem.create(cargo, storagePosition, storageDate);
    }

    private Date readDate(DataInput in) throws IOException {
        long storageDateMilli = in.readLong();
        return Date.from(Instant.ofEpochMilli(storageDateMilli));
    }

    private String readString(DataInput in, int stringLen) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = stringLen; i > 0; i--)
            sb.append(in.readChar());
        return sb.toString();
    }

    private Set<Hazard> readHazards(DataInput in) throws IOException {
        Set<Hazard> hazards = new HashSet<>();
        if (in.readBoolean())
            hazards.add(Hazard.FLAMMABLE);
        if (in.readBoolean())
            hazards.add(Hazard.EXPLOSIVE);
        if (in.readBoolean())
            hazards.add(Hazard.RADIOACTIVE);
        if (in.readBoolean())
            hazards.add(Hazard.TOXIC);
        return hazards;
    }
}