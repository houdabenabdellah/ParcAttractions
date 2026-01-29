# Conformité du projet aux diagrammes (séquence Visiteur, Maintenance, activité Visiteur)

Ce document compare le code du projet avec les trois diagrammes fournis.

---

## 1. Diagramme de séquence – Visiteur rejoint une attraction

| Élément du diagramme | Implémenté ? | Détail dans le projet |
|---------------------|--------------|------------------------|
| **Phase 1 : Arrivée et achat billet** | | |
| `parc.ajouterVisiteur(visiteur)` avant `acheterBillet` | ✅ | `GenerateurVisiteurs` appelle `parc.ajouterVisiteur(visiteur)` puis `visiteur.start()` ; dans `run()`, premier appel = `acheterBillet()`. |
| `stats.incrementerVisiteurs()` | ✅ | `Statistiques.ajouterVisiteur()` (équivalent), appelé dans `GestionnaireParc.ajouterVisiteur()`. |
| `v.acheterBillet()` → `calculerPrix(age, type)` | ✅ | `Tarification.calculerPrix(age, type)` dans `Visiteur.acheterBillet()`. |
| **Phase 2 : Choix d’attraction** | | |
| `choisirAttraction()` → boucle sur attractions | ✅ | Méthode abstraite implémentée par profil (ex. `VisiteurFamille`), boucle sur `getAttractionsDisponibles()`. |
| `attr.getTempsAttente()` → `file.size()` | ✅ | `Attraction.getTempsAttenteEstime()` utilise `getTailleFileTotal()` (files `fileNormale` / `fileFastPass`). |
| `calculerScore(attraction, temps)` | ✅ | Ex. `VisiteurFamille.calculerScore()` selon type, temps d’attente, intensité, etc. |
| `selectionnerMeilleure()` | ✅ | Choix de l’attraction avec le meilleur score dans `choisirAttraction()`. |
| **Phase 3 : Vérification restrictions** | | |
| `attr.accepteVisiteur(...)` | ✅ | `Attraction.accepteVisiteur(age, taille)` (âge, taille min). |
| `verifierAge` / `verifierTaille` | ✅ | Regroupés dans `accepteVisiteur(age, taille)`. |
| **Phase 4 : Rejoindre file** | | |
| `attr.ajouterVisiteur(visiteur, hasFastPass)` | ⚠️ | `attr.ajouterVisiteur(id, fastPass)` : on passe l’**id** du visiteur, pas l’objet. Comportement fonctionnellement équivalent. |
| `fileFastPass.add` / `fileNormale.add` | ✅ | `FileAttente<Integer>` ; ajout dans `fileFastPass` ou `fileNormale` selon `fastPass`. |
| `stats.mettreAJourFile(attraction)` | ❌ | Aucune méthode `mettreAJourFile` dans `Statistiques`. Pas de mise à jour des stats à l’ajout en file. |
| Notification « file saturée > 100 » | ❌ | Pas de `SystemeNotifications` quand la file dépasse un seuil (ex. 100). `CAPACITE_MAX_FILE = 150`, pas d’alerte. |
| **Phase 5 : Attente et tour** | | |
| `v.attendreTour()` | ⚠️ | Pas de méthode dédiée ; attente simulée par `Thread.sleep(tempsAttente)` dans `rejoindrefile()`. |
| `attr.demarrerTour()` / `executerTour` | ✅ | `Attraction.executerTour()` quand `getTailleFileTotal() >= capacite`. |
| `file.poll(capacite fois)` (priorité FastPass) | ✅ | `chargerVisiteurs()` : d’abord `fileFastPass`, puis `fileNormale`, jusqu’à `capacite`. |
| `attr.effectuerTour()` (simulation durée) | ✅ | `Thread.sleep(dureeTour * 1000L)` dans `executerTour()`. |
| `stats.incrementerToursEffectues()` | ✅ | `Statistiques.ajouterTour()` appelé dans `Attraction` à la fin d’un tour. |
| `attr.liberer()` vers visiteur | ❌ | Pas d’appel explicite « libérer » du visiteur. Le visiteur gère lui‑même attente + tour (sleep) puis reprend. Coordination différente du diagramme. |
| **Phase 6 : Décision suivante** | | |
| `quitterParc()` → `parc.retirerVisiteur()` → `stats.retirerVisiteur()` | ✅ | `Visiteur.quitterParc()` appelle `GestionnaireParc.retirerVisiteur(this)` qui décrémente les stats. |

