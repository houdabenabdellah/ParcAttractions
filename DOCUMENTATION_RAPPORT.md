# Documentation et rapport complet — Parc d’attractions

**Projet :** Grand Parc Simulator 2026  
**Date :** Février 2026  

---

## 1. Vue d’ensemble

Application Java Swing de simulation et gestion d’un parc d’attractions en temps réel : 8 attractions avec files d’attente, maintenance programmée, personnel, visiteurs, services (restaurants, boutiques). Le projet applique le **multithreading**, des **timers**, des **files d’attente** thread-safe, une **POO complète** et une **GUI réactive** avec gestion d’événements.

---

## 2. Thème : Multithreading, Timer, files d’attente

### 2.1 Multithreading

**Où c’est présent :** classes qui étendent `Thread` et/ou sont exécutées en parallèle.

| Élément | Fichier | Détail |
|--------|---------|--------|
| **Horloge (temps réel)** | `src/main/java/com/parcattractions/utils/Horloge.java` | `public class Horloge extends Thread` — boucle `run()` avec `Thread.sleep(1000)`, mode temps réel ou simulation accélérée (l.9, 74–115). |
| **Attractions** | `src/main/java/com/parcattractions/models/attractions/Attraction.java` | `public abstract class Attraction extends Thread` — chaque attraction a son propre thread (l.20). Boucle dans `run()` : exécution des tours, maintenance, panne (l.95–118). |
| **Visiteurs** | `src/main/java/com/parcattractions/models/visiteurs/Visiteur.java` | `public abstract class Visiteur extends Thread` — visiteur = thread (l.23). |
| **Employés** | `src/main/java/com/parcattractions/models/employes/Employe.java` | `public abstract class Employe extends Thread` — employé = thread (l.10). `run()` appelle une méthode abstraite de travail (l.39–50). |
| **Gestionnaire d’événements** | `src/main/java/com/parcattractions/controllers/GestionnaireEvenements.java` | `public class GestionnaireEvenements extends Thread` (l.13) — événements (Happy Hour, Parade, Spectacle) selon l’heure. |
| **Gestionnaire météo** | `src/main/java/com/parcattractions/controllers/GestionnaireMeteo.java` | `public class GestionnaireMeteo extends Thread` (l.11). |
| **Générateur de visiteurs** | `src/main/java/com/parcattractions/controllers/GenerateurVisiteurs.java` | `public class GenerateurVisiteurs extends Thread` (l.16). |
| **Maintenance / panne** | `Attraction.java` | `new Thread(() -> { ... tech.effectuerMaintenance(Attraction.this); ... }, "Maintenance-" + nom).start()` (l.280–302) et `demanderTechnicienUrgence()` dans `GestionnaireParc` lance un thread de réparation (l.689). |

**Démarrage des threads :**  
- `GestionnaireParc.ouvrirParc()` démarre l’horloge : `horloge = new Horloge(true); horloge.start();` (l.232–233).  
- Les attractions et employés sont créés ; leurs `start()` sont commentés en mode manuel (l.238–250, 490).

---

### 2.2 Timer (javax.swing.Timer)

**Où c’est présent :** rafraîchissement périodique de l’interface (simulation “temps réel” côté GUI).

| Fichier | Usage |
|---------|--------|
| `src/main/java/com/parcattractions/views/PanelDashboard.java` | `new Timer(1000, e -> rafraichir()).start();` — rafraîchit le tableau de bord toutes les secondes (l.32). |
| `src/main/java/com/parcattractions/views/PanelTransactions.java` | `updateTimer = new Timer(1000, e -> updateAffichage()); updateTimer.start();` — met à jour le moniteur financier chaque seconde (l.20–21). |
| `src/main/java/com/parcattractions/views/PanelStatistiques.java` | `refreshTimer = new Timer(500, e -> rafraichir()); refreshTimer.start();` — rafraîchit les stats toutes les 500 ms (l.42–43). |

Les **Timer** Swing exécutent leur `ActionListener` sur l’EDT, ce qui rend l’interface réactive sans bloquer le thread principal.

---

### 2.3 Files d’attente

**Où c’est présent :** file générique thread-safe + utilisation dans attractions et restaurant.

