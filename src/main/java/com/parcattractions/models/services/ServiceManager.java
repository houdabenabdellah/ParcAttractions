package main.java.com.parcattractions.models.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.exceptions.services.RestaurantPleinException;
import main.java.com.parcattractions.exceptions.services.StockEpuiseException;
import main.java.com.parcattractions.models.visiteurs.Visiteur;
import main.java.com.parcattractions.utils.CSVManager;
import main.java.com.parcattractions.utils.Logger;
import main.java.com.parcattractions.utils.TransactionManager;

/**
 * ServiceManager - Gestionnaire centralisé des services (restaurants & boutiques)
 * Gère les réservations, achats, et revenus
 */
public class ServiceManager {
    
    private final List<Restaurant> restaurants;
    private final List<Boutique> boutiques;
    private final List<ReservationRestaurant> reservations;
    private final List<AchatBoutique> achats;
    
    /**
     * Constructeur
     */
    public ServiceManager() {
        this.restaurants = new CopyOnWriteArrayList<>();
        this.boutiques = new CopyOnWriteArrayList<>();
        this.reservations = new CopyOnWriteArrayList<>();
        this.achats = new CopyOnWriteArrayList<>();
        
        Logger.logInfo("ServiceManager initialisé");
    }
    
    // ============= GESTION RESTAURANTS =============
    
