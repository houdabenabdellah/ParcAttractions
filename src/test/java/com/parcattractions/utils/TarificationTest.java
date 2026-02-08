package test.java.com.parcattractions.utils;

import main.java.com.parcattractions.utils.Tarification;
import main.java.com.parcattractions.utils.TransactionManager;
import main.java.com.parcattractions.enums.TypeBillet;

/**
 * Tests unitaires pour Tarification et TransactionManager
 * Vérifie les calculs de prix et l'enregistrement des transactions
 */
public class TarificationTest {
    
    /**
     * TEST 6 : Calcul prix standard adulte
     * Vérifie le prix de base sans réduction
     */
    public static void test_PrixStandardAdulte() {
        System.out.println("\n=== TEST 6: Calcul prix standard adulte ===");
        
        double prixAdulte = Tarification.calculerPrix(25, TypeBillet.STANDARD);
        assert prixAdulte > 0 : 
            "Prix adulte standard incorrect: " + prixAdulte;
        
        System.out.println("Prix adulte: " + prixAdulte + "€");
        System.out.println("✓ TEST RÉUSSI : Prix standard adulte correct");
    }
    
    /**
     * TEST 7 : Calcul prix enfant (réduction)
     * Vérifie que les enfants (< 12 ans) bénéficient d'une réduction
     */
    public static void test_PrixEnfantReduction() {
        System.out.println("\n=== TEST 7: Calcul prix enfant (réduction) ===");
        
        double prixEnfant = Tarification.calculerPrix(8, TypeBillet.STANDARD);
        double prixAdulte = Tarification.calculerPrix(25, TypeBillet.STANDARD);
        
        assert prixEnfant < prixAdulte : 
            "Prix enfant devrait être inférieur au prix adulte";
        assert prixEnfant == prixAdulte * 0.5 : 
            "Réduction enfants = 50%";
        
        System.out.println("Prix enfant: " + prixEnfant + "€");
        System.out.println("✓ TEST RÉUSSI : Réduction enfants appliquée (50%)");
    }
    
    /**
     * TEST 8 : Calcul prix senior (réduction)
     * Vérifie que les seniors (65+) bénéficient d'une réduction
     */
    public static void test_PrixSeniorReduction() {
        System.out.println("\n=== TEST 8: Calcul prix senior (réduction) ===");
        
        double prixSenior = Tarification.calculerPrix(70, TypeBillet.STANDARD);
        double prixAdulte = Tarification.calculerPrix(25, TypeBillet.STANDARD);
        
        assert prixSenior < prixAdulte : 
            "Prix senior devrait être inférieur";
        
        System.out.println("Prix senior: " + prixSenior + "€");
        System.out.println("✓ TEST RÉUSSI : Réduction seniors appliquée");
    }
    
    /**
     * TEST 9 : Fast Pass (prix majoré)
     * Vérifie que le Fast Pass coûte plus cher que le standard
     */
    public static void test_FastPassPrixMajore() {
        System.out.println("\n=== TEST 9: Fast Pass prix majoré ===");
        
        double prixStandard = Tarification.calculerPrix(25, TypeBillet.STANDARD);
        double prixFastPass = Tarification.calculerPrix(25, TypeBillet.FAST_PASS);
        
        assert prixFastPass > prixStandard : 
            "Prix Fast Pass devrait être > Standard";
        assert prixFastPass == prixStandard * 1.5 : 
            "Majoration Fast Pass = 50%";
        
        System.out.println("Standard: " + prixStandard + "€");
        System.out.println("Fast Pass: " + prixFastPass + "€");
        System.out.println("✓ TEST RÉUSSI : Fast Pass majoré de 50%");
    }
    
    /**
     * TEST 10 : Application réduction Happy Hour
     * Vérifie le calcul avec réduction événement
     */
    public static void test_ReductionHappyHour() {
        System.out.println("\n=== TEST 10: Réduction Happy Hour ===");
        
        double prixOriginal = 50.0;
        double tauxReduction = 0.2; // 20%
        double prixReduit = Tarification.appliquerReduction(prixOriginal, tauxReduction);
        
        assert prixReduit == prixOriginal * (1 - tauxReduction) : 
            "Réduction non appliquée correctement";
        assert prixReduit == 40.0 : "40€ après 20% de réduction";
        
        System.out.println("Prix original: " + prixOriginal + "€");
        System.out.println("Prix avec 20% réduction: " + prixReduit + "€");
        System.out.println("✓ TEST RÉUSSI : Réduction événement appliquée");
    }
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║  TESTS UNITAIRES - TARIFICATION       ║");
        System.out.println("╚═══════════════════════════════════════╝");
        
        try {
            test_PrixStandardAdulte();
            test_PrixEnfantReduction();
            test_PrixSeniorReduction();
            test_FastPassPrixMajore();
            test_ReductionHappyHour();
            
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║  ✓ TOUS LES TESTS RÉUSSIS (5/5)      ║");
            System.out.println("╚═══════════════════════════════════════╝\n");
        } catch (AssertionError e) {
            System.out.println("\n✗ ERREUR: " + e.getMessage());
            System.exit(1);
        }
    }
}
