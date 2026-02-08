package test.java.com.parcattractions.utils;

import main.java.com.parcattractions.utils.FileAttente;
import main.java.com.parcattractions.exceptions.services.RestaurantPleinException;

/**
 * Tests unitaires pour la classe FileAttente
 * Vérifie le comportement des files d'attente thread-safe
 */
public class FileAttenteTest {
    
    /**
     * TEST 1 : Création et ajout simple
     * Vérifie qu'on peut créer une file et ajouter des éléments
     */
    public static void test_CreationEtAjoutSimple() {
        System.out.println("\n=== TEST 1: Création et ajout simple ===");
        FileAttente<Integer> file = new FileAttente<>(10);
        
        assert file.getTaille() == 0 : "File initiale non vide";
        assert file.estVide() : "File devrait être vide";
        
        file.ajouter(1);
        assert file.getTaille() == 1 : "Taille devrait être 1";
        assert !file.estVide() : "File ne devrait pas être vide";
        
        System.out.println("✓ TEST RÉUSSI : File créée et élément ajouté");
    }
    
    /**
     * TEST 2 : Capacité et débordement
     * Vérifie que la file respecte sa capacité maximale
     */
    public static void test_CapaciteEtDebordement() {
        System.out.println("\n=== TEST 2: Capacité et débordement ===");
        FileAttente<String> file = new FileAttente<>(3);
        
        file.ajouter("A");
        file.ajouter("B");
        file.ajouter("C");
        
        assert file.getTaille() == 3 : "File devrait être pleine";
        assert file.estPleine() : "File devrait être pleine";
        assert file.getTauxRemplissage() == 1.0 : "Taux de remplissage = 100%";
        
        System.out.println("✓ TEST RÉUSSI : Capacité respectée");
    }
    
    /**
     * TEST 3 : Retrait des éléments (FIFO)
     * Vérifie l'ordre FIFO (First In First Out)
     */
    public static void test_RetraitFIFO() throws InterruptedException {
        System.out.println("\n=== TEST 3: Retrait FIFO ===");
        FileAttente<Integer> file = new FileAttente<>(10);
        
        file.ajouter(10);
        file.ajouter(20);
        file.ajouter(30);
        
        int premier = file.retirer();
        assert premier == 10 : "Premier élément devrait être 10";
        
        int deuxieme = file.retirer();
        assert deuxieme == 20 : "Deuxième élément devrait être 20";
        
        int troisieme = file.retirer();
        assert troisieme == 30 : "Troisième élément devrait être 30";
        
        assert file.estVide() : "File devrait être vide";
        System.out.println("✓ TEST RÉUSSI : Ordre FIFO respecté");
    }
    
    /**
     * TEST 4 : Vider la file
     * Vérifie que vider() fonctionne correctement
     */
    public static void test_ViderFile() {
        System.out.println("\n=== TEST 4: Vider la file ===");
        FileAttente<String> file = new FileAttente<>(10);
        
        file.ajouter("X");
        file.ajouter("Y");
        file.ajouter("Z");
        assert file.getTaille() == 3 : "File devrait avoir 3 éléments";
        
        file.vider();
        assert file.getTaille() == 0 : "File vidée devrait être vide";
        assert file.estVide() : "estVide() devrait retourner true";
        
        System.out.println("✓ TEST RÉUSSI : File vidée correctement");
    }
    
    /**
     * TEST 5 : Taux de remplissage
     * Vérifie le calcul du taux de remplissage
     */
    public static void test_TauxRemplissage() {
        System.out.println("\n=== TEST 5: Taux de remplissage ===");
        FileAttente<Integer> file = new FileAttente<>(100);
        
        file.ajouter(1);
        assert file.getTauxRemplissage() == 0.01 : "Taux = 1%";
        
        for (int i = 1; i < 50; i++) {
            file.ajouter(i);
        }
        assert file.getTauxRemplissage() == 0.5 : "Taux = 50%";
        
        System.out.println("✓ TEST RÉUSSI : Taux de remplissage calculé correctement");
    }
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║   TESTS UNITAIRES - FILE ATTENTE      ║");
        System.out.println("╚═══════════════════════════════════════╝");
        
        try {
            test_CreationEtAjoutSimple();
            test_CapaciteEtDebordement();
            test_RetraitFIFO();
            test_ViderFile();
            test_TauxRemplissage();
            
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║  ✓ TOUS LES TESTS RÉUSSIS (5/5)      ║");
            System.out.println("╚═══════════════════════════════════════╝\n");
        } catch (AssertionError e) {
            System.out.println("\n✗ ERREUR: " + e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            System.out.println("\n✗ ERREUR: " + e.getMessage());
            System.exit(1);
        }
    }
}
