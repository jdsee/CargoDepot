package htw.prog3.persistence.jbp;

import htw.prog3.persistence.StoragePersistenceStrategy;
import htw.prog3.sm.api.StorageManagement;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.Optional;

public class XmlEncodingStrategy implements StoragePersistenceStrategy {
    private static final String JBP_FILE_NAME = "./src/resources/persistence/jbp_storage_snapshot.xml";

    private final XmlEncodingHandler handler;

    public XmlEncodingStrategy(XmlEncodingHandler handler) {
        this.handler = handler;
    }

    public static XmlEncodingStrategy create() {
        XmlEncodingHelper helper = new XmlEncodingHelper();
        return new XmlEncodingStrategy(new XmlEncodingHandler(helper));
    }

    /**
     * Not testable: access on file system
     */
    public void save(StorageManagement source) {
        try (XMLEncoder out = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(JBP_FILE_NAME)))) {
            handler.writeStorageManagement(out, source);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Not testable: access on file system
     */
    @Override
    public Optional<StorageManagement> load() {
        File memo = new File(JBP_FILE_NAME);
        if (memo.exists()) {
            try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(memo)))) {
                return Optional.of(handler.readStorageManagement(decoder));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}
