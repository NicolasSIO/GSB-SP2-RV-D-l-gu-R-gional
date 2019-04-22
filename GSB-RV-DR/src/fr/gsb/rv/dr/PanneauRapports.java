/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gsb.rv.dr;

import fr.gsb.rv.dr.entites.RapportVisite;
import fr.gsb.rv.dr.entites.Visiteur;
import fr.gsb.rv.dr.modele.ModeleGsbRv;
import fr.gsb.rv.dr.technique.ConnexionException;
import fr.gsb.rv.dr.technique.Mois;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author etudiant
 */
public class PanneauRapports extends Pane{
    
    private ComboBox cbVisiteur = new ComboBox<Visiteur>();
    private ComboBox cbMois = new ComboBox<Mois>();
    private ComboBox cbAnnee = new ComboBox<Integer>();
    
    private Button btnValider = new Button("Valider");
    
    private TableView<RapportVisite> tableRapport = new TableView<>();
    
    
    public PanneauRapports(){
        super();
        
        this.setStyle("-fx-alignement: center; -fx-background-color: white;");
        
        VBox vbox = new VBox(15);
        
        HBox hbox = new HBox(15);
                
        TableColumn<RapportVisite, Integer> colNumero = new TableColumn<RapportVisite, Integer>("Numéro");
        TableColumn<RapportVisite, String> colNom = new TableColumn<RapportVisite, String>("Praticien");
        TableColumn<RapportVisite, String> colVille = new TableColumn<RapportVisite, String>("Ville");
        TableColumn<RapportVisite, LocalDate> colVisite = new TableColumn<RapportVisite, LocalDate>("Visite");
        TableColumn<RapportVisite, LocalDate> colRedaction = new TableColumn<RapportVisite, LocalDate>("Rédaction");
        
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        
        colNom.setCellValueFactory(
                param -> {
                    String nom = param.getValue().getLePraticien().getNom();
                    return new SimpleStringProperty(nom);
        });
        
        colVille.setCellValueFactory(
                param -> {
                    String ville = param.getValue().getLePraticien().getVille();
                    return new SimpleStringProperty(ville);
        });
        
        colVisite.setCellValueFactory(new PropertyValueFactory<>("dateVisite"));
        colVisite.setCellFactory(
        colonne -> {
            return new TableCell<RapportVisite, LocalDate>(){
                @Override
                protected void updateItem(LocalDate item, boolean empty){
                    super.updateItem(item, empty);
                    
                    if(empty){
                        setText("");
                    }else{
                        DateTimeFormatter formateur = DateTimeFormatter.ofPattern("dd/MM/uuuu");
                        setText(item.format(formateur));
                    }
                }
            };
        });
        
        colRedaction.setCellValueFactory(new PropertyValueFactory<>("dateRedaction"));
        colRedaction.setCellFactory(
        colonne -> {
            return new TableCell<RapportVisite, LocalDate>(){
                @Override
                protected void updateItem(LocalDate item, boolean empty){
                    super.updateItem(item, empty);
                    
                    if(empty){
                        setText("");
                    }else{
                        DateTimeFormatter formateur = DateTimeFormatter.ofPattern("dd/MM/uuuu");
                        setText(item.format(formateur));
                    }
                }
            };
        });
        
        tableRapport.getColumns().add(colNumero);
        tableRapport.getColumns().add(colNom);
        tableRapport.getColumns().add(colVille);
        tableRapport.getColumns().add(colVisite);
        tableRapport.getColumns().add(colRedaction);
        
        tableRapport.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableRapport.setOnMouseClicked(
                (MouseEvent event) -> {
                    if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2){
                        try {
                            int indiceRapport = tableRapport.getSelectionModel().getSelectedIndex();
                            Visiteur leVisiteur = (Visiteur) cbVisiteur.getSelectionModel().getSelectedItem();
                            RapportVisite rv = tableRapport.getItems().get(indiceRapport);
                            
                            if(rv.isLu() == false){
                                ModeleGsbRv.setRapportVisiteLu(rv.getLeVisiteur().getMatricule(), rv.getNumero());
                                this.rafraichir();

                                VueRapport vueRapport = new VueRapport(rv);
                                vueRapport.showAndWait();
                                
                            }else{
                                ModeleGsbRv.setRapportVisiteNonLu(rv.getLeVisiteur().getMatricule(), rv.getNumero());

                                this.rafraichir();
                            }
                        } catch (ConnexionException | SQLException ex) {
                            Logger.getLogger(PanneauRapports.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
        );
        tableRapport.setRowFactory(
        ligne -> {
            return new TableRow<RapportVisite>(){
                @Override
                protected void updateItem(RapportVisite item, boolean empty){
                    super.updateItem(item, empty);
                    
                    if(item != null){
                        if(item.isLu()){
                            setStyle("-fx-background-color: grey");
                        }else{
                            setStyle("-fx-background-color: gold");
                        }
                    }
                    if(item == null){
                        setStyle("-fx-background-color: white");
                    }
                }
            };
        }
        );
        
        try {
            List<Visiteur> visiteur = ModeleGsbRv.getVisiteur();for(Visiteur unVisiteur: visiteur){  
            cbVisiteur.getItems().add(unVisiteur);
        }
        } catch (ConnexionException ex) {
            Logger.getLogger(PanneauRapports.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PanneauRapports.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        for(Mois unMois : Mois.values()){
            cbMois.getItems().add(unMois);
        }
        
        LocalDate aujourdhui = LocalDate.now();
        int anneeCourante = aujourdhui.getYear();
        int anneeAnterieur = anneeCourante - 5;
        
        while(anneeAnterieur <= anneeCourante){
            cbAnnee.getItems().add(anneeAnterieur);
            anneeAnterieur ++;
        }
        
        btnValider.setOnAction((ActionEvent event) -> {       
            try {
                this.rafraichir();
            } catch (ConnexionException | SQLException ex) {
                Logger.getLogger(PanneauRapports.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        hbox.getChildren().add(cbVisiteur);
        hbox.getChildren().add(cbMois);
        hbox.getChildren().add(cbAnnee);
        
        GridPane gp = new GridPane();
        gp.getChildren().add(hbox);
        
        vbox.getChildren().add(gp);
        vbox.getChildren().add(btnValider);
        vbox.getChildren().add(tableRapport);
        vbox.setLayoutX(60);
        vbox.setLayoutY(10);
        
        this.getChildren().addAll(vbox);
    }
    
    public void rafraichir() throws ConnexionException, SQLException{
        if(cbVisiteur.getSelectionModel().isEmpty() || cbMois.getSelectionModel().isEmpty() || cbAnnee.getSelectionModel().isEmpty()){
            Alert alertChamp = new Alert(Alert.AlertType.INFORMATION);
            alertChamp.setTitle("Champ vide");
            alertChamp.setHeaderText("Attention !");
            alertChamp.setContentText("La sélection est incomplète !");
        }else{
            Visiteur leVisiteur = (Visiteur) cbVisiteur.getSelectionModel().getSelectedItem();
            int mois = cbMois.getSelectionModel().getSelectedIndex()+1;
            int annee = (int) cbAnnee.getSelectionModel().getSelectedItem();
            
            List<RapportVisite> rapport = ModeleGsbRv.getRapportVisite(leVisiteur.getMatricule(), annee, mois);
            ObservableList<RapportVisite> obsListe = FXCollections.observableArrayList(rapport);
            
            tableRapport.setItems(obsListe);
        }
    }
}