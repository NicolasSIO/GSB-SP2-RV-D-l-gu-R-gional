/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gsb.rv.dr;

import java.io.File;
import java.net.MalformedURLException;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author etudiant
 */
public class PanneauAccueil extends Pane{
    public PanneauAccueil() throws MalformedURLException{
        super();
        this.setStyle("-fx-alignement: center; -fx-background-color: white;");
                
        File file = new File("/home/etudiant/Téléchargements/index.jpeg");
 
        String localUrl = file.toURI().toURL().toString();

        Image image = new Image(localUrl);
        
        ImageView iv1 = new ImageView();
        iv1.setImage(image);
        iv1.setLayoutX(160);
        iv1.setLayoutY(170);
        
        this.getChildren().addAll(iv1);
    }
}