| Fichier | Rôle |
|---------|------|
| **Classe générique** | `src/main/java/com/parcattractions/utils/FileAttente.java` — `public class FileAttente<T>` (l.14). Utilise `Queue<T>`, `ReentrantLock`, `Condition nonVide` (l.16–19). Méthodes : `ajouter()`, `retirer()`, `retirerAvecTimeout()`, `regarder()`, `getTaille()`, `estVide()`, `estPleine()`, `vider()`, `getCapaciteMax()`, `getTauxRemplissage()` (l.42–218). **Thread-safe** via `lock.lock()` / `lock.unlock()` et `nonVide.await()` / `nonVide.signal()`. |
| **Attractions** | `Attraction.java` — deux files par attraction : `protected final FileAttente<Integer> fileNormale` et `fileFastPass` (l.40–41), capacité max 150 et 75 (l.83–84). `ajouterVisiteur(visiteurId, fastPass)` ajoute l’ID dans la file correspondante (l.222–229). `evacuerFiles()` vide les files en cas de panne (l.352–374). |
| **Restaurant** | `src/main/java/com/parcattractions/models/services/Restaurant.java` — `private final FileAttente<Integer> fileAttente` (l.15), capacité 100 (l.29). Méthodes “file” : ajout visiteur en file, taille de file (l.78, 185). |
| **Statistiques** | `src/main/java/com/parcattractions/utils/Statistiques.java` — `mettreAJourFile(String nomAttraction, int tailleFile)` pour suivre la taille des files (l.80–86). |

**Résumé :** Files d’attente = **FileAttente.java** (générique, ReentrantLock + Condition) ; utilisées dans **Attraction** (2 files par attraction) et **Restaurant** (1 file).

---

## 3. Fonctionnalités détaillées

### 3.1 Huit attractions (capacités) | Simulation temps réel

**Où c’est présent :**

- **Création des 8 attractions :**  
  `src/main/java/com/parcattractions/controllers/GestionnaireParc.java` — méthode `initialiserAttractionsParDefaut()` (l.109–120) :  
  `MontagnesRusses`, `GrandeRoue`, `ManegeEnfants`, `MaisonHantee`, `SimulateurVR`, `ChuteLibre`, `Carrousel`, `TobogganAquatique`.

- **Capacité par attraction (nombre de places par tour) :**

| Attraction | Fichier (sous-dossier `models/attractions/`) | Capacité |
|------------|------------------------------------------------|----------|
| Montagnes Russes Extrême | `MontagnesRusses.java` | 20 |
| Grande Roue Panoramique | `GrandeRoue.java` | 50 |
| Manège Enchanté | `ManegeEnfants.java` | 15 |
| Maison Hantée des Ombres | `MaisonHantee.java` | 12 |
| Simulateur VR Spatial | `SimulateurVR.java` | 8 |
| Chute Libre Vertigineuse | `ChuteLibre.java` | 4 |
| Carrousel Magique | `Carrousel.java` | 30 |
| Toboggan Aquatique Splash | `TobogganAquatique.java` | 10 |

Chaque classe concrète appelle `super(nom, capacite, dureeTour, ...)` dans son constructeur (ex. `MontagnesRusses.java` l.8–20).

#### 3.1.1 Simulation temps réel avec contrôle utilisateur

**Objectif :** Simuler une journée complète de parc d'attractions en temps accéléré, avec contrôle total de l'utilisateur : pause, reprise, avance pas à pas, réglage de vitesse, et fermeture automatique à 16h00.

**Architecture :**

**1. Classe Horloge (`utils/Horloge.java`)**

La classe `Horloge` gère la boucle de simulation temps réel. Modifications clés :

- **Fermeture à 16h00** : constante `HEURE_FERMETURE = 16` (l.6) remplace la valeur 22h00 précédente.
- **Callback fermeture automatique** (l.12) : champ `private Runnable onHeureFermeture` enregistre une fonction appelée quand l'heure simulée atteint 16h00.
- **Méthode `setOnHeureFermeture(Runnable r)`** (l.Z) : permet au gestionnaire du parc de passer la fonction de fermeture (`this::fermerParc`).
- **Méthode `getHeureFermeture()`** (l.Y) : retourne 16 pour affichage ("Fermeture automatique à 16:00").

