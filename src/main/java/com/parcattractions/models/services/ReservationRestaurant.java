package main.java.com.parcattractions.models.services;

import java.time.LocalDateTime;
import main.java.com.parcattractions.models.visiteurs.Visiteur;

/**
 * Représente une réservation de restaurant
 */
public class ReservationRestaurant {
    
    private final int idReservation;
    private final Visiteur visiteur;
    private final Restaurant restaurant;
    private final LocalDateTime dateHeure;
    private final double montant;
    private final String ordreCommmande;
    private EtatReservation etat;
    
    private static int compteurId = 0;
    
    public enum EtatReservation {
        EN_ATTENTE("En attente"),
        EN_SERVICE("En service"),
        TERMINEE("Terminée"),
        ANNULEE("Annulée");
        
        private final String libelle;
        
        EtatReservation(String libelle) {
            this.libelle = libelle;
        }
        
        public String getLibelle() {
            return libelle;
        }
    }
    
    /**
     * Constructeur
     */
    public ReservationRestaurant(Visiteur visiteur, Restaurant restaurant, 
                                  double montant, String ordreCommande) {
        this.idReservation = ++compteurId;
        this.visiteur = visiteur;
        this.restaurant = restaurant;
        this.dateHeure = LocalDateTime.now();
        this.montant = montant;
        this.ordreCommmande = ordreCommande;
        this.etat = EtatReservation.EN_ATTENTE;
    }
    
    // ========== GETTERS ==========
    
    public int getIdReservation() {
        return idReservation;
    }
    
    public Visiteur getVisiteur() {
        return visiteur;
    }
    
    public Restaurant getRestaurant() {
        return restaurant;
    }
    
    public LocalDateTime getDateHeure() {
        return dateHeure;
    }
    
    public double getMontant() {
        return montant;
    }
    
    public String getOrdreCommande() {
        return ordreCommmande;
    }
    
    public EtatReservation getEtat() {
        return etat;
    }
    
    // ========== SETTERS ==========
    
    public void setEtat(EtatReservation etat) {
        this.etat = etat;
    }
    
    @Override
    public String toString() {
        return String.format("Réservation #%d [%s] - %s @ %s - %.2f€ (%s)", 
            idReservation, visiteur.getNomVisiteur(), restaurant.getNom(), 
            dateHeure.toLocalTime(), montant, etat.libelle);
    }
}
