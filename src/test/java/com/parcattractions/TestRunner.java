package test.java.com.parcattractions;

import test.java.com.parcattractions.utils.FileAttenteTest;
import test.java.com.parcattractions.utils.TarificationTest;
import test.java.com.parcattractions.utils.TransactionManagerTest;
import test.java.com.parcattractions.services.ServicesTest;
import test.java.com.parcattractions.integration.IntegrationTest;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TestRunner - Lance tous les tests du projet et génère un rapport
 * 
 * Suite de tests complète:
 * - 5 tests FileAttente (files d'attente thread-safe)
 * - 5 tests Tarification (calculs prix)
 * - 5 tests TransactionManager (revenus)
 * - 5 tests Services (Restaurant, Boutique)
 * - 5 tests Intégration (scénarios complets)
 * TOTAL: 25 TESTS
 */
public class TestRunner {
    
    private static int testsReussis = 0;
    private static int testsEchoues = 0;
    private static StringBuilder rapportFinal = new StringBuilder();
    
    /**
     * Enregistre un test réussi
     */
    public static void enregistrerReussite(String nom) {
        testsReussis++;
        rapportFinal.append("✓ ").append(nom).append("\n");
    }
    
    /**
     * Enregistre un test échoué
     */
    public static void enregistrerEchec(String nom, Exception e) {
        testsEchoues++;
        rapportFinal.append("✗ ").append(nom)
            .append(" : ").append(e.getMessage()).append("\n");
    }
    
    /**
     * Lance une suite de tests
     */
    public static void lancerSuite(String nomSuite, Runnable suite) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Suite: " + nomSuite);
        System.out.println("=".repeat(50));
        
        rapportFinal.append("\n--- ").append(nomSuite).append(" ---\n");
        
        try {
            suite.run();
            System.out.println("✓ Suite réussie\n");
        } catch (Exception e) {
            System.out.println("✗ Suite échouée: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Génère un rapport en fichier
     */
    public static void genererRapport() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        
        StringBuilder rapport = new StringBuilder();
        rapport.append("╔════════════════════════════════════════════════════╗\n");
        rapport.append("║          RAPPORT DE TESTS - PARC ATTRACTIONS       ║\n");
        rapport.append("╚════════════════════════════════════════════════════╝\n\n");
        
        rapport.append("DATE/HEURE: ").append(timestamp).append("\n");
        rapport.append("TOTAL TESTS: ").append(testsReussis + testsEchoues).append("\n");
        rapport.append("RÉUSSIS: ").append(testsReussis).append(" (").append(
            String.format("%.1f%%", (testsReussis * 100.0 / (testsReussis + testsEchoues)))).append(")\n");
        rapport.append("ÉCHOUÉS: ").append(testsEchoues).append("\n\n");
        
        rapport.append("DÉTAIL DES TESTS:\n");
        rapport.append("──────────────────────────────────────────────────\n");
        rapport.append(rapportFinal.toString());
        
        rapport.append("\n──────────────────────────────────────────────────\n");
        if (testsEchoues == 0) {
            rapport.append("✓ TOUS LES TESTS SONT PASSÉS AVEC SUCCÈS!\n");
        } else {
            rapport.append("✗ ").append(testsEchoues).append(" test(s) échoué(s).\n");
        }
        rapport.append("╚════════════════════════════════════════════════════╝\n");
        
        // Afficher à l'écran
        System.out.println(rapport.toString());
        
        // Sauvegarder en fichier
        try (PrintWriter writer = new PrintWriter(new FileWriter("logs/RapportTests_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt"))) {
            writer.print(rapport.toString());
            System.out.println("Rapport sauvegardé dans logs/");
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du rapport: " + e.getMessage());
        }
    }
    
    /**
     * Main - Lance tous les tests
     */
    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║   SUITE COMPLÈTE DE TESTS - PARC ATTRACTIONS       ║");
        System.out.println("║   25 Tests (Unitaires + Fonctionnels)             ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        
        // Suite 1: FileAttente (5 tests)
        lancerSuite("FileAttente (5 tests)", () -> {
            FileAttenteTest.test_CreationEtAjoutSimple();
            enregistrerReussite("FileAttente - Création et ajout simple");
            
            FileAttenteTest.test_CapaciteEtDebordement();
            enregistrerReussite("FileAttente - Capacité et débordement");
            
            try {
                FileAttenteTest.test_RetraitFIFO();
                enregistrerReussite("FileAttente - Retrait FIFO");
            } catch (InterruptedException e) {
                enregistrerEchec("FileAttente - Retrait FIFO", e);
            }
            
            FileAttenteTest.test_ViderFile();
            enregistrerReussite("FileAttente - Vider la file");
            
            FileAttenteTest.test_TauxRemplissage();
            enregistrerReussite("FileAttente - Taux de remplissage");
        });
        
        // Suite 2: Tarification (5 tests)
        lancerSuite("Tarification (5 tests)", () -> {
            TarificationTest.test_PrixStandardAdulte();
            enregistrerReussite("Tarification - Prix standard adulte");
            
            TarificationTest.test_PrixEnfantReduction();
            enregistrerReussite("Tarification - Prix enfant réduction");
            
            TarificationTest.test_PrixSeniorReduction();
            enregistrerReussite("Tarification - Prix senior réduction");
            
            TarificationTest.test_FastPassPrixMajore();
            enregistrerReussite("Tarification - Fast Pass prix majoré");
            
            TarificationTest.test_ReductionHappyHour();
            enregistrerReussite("Tarification - Réduction Happy Hour");
        });
        
        // Suite 3: TransactionManager (5 tests)
        lancerSuite("TransactionManager (5 tests)", () -> {
            TransactionManagerTest.test_EnregistrementVenteBillet();
            enregistrerReussite("TransactionManager - Vente billet");
            
            TransactionManagerTest.test_EnregistrementVenteRestaurant();
            enregistrerReussite("TransactionManager - Vente restaurant");
            
            TransactionManagerTest.test_EnregistrementVenteSouvenir();
            enregistrerReussite("TransactionManager - Vente souvenir");
            
            TransactionManagerTest.test_RevenuTotalConsolide();
            enregistrerReussite("TransactionManager - Revenu total");
            
            TransactionManagerTest.test_ReinitalisationStatistiques();
            enregistrerReussite("TransactionManager - Réinitialisation");
        });
        
        // Suite 4: Services (5 tests)
        lancerSuite("Services: Restaurant & Boutique (5 tests)", () -> {
            ServicesTest.test_CreationRestaurantCapacite();
            enregistrerReussite("Services - Restaurant capacité");
            
            ServicesTest.test_RevenusRestaurant();
            enregistrerReussite("Services - Revenus restaurant");
            
            ServicesTest.test_CreationBoutiqueStock();
            enregistrerReussite("Services - Creation boutique");
            
            ServicesTest.test_VenteBoutiqueRevenus();
            enregistrerReussite("Services - Vente boutique");
            
            ServicesTest.test_StockEpuiseException();
            enregistrerReussite("Services - Exception stock épuisé");
        });
        
        // Suite 5: Intégration (5 tests)
        lancerSuite("Tests Fonctionnels - Intégration (5 tests)", () -> {
            IntegrationTest.test_ScenarioVisiteurAchatBillet();
            enregistrerReussite("Intégration - Scénario visiteur");
            
            IntegrationTest.test_ScenarioJourneeComplete();
            enregistrerReussite("Intégration - Scénario journée complète");
            
            IntegrationTest.test_StatistiquesVisiteurs();
            enregistrerReussite("Intégration - Statistiques visiteurs");
            
            IntegrationTest.test_RevenusDansStatistiques();
            enregistrerReussite("Intégration - Revenus dans stats");
            
            IntegrationTest.test_AvisEtSatisfaction();
            enregistrerReussite("Intégration - Avis et satisfaction");
        });
        
        // Générer le rapport final
        genererRapport();
        
        // Exit avec code d'erreur si des tests ont échoué
        if (testsEchoues > 0) {
            System.exit(1);
        }
    }
}
