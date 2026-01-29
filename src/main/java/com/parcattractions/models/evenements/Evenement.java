package main.java.com.parcattractions.models.evenements;

import java.time.LocalTime;

/**
 * Classe abstraite représentant un événement spécial
 */
public abstract class Evenement {
    
    protected final String nom;
    protected final LocalTime heureDebut;
    protected final int dureeMinutes;
    
    /**
     * Constructeur
     */
    protected Evenement(String nom, LocalTime heureDebut, int dureeMinutes) {
        this.nom = nom;
        this.heureDebut = heureDebut;
        this.dureeMinutes = dureeMinutes;
    }
    
    /**
     * Retourne le nom de l'événement
     */
    public String getNom() {
        return nom;
    }
    
    /**
     * Retourne l'heure de début
     */
    public LocalTime getHeureDebut() {
        return heureDebut;
    }
    
    /**
     * Retourne la durée en minutes
     */
    public int getDureeMinutes() {
        return dureeMinutes;
    }
    
    /**
     * Vérifie si l'événement est actif à l'heure donnée
     */
    public boolean estActif(LocalTime heure) {
        LocalTime heureFin = heureDebut.plusMinutes(dureeMinutes);
        return !heure.isBefore(heureDebut) && heure.isBefore(heureFin);
    }
    
    @Override
    public String toString() {
        return nom;
    }
}
