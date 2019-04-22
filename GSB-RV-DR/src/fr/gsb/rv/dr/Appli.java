/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gsb.rv.dr;

import fr.gsb.rv.dr.entites.Praticien;
import fr.gsb.rv.dr.entites.Visiteur;
import fr.gsb.rv.dr.modele.ModeleGsbRv;
import fr.gsb.rv.dr.technique.ConnexionException;
import fr.gsb.rv.dr.technique.Session;
import fr.gsb.rv.dr.utilitaires.ComparateurCoefConfiance;
import fr.gsb.rv.dr.utilitaires.ComparateurCoefNotoriete;
import fr.gsb.rv.dr.utilitaires.ComparateurDateVisite;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javax.swing.JOptionPane;

/**
 *
 * @author etudiant
 */
public class Appli extends Application {
        
        PanneauAccueil vueAccueil;
        PanneauRapports vueRapports = new PanneauRapports();
        PanneauPraticiens vuePraticiens = new PanneauPraticiens();

    public Appli() throws MalformedURLException {
        this.vueAccueil = new PanneauAccueil();
    }
    
    @Override
    public void start(Stage primaryStage) throws ConnexionException, SQLException {
        Button btn = new Button();
        
        MenuBar barreMenus = new MenuBar();
        
        Menu menuFichier = new Menu("Fichier");
        Menu menuRapports = new Menu("Rapports");
        Menu menuPraticiens = new Menu("Praticiens");
        
        MenuItem itemSeConnecter = new MenuItem("Se connecter");        
        MenuItem itemSeDeconnecter = new MenuItem("Se déconnecter");
        itemSeDeconnecter.setDisable(true);
        
        menuPraticiens.setDisable(true);
        menuRapports.setDisable(true);
        
        SeparatorMenuItem separatorQuitter = new SeparatorMenuItem();
        
        MenuItem itemQuitter = new MenuItem("Quitter");
        MenuItem itemConsulter = new MenuItem("Consulter");
        MenuItem itemHesitants = new MenuItem("Hésitants");
        
        StackPane pile = new StackPane();
        
        Label nom = new Label();
        
        itemQuitter.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        itemQuitter.setOnAction((ActionEvent event) -> {
            Alert alertQuitter = new Alert(Alert.AlertType.CONFIRMATION);
            
            ButtonType btnOui = new ButtonType("Oui", OK_DONE);
            ButtonType btnNon = new ButtonType("Non", CANCEL_CLOSE);
            
            alertQuitter.getButtonTypes().setAll(btnOui,btnNon);
            alertQuitter.setTitle("Quitter");
            alertQuitter.setHeaderText("Demande de confirmation");
            alertQuitter.setContentText("Voulez-vous quitter l'application?");
            
            Optional<ButtonType> reponse = alertQuitter.showAndWait();
            
            if(reponse.get() == btnOui){
                Platform.exit();
            }
        });
        
        itemSeConnecter.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        itemSeConnecter.setOnAction((ActionEvent event) -> {
            try {
                VueConnexion vueConnexion = new VueConnexion();
                
                Optional<Pair<String, String>> reponse = vueConnexion.showAndWait();
                
                if(reponse.isPresent()){
                    Visiteur visiteurTest = ModeleGsbRv.seConnecter(reponse.get().getKey(),reponse.get().getValue());
                    
                    if(visiteurTest != null){
                        Session.ouvrir(visiteurTest);
                        
                        itemSeConnecter.setDisable(true);
                        itemSeDeconnecter.setDisable(false);
                        
                        menuPraticiens.setDisable(false);
                        menuRapports.setDisable(false);
                        
                        primaryStage.setTitle(Session.getSession().getLeVisiteur().getNom() + " " + Session.getSession().getLeVisiteur().getPrenom() + "-GSB-RV-DR");
                    }else{
                        JOptionPane.showMessageDialog(null, "Connexion impossible");
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Erreur de la connexion");
                }
            } catch (ConnexionException ex) {
                Logger.getLogger(Appli.class.getName()).log(Level.SEVERE, null, ex);    
            }
        });
        
        itemConsulter.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        itemConsulter.setOnAction((ActionEvent event) -> {
            if(Session.estOuverte() == true){
                vueRapports.toFront();
            }
        });
        
        itemHesitants.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
        itemHesitants.setOnAction((ActionEvent event) -> {
            if(Session.estOuverte() == true){
                vuePraticiens.toFront();
                try {
                    List<Praticien> praticiens = ModeleGsbRv.getPraticiensHesitants();
                    Collections.sort(praticiens, new ComparateurCoefConfiance());
                    
                    Collections.sort(praticiens, new ComparateurCoefNotoriete());
                    Collections.reverse(praticiens);
                    
                    Collections.sort(praticiens, new ComparateurDateVisite());
                    Collections.reverse(praticiens);
                    
                } catch (ConnexionException | SQLException ex) {
                    Logger.getLogger(Appli.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        itemSeDeconnecter.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
        itemSeDeconnecter.setOnAction((ActionEvent event) -> {
            Session.fermer();
            
            itemSeConnecter.setDisable(false);
            itemSeDeconnecter.setDisable(true);
            
            menuPraticiens.setDisable(true);
            menuRapports.setDisable(true);
            
            primaryStage.setTitle("GSB-RV-DR");
            
            vuePraticiens = new PanneauPraticiens();
            vueRapports = new PanneauRapports();
            
            pile.getChildren().add(vueRapports);
            pile.getChildren().add(vuePraticiens);
            
            vueAccueil.toFront();
        });
        
        
        BorderPane root = new BorderPane();
        
        menuFichier.getItems().add(itemSeConnecter);
        menuFichier.getItems().add(itemSeDeconnecter);
        menuFichier.getItems().add(separatorQuitter);
        menuFichier.getItems().add(itemQuitter);
        menuRapports.getItems().add(itemConsulter);
        menuPraticiens.getItems().add(itemHesitants);
        
        barreMenus.getMenus().add(menuFichier);
        barreMenus.getMenus().add(menuRapports);
        barreMenus.getMenus().add(menuPraticiens);
        
        pile.getChildren().add(vueAccueil);
        pile.getChildren().add(vueRapports);
        pile.getChildren().add(vuePraticiens);
        
        vueAccueil.toFront();
        
        Scene scene = new Scene(root, 600, 550);
        
        root.setTop(barreMenus);
        root.setCenter(pile);
        
        primaryStage.setTitle("GSB-RV-DR");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
