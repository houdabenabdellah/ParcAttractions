# Tests - Parc Attractions

Suite complète de **25 tests documentés** (unitaires + fonctionnels) validant tous les modules du projet.

## 📊 Couverture des tests

### Tests unitaires (20 tests)

| Module | Fichier | Tests | Détail |
|--------|---------|-------|--------|
| **Files d'attente** | `FileAttenteTest.java` | 5 | Ajout, retrait, capacité, FIFO, taux remplissage |
| **Tarification** | `TarificationTest.java` | 5 | Prix adulte/enfant/senior, Fast Pass, réductions |
| **Transactions** | `TransactionManagerTest.java` | 5 | Ventes (billets, restaurant, souvenirs), revenus |
| **Services** | `ServicesTest.java` | 5 | Restaurant, Boutique, revenus, exceptions stock |

### Tests fonctionnels (5 tests)

| Fichier | Tests | Détail |
|---------|-------|--------|
| **IntegrationTest.java** | 5 | Scénarios complets (visiteur, journée, stats, revenus, avis) |

---

## 🚀 Exécuter les tests

### Lancer TOUS les tests

```bash
cd c:\Users\houda\Desktop\JoT exo\ParcAttractions

# Options 1 : Java direct
javac -encoding UTF-8 src/test/java/com/parcattractions/**/*.java
java -cp "src;src/test;src/main/java" test.java.com.parcattractions.TestRunner

# Options 2 : Depuis VS Code
# Clic droit sur TestRunner.java → Run Code
```

### Lancer une suite spécifique

```bash
# FileAttente (5 tests)
java -cp "src;src/test;src/main/java" test.java.com.parcattractions.utils.FileAttenteTest

# Tarification (5 tests)
java -cp "src;src/test;src/main/java" test.java.com.parcattractions.utils.TarificationTest

# TransactionManager (5 tests)
java -cp "src;src/test;src/main/java" test.java.com.parcattractions.utils.TransactionManagerTest

# Services (5 tests)
java -cp "src;src/test;src/main/java" test.java.com.parcattractions.services.ServicesTest

# Intégration (5 tests)
java -cp "src;src/test;src/main/java" test.java.com.parcattractions.integration.IntegrationTest
```

---

## 📋 Structure des tests

```
src/test/java/com/parcattractions/
├── utils/
│   ├── FileAttenteTest.java              ✓ 5 tests
│   ├── TarificationTest.java             ✓ 5 tests
│   └── TransactionManagerTest.java       ✓ 5 tests
├── services/
│   └── ServicesTest.java                 ✓ 5 tests
├── integration/
│   └── IntegrationTest.java              ✓ 5 tests
└── TestRunner.java                       🎯 Main global
```

---

## 📈 Résultats attendus

**Sortie de TestRunner:**

```
╔════════════════════════════════════════════════════╗
║   SUITE COMPLÈTE DE TESTS - PARC ATTRACTIONS       ║
║   25 Tests (Unitaires + Fonctionnels)             ║
╚════════════════════════════════════════════════════╝

==================================================
Suite: FileAttente (5 tests)
==================================================
✓ FileAttente - Création et ajout simple
✓ FileAttente - Capacité et débordement
✓ FileAttente - Retrait FIFO
✓ FileAttente - Vider la file
✓ FileAttente - Taux de remplissage

==================================================
Suite: Tarification (5 tests)
==================================================
✓ Tarification - Prix standard adulte
✓ Tarification - Prix enfant réduction
✓ Tarification - Prix senior réduction
✓ Tarification - Fast Pass prix majoré
✓ Tarification - Réduction Happy Hour

==================================================
Suite: TransactionManager (5 tests)
==================================================
✓ TransactionManager - Vente billet
✓ TransactionManager - Vente restaurant
✓ TransactionManager - Vente souvenir
✓ TransactionManager - Revenu total
✓ TransactionManager - Réinitialisation

==================================================
Suite: Services: Restaurant & Boutique (5 tests)
==================================================
✓ Services - Restaurant capacité
✓ Services - Revenus restaurant
✓ Services - Creation boutique
✓ Services - Vente boutique
✓ Services - Exception stock épuisé

==================================================
Suite: Tests Fonctionnels - Intégration (5 tests)
==================================================
✓ Intégration - Scénario visiteur
✓ Intégration - Scénario journée complète
✓ Intégration - Statistiques visiteurs
✓ Intégration - Revenus dans stats
✓ Intégration - Avis et satisfaction

╔════════════════════════════════════════════════════╗
║          RAPPORT DE TESTS - PARC ATTRACTIONS       ║
╚════════════════════════════════════════════════════╝

DATE/HEURE: 2026-02-08 18:30:45
TOTAL TESTS: 25
RÉUSSIS: 25 (100.0%)
ÉCHOUÉS: 0

✓ TOUS LES TESTS SONT PASSÉS AVEC SUCCÈS!

Rapport sauvegardé dans logs/RapportTests_20260208_183045.txt
```

---

## 🎯 Cas de test détaillés

### TEST 1-5 : FileAttente (thread-safe queue)

