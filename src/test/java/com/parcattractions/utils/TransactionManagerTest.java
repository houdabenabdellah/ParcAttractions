package test.java.com.parcattractions.utils;

import main.java.com.parcattractions.utils.TransactionManager;

/**
 * Tests unitaires pour TransactionManager
 * Vérifie l'enregistrement et le calcul des revenus
 */
public class TransactionManagerTest {
    
    /**
     * TEST 11 : Enregistrement vente billet
     * Vérifie l'enregistrement et mise à jour des revenus billets
     */
    public static void test_EnregistrementVenteBillet() {
        System.out.println("\n=== TEST 11: Enregistrement vente billet ===");
        
        // Réinitialiser les stats
        TransactionManager.reinitialiserStatistiques();
        assert TransactionManager.getRevenuTotal() == 0.0 : "Stats réinitialisées";
        
        // Enregistrer une vente
        TransactionManager.enregistrerVenteBillet(101, "Standard Adulte", 50.0, 25);
        
        assert TransactionManager.getRevenuBillets() == 50.0 : 
            "Revenu billets = 50€";
        assert TransactionManager.getRevenuTotal() == 50.0 : 
            "Revenu total = 50€";
        assert TransactionManager.getNbTransactions() == 1 : 
            "1 transaction enregistrée";
        
        System.out.println("Revenu billets: " + TransactionManager.getRevenuBillets() + "€");
        System.out.println("✓ TEST RÉUSSI : Vente billet enregistrée");
    }
    
    /**
     * TEST 12 : Enregistrement vente restaurant
     * Vérifie l'enregistrement des revenus restaurant
     */
    public static void test_EnregistrementVenteRestaurant() {
        System.out.println("\n=== TEST 12: Enregistrement vente restaurant ===");
        
        TransactionManager.reinitialiserStatistiques();
        
        TransactionManager.enregistrerVenteRestaurant(102, "Menu Enfant", 12.0);
        TransactionManager.enregistrerVenteRestaurant(103, "Menu Jour", 20.0);
        
        assert TransactionManager.getRevenuRestaurant() == 32.0 : 
            "Total restaurant = 32€";
        assert TransactionManager.getRevenuTotal() == 32.0 : 
            "Revenu total = 32€";
        assert TransactionManager.getNbTransactions() == 2 : 
            "2 transactions enregistrées";
        
        System.out.println("Revenu restaurant: " + TransactionManager.getRevenuRestaurant() + "€");
        System.out.println("✓ TEST RÉUSSI : Ventes restaurant enregistrées");
    }
    
    /**
     * TEST 13 : Enregistrement vente souvenir
     * Vérifie l'enregistrement des revenus boutique
     */
    public static void test_EnregistrementVenteSouvenir() {
        System.out.println("\n=== TEST 13: Enregistrement vente souvenir ===");
        
        TransactionManager.reinitialiserStatistiques();
        
        TransactionManager.enregistrerVenteSouvenir(104, "Peluche Mascotte", 15.0);
        TransactionManager.enregistrerVenteSouvenir(105, "T-Shirt", 25.0);
        TransactionManager.enregistrerVenteSouvenir(106, "Porte-clés", 5.0);
        
        assert TransactionManager.getRevenuSouvenirs() == 45.0 : 
            "Total souvenirs = 45€";
        assert TransactionManager.getNbTransactions() == 3 : 
            "3 transactions enregistrées";
        
        System.out.println("Revenu souvenirs: " + TransactionManager.getRevenuSouvenirs() + "€");
        System.out.println("✓ TEST RÉUSSI : Ventes souvenirs enregistrées");
    }
    
    /**
     * TEST 14 : Revenu total consolidé
     * Vérifie l'agrégation des 3 types de revenus
     */
    public static void test_RevenuTotalConsolide() {
        System.out.println("\n=== TEST 14: Revenu total consolidé ===");
        
        TransactionManager.reinitialiserStatistiques();
        
        TransactionManager.enregistrerVenteBillet(101, "Standard", 50.0, 25);
        TransactionManager.enregistrerVenteRestaurant(102, "Menu", 20.0);
        TransactionManager.enregistrerVenteSouvenir(103, "Article", 15.0);
        
        double revenuBillets = TransactionManager.getRevenuBillets();
        double revenuResto = TransactionManager.getRevenuRestaurant();
        double revenuSouvenirs = TransactionManager.getRevenuSouvenirs();
        double revenuTotal = TransactionManager.getRevenuTotal();
        
        assert revenuBillets == 50.0 : "Billets = 50€";
        assert revenuResto == 20.0 : "Restaurant = 20€";
        assert revenuSouvenirs == 15.0 : "Souvenirs = 15€";
        assert revenuTotal == 85.0 : "Total = 85€";
        assert TransactionManager.getNbTransactions() == 3 : "3 transactions";
        
        System.out.println("Billets: " + revenuBillets + "€");
        System.out.println("Restaurant: " + revenuResto + "€");
        System.out.println("Souvenirs: " + revenuSouvenirs + "€");
        System.out.println("TOTAL: " + revenuTotal + "€");
        System.out.println("✓ TEST RÉUSSI : Revenus consolidés correctement");
    }
    
    /**
     * TEST 15 : Réinitialisation statistiques
     * Vérifie que reinitialiserStatistiques() réinitialise tout
     */
    public static void test_ReinitalisationStatistiques() {
        System.out.println("\n=== TEST 15: Réinitialisation statistiques ===");
        
        TransactionManager.reinitialiserStatistiques();
        
        // Ajouter quelques transactions
        TransactionManager.enregistrerVenteBillet(101, "Standard", 50.0, 25);
        TransactionManager.enregistrerVenteRestaurant(102, "Menu", 20.0);
        assert TransactionManager.getRevenuTotal() == 70.0 : "Total = 70€";
        
        // Réinitialiser
        TransactionManager.reinitialiserStatistiques();
        
        assert TransactionManager.getRevenuTotal() == 0.0 : "Revenus = 0€ après réinit";
        assert TransactionManager.getRevenuBillets() == 0.0 : "Billets = 0€";
        assert TransactionManager.getRevenuRestaurant() == 0.0 : "Restaurant = 0€";
        assert TransactionManager.getRevenuSouvenirs() == 0.0 : "Souvenirs = 0€";
        assert TransactionManager.getNbTransactions() == 0 : "0 transactions";
        
        System.out.println("✓ TEST RÉUSSI : Statistiques réinitialisées correctement");
    }
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║ TESTS UNITAIRES - TRANSACTION MANAGER ║");
        System.out.println("╚═══════════════════════════════════════╝");
        
        try {
            test_EnregistrementVenteBillet();
            test_EnregistrementVenteRestaurant();
            test_EnregistrementVenteSouvenir();
            test_RevenuTotalConsolide();
            test_ReinitalisationStatistiques();
            
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║  ✓ TOUS LES TESTS RÉUSSIS (5/5)      ║");
            System.out.println("╚═══════════════════════════════════════╝\n");
        } catch (AssertionError e) {
            System.out.println("\n✗ ERREUR: " + e.getMessage());
            System.exit(1);
        }
    }
}
