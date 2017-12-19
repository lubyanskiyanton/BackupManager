/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ptk.soft.backupmanager.client.view;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ru.ptk.soft.backupmanager.client.ApplicationFX;
import ru.ptk.soft.backupmanager.client.Delivery;

/**
 * FXML Controller class
 *
 * @author antoxa
 */
public class MainController implements Initializable {
        
    private ResourceBundle bundleSettings = ResourceBundle.getBundle("ru.ptk.soft.backupmanager.client.resource.settings");
    
    private Delivery delivery;
    
    @FXML
    private Label status;
    
    @FXML
    private TextArea console;
    
    @FXML
    private MenuItem run;
    
    @FXML
    private MenuItem stop;
    
    private Stage root;

    public MainController() {
        delivery = new Delivery(this);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        run.setDisable(false);
        stop.setDisable(true);
    }    
        
    public void printToConsole(String text) {
        console.appendText(text + "\n");
    }
    
    @FXML
    private void clearConsole() {
        console.setText("Command history:\n");
    }
    
    @FXML
    private void copyFromConsoleToClipboard() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(console.getText()), null);
    }
    
    public void setStatus(String status) {
        this.status.setText("Status: " + status);
    }
    
    @FXML
    private void onRun() {
        long timeout = Long.parseLong(bundleSettings.getString("timeout"));
        run.setDisable(true);
        stop.setDisable(false);
        status.setText("Status: running...");
        delivery.execute();
    }
    
    @FXML
    private void onStop() {
        //app.stopDelivery();
        delivery.interrupt();
        status.setText("Status: stopped!");           
    }
    
    @FXML
    private void onClose() {
        System.exit(0);
    }
    
    @FXML
    private void onSettings() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Settings.fxml"), ResourceBundle.getBundle("ru.ptk.soft.backupmanager.client.resource.language"));
        Parent child = loader.load();
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.initOwner(root);
        ((SettingsController)loader.getController()).setRootStage(modalStage);
        Scene scene = new Scene(child);
        modalStage.setScene(scene);
        modalStage.setResizable(false);
        modalStage.showAndWait();        
    }
    
    public void deliveryEnd() {
        run.setDisable(false);
        stop.setDisable(true);
    }
    
    public void setRootScene(Stage root) {
        this.root = root;
    }
}