**Résumé séquence Visiteur :**  
Grande partie du flux est couverte (arrivée, billet, choix d’attraction, restrictions, files, tours, stats, sortie). Manques principaux : `stats.mettreAJourFile(attraction)`, notification « file saturée », et `attr.liberer()` vers le visiteur (remplacé par une coordination par sleep côté visiteur).

---

## 2. Diagramme de séquence – Maintenance et panne

| Élément du diagramme | Implémenté ? | Détail dans le projet |
|---------------------|--------------|------------------------|
| **Scénario 1 : Maintenance programmée** | | |
| `attr.verifierCompteurTours()` | ✅ | `verifierMaintenanceProgrammee()` : `toursEffectues % TOURS_AVANT_MAINTENANCE == 0`. |
| `etat = MAINTENANCE` | ✅ | `Attraction.lancerMaintenance()` met `etat = MAINTENANCE`. |
| Notif « Maintenance programmée » | ❌ | Seul `Logger` ; pas d’`ajouterNotification` (INFO, "Maintenance programmée"). |
| `parc.getTechnicienDisponible()` | ⚠️ | Méthode présente dans `GestionnaireParc`, mais **jamais utilisée** par l’attraction. |
| `tech.effectuerMaintenance(attraction)` | ❌ | `Technicien.effectuerMaintenance(attraction)` existe, mais **n’est jamais appelé**. |
| Maintenance par un **technicien** | ❌ | La maintenance est faite par un **thread anonyme** dans `Attraction.lancerMaintenance()` (sleep 30 s, puis `etat = OPERATIONNELLE`, `toursEffectues = 0`). Aucun `Technicien` impliqué. |
| Notif « Maintenance terminée » | ❌ | Seul `Logger` ; pas de notification. |
| **Scénario 2 : Panne aléatoire** | | |
| `attr.verifierPanneAleatoire()` | ✅ | `Math.random() < PROBABILITE_PANNE` → `declencherPanne()`. |
| `attr.etat = PANNE` | ✅ | Dans `declencherPanne()`. |
| `op.notifierPanne()` | ❌ | Aucun appel à l’opérateur. |
| `op.evacuerFile()` / `donnerFastPassCompensation` | ❌ | Pas d’évacuation par l’opérateur ; pas de compensation Fast‑Pass pour les visiteurs évacués. |
| Évacuation des files | ✅ | `Attraction.evacuerFiles()` vide les files (normale + FastPass). |
| Notif URGENCE « PANNE – Intervention requise » | ❌ | Pas d’`ajouterNotification` (URGENCE, …) pour une panne. |
| `notif.alerterManager()` | ❌ | `SystemeNotifications` n’a pas de méthode `alerterManager`. |
| `stats.mettreAJourDisponibilite(attraction, false/true)` | ❌ | Aucune méthode `mettreAJourDisponibilite` dans `Statistiques`. |
| `parc.demanderTechnicienUrgence()` | ❌ | N’existe pas. |
| `tech.intervenir(attr)` / réparation par technicien | ❌ | `Technicien.intervenir(attraction)` existe mais **n’est jamais appelé** pour une panne. |
| Réparation | ⚠️ | Réparation simulée par un **thread anonyme** dans `Attraction.declencherPanne()` (sleep 20–60 s), puis `etat = OPERATIONNELLE`. Aucun technicien. |
| **Scénario 3 : Impact météo** | | |
| `meteo.changerMeteo()` | ✅ | `GestionnaireMeteo` appelle `gestionnaireParc.setMeteoActuelle(...)`. |
| Pluie + extérieur → `attr.fermer()` | ✅ | `fermerAttractionsExterieures()` ferme les attractions extérieures. |
| `op.evacuerFile()` / « suggérer attraction intérieure » | ❌ | Pas d’évacuation par l’opérateur ni de suggestion. On ferme uniquement l’attraction. |
| Notif « Fermeture météo » | ❌ | Pas de notification. |
| Orage → `parc.evacuerParc()` | ✅ | `setMeteoActuelle(ORAGE)` déclenche `evacuerParc()` : fermeture attractions, arrêt des visiteurs. |
| Notif URGENCE « ÉVACUATION – Orage » | ❌ | Pas d’`ajouterNotification` pour l’évacuation orage. |
| `parc` → `attr.fermerUrgence()` pour chaque attraction | ⚠️ | On appelle `attraction.fermer()` dans `evacuerParc()`, pas une méthode `fermerUrgence` dédiée. Comportement équivalent. |

