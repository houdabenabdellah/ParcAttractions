package test.java.com.parcattractions.integration;

import main.java.com.parcattractions.utils.Statistiques;
import main.java.com.parcattractions.utils.TransactionManager;
import main.java.com.parcattractions.models.services.Billet;
import main.java.com.parcattractions.enums.TypeBillet;

/**
 * Tests fonctionnels et d'intégration
 * Simule des scénarios complets du système
 */
public class IntegrationTest {
    
    /**
     * TEST 21 : Scénario visiteur simple
     * Simule un visiteur qui achète un billet
     */
    public static void test_ScenarioVisiteurAchatBillet() {
        System.out.println("\n=== TEST 21: Scénario visiteur achat billet ===");
        
        TransactionManager.reinitialiserStatistiques();
        
        // Visiteur enfant achète un billet standard
        TransactionManager.enregistrerVenteBillet(1001, "Standard Enfant", 25.0, 8);
        
        assert TransactionManager.getRevenuBillets() == 25.0 : 
            "Revenu billet enregistré";
        assert TransactionManager.getNbTransactions() == 1 : 
            "1 transaction";
        
        System.out.println("✓ Visiteur 1001 a acheté un billet (25€)");
        System.out.println("Revenus billets: " + TransactionManager.getRevenuBillets() + "€");
        System.out.println("✓ TEST RÉUSSI");
    }
    
    /**
     * TEST 22 : Scénario multiple visiteurs (journée simulée)
     * Simule l'arrivée de plusieurs visiteurs et leurs achats
     */
    public static void test_ScenarioJourneeComplete() {
        System.out.println("\n=== TEST 22: Scénario journée complète ===");
        
        TransactionManager.reinitialiserStatistiques();
        
        // Matin : 5 visiteurs arrivent
        for (int i = 0; i < 5; i++) {
            int age = 20 + (i * 10);
            double prix = 50.0; // Prix forfaitaire
            TransactionManager.enregistrerVenteBillet(2000 + i, 
                "Standard", prix, age);
        }
        
        assert TransactionManager.getRevenuBillets() == 250.0 : 
            "5 billets = 250€";
        assert TransactionManager.getNbTransactions() == 5 : 
            "5 transactions billets";
        
        // Midi : repas au restaurant
        for (int i = 0; i < 3; i++) {
            TransactionManager.enregistrerVenteRestaurant(2000 + i, 
                "Menu Jour", 20.0);
        }
        
        assert TransactionManager.getRevenuRestaurant() == 60.0 : 
            "3 repas = 60€";
        
        // Soir : achats souvenirs
        for (int i = 0; i < 2; i++) {
            TransactionManager.enregistrerVenteSouvenir(2000 + i, 
                "Article", 15.0);
        }
        
        assert TransactionManager.getRevenuSouvenirs() == 30.0 : 
            "2 articles = 30€";
        
        double revenuTotal = TransactionManager.getRevenuTotal();
        double attendu = 250.0 + 60.0 + 30.0; // 340€
        assert revenuTotal == attendu : "Total journée = 340€";
        
        System.out.println("Revenus de la journée:");
        System.out.println("  Billets: " + TransactionManager.getRevenuBillets() + "€");
        System.out.println("  Restaurant: " + TransactionManager.getRevenuRestaurant() + "€");
        System.out.println("  Souvenirs: " + TransactionManager.getRevenuSouvenirs() + "€");
        System.out.println("  TOTAL: " + revenuTotal + "€");
        System.out.println("Transactions: " + TransactionManager.getNbTransactions());
        System.out.println("✓ TEST RÉUSSI : Journée simulée avec revenus consolidés");
    }
    