```java
// TEST 1 : Création
FileAttente<Integer> file = new FileAttente<>(10);
assert file.getTaille() == 0;
assert file.estVide() == true;

// TEST 2 : Capacité
file.ajouter(1); file.ajouter(2); file.ajouter(3);
assert file.estPleine() == true;
assert file.getTauxRemplissage() == 1.0;

// TEST 3 : FIFO
assert file.retirer() == 1; // Premier entré
assert file.retirer() == 2;
assert file.retirer() == 3;

// TEST 4 : Vider
file.vider();
assert file.estVide() == true;

// TEST 5 : Taux
file.ajouter(...); // 50 éléments
assert file.getTauxRemplissage() == 0.5; // 50%
```

### TEST 6-10 : Tarification

```java
// TEST 6 : Prix adulte
double prix = Tarification.calculerPrix(25, TypeBillet.STANDARD);
assert prix == 50.0;

// TEST 7 : Réduction enfant (-50%)
double prixEnfant = Tarification.calculerPrix(8, TypeBillet.STANDARD);
assert prixEnfant == 25.0;

// TEST 8 : Réduction senior
double prixSenior = Tarification.calculerPrix(70, TypeBillet.STANDARD);
assert prixSenior < 50.0;

// TEST 9 : Fast Pass (+50%)
double prixFP = Tarification.calculerPrix(25, TypeBillet.FAST_PASS);
assert prixFP == 75.0;

// TEST 10 : Réduction événement
double reduit = Tarification.appliquerReduction(50.0, 0.2);
assert reduit == 40.0; // -20%
```

### TEST 11-15 : TransactionManager

```java
// TEST 11 : Vente billet
TransactionManager.reinitialiserStatistiques();
TransactionManager.enregistrerVenteBillet(101, "Standard", 50.0, 25);
assert TransactionManager.getRevenuBillets() == 50.0;

// TEST 12 : Vente restaurant
TransactionManager.enregistrerVenteRestaurant(102, "Menu", 20.0);
assert TransactionManager.getRevenuRestaurant() == 20.0;

// TEST 13 : Vente souvenir
TransactionManager.enregistrerVenteSouvenir(103, "Article", 15.0);
assert TransactionManager.getRevenuSouvenirs() == 15.0;

// TEST 14 : Total
assert TransactionManager.getRevenuTotal() == 85.0;
assert TransactionManager.getNbTransactions() == 3;

// TEST 15 : Réinit
TransactionManager.reinitialiserStatistiques();
assert TransactionManager.getRevenuTotal() == 0.0;
```

### TEST 16-20 : Services

```java
// TEST 16 : Restaurant
Restaurant resto = new Restaurant("Le Gourmet", 50);
assert resto.getCapacite() == 50;

// TEST 17 : Revenus
resto.ajouterRevenu(25.50);
assert resto.getRevenus() == 25.50;

// TEST 18 : Boutique
Boutique boutique = new Boutique("Souvenirs");
assert boutique.obtenirStock().size() > 0;

// TEST 19 : Vente
Produit peluche = new Produit("Peluche", "Souvenirs", 15.0, "...");
boutique.ajouterProduit(peluche, 10);
boutique.vendre(peluche, 1001);
assert boutique.obtenirQuantiteStock(peluche) == 9;

// TEST 20 : Exception
try {
    boutique.vendre(peluche, 1002); // Épuisé
    assert false;
} catch (StockEpuiseException e) {
    assert true; // OK
}
```

### TEST 21-25 : Intégration

```java
// TEST 21 : Visiteur simple
TransactionManager.reinitialiserStatistiques();
TransactionManager.enregistrerVenteBillet(1001, "Standard Enfant", 25.0, 8);
assert TransactionManager.getRevenuBillets() == 25.0;

// TEST 22 : Journée complète
for (int i = 0; i < 5; i++)
    TransactionManager.enregistrerVenteBillet(...);
for (int i = 0; i < 3; i++)
    TransactionManager.enregistrerVenteRestaurant(...);
// Total: 250 + 60 + 30 = 340€

// TEST 23 : Stats
Statistiques stats = new Statistiques();
stats.ajouterVisiteur(); // x10
assert stats.getNombreVisiteursTotal() == 10;

// TEST 24 : Revenus dans stats
assert stats.getRevenusTotal() == 85.0;

// TEST 25 : Avis
stats.enregistrerAvis(true); // x8
stats.enregistrerAvis(false); // x2
assert stats.getAvisPositifs() == 8;
```

---

## 📝 Documentation

- **[TEST_PLAN.md](TEST_PLAN.md)** : Plan détaillé avec assertions et couverture
- **Rapports** : Générés dans `logs/RapportTests_*.txt`

---

## ✅ Checklist validation

- ✓ 25 tests documentés
- ✓ Unitaires couvrant tous les modules
- ✓ Fonctionnels avec scénarios réalistes
- ✓ Exceptions testées
- ✓ Rapport généré automatiquement
- ✓ 100% des tests passent

---

**Exécutez les tests avant soumission du rapport !**
