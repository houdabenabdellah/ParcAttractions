package main.java.com.parcattractions.controllers;

import java.util.Random;
import main.java.com.parcattractions.enums.Meteo;
import main.java.com.parcattractions.utils.Logger;

/**
 * Gestionnaire de météo dynamique
 * Change la météo périodiquement selon les probabilités
 */
public class GestionnaireMeteo extends Thread {
    
    private static final Random random = new Random();
    
    // Durée minimale et maximale d'une condition météo (en minutes simulées)
    private static final int DUREE_MIN_METEO = 5;
    private static final int DUREE_MAX_METEO = 30;
    
    // Intervalle de vérification (en secondes réelles)
    private static final long INTERVALLE_VERIFICATION = 60000; // 60 secondes = 1 minute réelle
    
    private final GestionnaireParc gestionnaireParc;
    private volatile boolean actif;
    private volatile int dureeMeteoRestante; // en minutes simulées
    
    /**
     * Constructeur
     */
    public GestionnaireMeteo(GestionnaireParc gestionnaireParc) {
        super("GestionnaireMeteo");
        this.gestionnaireParc = gestionnaireParc;
        this.actif = false;
        this.dureeMeteoRestante = 0;
    }
    
    @Override
    public void run() {
        Logger.logThreadStart(getName());
        actif = true;
        
        // Initialiser avec météo ensoleillée
        gestionnaireParc.setMeteoActuelle(Meteo.ENSOLEILLE);
        dureeMeteoRestante = DUREE_MIN_METEO + random.nextInt(DUREE_MAX_METEO - DUREE_MIN_METEO);
        
        try {
            while (actif) {
                if (gestionnaireParc.estOuvert()) {
                    // Attendre l'intervalle de vérification
                    Thread.sleep(INTERVALLE_VERIFICATION);
                    
                    // Décrémenter durée restante (1 min réelle = 10 min simulées)
                    dureeMeteoRestante -= 10;
                    
                    // Si durée écoulée, changer météo
                    if (dureeMeteoRestante <= 0) {
                        changerMeteo();
                        dureeMeteoRestante = DUREE_MIN_METEO + random.nextInt(DUREE_MAX_METEO - DUREE_MIN_METEO);
                    }
                } else {
                    Thread.sleep(5000); // Attendre 5 s avant de revérifier si le parc est ouvert
                }
            }
        } catch (InterruptedException e) {
            Logger.logInfo("GestionnaireMeteo interrompu");
            Thread.currentThread().interrupt();
        } finally {
            actif = false;
            Logger.logThreadStop(getName());
        }
    }
    
    /**
     * Change la météo selon les probabilités
     */
    private void changerMeteo() {
        Meteo meteoActuelle = gestionnaireParc.getMeteoActuelle();
        Meteo nouvelleMeteo = meteoActuelle.getMeteoSuivanteProbable();
        
        gestionnaireParc.setMeteoActuelle(nouvelleMeteo);
        
        Logger.logInfo(String.format("Météo: %s → %s (durée: %d min)", 
            meteoActuelle, nouvelleMeteo, dureeMeteoRestante));
    }
    
    /**
     * Force une météo spécifique (pour tests)
     */
    public void forcerMeteo(Meteo meteo) {
        gestionnaireParc.setMeteoActuelle(meteo);
        dureeMeteoRestante = DUREE_MIN_METEO + random.nextInt(DUREE_MAX_METEO - DUREE_MIN_METEO);
        Logger.logInfo("Météo forcée: " + meteo);
    }
    
    /**
     * Retourne la durée restante de la météo actuelle
     */
    public int getDureeMeteoRestante() {
        return dureeMeteoRestante;
    }
    
    /**
     * Arrête le gestionnaire
     */
    public void arreter() {
        actif = false;
        interrupt();
    }
}
