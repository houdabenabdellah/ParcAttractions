# Grand Parc Simulator 2026

Application Java de simulation et gestion d’un parc d’attractions en temps réel : attractions, files d’attente, maintenance programmée, personnel, visiteurs et services (restaurants, boutiques). Interface graphique Swing en **mode manuel** (contrôle total par le gestionnaire).

## Prérequis

- **Java 11** ou supérieur (JDK avec support Swing)
- Aucune dépendance externe (bibliothèques standards uniquement)

## Lancer l’application

1. Ouvrir le projet dans VS Code (ou autre IDE).
2. Compiler et exécuter la classe **`Main`** :
   - Point d’entrée : `src/Main.java`
   - Depuis la racine du projet :  
     `javac -encoding UTF-8 src/Main.java src/main/java/com/parcattractions/**/*.java` puis  
     `java -cp "src;src/main/java" Main`  
   - Ou utiliser le **Run** de l’IDE sur `Main.java`.

Au démarrage, le parc est **fermé**. Utiliser le menu **Le Parc → Ouvrir le Parc** pour démarrer la simulation, puis **Visiteurs → Ajouter un visiteur** pour ajouter des visiteurs manuellement.

## Fonctionnalités principales

- **8 attractions** avec capacités et files d’attente (normale + Fast Pass)
- **Simulation temps réel** : horloge, tableau de bord, statistiques et moniteur financier mis à jour en continu
- **Files d’attente** thread-safe (attractions et restaurants)
- **Maintenance programmée** (tous les N tours) et gestion des pannes
- **Gestion** : visiteurs, personnel, restaurants, boutiques, météo, événements (Happy Hour, Parade, Spectacle)
- **Rapports** : résumés, export CSV/HTML

## Structure du projet

```
ParcAttractions/
├── src/
│   ├── Main.java                    # Point d'entrée (mode manuel)
│   └── main/java/com/parcattractions/
│       ├── controllers/             # GestionnaireParc, événements, météo, notifications
│       ├── enums/                   # États, météo, types d'attractions, billets, etc.
│       ├── exceptions/              # Exceptions métier (attractions, employés, visiteurs, système)
│       ├── models/
│       │   ├── attractions/         # Attraction (abstract) + 8 implémentations
│       │   ├── employes/            # Employe (abstract) + Opérateur, Technicien, Vendeur, AgentSecurite
│       │   ├── evenements/          # Happy Hour, Parade, Spectacle Nocturne
│       │   ├── services/            # Restaurant, Boutique, Billet, ServiceManager
│       │   └── visiteurs/           # Visiteur (abstract) + 5 profils
│       ├── resources/               # Images des attractions, styles (UIStyles)
│       ├── utils/                   # Horloge, FileAttente, Logger, CSV, Statistiques, etc.
│       └── views/                   # MainFrame, Panel*, Dialog* (GUI Swing)
├── data/                            # Fichiers CSV (visiteurs, attractions, sessions, ventes)
├── logs/                            # Logs et rapports exportés
├── DOCUMENTATION_RAPPORT.md         # Documentation technique et rapport complet
└── README.md                        # Ce fichier
```

Les sorties compilées sont dans le dossier **`bin/`** par défaut.

## Documentation complète

Pour le détail technique (multithreading, timers, files d’attente, POO, GUI, gestion d’événements) et la localisation précise dans le code, voir **[DOCUMENTATION_RAPPORT.md](DOCUMENTATION_RAPPORT.md)**.

## Licence

Projet pédagogique — Parc d’attractions (JoT).
