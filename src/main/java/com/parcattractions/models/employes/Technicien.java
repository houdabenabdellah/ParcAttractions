package main.java.com.parcattractions.models.employes;

import main.java.com.parcattractions.exceptions.employes.FatigueExcessiveException;
import main.java.com.parcattractions.exceptions.employes.TechnicienIndisponibleException;
import main.java.com.parcattractions.models.attractions.Attraction;
import main.java.com.parcattractions.utils.Logger;

/**
 * Technicien de maintenance
 * Gère les réparations et maintenances
 */
public class Technicien extends Employe {
    
    private Attraction attractionEnCours;
    private volatile boolean enIntervention;
    
    /**
     * Constructeur
     */
    public Technicien(String nom) {
        super(nom, "Technicien");
        this.attractionEnCours = null;
        this.enIntervention = false;
    }
    
    @Override
    protected void travailler() throws InterruptedException {
        // Le technicien attend d'être assigné à une intervention
        // L'assignation se fait via la méthode intervenir()
    }
    
    /**
     * Intervient sur une attraction en panne
     */
    public synchronized void intervenir(Attraction attraction) 
            throws TechnicienIndisponibleException, InterruptedException, 
                   FatigueExcessiveException {
        
        if (!estDisponible()) {
            throw new TechnicienIndisponibleException(id, "Technicien déjà en intervention");
        }
        
        if (enIntervention) {
            throw new TechnicienIndisponibleException(id, "Technicien occupé");
        }
        
        this.attractionEnCours = attraction;
        this.enIntervention = true;
        marquerOccupe();
        
        Logger.logInfo(String.format("%s : Intervention sur %s", nom, attraction.getNom()));
        
        // Diagnostic (5 secondes réelles)
        Logger.logInfo(String.format("%s : Diagnostic en cours...", nom));
        Thread.sleep(5000);
        
        // Réparation (25 secondes réelles)
        Logger.logInfo(String.format("%s : Réparation en cours...", nom));
        Thread.sleep(25000);
        
        // Tests (5 secondes réelles)
        Logger.logInfo(String.format("%s : Tests de sécurité...", nom));
        Thread.sleep(5000);
        
        // Réparation terminée
        attraction.ouvrir(); // Rouvrir l'attraction
        Logger.logInfo(String.format("%s : Intervention terminée sur %s", 
            nom, attraction.getNom()));
        
        // Libérer
        this.attractionEnCours = null;
        this.enIntervention = false;
        marquerDisponible();
    }
    
    /**
     * Effectue une maintenance programmée
     */
    public synchronized void effectuerMaintenance(Attraction attraction) 
            throws TechnicienIndisponibleException, InterruptedException,
                   FatigueExcessiveException {
        
        if (!estDisponible()) {
            throw new TechnicienIndisponibleException(id, "Technicien indisponible");
        }
        
        this.attractionEnCours = attraction;
        this.enIntervention = true;
        marquerOccupe();
        
        Logger.logInfo(String.format("%s : Maintenance programmée sur %s", 
            nom, attraction.getNom()));
        
        // Maintenance (30 secondes réelles = 5 minutes simulées)
        Thread.sleep(30000);
        
        // Maintenance terminée
        attraction.ouvrir();
        Logger.logInfo(String.format("%s : Maintenance terminée sur %s", 
            nom, attraction.getNom()));
        
        // Libérer
        this.attractionEnCours = null;
        this.enIntervention = false;
        marquerDisponible();
    }
    
    /**
     * Vérifie si le technicien est en intervention
     */
    public boolean estEnIntervention() {
        return enIntervention;
    }
    
    /**
     * Retourne l'attraction en cours d'intervention
     */
    public Attraction getAttractionEnCours() {
        return attractionEnCours;
    }
}
