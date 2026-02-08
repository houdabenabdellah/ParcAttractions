package test.java.com.parcattractions.services;

import main.java.com.parcattractions.models.services.Restaurant;
import main.java.com.parcattractions.models.services.Boutique;
import main.java.com.parcattractions.models.services.Produit;
import main.java.com.parcattractions.exceptions.services.RestaurantPleinException;
import main.java.com.parcattractions.exceptions.services.StockEpuiseException;

/**
 * Tests unitaires pour Restaurant et Boutique
 * Vérifie la gestion des services (réservations, ventes, revenus)
 */
public class ServicesTest {
    
    /**
     * TEST 16 : Création restaurant et capacité
     * Vérifie la création d'un restaurant et sa gestion de capacité
     */
    public static void test_CreationRestaurantCapacite() {
        System.out.println("\n=== TEST 16: Création restaurant et capacité ===");
        
        Restaurant resto = new Restaurant("Le Gourmet", 50);
        
        assert resto.getNom().equals("Le Gourmet") : "Nom du restaurant";
        assert resto.getCapacite() == 50 : "Capacité = 50";
        assert resto.getClientsActuels() == 0 : "Pas de clients initialement";
        assert !resto.estComplet() : "Restaurant non complet au départ";
        assert resto.getTauxOccupation() == 0.0 : "Taux occupé = 0%";
        
        System.out.println("Restaurant: " + resto);
        System.out.println("✓ TEST RÉUSSI : Restaurant créé avec capacité correcte");
    }
    
    /**
     * TEST 17 : Revenus restaurant
     * Vérifie l'enregistrement des revenus du restaurant
     */
    public static void test_RevenusRestaurant() {
        System.out.println("\n=== TEST 17: Revenus restaurant ===");
        
        Restaurant resto = new Restaurant("Pizzeria", 30);
        
        assert resto.getRevenus() == 0.0 : "Aucun revenu initialement";
        
        resto.ajouterRevenu(25.50); // Repas 1
        assert resto.getRevenus() == 25.50 : "Revenu = 25.50€";
        
        resto.ajouterRevenu(35.00); // Repas 2
        assert resto.getRevenus() == 60.50 : "Revenu total = 60.50€";
        
        System.out.println("Revenus: " + resto.getRevenusFormates());
        System.out.println("✓ TEST RÉUSSI : Revenus enregistrés correctement");
    }
    
    /**
     * TEST 18 : Création boutique et stock
     * Vérifie la création d'une boutique et gestion du stock
     */
    public static void test_CreationBoutiqueStock() {
        System.out.println("\n=== TEST 18: Création boutique et stock ===");
        
        Boutique boutique = new Boutique("Souvenirs Parc");
        
        assert boutique.getNom().equals("Souvenirs Parc") : "Nom boutique";
        assert boutique.isOuverte() : "Boutique ouverte par défaut";
        
        // Vérifier qu'il y a du stock
        var stock = boutique.getStock();
        assert stock.size() > 0 : "Stock initialisé";
        
        System.out.println("Boutique: " + boutique.getNom());
        System.out.println("Produits en stock: " + stock.size());
        System.out.println("✓ TEST RÉUSSI : Boutique créée avec stock");
    }
    
    /**
     * TEST 19 : Vente boutique et revenus
     * Vérifie qu'une vente diminue le stock et augmente les revenus
     */
    public static void test_VenteBoutiqueRevenus() {
        System.out.println("\n=== TEST 19: Vente boutique et revenus ===");
        
        Boutique boutique = new Boutique("Shop");
        
        // Créer et vendre un produit
        Produit peluche = new Produit("Peluche", "Souvenirs", 15.0, "Peluche officielle");
        boutique.ajouterProduit(peluche, 10);
        
        assert boutique.getStock().get(peluche) == 10 : "Stock = 10";
        
        try {
            boutique.vendre(peluche, 1001);
        } catch (StockEpuiseException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        
        assert boutique.getStock().get(peluche) == 9 : "Stock = 9 après vente";
        assert boutique.getRevenus() == 15.0 : "Revenus = 15€";
        
        System.out.println("Stock après vente: " + boutique.getStock().get(peluche));
        System.out.println("Revenus: " + boutique.getRevenusFormates());
        System.out.println("✓ TEST RÉUSSI : Vente enregistrée, stock et revenus mis à jour");
    }
    
    /**
     * TEST 20 : Stock épuisé exception
     * Vérifie que vendre un produit épuisé lève une exception
     */
    public static void test_StockEpuiseException() {
        System.out.println("\n=== TEST 20: Stock épuisé exception ===");
        
        Boutique boutique = new Boutique("Boutique Test");
        Produit article = new Produit("Article rare", "Special", 50.0, "Rare");
        boutique.ajouterProduit(article, 1);
        
        // Vendre l'unique article
        try {
            boutique.vendre(article, 2001);
        } catch (StockEpuiseException e) {
            // Article vendu avec succès
        }
        
        // Essayer de vendre quand épuisé
        boolean exceptionLevee = false;
        try {
            boutique.vendre(article, 2002);
        } catch (StockEpuiseException e) {
            exceptionLevee = true;
            System.out.println("Exception attendue: " + e.getMessage());
        }
        
        assert exceptionLevee : "Exception StockEpuiseException devrait être levée";
        System.out.println("✓ TEST RÉUSSI : Exception levée pour stock épuisé");
    }
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║   TESTS UNITAIRES - SERVICES          ║");
        System.out.println("╚═══════════════════════════════════════╝");
        
        try {
            test_CreationRestaurantCapacite();
            test_RevenusRestaurant();
            test_CreationBoutiqueStock();
            test_VenteBoutiqueRevenus();
            test_StockEpuiseException();
            
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║  ✓ TOUS LES TESTS RÉUSSIS (5/5)      ║");
            System.out.println("╚═══════════════════════════════════════╝\n");
        } catch (AssertionError e) {
            System.out.println("\n✗ ERREUR: " + e.getMessage());
            System.exit(1);
        }
    }
}