**Boucle de simulation (`run()`, l.74–115) :**
- Initialisation : heure 09:00 (départ de la journée simulée).
- **Pause/Reprise** : flag `accelere` (booléen).
  - Si `accelere = false` : boucle attend sans avancer le temps.
  - Si `accelere = true` : chaque itération ajoute N minutes simulées (N = vitesse définie), avec sleep(1000 ms) entre les itérations.
- **Avance d'un pas** : incrémente manuelle de 1 minute (appelée en pause).
- **Fermeture auto** : quand `heure == 16` :
  1. Appel du callback `onHeureFermeture.run()` (déclenche `gestionnaire.fermerParc()`).
  2. Arrêt de la boucle.

**API de contrôle :**

| Méthode | Rôle |
|---------|------|
| `pause()` | Met `accelere = false` → pause la simulation |
| `reprendre()` | Met `accelere = true` → reprend la simulation |
| `avancerDUnPas()` | Incrémente manuellement 1 minute ; si `heure >= 16`, déclenche fermeture |
| `setVitesse(int minutes)` | Définit l'accélération : n minutes simulées par seconde réelle (ex. 1, 5, 10, 30) |
| `setOnHeureFermeture(Runnable r)` | Enregistre callback appelé à 16h00 |
| `getHeureFermeture()` | Retourne 16 pour affichage |
| `getHeure()`, `getMinute()` | Accesseurs pour heure/minute courante |

**2. Ouverture du parc (`controllers/GestionnaireParc.java`)**

Modifications clés :

- **Initialisation** (l.232–233) :
  ```java
  horloge = new Horloge(1);  // 1 minute simulée par seconde réelle
  horloge.setOnHeureFermeture(this::fermerParc);  // Callback pour fermeture auto à 16h00
  horloge.start();
  ```
- **Méthode `fermerParc()`** (existante, appelée ou en pause) : 
  - Sauvegarde des données.
  - Génération des rapports (CSV, HTML).
  - Arrêt des threads (horloge, attractions, visiteurs, événements).
  - Notification "Parc fermé".
  - Mise à jour UI.

**3. Contrôle de temps par l'utilisateur**

**Menu Parc** (`MainFrame`, l.35–100) :

- **Pause / Reprendre** (`iPause`) :
  - Appelle `horloge.pause()` pour mettre en pause.
  - Appelle `horloge.reprendre()` pour relancer.
  - Toggle : affiche "Pause" en fonctionnement, "Reprendre" si en pause.
  
- **Avancer d'un pas** (`iAvancerDUnPas`) :
  - Visible uniquement quand le parc est ouvert ET en pause.
  - Appelle `horloge.avancerDUnPas()` → avance 1 minute simulée.
  - Si le pas dépasse 16h00, la fermeture automatique est déclenchée immédiatement.

**Menu Météo → Configuration** (`DialogConfiguration`, l.35–60) :

- **Affichage statut** : mention "Fermeture automatique du parc à 16:00".
- **Sélecteur de vitesse** (JComboBox) :
  - Options : `1`, `5`, `10`, `30` (nombre de minutes simulées par seconde réelle).
  - Défaut : 1 min/sec (temps naturel, 1 seconde réelle = 1 minute simulée).
  - 30 min/sec = 30x plus rapide (3 heures simulées = 6 minutes réelles).
  - Action : appelle `horloge.setVitesse(vitesse)`.
  - Modifiable à tout moment (parc ouvert).

**Comportement résultant :**

| Action | Résultat |
|--------|----------|
| Ouvrir parc | Heure = 09:00, horloge marche (par défaut vitesse 1) |
| Pause | Horloge gèle l'heure actuelle |
| En pause + Avancer d'un pas | Heure += 1 minute |
| Reprendre | Horloge relance l'incrémentation |
| Régler vitesse à 30 | 30 min/sec : l'horloge avance 30x plus vite |
| 16h00 atteint | `horloge.onHeureFermeture.run()` → `fermerParc()` → sauvegarde + rapports + arrêt |

**Exemple de journée simulée :**
1. 09:00 → Ouvrir parc (vitesse 1 min/sec).
2. Ajouter visiteurs → ils circulent, paient, font files.
3. 09:30 → Mettre en pause, avancer manuellement pour voir des événements.
4. 10:00 → Reprendre (vitesse 5 min/sec) → accélère 5x.
5. 14:00 → Augmenter vitesse à 30 min/sec → arrive à 16h00 rapidement.
6. 16h00 → Fermeture auto → UI montre "Parc ferme", rapports générés.

