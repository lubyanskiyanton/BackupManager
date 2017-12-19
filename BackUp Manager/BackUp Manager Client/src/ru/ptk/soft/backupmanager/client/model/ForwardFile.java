package ru.ptk.soft.backupmanager.client.model;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.ptk.soft.backupmanager.client.ApplicationFX;
import ru.ptk.soft.backupmanager.client.Delivery;
import ru.ptk.soft.backupmanager.client.view.MainController;
import ru.ptk.soft.backupmanager.library.BackupFile;
import ru.ptk.soft.backupmanager.library.CopiedableFile;
import ru.ptk.soft.backupmanager.library.ServicePacket;

public class ForwardFile extends BackupFile {

    private final static ResourceBundle bundleSettings;

    private static Delivery delivery;

    private final static String serverAddress;

    private final static int serverPort;

    private final static int packetSize;

    private boolean status;

    private boolean deleteAfterBackup;

    static {
        bundleSettings = ResourceBundle.getBundle("ru.ptk.soft.backupmanager.client.resource.settings");
        serverAddress = bundleSettings.getString("serverAddress");
        serverPort = Integer.parseInt(bundleSettings.getString("serverPort"));
        packetSize = Integer.parseInt(bundleSettings.getString("packetSize"));
    }

    public ForwardFile(File file) {
        super(file);
        status = false;
        deleteAfterBackup = bundleSettings.getString("deleteAfterBackup") == "1" ? true : false;
    }

    @Override
    public void startBackUp() {

        try (   Socket socket = new Socket(serverAddress, serverPort);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                FileInputStream fis = new FileInputStream(getFile())) {
            String fileName = getFileName();
            delivery.publishProgress("Connect to " + serverAddress + " is OK!");
            ServicePacket sPacket = new ServicePacket("CPU-11", fileName, getPathToFile(), getCheckSum(), new Date());
            oos.writeObject(sPacket);
            try (DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                byte[] buffer = new byte[packetSize];
                int i;
                delivery.publishProgress("Start transfer " + fileName + " file...");
                while ((i = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, i);
                }
                delivery.publishProgress(fileName + " transfer successfully!");
                status = true;
                if (deleteAfterBackup) {
                    if (getFile().delete()) {
                        delivery.publishProgress("Файл: " + fileName + " успешно удален.");
                    } else {
                        delivery.publishProgress("Ошибка удаления файла: " + fileName);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (ConnectException ex) {
            delivery.publishProgress("Ошибка подключения к серверу!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void setDelivery(Delivery d) {
        delivery = d;
    }

    @Override
    public boolean isBackUp() {
        return status;
    }

    @Override
    public LocalDate dateBackUp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
