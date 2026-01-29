package main.java.com.parcattractions.utils;

import  java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Horloge de simulation du parc
 * Gère le temps virtuel accéléré
 */
public class Horloge extends Thread {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("HH:mm");
    
    // Heures d'ouverture
    private static final LocalTime HEURE_OUVERTURE = LocalTime.of(9, 0);
    private static final LocalTime HEURE_FERMETURE = LocalTime.of(22, 0);
    
    // Vitesse de simulation (1 seconde réelle = X minutes simulées)
    private int vitesseSimulation;
    
    // Temps actuel
    private LocalTime heureActuelle;
    
    // État
    private volatile boolean enCours;
    private volatile boolean pausee;
    
    /**
     * Constructeur avec vitesse par défaut (1 sec = 10 min)
     */
    public Horloge() {
        this(10);
    }
    
    /**
     * Constructeur avec vitesse personnalisée
     * @param vitesseSimulation Nombre de minutes simulées par seconde réelle
     */
    public Horloge(int vitesseSimulation) {
        super("Horloge");
        this.vitesseSimulation = vitesseSimulation;
        this.heureActuelle = HEURE_OUVERTURE;
        this.enCours = false;
        this.pausee = false;
    }

    /**
     * Change la vitesse de simulation (minutes simulées par seconde réelle)
     */
    public synchronized void setVitesseSimulation(int nouvelleVitesse) {
        if (nouvelleVitesse <= 0) return;
        this.vitesseSimulation = nouvelleVitesse;
        Logger.logInfo("Vitesse de simulation changée: x" + nouvelleVitesse);
    }
    
    @Override
    public void run() {
        enCours = true;
        Logger.logThreadStart("Horloge");
        Logger.logInfo("Horloge démarrée - Vitesse: x" + vitesseSimulation);
        
        try {
            while (enCours && !isInterrupted()) {
                // Attendre si en pause
                while (pausee) {
                    Thread.sleep(100);
                }
                
                // Attendre 1 seconde réelle
                Thread.sleep(1000);
                
                // Avancer le temps simulé
                heureActuelle = heureActuelle.plusMinutes(vitesseSimulation);
                
                // Vérifier heure de fermeture
                if (heureActuelle.isAfter(HEURE_FERMETURE)) {
                    Logger.logInfo("Heure de fermeture atteinte: " + heureActuelle.format(FORMATTER));
                    // TODO: Signaler fermeture du parc
                    break;
                }
                
                // Log toutes les heures simulées
                if (heureActuelle.getMinute() == 0) {
                    Logger.logInfo("Heure actuelle: " + heureActuelle.format(FORMATTER));
                }
            }
        } catch (InterruptedException e) {
            Logger.logInfo("Horloge interrompue");
            Thread.currentThread().interrupt();
        } finally {
            enCours = false;
            Logger.logThreadStop("Horloge");
        }
    }
    
    /**
     * Met en pause l'horloge
     */
    public synchronized void mettrePause() {
        pausee = true;
        Logger.logInfo("Horloge en pause");
    }
    
    /**
     * Reprend l'horloge
     */
    public synchronized void reprendre() {
        pausee = false;
        Logger.logInfo("Horloge reprise");
    }

    /**
     * Avance l'horloge d'un pas (vitesseSimulation minutes).
     * Utile pour le mode pas-à-pas lorsque l'horloge est en pause.
     */
    public synchronized void avancerUnPas() {
        // Si l'horloge n'est pas en pause, on la met en pause temporairement
        if (!pausee) {
            mettrePause();
        }

        heureActuelle = heureActuelle.plusMinutes(vitesseSimulation);
        Logger.logInfo("Horloge avancée d'un pas: " + heureActuelle.format(FORMATTER));

        // Vérifier heure de fermeture
        if (heureActuelle.isAfter(HEURE_FERMETURE)) {
            Logger.logInfo("Heure de fermeture atteinte: " + heureActuelle.format(FORMATTER));
        }
    }

    /**
     * Indique si l'horloge est en pause
     */
    public boolean estEnPause() {
        return pausee;
    }
    
    /**
     * Arrête l'horloge
     */
    public void arreter() {
        enCours = false;
        interrupt();
    }
    
    /**
     * @return Heure actuelle simulée
     */
    public synchronized LocalTime getHeureActuelle() {
        return heureActuelle;
    }
    
    /**
     * @return Heure actuelle formatée (HH:mm)
     */
    public synchronized String getHeureFormatee() {
        return heureActuelle.format(FORMATTER);
    }
    
    /**
     * @return Vitesse de simulation
     */
    public int getVitesseSimulation() {
        return vitesseSimulation;
    }
    
    /**
     * @return Vrai si le parc est ouvert (entre 9h et 22h)
     */
    public synchronized boolean parcOuvert() {
        return !heureActuelle.isBefore(HEURE_OUVERTURE) && 
               heureActuelle.isBefore(HEURE_FERMETURE);
    }
    
    /**
     * @return Vrai si c'est le matin (9h-12h)
     */
    public synchronized boolean estMatin() {
        return heureActuelle.getHour() >= 9 && heureActuelle.getHour() < 12;
    }
    
    /**
     * @return Vrai si c'est l'après-midi (12h-18h)
     */
    public synchronized boolean estApresMidi() {
        return heureActuelle.getHour() >= 12 && heureActuelle.getHour() < 18;
    }
    
    /**
     * @return Vrai si c'est le soir (18h-22h)
     */
    public synchronized boolean estSoir() {
        return heureActuelle.getHour() >= 18 && heureActuelle.getHour() < 22;
    }
    
    /**
     * Réinitialise l'heure à l'ouverture
     */
    public synchronized void reinitialiser() {
        heureActuelle = HEURE_OUVERTURE;
        Logger.logInfo("Horloge réinitialisée à " + HEURE_OUVERTURE);
    }
}
