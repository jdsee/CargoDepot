package htw.prog3.persistence.jbp;

import htw.prog3.sm.api.StorageManagement;
import htw.prog3.sm.core.*;
import htw.prog3.storageContract.administration.Customer;

import java.beans.IntrospectionException;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unchecked")
public class XmlEncodingHandler {

    private final XmlEncodingHelper helper;

    public XmlEncodingHandler(XmlEncodingHelper helper) {
        this.helper = helper;
    }

    public void writeStorageManagement(XMLEncoder encoder, StorageManagement source) {
        try {
            setPersistenceDelegates(encoder);

            writeCustomerRecords(encoder, source);
            writeStorageItems(encoder, source);
        } catch (IntrospectionException | NoSuchMethodException e) {
            throw new AssertionError("Persistence Delegates not properly set.", e);
        }
    }

    public StorageManagement readStorageManagement(XMLDecoder decoder) {
        CustomerAdministration customerAdministration = readCustomerAdministration(decoder);

        int capacity = (int) decoder.readObject();
        List<StorageItem> itemList = (List<StorageItem>) decoder.readObject();

        Set<Integer> revokedPositions = calcRevokedPositions(itemList);

        Storage storage = new Storage(capacity, revokedPositions);
        itemList.forEach(item -> {
            storage.addItem(item);
            customerAdministration.addStorageItemAsset(item);
        });

        return new StorageManagementImpl(storage, customerAdministration);
    }

    private Set<Integer> calcRevokedPositions(List<StorageItem> itemList) {
        Integer maxPosition = itemList.stream()
                .map(StorageItem::getStoragePosition).max(Integer::compareTo).orElse(0);
        Set<Integer> revokedPositions = IntStream.range(0, maxPosition).boxed()
                .collect(Collectors.toSet());
        revokedPositions.removeAll(itemList.stream()
                .map(StorageItem::getStoragePosition)
                .collect(Collectors.toList()));
        return revokedPositions;
    }

    private CustomerAdministration readCustomerAdministration(XMLDecoder decoder) {
        List<Customer> recordList = (List<Customer>) decoder.readObject();

        CustomerAdministration administration = CustomerAdministration.create();
        recordList.forEach(administration::addCustomer);

        return administration;
    }

    private void writeStorageItems(XMLEncoder encoder, StorageManagement source) {
        List<StorageItem> items = new ArrayList<>(source.getStorageItems().values());
        encoder.writeObject(source.getCapacity());
        encoder.writeObject(items);
    }

    private void writeCustomerRecords(XMLEncoder encoder, StorageManagement source) {
        List<Customer> customers = source.getCustomerRecords().values().stream()
                .map(CustomerRecord::getCustomer)
                .collect(Collectors.toList());
        encoder.writeObject(customers);
    }

    private void setPersistenceDelegates(XMLEncoder encoder) throws IntrospectionException, NoSuchMethodException {
        helper.setBigDecimalPersistenceDelegate(encoder);
        helper.setDurationPersistenceDelegate(encoder);
        helper.setCustomerPersistenceDelegate(encoder);
        helper.setLiquidBulkCargoPersistenceDelegate(encoder);
        helper.setUnitisedCargoPersistenceDelegate(encoder);
        helper.setMixedCargoPersistenceDelegate(encoder);
        helper.setStorageItemPersistenceDelegate(encoder);
        helper.setCustomerRecordPersistenceDelegate(encoder);
    }
}