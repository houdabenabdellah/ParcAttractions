package main.java.com.parcattractions.utils;

import  java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Horloge du parc : simulation temps réel (utilisateur contrôle vitesse/pause)
 * ou heure système. Fermeture automatique à 16h00 en mode simulation.
 */
public class Horloge extends Thread {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("HH:mm");
    
    // Heures d'ouverture et fermeture (simulation : 9h → 16h, fermeture auto)
    private static final LocalTime HEURE_OUVERTURE = LocalTime.of(9, 0);
    private static final LocalTime HEURE_FERMETURE = LocalTime.of(16, 0);
    
    /** Callback appelé lorsque l'heure de fermeture est atteinte (ex: fermer le parc). */
    private Runnable onHeureFermeture;
    
    /** Si true, l'heure suit l'heure système et avance en temps réel. */
    private final boolean tempsReel;
    
    // Vitesse de simulation (1 sec réelle = X min simulées) — utilisé si !tempsReel
    private int vitesseSimulation;
    
    // Temps actuel
    private volatile LocalTime heureActuelle;
    
    // État
    private volatile boolean enCours;
    private volatile boolean pausee;
    
    /**
     * Constructeur avec vitesse par défaut (1 sec = 10 min) — mode simulation.
     */
    public Horloge() {
        this(10);
    }
    
    /**
     * Constructeur avec vitesse personnalisée — mode simulation.
     * @param vitesseSimulation Nombre de minutes simulées par seconde réelle
     */
    public Horloge(int vitesseSimulation) {
        super("Horloge");
        this.tempsReel = false;
        this.vitesseSimulation = vitesseSimulation;
        this.heureActuelle = HEURE_OUVERTURE;
        this.enCours = false;
        this.pausee = false;
    }
    
    /**
     * Mode temps réel : heure = heure système, avance en temps réel.
     * @param tempsReel true pour heure système, false pour simulation
     */
    public Horloge(boolean tempsReel) {
        super("Horloge");
        this.tempsReel = tempsReel;
        this.vitesseSimulation = 1;
        this.heureActuelle = java.time.LocalTime.now();
        this.enCours = false;
        this.pausee = false;
    }

    /**
     * Définit le callback appelé quand l'heure de fermeture (16h00) est atteinte.
     */
    public void setOnHeureFermeture(Runnable r) {
        this.onHeureFermeture = r;
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
        if (tempsReel) {
            Logger.logInfo("Horloge démarrée - Mode temps réel (heure système)");
        } else {
            Logger.logInfo("Horloge démarrée - Vitesse: x" + vitesseSimulation);
        }
        
        try {
            while (enCours && !isInterrupted()) {
                // Attendre si en pause
                while (pausee) {
                    Thread.sleep(100);
                }
                
                // Attendre 1 seconde réelle
                Thread.sleep(1000);
                
                if (tempsReel) {
                    heureActuelle = java.time.LocalTime.now();
                } else {
                    // Avancer le temps simulé (contrôle utilisateur : vitesse + pause)
                    heureActuelle = heureActuelle.plusMinutes(vitesseSimulation);
                    // Fermeture automatique à 16h00
                    if (!heureActuelle.isBefore(HEURE_FERMETURE)) {
                        Logger.logInfo("Heure de fermeture atteinte: " + heureActuelle.format(FORMATTER));
                        if (onHeureFermeture != null) {
                            onHeureFermeture.run();
                        }
                        break;
                    }
                    if (heureActuelle.getMinute() == 0) {
                        Logger.logInfo("Heure actuelle: " + heureActuelle.format(FORMATTER));
                    }
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

        // Fermeture automatique à 16h00 si l'utilisateur avance le temps jusqu'à l'heure
        if (!heureActuelle.isBefore(HEURE_FERMETURE) && onHeureFermeture != null) {
            Logger.logInfo("Heure de fermeture atteinte: " + heureActuelle.format(FORMATTER));
            onHeureFermeture.run();
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
     * @return Vrai si le parc est ouvert (en temps réel: toujours true tant que l'horloge tourne ; en simulation: entre 9h et 16h)
     */
    public synchronized boolean parcOuvert() {
        if (tempsReel) return enCours;
        return !heureActuelle.isBefore(HEURE_OUVERTURE) && 
               heureActuelle.isBefore(HEURE_FERMETURE);
    }
    
    /** Heure de fermeture automatique (16h00). */
    public static LocalTime getHeureFermeture() {
        return HEURE_FERMETURE;
    }
    
    /** @return true si l'horloge est en mode temps réel (heure système). */
    public boolean isTempsReel() {
        return tempsReel;
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
