package main.java.com.parcattractions.models.employes;

import main.java.com.parcattractions.controllers.SystemeNotifications;
import main.java.com.parcattractions.enums.EtatAttraction;
import main.java.com.parcattractions.enums.TypeNotification;
import main.java.com.parcattractions.models.attractions.Attraction;
import main.java.com.parcattractions.utils.Logger;

/**
 * Opérateur d'attraction
 * Un opérateur par attraction
 */
public class Operateur extends Employe {
    
    private final Attraction attraction;
    
    /**
     * Constructeur
     */
    public Operateur(String nom, Attraction attraction) {
        super(nom, "Opérateur");
        this.attraction = attraction;
    }
    
    @Override
    protected void travailler() throws InterruptedException {
        // L'opérateur surveille son attraction
        // Vérifie l'état et gère les opérations
        
        if (attraction.getEtat() == EtatAttraction.OPERATIONNELLE) {
            // Attraction fonctionne normalement
            // L'opérateur peut effectuer des vérifications de sécurité
            if (Math.random() < 0.1) { // 10% de chance
                verifierSecurite();
            }
        } else if (attraction.getEtat() == EtatAttraction.PANNE) {
            // En cas de panne, l'opérateur doit gérer l'évacuation
            gererEvacuation();
        }
    }
    
    /**
     * Vérifie la sécurité de l'attraction
     */
    private void verifierSecurite() {
        Logger.logDebug(String.format("%s : Vérification sécurité de %s", 
            nom, attraction.getNom()));
    }
    
    /**
     * Gère l'évacuation en cas de panne
     */
    private void gererEvacuation() {
        Logger.logWarning(String.format("%s : Gestion évacuation de %s", 
            nom, attraction.getNom()));
        // L'évacuation est déjà gérée par l'attraction elle-même
    }
    
    /**
     * Retourne l'attraction assignée
     */
    public Attraction getAttraction() {
        return attraction;
    }
    
    /**
     * Notifie une panne sur l'attraction assignée (diagramme maintenance).
     */
    public void notifierPanne() {
        Logger.logWarning(String.format("%s : Panne signalée sur %s", nom, attraction.getNom()));
        SystemeNotifications.getInstance().ajouterNotification(
            TypeNotification.ATTENTION, "Panne " + attraction.getNom() + " – Opérateur notifié");
    }
}
