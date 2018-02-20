/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 *
 * @author sen INSERT INTO public."BESTELLUNG" values ((SELECT
 * COALESCE(MAX("ID")+1,0) from public."BESTELLUNG"),'pho',
 * 'fisch',CURRENT_TIMESTAMP); COMMIT;
 */
public class BestellungController implements Initializable {

    // Bestellfelder "Name" und "Gericht", welche in die DB geschrieben werden
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField gerichtTextField;
    @FXML
    private ChoiceBox vorschlagChoiceBox;
    private ObservableList<ObservableList> box = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        befuellenChoiceBox();
    }

    /*
    Befüllen der ChoicBox
     */
    public void befuellenChoiceBox() {
        //System.out.println("befüllen ausgefürht");
        try {
            String url = "jdbc:postgresql://localhost:5432/essensbestellung";
            Properties props = new Properties();
            props.setProperty("user", "celonis");
            props.setProperty("password", "celonis");
            Connection c = DriverManager.getConnection(url, props);

            String SQL = "SELECT DISTINCT \"GERICHT\" from public.\"BESTELLUNG\" ORDER BY \"GERICHT\" ASC;";
            ResultSet rs = c.createStatement().executeQuery(SQL);
            while (rs.next()) {
                ObservableList<String> roww = FXCollections.observableArrayList();
                roww.add(rs.getString(1));
                // System.out.println(rs.getString(1));
                box.add(roww);

            }
            vorschlagChoiceBox.setItems(box);
            c.close();
        } catch (SQLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);

        }
        //System.out.println(vorschlagChoiceBox.getItems());
    }

    /*
    Wechsel in den Reiter "Bestelluebersicht", welcher alle Daten anzeigt
     */
    @FXML
    public void bestellueberischtAnzeigen() throws SQLException, IOException {
        Main main = new Main();
        main.wechsleScene("bestelluebersicht.fxml");
    }

    @FXML
    public void bestellungSenden() throws SQLException {
        String benutzer = "";
        String gericht = "";
        String choiceBox = "";

        if (nameTextField.getText() != null) {
            benutzer = nameTextField.getText();
        }
        if (gerichtTextField.getText() != null) {
            gericht = gerichtTextField.getText();
        }
        //System.out.print(vorschlagChoiceBox.getSelectionModel().getSelectedItem().toString());
        if (vorschlagChoiceBox.getSelectionModel().getSelectedItem() != null) {
            choiceBox = vorschlagChoiceBox.getSelectionModel().getSelectedItem().toString();
            // Da die ChoiceBox noch am Anfang "[" und und am Ende "]" enthält, werden diese Klammern mittels Substring entfernt.
            choiceBox = choiceBox.substring(1, choiceBox.length() - 1);
        }
        if(pruefeTextfeld(benutzer + gericht + choiceBox)){
        insertBestellung(benutzer, gericht, choiceBox);
        }
    }

    public static void insertBestellung(String benutzer, String gericht, String choiceBox) throws SQLException {
        //Prüfung ob Benutzer und Gericht gesetzt ist. Bei Gericht muss enweder das Eingabefeld oder die ChoiceBox gesetzt sein
        if (benutzer.isEmpty() || (gericht.isEmpty() && choiceBox.isEmpty())) {
            JOptionPane.showMessageDialog(null, "Bitte überprüfen Sie Ihre Eingaben", "Hinweis", JOptionPane.ERROR_MESSAGE);
            // Enowa User haben die Länge 3
        } else if (benutzer.length() != 3) {
            JOptionPane.showMessageDialog(null, "Geben Sie bitte Ihren enowa User ein! (3 Zeichen)", "Hinweis", JOptionPane.CANCEL_OPTION);
            // Prüfung, ob Eingabefeld UND ChoiceBox gesetzt sind. Ist dies der Fall, kommt die Fehlermeldung, dass nur eines von beiden gefüllt sein darf.
        } else if (!gericht.isEmpty() && !choiceBox.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Entweder Gericht per Dropdwon ODER manuell wählen", "Hinweis", JOptionPane.ERROR_MESSAGE);
        } else {
            //Einfügen in die Datenbank falls Benutzer und Gericht gesetzt ist
            try {
                String url = "jdbc:postgresql://localhost:5432/essensbestellung";
                Properties props = new Properties();
                props.setProperty("user", "celonis");
                props.setProperty("password", "celonis");
                Connection conn = DriverManager.getConnection(url, props);

                Statement st = conn.createStatement();
                String gerichtdb = gericht + choiceBox;
                st.executeUpdate("INSERT INTO public.\"BESTELLUNG\" values ((SELECT COALESCE(MAX(\"ID\")+1,0) from public.\"BESTELLUNG\"), '"
                        + benutzer + "', '" + gerichtdb + "',CURRENT_TIMESTAMP); COMMIT;");
                conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);

            }

            JOptionPane.showMessageDialog(null, "Ihre Bestellung war erfolgreich", "Hinweis", JOptionPane.PLAIN_MESSAGE);
        }
    }
    
    //Prüfung, ob jemand die Datenbank manipulieren will
    public boolean pruefeTextfeld(String s){  
        if(s.toLowerCase().contains("select") || s.toLowerCase().contains("delete") || s.toLowerCase().contains("truncate") 
                || s.toLowerCase().contains("insert") || s.toLowerCase().contains("commit") || s.toLowerCase().contains("bestellung")){
               JOptionPane.showMessageDialog(null, "Datenbankmanipulation unerwünscht.","Zugriff verweigert", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

}
