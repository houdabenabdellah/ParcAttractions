# 📋 Mode de Gestion - Guide d'utilisation

## 🎯 Modes de Fonctionnement

### 1. **Mode SIMULATION** 🤖
- Génération automatique de visiteurs
- Les attractions s'ouvrent/ferment automatiquement
- Les météo et événements s'activent en continu
- Parfait pour tester la logique métier

### 2. **Mode GESTION** 👨‍💼
- Gestion complètement manuelle
- Ajout/suppression de visiteurs via interface
- Contrôle manuel des états des attractions
- Les données se chargent depuis `data/` au démarrage
- Parfait pour démonstration et gestion administrative

### 3. **Mode HYBRIDE** ⚙️
- Combinaison simulation + gestion manuelle
- Générateur de visiteurs actif
- Possibilité d'ajout manuel en parallèle
- Contrôle partagé

---

## 📊 Structure des Fichiers de Données

### Répertoire: `data/`

#### 1. `attractions.csv`
```
Nom;Type;État;CapacitéMax;VisiteursTotaux;TempsAttente;Revenus
```
- **Nom**: Nom de l'attraction
- **Type**: MONTAGNES_RUSSES, MAISON_HANTEE, GRANDE_ROUE, etc.
- **État**: OUVERTE, MAINTENANCE, FERMEE
- **CapacitéMax**: Nombre de places
- **VisiteursTotaux**: Nombre de visiteurs ayant utilisé l'attraction
- **TempsAttente**: Temps d'attente estimé en secondes
- **Revenus**: Revenus générés en euros

#### 2. `employes.csv`
```
ID;Nom;Poste;État;Fatigue;SalaireJour
```
- **ID**: Identifiant unique
- **Nom**: Nom complet
- **Poste**: Animateur, Contremaître, Caissière, Maintenance
- **État**: EN_SERVICE, EN_REPOS, ABSENCE
- **Fatigue**: Niveau de fatigue en %
- **SalaireJour**: Salaire journalier en euros

#### 3. `visiteurs.csv`
```
ID;Nom;Age;Taille;Profil;État;Satisfaction;Tours
```
- **ID**: Identifiant unique
- **Nom**: Nom complet
- **Age**: Âge en années
- **Taille**: Taille en mètres
- **Profil**: SOLO, COUPLE, FAMILLE, GROUPE, ENFANT, EXTREME
- **État**: EN_ATTENTE, EN_ATTRACTION, SORTIE
- **Satisfaction**: Niveau de satisfaction en %
- **Tours**: Nombre de tours effectués

#### 4. `ventes_billets.csv`
```
ID;IdVisiteur;TypeBillet;Prix;DateTime
```
- Historique des ventes de billets

#### 5. `ventes_restaurant.csv`
```
ID;IdVisiteur;Repas;Prix;DateTime
```
- Historique des ventes en restaurant

#### 6. `ventes_souvenirs.csv`
```
ID;IdVisiteur;Article;Prix;DateTime
```
- Historique des ventes de souvenirs/boutiques

#### 7. `sessions.csv`
```
DateDébut;DateFin;NombreVisiteurs;RevenuTotal;DuréeHeures
```
- Historique des sessions d'exploitation du parc

---

## 🚀 Démarrage de l'Application

### Étape 1: Lancer Main.java
Une fenêtre de dialogue s'affiche pour sélectionner le mode

### Étape 2: Sélectionner un mode
- SIMULATION: Automatique complet
- GESTION: Manuel complet (recommandé pour démonstration)
- HYBRIDE: Combinaison des deux

### Étape 3: Données chargées
Les données existantes du répertoire `data/` sont chargées automatiquement

### Étape 4: Interface active
- Tableau de bord avec statistiques
- Gestion des attractions
- Gestion des employés
- Transactions et ventes

---

## 💾 Sauvegarde des Données

### Automatique à la Fermeture
- Toutes les modifications sont sauvegardées automatiquement
- Les fichiers CSV sont mis à jour
- L'historique des sessions est conservé

### Sauvegarde Manuelle
```java
DataManager.saveAllData(gestionnaireParc);
```

### Chargement Depuis CSV
```java
DataManager.loadAllData(gestionnaireParc);
```

---

## ⚙️ API DataManager

### Méthodes principales

```java
// Initialiser les répertoires
DataManager.initializeDataDirectory();

// Charger toutes les données
DataManager.loadAllData(gestionnaireParc);

// Charger spécifiquement
DataManager.loadAttractions(gestionnaireParc);
DataManager.loadEmployes(gestionnaireParc);
DataManager.loadVisiteurs(gestionnaireParc);

// Sauvegarder toutes les données
DataManager.saveAllData(gestionnaireParc);

// Sauvegarder spécifiquement
DataManager.saveAttractions(gestionnaireParc);
DataManager.saveEmployes(gestionnaireParc);
DataManager.saveVisiteurs(gestionnaireParc);
DataManager.saveSession(gestionnaireParc);
```

---

## 📝 Exemples de Fichiers

### Exemple: attractions.csv
```csv
Nom;Type;État;CapacitéMax;VisiteursTotaux;TempsAttente;Revenus
Montagnes Russes 1;MONTAGNES_RUSSES;OUVERTE;50;150;45;1500.00€
Maison Hantée;MAISON_HANTEE;OUVERTE;30;100;30;800.00€
Grande Roue;GRANDE_ROUE;OUVERTE;80;200;20;2000.00€
```

### Exemple: employes.csv
```csv
ID;Nom;Poste;État;Fatigue;SalaireJour
EMP001;Jean Dupont;Animateur;EN_SERVICE;25.5%;50.00€
EMP002;Marie Martin;Contremaître;EN_SERVICE;15.3%;55.00€
```

### Exemple: visiteurs.csv
```csv
ID;Nom;Age;Taille;Profil;État;Satisfaction;Tours
V001;Alice Dupont;8;1.20;ENFANT;EN_ATTENTE;85.0%;3
V002;Bob Martin;35;1.75;SOLO;EN_ATTENTE;75.0%;2
```

---

## 🔍 Troubleshooting

### Les données ne se chargent pas
- Vérifier que le répertoire `data/` existe
- Vérifier le format des fichiers CSV
- Consulter les logs pour plus de détails

### Erreur "Fichier non trouvé"
- Les fichiers CSV sont optionnels
- L'application fonctionne avec des données par défaut
- Les fichiers sont créés à la fermeture

### Corruption de données
- Les sauvegardes automatiques créent des backups
- Restaurer depuis un point de sauvegarde antérieur

---

## 🎓 Points Clés

✅ **Persistance**: Les données sont sauvegardées en CSV  
✅ **Flexibilité**: 3 modes de fonctionnement  
✅ **Scalabilité**: Structure extensible pour nouveaux éléments  
✅ **Audit**: Historique complet des sessions et transactions  
✅ **Facilité**: Interface intuitive pour gestion manuelle  

---

**Version**: 1.0  
**Date**: 29/01/2026  
**Auteur**: Équipe Développement
