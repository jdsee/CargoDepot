package htw.prog3.persistence.ra;

import java.io.*;

/**
 * class not testable: accesses file system
 */
public class FileAccessSupplier {
    public DataOutputStream createDataOutputStream(String name, boolean append) throws FileNotFoundException {
        return new DataOutputStream(new FileOutputStream(name, append));
    }

    public ObjectInputStream createObjectInputStream(String name) throws IOException {
        File file = new File(name);
        return (file.exists()) ? new ObjectInputStream(new FileInputStream(file)) : null;
    }

    public RandomAccessFile createRandomAccessFile(String name, String mode) throws FileNotFoundException {
        File file = new File(name);
        return (file.exists()) ? new RandomAccessFile(name, mode) : null;
    }

    public ObjectOutputStream createObjectOutputStream(String name, boolean append) throws IOException {
        return new ObjectOutputStream(new FileOutputStream(name, append));
    }

    public long getActualFileSize(String name) {
        File file = new File(name);
        return file.length();
    }

    public boolean isExistentFile(String name) {
        return new File(name).exists();
    }
}