---

- **Simulation temps réel :**  
  - **Horloge :** `Horloge.java` — Simulation accélérée (1 à 30 min/sec) démarrant à 09:00, avec pause/reprise et avance pas à pas. Fermeture automatique à 16h00 via callback déclenché aupres du gestionnaire du parc.  
  - **GUI :** `PanelDashboard`, `PanelTransactions`, `PanelStatistiques` utilisent des **Timer** (500–1000 ms) pour afficher heure, revenus et stats en continu (voir § 2.2). Menu Parc offre pause/reprise et avance. Menu Configuration offre réglage de vitesse (1, 5, 10, 30 min/sec).

---

### 3.2 Files d’attente (résumé)

- **Implémentation :** `utils/FileAttente.java` (générique, thread-safe).  
- **Attractions :** `Attraction.java` — `fileNormale` et `fileFastPass`, capacités 150 et 75.  
- **Restaurant :** `Restaurant.java` — `fileAttente` capacité 100.  
- **Statistiques :** `Statistiques.mettreAJourFile()` pour le suivi des files.

---

### 3.3 Maintenance programmée

**Où c’est présent :** uniquement dans le modèle des attractions.

| Fichier | Détail |
|---------|--------|
| `src/main/java/com/parcattractions/models/attractions/Attraction.java` | Constante `TOURS_AVANT_MAINTENANCE = 10` (l.24). Après chaque tour, `verifierMaintenanceProgrammee()` (l.252–257) : si `toursEffectues % TOURS_AVANT_MAINTENANCE == 0`, appel à `lancerMaintenance()`. |
| | `lancerMaintenance()` (l.270–312) : état `MAINTENANCE`, `tempsMaintenanceRestant = 30`, notification. Si un technicien est disponible : thread dédié `tech.effectuerMaintenance(Attraction.this)` ; sinon thread qui décrémente le temps puis `finishMaintenanceFallback()`. |
| | `finishMaintenanceFallback()` (l.305–311) : remet l’attraction en `OPERATIONNELLE`, remet `toursEffectues` à 0, met à jour les stats et notifications. |

La **maintenance programmée** est donc déclenchée automatiquement tous les 10 tours dans la boucle `run()` de chaque attraction.

---

## 4. POO complète (encapsulation, héritage, polymorphisme, abstraction)

### 4.1 Encapsulation

**Où c’est présent :** attributs privés/protected + accesseurs/mutateurs ou API contrôlée.

- **Attraction :** champs `protected` (nom, capacite, files, etat, lock, etc.) ; API publique : `getNom()`, `getCapacite()`, `getEtat()`, `ajouterVisiteur()`, `accepteVisiteur()`, `getTailleFileTotal()`, etc.  
- **Visiteur :** champs `protected` (id, nom, age, argent, etat, etc.) ; accès via getters/setters (ex. `getArgent()`, `payer()`).  
- **Employe :** id, nom, poste, etat, fatigue en `protected` ; comportement exposé via méthodes.  
- **FileAttente :** `private final Queue<T> queue`, `ReentrantLock lock`, `Condition nonVide` ; pas d’accès direct, uniquement `ajouter()`, `retirer()`, `getTaille()`, etc.  
- **Statistiques :** compteurs en `AtomicInteger`/`AtomicLong` et maps ; pas d’exposition directe, seulement `ajouterVisiteur()`, `ajouterRevenus()`, `mettreAJourFile()`, `getNombreVisiteursActuels()`, `getRevenusTotalFormate()`, etc.  
- **UIStyles :** couleurs et polices en `public static final` ; pas d’état modifiable exposé.

---

### 4.2 Héritage

**Où c’est présent :** hiérarchies de classes.

| Classe de base | Sous-classes | Fichiers |
|----------------|-------------|----------|
| **Attraction** (abstract) | MontagnesRusses, GrandeRoue, ManegeEnfants, MaisonHantee, SimulateurVR, ChuteLibre, Carrousel, TobogganAquatique | `models/attractions/*.java` |
| **Visiteur** (abstract) | VisiteurEnfant, VisiteurCouple, VisiteurFamille, VisiteurGroupe, VisiteurExtreme | `models/visiteurs/*.java` |
| **Employe** (abstract) | Operateur, Technicien, AgentSecurite, Vendeur | `models/employes/*.java` |
| **Evenement** (abstract) | HappyHour, Parade, SpectacleNocturne | `models/evenements/*.java` |

