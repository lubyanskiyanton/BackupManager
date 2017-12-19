package ru.ptk.soft.backupmanager.client.view;

import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author antoxa
 */
public class SettingsController implements Initializable {
    
    Properties bundleSettings;
    
    String pathToFileSettings = "src/ru/ptk/soft/backupmanager/client/resource/settings.properties";

    // блок параметров сервера
    @FXML
    private CheckBox doCopyToServer;
    
    @FXML
    private TextField serverAddress;
    
    @FXML
    private TextField serverPort;
    
    //блок локальных копий
    @FXML
    private CheckBox doCopyLocal;
    
    @FXML
    private Label pathToDirCopyLocal;
    
    @FXML
    private Button browseCopyLocal;
    
    // удалять файлы после копирования
    @FXML
    private CheckBox delFiles;
    
    // блок выбора каталога источника файлов
    @FXML
    private Label pathToDir;
    
    @FXML
    private Button browse;
    
    // управляющие кнопки    
    @FXML
    private Button save;
    
    @FXML 
    private Button cancel;
    
    private Stage stage;

    public SettingsController() {
        bundleSettings = new Properties();
        try {
            FileInputStream fis = new FileInputStream(pathToFileSettings);
            bundleSettings.load(fis);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SettingsController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle bundleLanguage) {
        
        // установка текста согласно локализации
        browse.setText(bundleLanguage.getString("buttonBrowse"));
        save.setText(bundleLanguage.getString("buttonSave"));
        cancel.setText(bundleLanguage.getString("buttonCancel"));
        
        // установка параметров согласно файла с настройками
        
        if (bundleSettings.getProperty("doCopyToServer").equals("1")) {
            doCopyToServer.setSelected(true);
        }
        else {
            doCopyToServer.setSelected(false);
        }
        onDoCopyLocal();
        if (bundleSettings.getProperty("doCopyLocal").equals("1")) {
            doCopyLocal.setSelected(true);
        }
        else {
            doCopyLocal.setSelected(false);
        }
        onDoCopyToServer();
        
        serverAddress.setText(bundleSettings.getProperty("serverAddress"));
        serverPort.setText(bundleSettings.getProperty("serverPort"));
        delFiles.setText(bundleLanguage.getString("window.settings.delOrNotDel"));
        pathToDir.setText(bundleSettings.getProperty("backupFilesPath"));
        
        
    }    
    
    @FXML
    private void onSaveButton() {
        bundleSettings.setProperty("serverAddress", serverAddress.getText());
        bundleSettings.setProperty("serverPort", serverPort.getText());
        bundleSettings.setProperty("backupFilesPath", pathToDir.getText());
        bundleSettings.setProperty("deleteAfterBackup", delFiles.isSelected() ? "1" : "0");
        bundleSettings.setProperty("doCopyToServer", doCopyToServer.isSelected() ? "1" : "0");
        bundleSettings.setProperty("doCopyLocal", doCopyLocal.isSelected() ? "1" : "0");
        try {
            FileOutputStream fos = new FileOutputStream(pathToFileSettings);
            bundleSettings.store(fos, Calendar.YEAR + "-" + Calendar.MONTH + "-" + Calendar.DAY_OF_MONTH);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        close();
    }
    
    @FXML
    private void onCancelButton() {
        close();
    }
    
    @FXML
    private void onDoCopyToServer() {
        if (doCopyToServer.isSelected()) {
            serverAddress.setDisable(false);
            serverPort.setDisable(false);
        }
        else {
            serverAddress.setDisable(true);
            serverPort.setDisable(true);
        }
    }
    
    @FXML
    private void onDoCopyLocal() {
        if (doCopyLocal.isSelected()) {
            browseCopyLocal.setDisable(false);
        }
        else {
            browseCopyLocal.setDisable(true);
        }
    }
    
    public void setRootStage(Stage stage) {
        this.stage = stage;
    }

    private void close() {
        stage.close();
    }
    
}
