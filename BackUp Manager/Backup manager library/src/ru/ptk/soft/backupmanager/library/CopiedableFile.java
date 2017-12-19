package ru.ptk.soft.backupmanager.library;

import java.io.File;
import java.time.LocalDate;

public interface CopiedableFile{
       
    /**
     * Метод начинающий передачу файлов на сервер.
     */
    abstract public void startBackUp();
   
    /**
     * Сделана ли копия файла на сервер
     * @return true если копия сделана иначе false
     */
    abstract public boolean isBackUp();
    
    
    
}
