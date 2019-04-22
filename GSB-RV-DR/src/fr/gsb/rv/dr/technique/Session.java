/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gsb.rv.dr.technique;

import fr.gsb.rv.dr.entites.Visiteur;

/**
 *
 * @author etudiant
 */
public class Session {
    private static Session session = null;
    private Visiteur leVisiteur;
    
    public Session (Visiteur leVisiteur){
        this.leVisiteur = leVisiteur;
    }
    
    public static void ouvrir(Visiteur leVisiteur){
        session = new Session(leVisiteur);
    }
    
    public static void fermer(){
        session = null;
    }
    
    public static Session getSession(){
        return session;
    }
    
    public void setSession(Session session){
        this.session = session;
    }
    
    public Visiteur getLeVisiteur(){
        return leVisiteur;
    }
    
    public void setLeVisiteur(){
        this.leVisiteur = leVisiteur;
    }
    
    public static boolean estOuverte(){
        if(session != null){
            return true;
        }else{
            return false;
        }
    }
}
