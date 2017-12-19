package ru.ptk.soft.backupmanager.client;

import java.io.IOException;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.ptk.soft.backupmanager.client.view.MainController;

public class ApplicationFX extends Application {
    
    private static ResourceBundle bundleSettings = ResourceBundle.getBundle("ru.ptk.soft.backupmanager.client.resource.settings");
    
    private static ResourceBundle bundleLanguage = ResourceBundle.getBundle("ru.ptk.soft.backupmanager.client.resource.language");
            
    private Delivery delivery;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws IOException {
        
        Stage mainStage = stage;
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/Main.fxml"), bundleLanguage);        
        
        Parent root = loader.load();
        ((MainController)loader.getController()).setRootScene(stage);
        
        Scene scene = new Scene(root);
                        
        mainStage.setScene(scene);
        mainStage.show();
    }
           
    public void startDelivery() {
        //delivery = new Delivery();
        delivery.start();
    }
    
    public void stopDelivery() {
        delivery.interrupt();
    }
    
    public synchronized void logging(String text) {
        System.out.println(text);
    }
    
}
