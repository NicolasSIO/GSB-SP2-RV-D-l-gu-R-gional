package fr.gsb.rv.dr.modele;

import fr.gsb.rv.dr.entites.Praticien;
import fr.gsb.rv.dr.entites.RapportVisite;
import fr.gsb.rv.dr.entites.Visiteur;
import fr.gsb.rv.dr.technique.ConnexionBD;
import fr.gsb.rv.dr.technique.ConnexionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModeleGsbRv {
    
    public static Visiteur seConnecter( String matricule , String mdp ) throws ConnexionException{
        
        Connection connexion = ConnexionBD.getConnexion() ;
        
        String requete = "select v.vis_matricule, v.vis_nom, v.vis_prenom, t1.tra_role, t1.jjmmaa "
                + "from Travailler t1 "
                + "inner join Visiteur v "
                + "on v.vis_matricule = t1.vis_matricule "
                + "where t1.vis_matricule = \""+matricule+"\" "
                + "and v.vis_mdp = \""+mdp+"\" "
                + "and jjmmaa = (select max(jjmmaa) "
                + "from Travailler t2 "
                + "where t2.vis_matricule = t1.vis_matricule) "
                + "and t1.tra_role = \"Délégué\";";
                    
        
        try {
            PreparedStatement requetePreparee = (PreparedStatement) connexion.prepareStatement( requete ) ;
            ResultSet resultat = requetePreparee.executeQuery() ;
            
            if( resultat.next() ){
                
                Visiteur visiteur = new Visiteur() ;
                visiteur.setMatricule( matricule );
                visiteur.setNom( resultat.getString( "vis_nom" ) ) ;
                visiteur.setPrenom( resultat.getString( "vis_prenom" ) ) ;
                
                requetePreparee.close() ;
                return visiteur ;
            }
            else {
                return null ;
            }
        }
        catch( Exception e ){
            return null ;
        } 
    }
    
    public static List<Praticien> getPraticiensHesitants() throws ConnexionException, SQLException{
        
        Connection connexion = ConnexionBD.getConnexion() ;
        
        String requete = "select * "
                + "from RapportVisite r1, Praticien p1 "
                + "where r1.rap_coeff_confiance < 5 "
                + "and r1.pra_num = p1.pra_num "
                + "and r1.rap_date_visite = "
                + "(select max(r2.rap_date_visite) "
                + "from RapportVisite r2 "
                + "where r2.pra_num = r1.pra_num and r2.vis_matricule = r1.vis_matricule);";              
        
        try {            
            Statement  requetePreparee = (Statement) connexion.createStatement();
            ResultSet resultat = requetePreparee.executeQuery(requete) ;
            
            List<Praticien> listePra = new ArrayList<Praticien>();   
            
            while ( resultat.next() ){
                
                Praticien pra = new Praticien();
                pra.setNumero(resultat.getInt("pra_num"));
                pra.setNom(resultat.getString("pra_nom"));
                pra.setVille(resultat.getString("pra_ville"));
                pra.setCoefNotoriete(resultat.getDouble("pra_coefnotoriete"));
                pra.setDateDerniereVisite(resultat.getDate("rap_date_visite").toLocalDate());
                pra.setDernierCoefConfiance(resultat.getInt("rap_coeff_confiance"));    
                
                listePra.add(pra);              
            }
            return listePra;
           }
        catch( Exception e ){
            return null ;
        }
    }
    
    public static List<Visiteur> getVisiteur() throws ConnexionException, SQLException{
        
        Connection connexion = ConnexionBD.getConnexion() ;
        
        String requete = "select vis_matricule, vis_nom, vis_prenom "
                + "from Visiteur;";              
        
        try {            
            Statement  requetePreparee = (Statement) connexion.createStatement();
            ResultSet resultat = requetePreparee.executeQuery(requete);
            
            List<Visiteur> listeVisi = new ArrayList<Visiteur>();   
            
            while ( resultat.next() ){
                Visiteur visi = new Visiteur();
                visi.setMatricule(resultat.getString("vis_matricule"));
                visi.setNom(resultat.getString("vis_nom"));
                visi.setPrenom(resultat.getString("vis_prenom"));  
                
                listeVisi.add(visi);              
            }
            return listeVisi;
           }
        catch( Exception e ){
            return null ;
        }
    }
    
    public static List<RapportVisite> getRapportVisite(String matricule, int annee, int mois) throws ConnexionException, SQLException{
        
        Connection connexion = ConnexionBD.getConnexion() ;
        
        String requete = "select * "
                + "from RapportVisite rap "
                + "inner join Praticien pra "
                + "on rap.pra_num = pra.pra_num "
                + "inner join Visiteur v "
                + "on rap.vis_matricule = v.vis_matricule "
                + "inner join Motif m "
                + "on m.mo_code = rap.mo_code "
                + "where rap.vis_matricule = \"" + matricule +"\" "
                + "and rap.rap_date_visite like \""+ annee + "-" + "%" + mois + "%\";";              
        
        try {            
            Statement  requetePreparee = (Statement) connexion.createStatement();
            ResultSet resultat = requetePreparee.executeQuery(requete) ;
            
            List<RapportVisite> listeRap = new ArrayList<RapportVisite>(); 
            
            while ( resultat.next() ){
                
                RapportVisite rap = new RapportVisite();
                
                rap.setNumero(resultat.getInt("rap_num"));
                rap.setDateVisite(resultat.getDate("rap_date_visite").toLocalDate());
                rap.setDateRedaction(resultat.getDate("rap_date_redaction").toLocalDate());
                rap.setBilan(resultat.getString("rap_bilan"));
                rap.setMotif(resultat.getString("mo_libelle"));
                rap.setCoefConfiance(resultat.getInt("rap_coeff_confiance"));
                rap.setLu(resultat.getBoolean("rap_lu"));
                
                Visiteur leVisiteur = new Visiteur();
                leVisiteur.setMatricule(resultat.getString("vis_matricule"));
                leVisiteur.setNom(resultat.getString("vis_nom"));
                leVisiteur.setPrenom(resultat.getString("vis_prenom"));
                rap.setLeVisiteur(leVisiteur);
                
                Praticien lePraticien = new Praticien();
                lePraticien.setNumero(resultat.getInt("pra_num"));
                lePraticien.setVille(resultat.getString("pra_ville"));
                lePraticien.setNom(resultat.getString("pra_nom"));
                lePraticien.setPrenom(resultat.getString("pra_prenom"));
                rap.setLePraticien(lePraticien);
                
                listeRap.add(rap);              
            }
            return listeRap;
           }
        catch( SQLException e ){
            Logger.getLogger(ModeleGsbRv.class.getName()).log(Level.SEVERE, null,e);
            return null ;
        }
    }
    
    public static List<RapportVisite> setRapportVisiteLu(String matricule, int numRapport) throws ConnexionException, SQLException{
        
        Connection connexion = ConnexionBD.getConnexion();
        
        String requete = "update RapportVisite "
                + "set rap_lu = \"1\" "
                + "where rap_num = \"" + numRapport + "\" "
                + "and vis_matricule = \"" + matricule + "\";";
        try {            
                Statement  requetePreparee = (Statement) connexion.createStatement();
                requetePreparee.executeUpdate(requete) ;
               }
        catch( Exception e ){
            return null ;
        }
        return null;
    }
    
    public static List<RapportVisite> setRapportVisiteNonLu(String matricule, int numRapport) throws ConnexionException, SQLException{
        
        Connection connexion = ConnexionBD.getConnexion();
        
        String requete = "update RapportVisite "
                + "set rap_lu = \"0\" "
                + "where rap_num = \"" + numRapport + "\" "
                + "and vis_matricule = \"" + matricule + "\";";
        try {            
                Statement  requetePreparee = (Statement) connexion.createStatement();
                requetePreparee.executeUpdate(requete) ;
               }
        catch( Exception e ){
            return null ;
        }
        return null;
    }
}