Chaque sous-classe appelle `super(...)` dans son constructeur et réutilise le comportement de la classe mère (ex. `MontagnesRusses` ne fait qu’appeler `super(...)` avec des paramètres fixes).

---

### 4.3 Polymorphisme

**Où c’est présent :** utilisation de types de base pour manipuler des sous-classes.

- **Attractions :** `GestionnaireParc` stocke `List<Attraction>` et appelle `getAttractions()`, `getAttractionParNom()`, boucles sur `attractions` pour ouvrir/fermer/météo (ex. `fermerAttractionsExterieures()`, `rouvrirAttractionsExterieures()`). Chaque élément peut être une sous-classe concrète.  
- **Visiteurs :** `List<Visiteur> visiteurs` ; ajout/retrait via `ajouterVisiteur(Visiteur)`, `retirerVisiteur(Visiteur)`. Les profils (Enfant, Couple, Famille, etc.) sont des sous-types.  
- **Employés :** `List<Employe> employes` ; `getTechnicienDisponible()` et `getOperateurPourAttraction(Attraction)` utilisent `instanceof` pour caster (Technicien, Operateur).  
- **Employe :** méthode abstraite de travail dans `run()` : chaque type d’employé a un comportement différent (polymorphisme par héritage).  
- **Evenement :** `GestionnaireEvenements` utilise `Evenement evenementActuel` et des types concrets (HappyHour, Parade, SpectacleNocturne) pour lancer/terminer les événements.

---

### 4.4 Abstraction

**Où c’est présent :** classes et méthodes abstraites.

- **Attraction** : `public abstract class Attraction extends Thread` — pas d’instanciation directe ; méthodes concrètes (run, files, maintenance, panne) et éventuelles méthodes abstraites si présentes dans les sous-classes.  
- **Visiteur** : `public abstract class Visiteur extends Thread` — comportement commun (état, argent, billet) ; sous-classes pour Enfant, Couple, Famille, Groupe, Extreme.  
- **Employe** : `public abstract class Employe extends Thread` — méthode abstraite de travail (chaque type d’employé l’implémente).  
- **Evenement** : classe abstraite dans `models/evenements/Evenement.java` ; sous-classes pour les différents événements.

---

## 5. Application GUI professionnelle réactive

**Où c’est présent :** package `views` et ressources de style.

- **Fenêtre principale :** `src/main/java/com/parcattractions/views/MainFrame.java` — JFrame, barre de menus (Parc, Visiteurs, Attractions, Météo, Manager, Services, Gestion RH, Affichage), CardLayout pour basculer entre vues (dashboard, notifications, transactions, rapports).  
- **Panneaux :**  
  - `PanelDashboard.java` — tableau de bord (heure, visiteurs, attractions, météo, statut, recettes).  
  - `PanelStatistiques.java` — indicateurs et graphiques textuels.  
  - `PanelAttractions.java` — liste d’attractions, boutons Ouvrir/Fermer/Détails/Prendre ticket.  
  - `PanelNotifications.java` — liste des notifications/alertes.  
  - `PanelTransactions.java` — moniteur financier (billets, restaurant, souvenirs, total).  
  - `PanelRapports.java` — types de rapports, génération, export CSV/HTML.  
- **Dialogs :**  
  - `DialogAjoutVisiteur`, `DialogAjoutEmploye`, `DialogAjoutAttraction` — formulaires d’ajout.  
  - `DialogGestionVisiteurs`, `DialogGestionPersonnel`, `DialogGestionRestaurants`, `DialogGestionBoutiques` — gestion des entités.  
  - `DialogConfiguration` — configuration (ex. météo).  
  - `DialogDetailsAttraction` — détails d’une attraction, prise de ticket, ouvrir/fermer/mettre en panne.  
  - `DialogRapports` — onglets Finance, Frequentation, Satisfaction, Rapport global.  