    /**
     * TEST 23 : Statistiques visiteurs
     * Vérifie le suivi des visiteurs dans les statistiques
     */
    public static void test_StatistiquesVisiteurs() {
        System.out.println("\n=== TEST 23: Statistiques visiteurs ===");
        
        Statistiques stats = new Statistiques();
        
        assert stats.getNombreVisiteursTotal() == 0 : "Aucun visiteur initialement";
        assert stats.getNombreVisiteursActuels() == 0 : "Aucun actuellement";
        
        // Ajouter 10 visiteurs
        for (int i = 0; i < 10; i++) {
            stats.ajouterVisiteur();
        }
        
        assert stats.getNombreVisiteursTotal() == 10 : "10 visiteurs total";
        assert stats.getNombreVisiteursActuels() == 10 : "10 actuellement";
        
        // Certains quittent
        for (int i = 0; i < 3; i++) {
            stats.retirerVisiteur();
        }
        
        assert stats.getNombreVisiteursTotal() == 10 : "Toujours 10 total";
        assert stats.getNombreVisiteursActuels() == 7 : "7 visiteurs actuels";
        
        System.out.println("Total visiteurs: " + stats.getNombreVisiteursTotal());
        System.out.println("Visiteurs actuels: " + stats.getNombreVisiteursActuels());
        System.out.println("✓ TEST RÉUSSI : Suivi visiteurs correct");
    }
    
    /**
     * TEST 24 : Revenus dans statistiques
     * Vérifie que les revenus sont correctement agrégés dans Statistiques
     */
    public static void test_RevenusDansStatistiques() {
        System.out.println("\n=== TEST 24: Revenus dans statistiques ===");
        
        TransactionManager.reinitialiserStatistiques();
        Statistiques stats = new Statistiques();
        
        // Simuler quelques revenus
        TransactionManager.enregistrerVenteBillet(3001, "Standard", 50.0, 25);
        TransactionManager.enregistrerVenteRestaurant(3001, "Menu", 20.0);
        TransactionManager.enregistrerVenteSouvenir(3002, "Peluche", 15.0);
        
        // Récupérer revenus des stats
        double revenuStats = stats.getRevenusTotal();
        double revenuTransaction = TransactionManager.getRevenuTotal();
        
        assert revenuStats == revenuTransaction : 
            "Revenus Stats = Revenus TransactionManager";
        assert revenuStats == 85.0 : "Total = 85€";
        
        System.out.println("Revenus (TransactionManager): " + revenuTransaction + "€");
        System.out.println("Revenus (Statistiques): " + revenuStats + "€");
        System.out.println("Revenus formatés: " + stats.getRevenusTotalFormate());
        System.out.println("✓ TEST RÉUSSI : Revenus consolidés dans Statistiques");
    }
    
    /**
     * TEST 25 : Avis visiteurs satisfaction
     * Vérifie l'enregistrement des avis positifs/négatifs
     */
    public static void test_AvisEtSatisfaction() {
        System.out.println("\n=== TEST 25: Avis et satisfaction ===");
        
        Statistiques stats = new Statistiques();
        
        assert stats.getAvisPositifs() == 0 : "Aucun avis initialement";
        assert stats.getAvisNegatifs() == 0 : "Aucun avis négatif initialement";
        
        // 8 avis positifs
        for (int i = 0; i < 8; i++) {
            stats.enregistrerAvis(true);
        }
        
        // 2 avis négatifs
        for (int i = 0; i < 2; i++) {
            stats.enregistrerAvis(false);
        }
        
        assert stats.getAvisPositifs() == 8 : "8 avis positifs";
        assert stats.getAvisNegatifs() == 2 : "2 avis négatifs";
        
        double tauxSatisfaction = (8.0 / 10.0) * 100; // 80%
        System.out.println("Avis positifs: " + stats.getAvisPositifs());
        System.out.println("Avis négatifs: " + stats.getAvisNegatifs());
        System.out.println("Taux de satisfaction: " + tauxSatisfaction + "%");
        System.out.println("✓ TEST RÉUSSI : Avis enregistrés correctement");
    }
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║    TESTS FONCTIONNELS - INTÉGRATION   ║");
        System.out.println("╚═══════════════════════════════════════╝");
        
        try {
            test_ScenarioVisiteurAchatBillet();
            test_ScenarioJourneeComplete();
            test_StatistiquesVisiteurs();
            test_RevenusDansStatistiques();
            test_AvisEtSatisfaction();
            
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║  ✓ TOUS LES TESTS RÉUSSIS (5/5)      ║");
            System.out.println("╚═══════════════════════════════════════╝\n");
        } catch (AssertionError e) {
            System.out.println("\n✗ ERREUR: " + e.getMessage());
            System.exit(1);
        }
    }
}
