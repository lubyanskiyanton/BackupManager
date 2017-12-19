package ru.ptk.soft.backupmanager.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

public abstract class BackupFile implements CopiedableFile {

    private File file;
    
    protected BackupFile(File file) {
        this.file = file;
    }

    /**
     * Возвращает контрольную сумму файла.
     * @return контрольная сумма.
     */
    public final String getCheckSum() {
        StringBuilder sb = new StringBuilder();
        try {
            //Use SHA-1 algorithm
            MessageDigest shaDigest = MessageDigest.getInstance("SHA-1");

            //Get file input stream for reading the file content
            FileInputStream fis = new FileInputStream(file);

            //Create byte array to read data in chunks
            byte[] byteArray = new byte[1024];
            int bytesCount = 0;

            //Read file data and update in message digest
            while ((bytesCount = fis.read(byteArray)) != -1) {
                shaDigest.update(byteArray, 0, bytesCount);
            };

            //close the stream; We don't need it now.
            fis.close();

            //Get the hash's bytes
            byte[] bytes = shaDigest.digest();

            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Возвращает дату резервного копирования фала
     * @return LocalDate
     */
    abstract public LocalDate dateBackUp();
    
    public File getFile() {
        return file;
    }
    
    public String getPathToFile() {
        return "";//file.getPath();
    }
    
    /**
     * Возвращает имя файла.
     * @return имя файла.
     */
    public String getFileName() {
        return file.getName();
    }
}