- **Style :** `src/main/java/com/parcattractions/resources/styles/UIStyles.java` — palette (DUSK_BLUE, DUSTY_LAVENDER, ROSEWOOD, LIGHT_CORAL, LIGHT_BRONZE), polices (TITLE_FONT, HEADER_FONT, REGULAR_FONT, MONOSPACE_FONT), `createStyledBorder()`, `stylePrimaryButton()`, `styleSecondaryButton()`, `styleAccentButton()`, `getStateColor()`.  
- **Réactivité :**  
  - Timer dans Dashboard, Transactions, Statistiques pour mise à jour périodique.  
  - `rafraichirTousLesPanels()` dans MainFrame (après actions menu) qui rafraîchit le dashboard.  
  - Pas de boucles bloquantes dans l’EDT ; logique longue dans threads séparés (ex. maintenance, réparation).

---

## 6. Gestion d’événements (ActionListener, MouseAdapter, KeyAdapter)

### 6.1 ActionListener (très utilisé)

**Où c’est présent :** menus et boutons dans toute la couche vue.

| Fichier | Usage |
|---------|--------|
| **MainFrame.java** | Tous les items de menu : `iOuvrir.addActionListener(e -> { gp.ouvrirParc(); ... })`, `iFermer.addActionListener(...)`, `iPause.addActionListener(...)`, `iAjouterVisiteur.addActionListener(...)`, `iListeVisiteurs.addActionListener(...)`, `iOuvrirAttraction` / `iFermerAttraction`, `iChangerMeteo`, `iEvenement`, `iGestionPersonnel`, `iGestionRestaurants`, `iGestionBoutiques`, `iAjouterEmploye`, `iGererRH`, `iDashboard` / `iNotifications` / `iTransactions` / `iRapports` (cardLayout.show) (l.35–194). |
| **PanelAttractions.java** | `btnOuvrir.addActionListener(e -> actionAttraction("ouvrir"))`, `btnDetails.addActionListener(e -> ouvrirDetails())`, `comboAttractions.addActionListener(...)`, `btnTicket.addActionListener(...)` (l.55–56, 73, 143). |
| **PanelRapports.java** | `typeRapportCombo.addActionListener(e -> rafraichir())`, `btnGenerer.addActionListener(e -> genererRapport())`, `btnExporter.addActionListener(e -> exporterCSV())`, `btnExporterHTML.addActionListener(e -> exporterHTML())` (l.48–60). |
| **PanelStatistiques.java** | `btnRafraichir.addActionListener(e -> rafraichir())` (l.39). |
| **PanelNotifications.java** | `clearBtn.addActionListener(e -> { ... })` (l.37). |
| **DialogDetailsAttraction.java** | `prendreTicketBtn.addActionListener(e -> onPrendreTicket())`, `ouvrirAttraction.addActionListener(...)`, `fermerAttraction.addActionListener(...)`, `mettrePanne.addActionListener(...)`, `close.addActionListener(e -> dispose())` (l.98–146). |
| **DialogGestionRestaurants.java** | `menuBtn.addActionListener(e -> logicAfficherMenu())`, `reserverBtn.addActionListener(e -> logicReserver())`, `revenusBtn.addActionListener(e -> afficherRecapitulatifRevenus())`, `close.addActionListener(e -> dispose())` (l.63–75). |
| **DialogGestionBoutiques.java** | `stockBtn`, `acheterBtn`, `revenusBtn`, `close` — idem (l.64–76). |
| **DialogGestionVisiteurs.java** | `addBtn`, `delBtn`, `detailsBtn`, `close` (l.55–67). |
| **DialogGestionPersonnel.java** | `addBtn`, `delBtn`, `close` (l.57–65). |
| **DialogConfiguration.java** | `closeBtn.addActionListener(e -> dispose())` (l.47). |
| **DialogAjoutVisiteur.java** | `ajouterBtn.addActionListener(e -> logicAjouter())`, `annulerBtn.addActionListener(e -> dispose())` (l.67–68). |
| **DialogAjoutEmploye.java** | `ajouterBtn`, `annulerBtn` (l.61–65). |
| **DialogAjoutAttraction.java** | `okBtn.addActionListener(e -> { valide = true; setVisible(false); })`, `annulerBtn.addActionListener(e -> setVisible(false))` (l.74–78). |
| **DialogRapports.java** | `csvBtn`, `htmlBtn`, `quit` (l.59–67). |

L’ensemble des actions utilisateur (menus, boutons, listes déroulantes) est géré via **ActionListener** (souvent en lambdas `e -> ...`).