    /**
     * Ajoute un restaurant
     */
    public synchronized void ajouterRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
        Logger.logInfo("Restaurant ajouté: " + restaurant.getNom());
    }
    
    /**
     * Retourne tous les restaurants
     */
    public List<Restaurant> getRestaurants() {
        return Collections.unmodifiableList(restaurants);
    }
    
    /**
     * Obtient un restaurant par nom
     */
    public Restaurant getRestaurantParNom(String nom) {
        return restaurants.stream()
            .filter(r -> r.getNom().equalsIgnoreCase(nom))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Effectue une réservation de restaurant
     * @param visiteur Le visiteur
     * @param nomRestaurant Nom du restaurant
     * @param montant Montant de la réservation
     * @param ordreCommande Commande
     * @return Réservation effectuée ou message d'erreur
     */
    public String effectuerReservationRestaurant(Visiteur visiteur, String nomRestaurant, 
                                                  double montant, String ordreCommande) {
        // Vérifier budget du visiteur
        if (visiteur.getArgent() < montant) {
            return "Budget insuffisant. Montant: " + montant + "€, Disponible: " + visiteur.getArgent() + "€";
        }
        
        // Récupérer le restaurant
        Restaurant resto = getRestaurantParNom(nomRestaurant);
        if (resto == null) {
            return "Restaurant non trouvé: " + nomRestaurant;
        }
        // Vérifier que le restaurant est en service
        if (!resto.isEnService()) {
            return "Restaurant " + nomRestaurant + " est hors service";
        }
        
        // Vérifier disponibilité
        if (resto.estComplet()) {
            return "Restaurant " + nomRestaurant + " est complet";
        }
        
        try {
            // Ajouter à la file
            resto.ajouterFile((int) visiteur.getId());
            
            // Débiter le visiteur
            visiteur.payer(montant);
            // Mettre à jour le solde du visiteur dans le CSV
            CSVManager.mettreAJourArgentVisiteur((int) visiteur.getId(), visiteur.getArgent());
            // Sauvegarder les visiteurs (persist les changements dans visiteurs.csv)
            GestionnaireParc.getInstance().sauvegarderVisiteurs();

            // Créer la réservation
            ReservationRestaurant reservation = new ReservationRestaurant(
                visiteur, resto, montant, ordreCommande);
            reservations.add(reservation);
            
            // Ajouter revenus au restaurant (interne)
            resto.ajouterRevenu(montant);
            // Persister la vente au restaurant dans data/
            CSVManager.enregistrerVenteRestaurant((int) visiteur.getId(), ordreCommande, montant);
            // Enregistrer dans TransactionManager pour affichage dans le dashboard
            TransactionManager.enregistrerVenteRestaurant((int) visiteur.getId(), ordreCommande, montant);

            Logger.logInfo("Réservation effectuée: " + reservation);

            return "Réservation confirmée: " + ordreCommande + " (Montant: " + montant + "€)";
            
        } catch (RestaurantPleinException e) {
            return "Impossible d'ajouter à la file: " + e.getMessage();
        }
    }
    
    /**
     * Annule une réservation
     */
    public String annulerReservation(int idReservation) {
        ReservationRestaurant res = reservations.stream()
            .filter(r -> r.getIdReservation() == idReservation)
            .findFirst()
            .orElse(null);
        
        if (res == null) {
            return "Réservation non trouvée";
        }
        
        if (res.getEtat() == ReservationRestaurant.EtatReservation.TERMINEE) {
            return "Impossible d'annuler une réservation terminée";
        }
        
        // Rembourser le visiteur
        res.getVisiteur().payer(-res.getMontant()); // Négatif = crédit
        res.setEtat(ReservationRestaurant.EtatReservation.ANNULEE);
        
        Logger.logInfo("Réservation annulée: #" + idReservation);
        return "Réservation annulée. Remboursement: " + res.getMontant() + "€";
    }
    
    /**
     * Retourne toutes les réservations
     */
    public List<ReservationRestaurant> getReservations() {
        return Collections.unmodifiableList(reservations);
    }
    
    /**
     * Retourne les réservations d'un visiteur
     */
    public List<ReservationRestaurant> getReservationsVisiteur(Visiteur visiteur) {
        List<ReservationRestaurant> result = new ArrayList<>();
        for (ReservationRestaurant res : reservations) {
            if (res.getVisiteur() == visiteur) {
                result.add(res);
            }
        }
        return result;
    }
    
    // ============= GESTION BOUTIQUES =============
    
    /**
     * Ajoute une boutique
     */
    public synchronized void ajouterBoutique(Boutique boutique) {
        boutiques.add(boutique);
        Logger.logInfo("Boutique ajoutée: " + boutique.getNom());
    }
    
    /**
     * Retourne toutes les boutiques
     */
    public List<Boutique> getBoutiques() {
        return Collections.unmodifiableList(boutiques);
    }
    
    /**
     * Obtient une boutique par nom
     */
    public Boutique getBoutiqueParNom(String nom) {
        return boutiques.stream()
            .filter(b -> b.getNom().equalsIgnoreCase(nom))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Effectue un achat dans une boutique
     * @param visiteur Le visiteur
     * @param nomBoutique Nom de la boutique
     * @param produit Le produit à acheter
     * @return Message de succès ou d'erreur
     */
    public String effectuerAchatBoutique(Visiteur visiteur, String nomBoutique, Produit produit) {
        // Vérifier budget du visiteur
        if (visiteur.getArgent() < produit.getPrix()) {
            return "Budget insuffisant. Prix: " + produit.getPrix() + "€, Disponible: " + visiteur.getArgent() + "€";
        }
        
        // Récupérer la boutique
        Boutique boutique = getBoutiqueParNom(nomBoutique);
        if (boutique == null) {
            return "Boutique non trouvée: " + nomBoutique;
        }
        // Vérifier que la boutique est ouverte
        if (!boutique.isOuverte()) {
            return "Boutique " + nomBoutique + " est fermée";
        }
        
        try {
            // Vendre le produit
            boutique.vendre(produit, (int) visiteur.getId());
            
            // Débiter le visiteur
            visiteur.payer(produit.getPrix());
            // Mettre à jour le solde du visiteur dans le CSV
            CSVManager.mettreAJourArgentVisiteur((int) visiteur.getId(), visiteur.getArgent());
            // Sauvegarder les visiteurs (persist les changements dans visiteurs.csv)
            GestionnaireParc.getInstance().sauvegarderVisiteurs();

            // Enregistrer l'achat
            AchatBoutique achat = new AchatBoutique(
                visiteur, boutique, produit, produit.getPrix());
            achats.add(achat);

            // Ajouter revenus à la boutique
            boutique.ajouterRevenu(produit.getPrix());
            // Persister la vente souvenir
            CSVManager.enregistrerVenteSouvenir((int) visiteur.getId(), produit.getNom(), produit.getPrix());
            // Enregistrer dans TransactionManager pour affichage dans le dashboard
            TransactionManager.enregistrerVenteSouvenir((int) visiteur.getId(), produit.getNom(), produit.getPrix());

            Logger.logInfo("Achat effectué: " + achat);

            return "Achat confirmé: " + produit.getNom() + " (" + produit.getPrix() + "€)";
            
        } catch (StockEpuiseException e) {
            return "Produit épuisé: " + e.getMessage();
        }
    }
    
    /**
     * Retourne tous les achats
     */
    public List<AchatBoutique> getAchats() {
        return Collections.unmodifiableList(achats);
    }
    
    /**
     * Retourne les achats d'un visiteur
     */
    public List<AchatBoutique> getAchatsVisiteur(Visiteur visiteur) {
        List<AchatBoutique> result = new ArrayList<>();
        for (AchatBoutique achat : achats) {
            if (achat.getVisiteur() == visiteur) {
                result.add(achat);
            }
        }
        return result;
    }
    
    // ============= GESTION DES REVENUS =============
    
    /**
     * Calcule le revenu total de tous les restaurants
     */
    public double getRevenusRestaurants() {
        double total = 0;
        for (Restaurant resto : restaurants) {
            total += resto.getRevenus();
        }
        return total;
    }
    
    /**
     * Calcule le revenu total de toutes les boutiques
     */
    public double getRevenusBoutiques() {
        double total = 0;
        for (Boutique boutique : boutiques) {
            total += boutique.getRevenus();
        }
        return total;
    }
    
    /**
     * Calcule le revenu total des services (restaurants + boutiques)
     */
    public double getRevenusTotal() {
        return getRevenusRestaurants() + getRevenusBoutiques();
    }
    
    /**
     * Retourne un récapitulatif des revenus
     */
    public String genererRecapitulatifRevenus() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("   RÉCAPITULATIF DES REVENUS - SERVICES\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");
        
        // Restaurants
        sb.append("--- RESTAURANTS ---\n");
        double totalResto = 0;
        for (Restaurant resto : restaurants) {
            double revenus = resto.getRevenus();
            totalResto += revenus;
            sb.append(String.format("  %-30s: %8.2f €\n", resto.getNom(), revenus));
        }
        sb.append(String.format("  %-30s: %8.2f €\n", "TOTAL RESTAURANTS", totalResto));
        
        sb.append("\n--- BOUTIQUES ---\n");
        double totalBoutique = 0;
        for (Boutique boutique : boutiques) {
            double revenus = boutique.getRevenus();
            totalBoutique += revenus;
            sb.append(String.format("  %-30s: %8.2f €\n", boutique.getNom(), revenus));
        }
        sb.append(String.format("  %-30s: %8.2f €\n", "TOTAL BOUTIQUES", totalBoutique));
        
        sb.append("\n═══════════════════════════════════════════════════════════\n");
        sb.append(String.format("  TOTAL SERVICES (Resto + Boutiques): %8.2f €\n", 
            totalResto + totalBoutique));
        sb.append("═══════════════════════════════════════════════════════════");
        
        return sb.toString();
    }
    
    /**
     * Retourne un récapitulatif détaillé des transactions
     */
    public String genererRecapitulatifTransactions() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("   RÉCAPITULATIF DES TRANSACTIONS\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");
        
        // Réservations
        sb.append(String.format("--- RÉSERVATIONS RESTAURANTS (%d) ---\n", reservations.size()));
        for (ReservationRestaurant res : reservations) {
            sb.append(String.format("  #%-3d [%-10s] %s @ %s - %.2f€\n", 
                res.getIdReservation(), 
                res.getEtat().getLibelle(),
                res.getVisiteur().getNomVisiteur(),
                res.getRestaurant().getNom(),
                res.getMontant()));
        }
        
        // Achats
        sb.append(String.format("\n--- ACHATS BOUTIQUES (%d) ---\n", achats.size()));
        for (AchatBoutique achat : achats) {
            sb.append(String.format("  #%-3d %s @ %s - %s (%.2f€)\n", 
                achat.getIdAchat(),
                achat.getVisiteur().getNomVisiteur(),
                achat.getBoutique().getNom(),
                achat.getProduit().getNom(),
                achat.getMontant()));
        }
        
        sb.append("\n═══════════════════════════════════════════════════════════");
        return sb.toString();
    }
}
