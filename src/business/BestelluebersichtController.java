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
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author sen
 */
public class BestelluebersichtController implements Initializable {

    @FXML
    private TableView bestelluebersichtTableView;
    private ObservableList<ObservableList> data;

    public void wechselZuBestellung() throws IOException {
        Main main = new Main();
        main.wechsleScene("bestellung.fxml");
    }

    /**
     * Initiales Befüllen der TableView
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bestelluebersichtTableView.setPrefSize(560, 450);
        try {
            buildData();
        } catch (SQLException ex) {
            Logger.getLogger(BestelluebersichtController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //bestelluebersichtTableView.setItems(data);
    }

    /*
    Befüllen der TableView
     */
    public void buildData() throws SQLException {
        data = FXCollections.observableArrayList();
        try {
            String url = "jdbc:postgresql://localhost:5432/essensbestellung";
            Properties props = new Properties();
            props.setProperty("user", "celonis");
            props.setProperty("password", "celonis");
            Connection c = DriverManager.getConnection(url, props);

            String SQL = "SELECT \"ID\", \"BENUTZER\", \"GERICHT\", TO_CHAR(\"BESTELLZEIT\",\'DD.MM.YYYY  HH24:MI:SS\') "
                    + "from public.\"BESTELLUNG\" ORDER BY \"BESTELLZEIT\"";
            ResultSet rs = c.createStatement().executeQuery(SQL);

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {

                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));

                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                }
                );
                bestelluebersichtTableView.getColumns().addAll(col);
                //Festlegen der relativen Spaltenbreite in der Tableview und setzen der Spaltennamen, falls notwendig.
                switch (i) {
                    case 0:
                        col.prefWidthProperty().bind(bestelluebersichtTableView.widthProperty().multiply(0.10));
                        break;
                    case 1:
                        col.prefWidthProperty().bind(bestelluebersichtTableView.widthProperty().multiply(0.15));
                        break;
                    case 2:
                        col.prefWidthProperty().bind(bestelluebersichtTableView.widthProperty().multiply(0.45));
                        break;
                    case 3:
                        col.prefWidthProperty().bind(bestelluebersichtTableView.widthProperty().multiply(0.30));
                        col.setText("BESTELLZEIT");
                        break;
                }

            }
            while (rs.next()) {
                ObservableList<String> roww = FXCollections.observableArrayList();
                //colId.
                roww.add(rs.getString(1));
                roww.add(rs.getString(2));
                roww.add(rs.getString(3));
                roww.add(rs.getString(4));
                data.add(roww);
            }
            bestelluebersichtTableView.setItems(data);
        } catch (SQLException ex) {
            Logger.getLogger(BestelluebersichtController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
    
     */
    public void aktualisiereBestelluebersicht() throws SQLException, IOException {
        Main main = new Main();
        main.wechsleScene("bestelluebersicht.fxml");
    }

}