**Résumé séquence Maintenance :**  
Maintenance et panne sont **simulées côté Attraction** (threads anonymes), sans `Technicien` ni `Operateur`. Les rôles « maintenance par technicien », « panne → opérateur → technicien », notifications (maintenance, panne, météo) et `stats.mettreAJourDisponibilite` du diagramme ne sont pas implémentés.

---

## 3. Diagramme d’activité – Comportement visiteur

| Élément du diagramme | Implémenté ? | Détail dans le projet |
|---------------------|--------------|------------------------|
| Arriver → Acheter billet | ✅ | `run()` commence par `acheterBillet()`. |
| Type billet (Standard, Fast‑Pass, Pass journée) | ✅ | `TypeBillet` + `Tarification.calculerPrix(age, type)`. |
| Boucle « Patience > 0 et Heure < 22h » | ⚠️ | Boucle `actif && patience > 0 && satisfaction > 20`. Pas de test explicite « Heure < 22h » dans le visiteur ; la fermeture 22h est gérée par l’`Horloge` / le parc. |
| Faim > 70 → restaurant | ✅ | `if (faim > 70) allerRestaurant();` |
| Besoin toilettes | ✅ | `allerToilettes()` selon `besoinToilettes`. |
| Lister attractions, restrictions, temps attente, préférences | ✅ | `choisirAttraction()` (ex. `VisiteurFamille`) utilise `getAttractionsDisponibles()`, `accepteVisiteur`, `getTempsAttenteEstime`, préférences par type. |
| Calcul des scores | ✅ | Ex. `calculerScore()` dans les sous‑classes de `Visiteur`. |
| Fast‑Pass → file Fast‑Pass, sinon file normale | ✅ | `billet.donnePriorite()` puis `attr.ajouterVisiteur(id, fastPass)`. |
| Attendre dans la file ; patience – – ; quitter si patience ≤ 0 | ✅ | Attente par sleep ; vérification `tempsAttente > patience` → `PatienceEpuiseeException`, satisfaction – –, on quitte la file. |
| « Tour pas commencé » → boucle attente | ⚠️ | Pas de boucle fine « tour pas commencé ». On simule une attente globale (sleep) puis le tour (sleep). Comportement plus simplifié. |
| Monter → faire le tour → sortir ; Satisfaction ++ | ✅ | `rejoindrefile()` : attente, puis `etat = EN_ATTRACTION`, sleep durée tour, `satisfaction += 15`, etc. |
| Aucune attraction compatible → boutique | ⚠️ | Si `choisirAttraction()` retourne `null`, on peut `acheterSouvenir()` avec une proba, puis `break`. Pas exactement « aller en boutique » systématique. |
| Heure > 18h + souvenirs non achetés → boutique | ❌ | Pas de test « Heure > 18h » ni « souvenirs non achetés » dans la boucle. |
| Événement en cours (Parade, Spectacle, Happy Hour) | ❌ | Les événements existent (`GestionnaireEvenements`), mais le **visiteur ne vérifie pas** « Événement en cours » ni n’adapte son comportement (observer parade, assister spectacle, etc.). |
| Orage → évacuer vers sortie | ✅ | Orage → `evacuerParc()` → `visiteur.arreter()` ; le visiteur sort. |
| Satisfaction finale, avis positif/négatif | ❌ | On log la satisfaction en quittant, mais pas « laisser avis positif/négatif » ni structure dédiée. |
| Quitter → Enregistrer statistiques | ✅ | `quitterParc()` → `retirerVisiteur(this)` → `stats.retirerVisiteur()`. |

