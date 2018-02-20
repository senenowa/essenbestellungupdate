/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *SQL für Postgres. Die Datenbankabfragen sind momentan auf den User "celonis" ausgelegt.
 * -- Table: public."BESTELLUNG"

-- DROP TABLE public."BESTELLUNG";

CREATE TABLE public."BESTELLUNG"
(
  "ID" bigint,
  "BENUTZER" character varying(255),
  "GERICHT" character varying(255),
  "BESTELLZEIT" timestamp without time zone
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public."BESTELLUNG"
  OWNER TO celonis;
  * 
 * @author sen
 */
public class Main extends Application {

    private static Stage stage;
    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        wechsleScene("main.fxml");       
    }

    public void wechsleScene(String neueScene) throws IOException {
        try{
        Stage stage = Main.getStage();
        Pane myPane = null;
        myPane = FXMLLoader.load(getClass().getResource(neueScene));
        Scene scene = new Scene(myPane);
        stage.setScene(scene);
        Main.setStage(stage);
        Main.setScene(scene);
        
        stage.setTitle("enowa Essensapp");
        stage.setWidth(800);
        stage.setHeight(800);
        
        stage.show();
        // Sytle setzen -> verweist immer auf "main.css"
        getScene().getStylesheets().add(this.getClass().getResource("main.css").toExternalForm());
        }
        
        catch(Exception e){
              e.printStackTrace();
        System.out.println("Fehler beim Scenewechsel");            
        }
    }
    
    public void wechsleBestellung() throws IOException{
        wechsleScene("bestellung.fxml");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    //getter and setter
    public static void setStage(Stage stage) {
        Main.stage = stage;
    }

    public static void setScene(Scene scene) {
        Main.scene = scene;
    }

    public static Stage getStage() {
        return Main.stage;
    }

    public static Scene getScene() {
        return Main.scene;
    }

}
