/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gsb.rv.dr;

import fr.gsb.rv.dr.entites.Praticien;
import fr.gsb.rv.dr.modele.ModeleGsbRv;
import fr.gsb.rv.dr.technique.ConnexionException;
import fr.gsb.rv.dr.utilitaires.ComparateurCoefConfiance;
import fr.gsb.rv.dr.utilitaires.ComparateurCoefNotoriete;
import fr.gsb.rv.dr.utilitaires.ComparateurDateVisite;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author etudiant
 */
public class PanneauPraticiens extends Pane{
    
    public Integer CRITERE_COEF_CONFIANCE = 1;
    public Integer CRITERE_COEF_NOTORIETE = 2;
    public Integer CRITERE_COEF_VISITE = 3;
    
    private Integer critereTri = CRITERE_COEF_CONFIANCE;
        
    RadioButton rbCoefConfiance = new RadioButton("Confiance");
    RadioButton rbCoefNotoriete = new RadioButton("Notoriété");
    RadioButton rbDateVisite = new RadioButton("Date Visite");
    
    TableView<Praticien> tablePraticien = new TableView<Praticien>();
    
    public PanneauPraticiens(){
        super();
        
        this.setStyle("-fx-alignement: center; -fx-background-color: white;");
        
        Label label = new Label("Sélectionner un critère de tri");
        label.setStyle("-fx-font-weight: bold");
        
        VBox vbox = new VBox(10);
        
        ToggleGroup radioBtn = new ToggleGroup();
        
        HBox hbox = new HBox(10);
        
        GridPane gp = new GridPane();
        
        rbCoefConfiance.setToggleGroup(radioBtn);
        rbCoefConfiance.setSelected(true);  
        rbCoefNotoriete.setToggleGroup(radioBtn);
        rbDateVisite.setToggleGroup(radioBtn);
        
        hbox.getChildren().add(rbCoefConfiance);
        hbox.getChildren().add(rbCoefNotoriete);
        hbox.getChildren().add(rbDateVisite);
       
        gp.getChildren().add(hbox);
        
        TableColumn<Praticien, Integer> colNumero = new TableColumn<Praticien, Integer>("Numéro");
        TableColumn<Praticien, String> colNom = new TableColumn<Praticien, String>("Nom");
        TableColumn<Praticien, String> colVille = new TableColumn<Praticien, String>("Ville");
        
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colVille.setCellValueFactory(new PropertyValueFactory<>("ville"));
        
        tablePraticien.getColumns().add(colNumero);
        tablePraticien.getColumns().add(colNom);
        tablePraticien.getColumns().add(colVille);
        tablePraticien.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.rafraichir();
        
        rbCoefConfiance.setOnAction((ActionEvent event) -> {
            critereTri = CRITERE_COEF_CONFIANCE;
                    this.rafraichir();
        });
        
        rbCoefNotoriete.setOnAction((ActionEvent event) -> {
            critereTri = CRITERE_COEF_NOTORIETE;
                    this.rafraichir();
        });
        
        rbDateVisite.setOnAction((ActionEvent event) -> {
            critereTri = CRITERE_COEF_VISITE;
                    this.rafraichir();
        });
        
        vbox.getChildren().addAll(label, gp, tablePraticien);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10,10,10,10));
        vbox.setLayoutX(145);
        
        this.getChildren().addAll(vbox);
    }

    public void setCritereTri(Integer critereTri) {
        this.critereTri = critereTri;
    }
    
    public void rafraichir() {
        List<Praticien> praticien;
        
        try {
            praticien = ModeleGsbRv.getPraticiensHesitants();
            
            ObservableList<Praticien> obsListe = FXCollections.observableArrayList(praticien);
            
            if(critereTri == 1){
                Collections.sort(obsListe, new ComparateurCoefConfiance());
                tablePraticien.setItems(obsListe);
            }else if(critereTri == 2){
                Collections.sort(obsListe, new ComparateurCoefNotoriete());
                Collections.reverse(obsListe);
                tablePraticien.setItems(obsListe);
            }else if(critereTri == 3){
                Collections.sort(obsListe, new ComparateurDateVisite());
                Collections.reverse(obsListe);
                tablePraticien.setItems(obsListe);
            }
        } catch (ConnexionException ex) {
            Logger.getLogger(PanneauPraticiens.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PanneauPraticiens.class.getName()).log(Level.SEVERE, null, ex);
        }
            
    }

    public int getCritereTri() {
        return critereTri;
    }
}
