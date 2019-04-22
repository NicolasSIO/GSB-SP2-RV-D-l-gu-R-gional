/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gsb.rv.dr;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 *
 * @author etudiant
 */
public class VueConnexion extends Dialog<Pair<String,String>>{

    public VueConnexion() {
        Label lMatricule = new Label("Matricule");
        Label lPassword = new Label("Password");
        
        TextField tfMatricule = new TextField();
        PasswordField pfPassword = new PasswordField();
        
        VBox vbox = new VBox(15);
        HBox hbox = new HBox(15);
        HBox hbox2 = new HBox(15);
        
        //Dialog<Pair<String, String>> vueConnexion = new Dialog<Pair<String, String>>();
        
        setTitle("Connexion");
        setHeaderText("Veuillez vous connecter");
        
        hbox.getChildren().addAll(lMatricule, tfMatricule);
        hbox2.getChildren().addAll(lPassword, pfPassword);
        vbox.getChildren().addAll(hbox, hbox2);
        
        getDialogPane().setContent(vbox);
        
        ButtonType btnValider = new ButtonType("Valider", OK_DONE);
        ButtonType btnAnnuler = new ButtonType("Annuler", CANCEL_CLOSE); 
        
        setResultConverter(
        new Callback<ButtonType,Pair<String, String>>(){
            @Override
            public Pair<String, String> call(ButtonType typeBouton){
                if(typeBouton == btnValider){
                    return new Pair<String, String>(tfMatricule.getText(), pfPassword.getText());
                }
                return null;
            }
        });
        
        getDialogPane().getButtonTypes().addAll(btnValider, btnAnnuler);
        
    }
}
