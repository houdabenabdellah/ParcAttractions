package main.java.com.parcattractions.models.employes;

import main.java.com.parcattractions.models.services.Boutique;
import main.java.com.parcattractions.models.services.Restaurant;
import main.java.com.parcattractions.utils.Logger;

/**
 * Vendeur dans les boutiques et restaurants
 */
public class Vendeur extends Employe {
    
    private final Boutique boutique;
    private final Restaurant restaurant;
    
    /**
     * Constructeur pour boutique
     */
    public Vendeur(String nom, Boutique boutique) {
        super(nom, "Vendeur Boutique");
        this.boutique = boutique;
        this.restaurant = null;
    }
    
    /**
     * Constructeur pour restaurant
     */
    public Vendeur(String nom, Restaurant restaurant) {
        super(nom, "Vendeur Restaurant");
        this.boutique = null;
        this.restaurant = restaurant;
    }
    
    @Override
    protected void travailler() throws InterruptedException {
        if (boutique != null) {
            gererBoutique();
        } else if (restaurant != null) {
            gererRestaurant();
        }
    }
    
    /**
     * Gère la boutique
     */
    private void gererBoutique() {
        // Vérifier stock faible
        var stockFaible = boutique.getProduitsStockFaible();
        if (!stockFaible.isEmpty()) {
            Logger.logWarning(String.format("%s : Stock faible détecté dans %s", 
                nom, boutique.getNom()));
        }
        
        // Vérifier produits épuisés
        var produitsEpuises = boutique.getProduitsEpuises();
        if (!produitsEpuises.isEmpty()) {
            Logger.logWarning(String.format("%s : Produits épuisés dans %s", 
                nom, boutique.getNom()));
        }
    }
    
    /**
     * Gère le restaurant
     */
    private void gererRestaurant() {
        // Vérifier occupation
        double tauxOccupation = restaurant.getTauxOccupation();
        if (tauxOccupation > 0.9) {
            Logger.logInfo(String.format("%s : Restaurant %s presque complet (%.0f%%)", 
                nom, restaurant.getNom(), tauxOccupation * 100));
        }
    }
    
    /**
     * Retourne la boutique assignée
     */
    public Boutique getBoutique() {
        return boutique;
    }
    
    /**
     * Retourne le restaurant assigné
     */
    public Restaurant getRestaurant() {
        return restaurant;
    }
}