### 6.2 MouseAdapter / KeyAdapter

**Présence dans le projet :** aucune occurrence de `MouseAdapter`, `KeyAdapter`, `addMouseListener` ou `addKeyListener` dans les sources. La gestion d’événements repose sur **ActionListener** (clic boutons, choix de menu, changement de JComboBox). Pour étendre l’application (ex. clic droit, raccourcis clavier), on pourrait ajouter des MouseListener/MouseAdapter et KeyListener/KeyAdapter sur les composants concernés.

---

## 7. Synthèse : tableau de localisation

| Thème / Fonctionnalité | Fichier(s) principal(aux) |
|------------------------|----------------------------|
| **Multithreading** | `Horloge.java`, `Attraction.java`, `Visiteur.java`, `Employe.java`, `GestionnaireEvenements.java`, `GestionnaireMeteo.java`, `GenerateurVisiteurs.java`, `GestionnaireParc.java` (threads maintenance/urgence) |
| **Timer (Swing)** | `PanelDashboard.java`, `PanelTransactions.java`, `PanelStatistiques.java` |
| **Files d’attente** | `FileAttente.java`, `Attraction.java` (fileNormale, fileFastPass), `Restaurant.java`, `Statistiques.java` |
| **8 attractions (capacités)** | `GestionnaireParc.initialiserAttractionsParDefaut()`, et chaque classe dans `models/attractions/` (MontagnesRusses, GrandeRoue, etc.) |
| **Simulation temps réel** | `Horloge.java` (mode temps réel), Timer dans les panels pour affichage en continu |
| **Maintenance programmée** | `Attraction.java` (`verifierMaintenanceProgrammee`, `lancerMaintenance`, `finishMaintenanceFallback`) |
| **Encapsulation** | Toutes les classes modèles et utils (attributs private/protected, API publique) |
| **Héritage** | `Attraction` → 8 sous-classes ; `Visiteur` → 5 sous-classes ; `Employe` → 4 sous-classes ; `Evenement` → 3 sous-classes |
| **Polymorphisme** | Listes `Attraction`, `Visiteur`, `Employe` dans `GestionnaireParc` ; `instanceof` pour Technicien/Operateur |
| **Abstraction** | `Attraction`, `Visiteur`, `Employe`, `Evenement` (abstract) |
| **GUI réactive** | `MainFrame.java`, tous les `Panel*` et `Dialog*` dans `views/`, `UIStyles.java` |
| **ActionListener** | `MainFrame`, `PanelAttractions`, `PanelRapports`, `PanelStatistiques`, `PanelNotifications`, tous les `Dialog*` |
| **MouseAdapter / KeyAdapter** | Non utilisés ; possibilité d’extension pour clics souris et clavier |

---

## 8. Structure des packages (rappel)

```
src/main/java/com/parcattractions/
├── controllers/     (GestionnaireParc, GestionnaireEvenements, GestionnaireMeteo, GenerateurVisiteurs, SystemeNotifications)
├── enums/           (EtatAttraction, EtatEmploye, EtatVisiteur, Meteo, NiveauIntensite, ProfilVisiteur, TypeAttraction, TypeBillet, TypeNotification)
├── exceptions/      (attractions, employes, services, systeme, visiteurs)
├── models/
│   ├── attractions/ (Attraction + 8 implémentations)
│   ├── employes/    (Employe + 4 implémentations)
│   ├── evenements/  (Evenement, HappyHour, Parade, SpectacleNocturne)
│   ├── services/    (Boutique, Restaurant, ServiceManager, Billet, etc.)
│   └── visiteurs/   (Visiteur + 5 implémentations)
├── resources/       (images, styles/UIStyles)
├── utils/           (CSVManager, DataManager, ExporteurCSV, FileAttente, GenerateurRapports, Horloge, Logger, ModeConfig, Notification, Statistiques, Tarification, TransactionManager)
└── views/           (MainFrame, Panel*, Dialog*)
```

Point d’entrée : `src/Main.java` — mode manuel, pas de génération automatique de visiteurs, ouverture du parc et ajout de visiteurs via le menu.

---

*Document généré pour le projet Parc d’attractions — Thème : Multithreading, Timer, files d’attente ; POO ; GUI réactive ; gestion d’événements (ActionListener).*
