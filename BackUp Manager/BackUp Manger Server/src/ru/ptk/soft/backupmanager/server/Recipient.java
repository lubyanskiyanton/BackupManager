package ru.ptk.soft.backupmanager.server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.ptk.soft.backupmanager.library.ServicePacket;

public class Recipient implements Runnable {

    private final Thread t;

    private boolean interrupt = false;

    private final int packetSize;

    private final String backupPath;

    private final ResourceBundle boundle;

    public Recipient(String backupPath, int packetSize) {
        this.backupPath = backupPath;
        this.packetSize = packetSize;
        t = new Thread(this);
        t.setDaemon(true);
        boundle = ResourceBundle.getBundle("ru.ptk.soft.backupmanager.server.resource.settings");
    }

    public Recipient() {
        this("", 1024);
    }

    public void start() {
        t.start();
    }

    @Override
    public void run() {

        try (ServerSocket sSocket = new ServerSocket(Integer.parseInt(boundle.getString("portNumber")))) {

            while (true) {

                System.out.println(boundle.getString("waitingInputFile"));
                Socket socket = sSocket.accept();
                if (isInterrupt()) {
                    break;
                }
                
                try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                    ServicePacket sPacket = new ServicePacket(ois.readObject());
                    String filename = sPacket.getFileName();
                    // получаем дату для формирования дерева каталогов
                    Date date = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("MM");
                    String month = format.format(date);
                    format = new SimpleDateFormat("dd");
                    String day = format.format(date);
                    format = new SimpleDateFormat("yyyy");
                    String year = format.format(date);
                    
                    String dirBackUp = backupPath + sPacket.getId() + File.separatorChar + year + File.separatorChar + month + File.separatorChar + day + File.separatorChar;
                    String pathToFile = dirBackUp + sPacket.getPathToFile() + File.separatorChar;

                    try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
                        File dir = new File(dirBackUp);
                        if (!dir.exists()) {
                            System.out.println("Created dir...");
                            dir.mkdirs();
                        }
                        File file = new File(pathToFile + sPacket.getFileName());
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            byte[] buffer = new byte[packetSize];
                            int i = 0;
                            System.out.println(boundle.getString("connectAccept"));
                            System.out.println(boundle.getString("startTransferFile"));
                            while ((i = dis.read(buffer)) != -1) {
                                fos.write(buffer, 0, i);
                            }
                            System.out.println(boundle.getString("fileAccept") + filename);
                            System.out.println();
                        } catch (FileNotFoundException ex) {
                            System.err.println("File not found!");
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Recipient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void interrupt() {
        interrupt = !isInterrupt();
    }

    private boolean isInterrupt() {
        return interrupt;
    }

}
