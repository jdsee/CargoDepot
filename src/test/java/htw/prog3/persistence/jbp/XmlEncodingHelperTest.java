package htw.prog3.persistence.jbp;

import htw.prog3.persistence.jbp.XmlEncodingHelper;
import htw.prog3.sm.core.*;
import htw.prog3.storageContract.administration.Customer;
import htw.prog3.storageContract.cargo.LiquidBulkCargo;
import htw.prog3.storageContract.cargo.MixedCargoLiquidBulkAndUnitised;
import htw.prog3.storageContract.cargo.UnitisedCargo;
import org.junit.jupiter.api.Test;

import java.beans.IntrospectionException;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class XmlEncodingHelperTest {
    @Test
    void create_returnsNewInstance() {
        XmlEncodingHelper helper = XmlEncodingHelper.create();
        XmlEncodingHelper other = XmlEncodingHelper.create();

        assertThat(helper).isNotNull().isNotEqualTo(other);
    }

    @Test
    void encoding_BigDecimal_WithCustomPersistenceDelegatesClonesTheObject_Test() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BigDecimal initialBigDecimal = BigDecimal.TEN;
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(baos))) {
            XmlEncodingHelper helper = new XmlEncodingHelper();
            helper.setBigDecimalPersistenceDelegate(encoder);
            encoder.writeObject(initialBigDecimal);

        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(bais))) {
            BigDecimal decodedBigDecimal = (BigDecimal) decoder.readObject();
            assertThat(decodedBigDecimal).isEqualTo(initialBigDecimal);
        } catch (IOException ignored) {
        }
    }

    @Test
    void encoding_Duration_WithCustomPersistenceDelegatesClonesTheObject_Test() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Duration initialDuration = Duration.ofDays(13);
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(baos))) {
            XmlEncodingHelper helper = new XmlEncodingHelper();
            helper.setDurationPersistenceDelegate(encoder);
            encoder.writeObject(initialDuration);
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(bais))) {
            Duration decodedDuration = (Duration) decoder.readObject();
            assertThat(decodedDuration).isEqualTo(initialDuration);
        } catch (IOException ignored) {
        }
    }

    @Test
    void encoding_Date_worksWithDefaultPersistenceDelegate_Test() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Date initialDate = Calendar.getInstance().getTime();
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(baos))) {
            encoder.writeObject(initialDate);
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(bais))) {
            Date decodedDate = (Date) decoder.readObject();
            assertThat(decodedDate).isEqualTo(initialDate);
        } catch (IOException ignored) {
        }
    }

    @Test
    void encoding_Customer_WithCustomPersistenceDelegatesClonesTheObject_Test() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Customer initialCustomer = new CustomerImpl("Peter");

        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(baos))) {
            XmlEncodingHelper helper = new XmlEncodingHelper();
            helper.setCustomerPersistenceDelegate(encoder);
            helper.setBigDecimalPersistenceDelegate(encoder);
            helper.setDurationPersistenceDelegate(encoder);
            encoder.writeObject(initialCustomer);
        }
        Customer decodedCustomer = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(bais))) {
            decodedCustomer = (Customer) decoder.readObject();
        } catch (IOException ignored) {
        }

        assertCustomersEqual(initialCustomer, decodedCustomer);
    }


    private void assertCustomersEqual(Customer one, Customer another) {
        assertThat(one).extracting(
                Customer::getName,
                Customer::getMaxValue,
                Customer::getMaxDurationOfStorage
        ).containsExactly(
                another.getName(),
                another.getMaxValue(),
                another.getMaxDurationOfStorage());
    }

    @Test
    void encoding_UnitisedCargo_WithCustomPersistenceDelegatesClonesTheObject_Test() throws IntrospectionException, NoSuchMethodException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Customer customer = new CustomerImpl("Peter");
        UnitisedCargo initialCargo = new UnitisedCargoImpl(
                customer, BigDecimal.TEN, Duration.ofDays(100), new HashSet<>(), true);

        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(baos))) {
            XmlEncodingHelper helper = new XmlEncodingHelper();
            helper.setUnitisedCargoPersistenceDelegate(encoder);
            helper.setCustomerPersistenceDelegate(encoder);
            helper.setBigDecimalPersistenceDelegate(encoder);
            helper.setDurationPersistenceDelegate(encoder);
            encoder.writeObject(initialCargo);
        }
        UnitisedCargo decodedCargo = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(bais))) {
            decodedCargo = (UnitisedCargo) decoder.readObject();
        } catch (IOException ignored) {
        }

        assertThat(decodedCargo)
                .satisfies(cargo -> assertCustomersEqual(cargo.getOwner(), initialCargo.getOwner()))
                .extracting(
                        UnitisedCargo::getValue,
                        UnitisedCargo::getDurationOfStorage,
                        UnitisedCargo::getHazards,
                        UnitisedCargo::isFragile)
                .containsExactly(
                        initialCargo.getValue(),
                        initialCargo.getDurationOfStorage(),
                        initialCargo.getHazards(),
                        initialCargo.isFragile()
                );
    }

    @Test
    void encoding_LiquidBulkCargo_WithCustomPersistenceDelegatesClonesTheObject_Test()
            throws IntrospectionException, NoSuchMethodException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Customer customer = new CustomerImpl("Peter");
        LiquidBulkCargo initialCargo = new LiquidBulkCargoImpl(
                customer, BigDecimal.TEN, Duration.ofDays(100), new HashSet<>(), true);

        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(baos))) {
            XmlEncodingHelper helper = new XmlEncodingHelper();
            helper.setLiquidBulkCargoPersistenceDelegate(encoder);
            helper.setCustomerPersistenceDelegate(encoder);
            helper.setBigDecimalPersistenceDelegate(encoder);
            helper.setDurationPersistenceDelegate(encoder);
            encoder.writeObject(initialCargo);
        }
        LiquidBulkCargo decodedCargo = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(bais))) {
            decodedCargo = (LiquidBulkCargo) decoder.readObject();
        } catch (IOException ignored) {
        }

        assertThat(decodedCargo)
                .satisfies(cargo -> assertCustomersEqual(cargo.getOwner(), initialCargo.getOwner()))
                .extracting(
                        LiquidBulkCargo::getValue,
                        LiquidBulkCargo::getDurationOfStorage,
                        LiquidBulkCargo::getHazards,
                        LiquidBulkCargo::isPressurized)
                .containsExactly(
                        initialCargo.getValue(),
                        initialCargo.getDurationOfStorage(),
                        initialCargo.getHazards(),
                        initialCargo.isPressurized()
                );
    }

    @Test
    void encoding_MixedCargo_WithCustomPersistenceDelegatesClonesTheObject_Test()
            throws IntrospectionException, NoSuchMethodException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Customer customer = new CustomerImpl("Peter");
        MixedCargoLiquidBulkAndUnitised initialCargo = new MixedCargoLiquidBulkAndUnitisedImpl(
                customer, BigDecimal.TEN, Duration.ofDays(100), new HashSet<>(), true, true);

        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(baos))) {
            XmlEncodingHelper helper = new XmlEncodingHelper();
            helper.setMixedCargoPersistenceDelegate(encoder);
            helper.setCustomerPersistenceDelegate(encoder);
            helper.setBigDecimalPersistenceDelegate(encoder);
            helper.setDurationPersistenceDelegate(encoder);
            encoder.writeObject(initialCargo);
        }
        MixedCargoLiquidBulkAndUnitised decodedCargo = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(bais))) {
            decodedCargo = (MixedCargoLiquidBulkAndUnitised) decoder.readObject();
        } catch (IOException ignored) {
        }

        assertThat(decodedCargo)
                .satisfies(cargo -> assertCustomersEqual(cargo.getOwner(), initialCargo.getOwner()))
                .extracting(
                        MixedCargoLiquidBulkAndUnitised::getValue,
                        MixedCargoLiquidBulkAndUnitised::getDurationOfStorage,
                        MixedCargoLiquidBulkAndUnitised::getHazards,
                        MixedCargoLiquidBulkAndUnitised::isFragile,
                        MixedCargoLiquidBulkAndUnitised::isPressurized)
                .containsExactly(
                        initialCargo.getValue(),
                        initialCargo.getDurationOfStorage(),
                        initialCargo.getHazards(),
                        initialCargo.isFragile(),
                        initialCargo.isPressurized()
                );
    }

    @Test
    void encoding_StorageItem_WithCustomPersistenceDelegatesClonesTheObject_Test()
            throws IntrospectionException, NoSuchMethodException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Customer customer = new CustomerImpl("Peter");
        MixedCargoLiquidBulkAndUnitised cargo = new MixedCargoLiquidBulkAndUnitisedImpl(
                customer, BigDecimal.TEN, Duration.ofDays(100), new HashSet<>(), true, true);
        StorageItem initialItem = new StorageItem(cargo, 333);

        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(baos))) {
            XmlEncodingHelper helper = new XmlEncodingHelper();
            helper.setStorageItemPersistenceDelegate(encoder);
            helper.setMixedCargoPersistenceDelegate(encoder);
            helper.setCustomerPersistenceDelegate(encoder);
            helper.setBigDecimalPersistenceDelegate(encoder);
            helper.setDurationPersistenceDelegate(encoder);
            encoder.writeObject(initialItem);
        }
        StorageItem decodedItem = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(bais))) {
            decodedItem = (StorageItem) decoder.readObject();
        } catch (IOException ignored) {
        }

        assertThat(decodedItem)
                .satisfies(item -> assertCustomersEqual(item.getOwner(), item.getOwner()))
                .extracting(
                        StorageItem::getStoragePosition,
                        StorageItem::getStorageDate,
                        StorageItem::getValue,
                        StorageItem::getDurationOfStorage,
                        StorageItem::getHazards)
                .containsExactly(
                        initialItem.getStoragePosition(),
                        initialItem.getStorageDate(),
                        initialItem.getValue(),
                        initialItem.getDurationOfStorage(),
                        initialItem.getHazards()

                );
    }

    @Test
    void setCustomerRecordPersistenceDelegate() throws IntrospectionException, NoSuchMethodException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Customer customer = new CustomerImpl("Peter");
        MixedCargoLiquidBulkAndUnitised cargo = new MixedCargoLiquidBulkAndUnitisedImpl(
                customer, BigDecimal.TEN, Duration.ofDays(100), new HashSet<>(), true, true);
        StorageItem item = new StorageItem(cargo, 333);

        CustomerRecord record = new CustomerRecord(customer, asList(item), BigDecimal.TEN, Duration.ofDays(100));

        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(baos))) {
            XmlEncodingHelper helper = new XmlEncodingHelper();
            helper.setStorageItemPersistenceDelegate(encoder);
            helper.setMixedCargoPersistenceDelegate(encoder);
            helper.setCustomerPersistenceDelegate(encoder);
            helper.setBigDecimalPersistenceDelegate(encoder);
            helper.setDurationPersistenceDelegate(encoder);
            helper.setCustomerRecordPersistenceDelegate(encoder);

            encoder.writeObject(record);
        }
        CustomerRecord decodedRecord = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
             XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(bais))) {
            decodedRecord = (CustomerRecord) decoder.readObject();
        } catch (IOException ignored) {
        }

        assertThat(decodedRecord.getCustomer().getName()).isEqualTo("Peter");
        assertThat(decodedRecord.getStorageItems()).hasOnlyOneElementSatisfying(
                storageItem -> {
                    assertThat(storageItem.getOwner().getName()).isEqualTo("Peter");
                    assertThat(storageItem.getStoragePosition()).isEqualTo(333);
                }
        );
    }
}