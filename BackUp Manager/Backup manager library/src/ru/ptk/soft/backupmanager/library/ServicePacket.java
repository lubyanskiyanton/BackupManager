package ru.ptk.soft.backupmanager.library;

import java.io.Serializable;
import java.util.Date;

public class ServicePacket implements Serializable {
    
    // уникальный идентификатор клиента
    private final String id;

    // имя файла
    private final String fileName;
    
    // путь к файлу, относительно корневого каталога хранения
    private final String pathToFile;
    
    // контрольная сумма
    private final String checkSum;
    
    // статус успешного копирование
    private boolean status;
    
    // timestamp отправки пакета
    private Date date;
    
    public ServicePacket(Object o) {
        ServicePacket s = (ServicePacket)o;
        id = s.id;
        fileName = s.fileName;
        pathToFile = s.pathToFile;
        checkSum = s.checkSum;
    }

    public ServicePacket(String id, String fileName, String pathToFile, String checkSum, Date date) {
        this.id = id;
        this.fileName = fileName;
        this.pathToFile = pathToFile;
        this.checkSum = checkSum;
        this.date = date;
    }
    
    public void setStatus(boolean status) {
        this.status = status;
    }
    
    public String getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }
    
    public String getPathToFile() {
        return pathToFile;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public boolean getStatus() {
        return status;
    }

    public Date getDate() {
        return date;
    }
    
    
    
}