**Résumé activité Visiteur :**  
Le cœur du parcours (billet, faim/toilettes, choix d’attraction, files, tour, satisfaction, sortie) est bien présent. Manques ou simplifications : boucle explicitement conditionnée par « Heure < 22h », gestion fine « tour pas commencé », heure > 18h + souvenirs, réaction aux événements (Parade, Spectacle, Happy Hour), et « avis positif/négatif ».

---

## 4. Synthèse des écarts

### À ajouter / corriger pour se rapprocher des diagrammes

1. **Statistiques**
   - `mettreAJourFile(attraction)` (ou équivalent) lors de l’ajout en file.
   - `mettreAJourDisponibilite(attraction, boolean)` en maintenance / panne / réparation.

2. **Notifications**
   - Maintenance programmée / terminée (INFO).
   - Panne : URGENCE « PANNE – Intervention requise » (et éventuellement `alerterManager` si vous l’ajoutez).
   - File saturée (ex. > 100) : ATTENTION.
   - Météo : fermeture extérieur (ATTENTION), évacuation orage (URGENCE).

3. **Maintenance et panne**
   - Utiliser `parc.getTechnicienDisponible()` puis `technicien.effectuerMaintenance(attraction)` pour la maintenance programmée.
   - Pour la panne : impliquer l’opérateur (évacuation, compensation Fast‑Pass si prévu), puis `Technicien.intervenir(attraction)` (ou équivalent) au lieu d’un thread anonyme.
   - Optionnel : `demanderTechnicienUrgence()` côté parc.

4. **Visiteur**
   - Si vous souhaitez coller au diagramme : coordination plus explicite avec l’attraction (ex. « libération » du visiteur à la fin du tour), ou au moins documenter que la coordination actuelle (sleep) en tient lieu.
   - Condition « Heure < 22h » dans la boucle du visiteur (ou arrêt propre quand le parc ferme).
   - Réaction aux événements (Parade, Spectacle, Happy Hour) et règles « heure > 18h » + « souvenirs non achetés » si vous voulez suivre l’activité à la lettre.
   - Avis positif/négatif en fin de visite si vous décidez de les matérialiser dans le modèle.

5. **Météo**
   - Notifications pour fermeture extérieur et évacuation orage (voir ci‑dessus).
   - Rôle de l’opérateur (évacuation file, suggestion attraction intérieure) seulement si vous voulez aligner avec le séquence météo.

---

## 5. Conclusion

Le projet **contient** l’essentiel des flux décrits dans les diagrammes (arrivée, billet, choix d’attraction, files, tours, maintenance/panne simulées, météo, sortie). En revanche, **plusieurs éléments des diagrammes ne sont pas (ou pas entièrement) présents** :

- Implication réelle des **Techniciens** et **Opérateurs** dans maintenance et panne.
- **Notifications** (maintenance, panne, file saturée, météo).
- **Statistiques** « file » et « disponibilité ».
- **Visiteur** : condition 22h, réaction aux événements, règles boutique/heure, avis.

En ajoutant les points listés dans la section 4, le projet se rapprocherait nettement des trois diagrammes.
