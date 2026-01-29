package main.java.com.parcattractions.models.employes;

import main.java.com.parcattractions.enums.EtatEmploye;
import main.java.com.parcattractions.utils.Logger;

/**
 * Agent de sécurité
 * Patrouille et gère les urgences
 */
public class AgentSecurite extends Employe {
    
    private volatile boolean enPatrouille;
    private volatile String zonePatrouille;
    
    /**
     * Constructeur
     */
    public AgentSecurite(String nom) {
        super(nom, "Agent Sécurité");
        this.enPatrouille = false;
        this.zonePatrouille = null;
    }
    
    @Override
    protected void travailler() throws InterruptedException {
        // Patrouille dans le parc
        if (!enPatrouille) {
            commencerPatrouille();
        } else {
            continuerPatrouille();
        }
    }
    
    /**
     * Commence une patrouille
     */
    private void commencerPatrouille() {
        String[] zones = {"Zone A - Entrée", "Zone B - Attractions", 
                         "Zone C - Restaurants", "Zone D - Sortie"};
        zonePatrouille = zones[(int)(Math.random() * zones.length)];
        enPatrouille = true;
        etat = EtatEmploye.EN_DEPLACEMENT;
        
        Logger.logDebug(String.format("%s : Patrouille commencée dans %s", 
            nom, zonePatrouille));
    }
    
    /**
     * Continue la patrouille
     */
    private void continuerPatrouille() {
        // Vérifications de sécurité aléatoires
        if (Math.random() < 0.05) { // 5% de chance
            Logger.logDebug(String.format("%s : Vérification sécurité dans %s", 
                nom, zonePatrouille));
        }
        
        // Fin de patrouille après un certain temps
        if (Math.random() < 0.1) { // 10% de chance
            terminerPatrouille();
        }
    }
    
    /**
     * Termine la patrouille
     */
    private void terminerPatrouille() {
        enPatrouille = false;
        zonePatrouille = null;
        etat = EtatEmploye.DISPONIBLE;
        
        Logger.logDebug(nom + " : Patrouille terminée");
    }
    
    /**
     * Gère une urgence
     */
    public synchronized void gererUrgence(String description) {
        Logger.logError(String.format("%s : URGENCE - %s", nom, description));
        enPatrouille = false;
        etat = EtatEmploye.OCCUPE;
    }
    
    /**
     * Vérifie si l'agent est en patrouille
     */
    public boolean estEnPatrouille() {
        return enPatrouille;
    }
    
    /**
     * Retourne la zone de patrouille
     */
    public String getZonePatrouille() {
        return zonePatrouille;
    }
}
