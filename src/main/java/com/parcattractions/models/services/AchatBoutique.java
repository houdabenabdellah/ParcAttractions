package main.java.com.parcattractions.models.services;

import java.time.LocalDateTime;
import main.java.com.parcattractions.models.visiteurs.Visiteur;

/**
 * Représente un achat effectué dans une boutique
 */
public class AchatBoutique {
    
    private final int idAchat;
    private final Visiteur visiteur;
    private final Boutique boutique;
    private final LocalDateTime dateHeure;
    private final Produit produit;
    private final double montant;
    
    private static int compteurId = 0;
    
    /**
     * Constructeur
     */
    public AchatBoutique(Visiteur visiteur, Boutique boutique, 
                         Produit produit, double montant) {
        this.idAchat = ++compteurId;
        this.visiteur = visiteur;
        this.boutique = boutique;
        this.dateHeure = LocalDateTime.now();
        this.produit = produit;
        this.montant = montant;
    }
    
    // ========== GETTERS ==========
    
    public int getIdAchat() {
        return idAchat;
    }
    
    public Visiteur getVisiteur() {
        return visiteur;
    }
    
    public Boutique getBoutique() {
        return boutique;
    }
    
    public LocalDateTime getDateHeure() {
        return dateHeure;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public double getMontant() {
        return montant;
    }
    
    @Override
    public String toString() {
        return String.format("Achat #%d [%s] - %s @ %s - %s (%.2f€)", 
            idAchat, visiteur.getNomVisiteur(), boutique.getNom(), 
            dateHeure.toLocalTime(), produit.getNom(), montant);
    }
}
