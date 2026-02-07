package main.java.com.parcattractions.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import main.java.com.parcattractions.enums.EtatAttraction;
import main.java.com.parcattractions.enums.Meteo;
import main.java.com.parcattractions.enums.TypeBillet;
import main.java.com.parcattractions.enums.TypeNotification;
import main.java.com.parcattractions.exceptions.attractions.AttractionException;
import main.java.com.parcattractions.exceptions.systeme.ParcFermeException;
import main.java.com.parcattractions.models.attractions.Attraction;
import main.java.com.parcattractions.models.attractions.Carrousel;
import main.java.com.parcattractions.models.attractions.ChuteLibre;
import main.java.com.parcattractions.models.attractions.GrandeRoue;
import main.java.com.parcattractions.models.attractions.MaisonHantee;
import main.java.com.parcattractions.models.attractions.ManegeEnfants;
import main.java.com.parcattractions.models.attractions.MontagnesRusses;
import main.java.com.parcattractions.models.attractions.SimulateurVR;
import main.java.com.parcattractions.models.attractions.TobogganAquatique;
import main.java.com.parcattractions.models.employes.AgentSecurite;
import main.java.com.parcattractions.models.employes.Employe;
import main.java.com.parcattractions.models.employes.Operateur;
import main.java.com.parcattractions.models.employes.Technicien;
import main.java.com.parcattractions.models.employes.Vendeur;
import main.java.com.parcattractions.models.services.Boutique;
import main.java.com.parcattractions.models.services.Restaurant;
import main.java.com.parcattractions.models.visiteurs.Visiteur;
import main.java.com.parcattractions.utils.CSVManager;
import main.java.com.parcattractions.utils.ExporteurCSV;
import main.java.com.parcattractions.utils.Horloge;
import main.java.com.parcattractions.utils.Logger;
import main.java.com.parcattractions.utils.Statistiques;
import main.java.com.parcattractions.utils.TransactionManager;

/**
 * Gestionnaire principal du parc d'attractions
 * Singleton pattern pour garantir une seule instance
 */
public class GestionnaireParc {

    private volatile double tauxReductionActif = 0.0;
    
    private static GestionnaireParc instance;
    
    // Attractions
    private final List<Attraction> attractions;
    
    // Services
    private final List<Restaurant> restaurants;
    private final List<Boutique> boutiques;
    
    // Personnel
    private final List<Employe> employes;
    
    // Visiteurs
    private final List<Visiteur> visiteurs;
    
    // Systèmes
    private Horloge horloge;
    private Statistiques statistiques;
    private volatile Meteo meteoActuelle;
    private volatile boolean parcOuvert;
    private GestionnaireEvenements gestionnaireEvenements;
    
    // Persistance et session
    private LocalDateTime dateOuvertureSession;
    private double revenuSessionActuelle;
    
    /**
     * Constructeur privé (Singleton)
     */
    private GestionnaireParc() {
        this.attractions = new CopyOnWriteArrayList<>();
        this.restaurants = new CopyOnWriteArrayList<>();
        this.boutiques = new CopyOnWriteArrayList<>();
        this.employes = new CopyOnWriteArrayList<>();
        this.visiteurs = new CopyOnWriteArrayList<>();
        this.meteoActuelle = Meteo.ENSOLEILLE;
        this.parcOuvert = false;
        this.statistiques = new Statistiques();
        this.revenuSessionActuelle = 0.0;
        
        // Initialiser CSVManager
        CSVManager.initialiserFichiers();
        Logger.logInfo("CSVManager initialisé");
        
        initialiserParc();
    }
    
    /**
     * Retourne l'instance unique (Singleton)
     */
    public static synchronized GestionnaireParc getInstance() {
        if (instance == null) {
            instance = new GestionnaireParc();
        }
        return instance;
    }
    
    /**
     * Ajoute les 8 attractions par défaut si la liste est vide (après chargement CSV vide par ex.).
     */
    public synchronized void initialiserAttractionsParDefaut() {
        if (!attractions.isEmpty()) return;
        attractions.add(new MontagnesRusses());
        attractions.add(new GrandeRoue());
        attractions.add(new ManegeEnfants());
        attractions.add(new MaisonHantee());
        attractions.add(new SimulateurVR());
        attractions.add(new ChuteLibre());
        attractions.add(new Carrousel());
        attractions.add(new TobogganAquatique());
        Logger.logInfo("8 attractions par défaut créées");
    }

    /**
     * Initialise le parc avec toutes les attractions
     */
    private void initialiserParc() {
        Logger.logInfo("Initialisation du parc d'attractions...");
        
        // Créer les 8 attractions selon spécifications PDF
        initialiserAttractionsParDefaut();
        
        // Créer restaurants
        restaurants.add(new Restaurant("Restaurant Central", 80));
        restaurants.add(new Restaurant("Snack Rapide", 40));
        
        Logger.logInfo("2 restaurants créés");
        
        // Créer boutiques
        boutiques.add(new Boutique("Boutique Souvenirs"));
        boutiques.add(new Boutique("Boutique Photos"));
        
        Logger.logInfo("2 boutiques créées");
        
        // Créer employés selon spécifications PDF
        initialiserEmployes();
        
        Logger.logInfo("Parc initialisé avec succès");
    }
    
    /**
     * Initialise tous les employés du parc
     */
    private void initialiserEmployes() {
        // 8 Opérateurs (un par attraction)
        String[] nomsOperateurs = {
            "Jean Dupont", "Marie Martin", "Pierre Bernard", "Sophie Dubois",
            "Luc Moreau", "Emma Laurent", "Thomas Simon", "Julie Michel"
        };
        
        for (int i = 0; i < attractions.size() && i < nomsOperateurs.length; i++) {
            Operateur operateur = new Operateur(nomsOperateurs[i], attractions.get(i));
            employes.add(operateur);
        }
        Logger.logInfo("8 opérateurs créés");
        
        // 4 Techniciens (pool)
        String[] nomsTechniciens = {
            "Robert Durand", "Claire Leroy", "Marc Petit", "Isabelle Roux"
        };
        
        for (String nom : nomsTechniciens) {
            Technicien technicien = new Technicien(nom);
            employes.add(technicien);
        }
        Logger.logInfo("4 techniciens créés");
        
        // 3 Agents de Sécurité
        String[] nomsAgents = {
            "Paul Girard", "Anne Lefebvre", "Michel Bonnet"
        };
        
        for (String nom : nomsAgents) {
            AgentSecurite agent = new AgentSecurite(nom);
            employes.add(agent);
        }
        Logger.logInfo("3 agents de sécurité créés");
        
        // 5 Vendeurs (2 pour restaurants, 3 pour boutiques)
        Vendeur vendeur1 = new Vendeur("Camille Vincent", restaurants.get(0));
        Vendeur vendeur2 = new Vendeur("Nicolas Fournier", restaurants.get(1));
        Vendeur vendeur3 = new Vendeur("Laura Garcia", boutiques.get(0));
        Vendeur vendeur4 = new Vendeur("David Martinez", boutiques.get(1));
        Vendeur vendeur5 = new Vendeur("Sarah Brown", boutiques.get(0)); // Second vendeur boutique souvenirs
        
        employes.add(vendeur1);
        employes.add(vendeur2);
        employes.add(vendeur3);
        employes.add(vendeur4);
        employes.add(vendeur5);
        
        Logger.logInfo("5 vendeurs créés");
        Logger.logInfo("Total: " + employes.size() + " employés créés");
    }
    
    /**
     * Ouvre le parc et démarre la simulation
     */
    public synchronized void ouvrirParc() throws ParcFermeException {
        if (parcOuvert) {
            throw new ParcFermeException("Le parc est déjà ouvert");
        }
        
        Logger.logInfo("═══════════════════════════════════════");
        Logger.logInfo("   OUVERTURE DU PARC D'ATTRACTIONS");
        Logger.logInfo("═══════════════════════════════════════");
        
        parcOuvert = true;
        dateOuvertureSession = LocalDateTime.now();
        revenuSessionActuelle = 0.0;
        
        // Horloge temps réel : heure système, avance en temps réel
        horloge = new Horloge(true);
        horloge.start();
        Logger.logInfo("Horloge démarrée (temps réel = heure système)");
        
        // Démarrer toutes les attractions
        //for (Attraction attraction : attractions) {
            //attraction.start();
            //Logger.logInfo("Attraction démarrée: " + attraction.getNom());
        //}

        // Attractions créées mais en mode manuel
        Logger.logInfo("Attractions prêtes (mode manuel)");
        
        // Démarrer tous les employés
        /**for (Employe employe : employes) {
            employe.start();
            Logger.logInfo("Employé démarré: " + employe.getNom());
        }
        
        Logger.logInfo("Parc ouvert et opérationnel");*/
        
        // Employés créés mais en mode manuel
        Logger.logInfo("Employés prêts (mode manuel)");
    }

    
        
    /**
     * Renvoie le nombre d'employés dans le parc
     */
    public int getNombreEmployes() {
        return employes.size();
    }
    
    /**
     * Ferme le parc proprement
     */
    public synchronized void fermerParc() {
        if (!parcOuvert) {
            return;
        }
        
        Logger.logInfo("═══════════════════════════════════════");
        Logger.logInfo("   FERMETURE DU PARC D'ATTRACTIONS");
        Logger.logInfo("═══════════════════════════════════════");
        
        parcOuvert = false;
        
        // Arrêter l'horloge
        if (horloge != null) {
            horloge.arreter();
        }
        
        // Arrêter toutes les attractions
        /**for (Attraction attraction : attractions) {
            attraction.arreter();
        }
        
        // Arrêter tous les visiteurs
        for (Visiteur visiteur : new ArrayList<>(visiteurs)) {
            visiteur.arreter();
        }
        
        // Arrêter tous les employés
        for (Employe employe : employes) {
            employe.arreter();
        }
        */
        // Sauvegarder les données de session
        sauvegarderSession();
        
        Logger.logInfo("Parc fermé");
        Logger.logInfo(statistiques.genererRapport());
        
        // Exporter automatiquement les rapports
        Logger.logInfo("Génération des rapports de fermeture...");
        ExporteurCSV exporteur = new ExporteurCSV(this);
        String fichierCSV = exporteur.exporterResume();
        String fichierHTML = exporteur.exporterHTML();
        
        if (fichierCSV != null) {
            Logger.logInfo("Rapport CSV généré : " + fichierCSV);
        }
        if (fichierHTML != null) {
            Logger.logInfo("Rapport HTML généré : " + fichierHTML);
        }
    }

    /**
     * Met la simulation en pause (horloge en pause)
     */
    public synchronized void pauseSimulation() {
        if (horloge != null) {
            horloge.mettrePause();
            Logger.logInfo("Simulation mise en pause");
        }
    }

    /**
     * Reprend la simulation (horloge reprise)
     */
    public synchronized void reprendreSimulation() {
        if (horloge != null) {
            horloge.reprendre();
            Logger.logInfo("Simulation reprise");
        }
    }

    /**
     * Avance la simulation d'un pas (utile en mode pas-à-pas)
     */
    public synchronized void stepSimulation() {
        if (horloge != null) {
            horloge.avancerUnPas();
            Logger.logInfo("Simulation avancée d'un pas");
        }
    }

    /**
     * Indique si la simulation est en pause
     */
    public boolean estSimulationEnPause() {
        return horloge != null && horloge.estEnPause();
    }
    
    /**
     * Ajoute un visiteur au parc
     */
    public synchronized void ajouterVisiteur(Visiteur visiteur) {
        if (!parcOuvert) {
            Logger.logWarning("Tentative d'ajout de visiteur alors que le parc est fermé");
            return;
        }
        
        visiteurs.add(visiteur);
        statistiques.ajouterVisiteur();
        //visiteur.start();
        
        Logger.logDebug("Visiteur ajouté: " + visiteur);
    }
    
    /**
     * Retire un visiteur du parc
     */
    public synchronized void retirerVisiteur(Visiteur visiteur) {
        visiteurs.remove(visiteur);
        statistiques.retirerVisiteur();
    }
    
    /**
     * Retourne la liste des attractions disponibles (non fermées)
     */
    public List<Attraction> getAttractionsDisponibles() {
        List<Attraction> disponibles = new ArrayList<>();
        
        for (Attraction attraction : attractions) {
            if (attraction.getEtat() == EtatAttraction.OPERATIONNELLE) {
                // Vérifier si attraction extérieure et météo défavorable
                if (attraction.estExterieure() && 
                    meteoActuelle.fermeAttractionsExterieures()) {
                    continue; // Skip attractions extérieures en cas de pluie/orage
                }
                disponibles.add(attraction);
            }
        }
        
        return Collections.unmodifiableList(disponibles);
    }
    
    /**
     * Retourne toutes les attractions (même fermées)
     */
    public List<Attraction> getAttractions() {
        return Collections.unmodifiableList(attractions);
    }
    
    /**
     * Retourne une attraction par son nom
     */
    public Attraction getAttractionParNom(String nom) {
        for (Attraction attraction : attractions) {
            if (attraction.getNom().equals(nom)) {
                return attraction;
            }
        }
        return null;
    }
    
    /**
     * Vide la liste des attractions (utilisé avant chargement CSV pour éviter doublons).
     */
    public synchronized void clearAttractions() {
        attractions.clear();
        Logger.logInfo("Liste des attractions vidée");
    }

    /**
     * Ajoute une attraction
     */
    public synchronized void ajouterAttraction(Attraction attraction) {
        attractions.add(attraction);
        Logger.logInfo("Attraction ajoutée: " + attraction.getNom());
    }
    
    /**
     * Retourne la liste des visiteurs actuels
     */
    public List<Visiteur> getVisiteurs() {
        return Collections.unmodifiableList(visiteurs);
    }
    
    /**
     * Retourne le visiteur ayant le nom donné (recherche insensible à la casse)
     * @param nom Nom du visiteur
     * @return Le visiteur ou null si non trouvé
     */
    public Visiteur getVisiteurParNom1(String nom) {
        if (nom == null || nom.isBlank()) return null;
        String nomNorm = nom.trim();
        for (Visiteur v : visiteurs) {
            if (v.getNomVisiteur().equalsIgnoreCase(nomNorm)) {
                return v;
            }
        }
        return null;
    }
    
    /**
     * Achète un ticket pour une attraction (logique métier)
     * Vérifie que le visiteur existe, a assez d'argent, et que l'attraction accepte
     * @param attraction L'attraction
     * @param visiteur Le visiteur (doit exister dans le parc)
     * @param type STANDARD ou FAST_PASS
     * @return Message de succès ou d'erreur
     */
    public String acheterTicketAttraction1(Attraction attraction, Visiteur visiteur, TypeBillet type) {
        if (attraction == null || visiteur == null) {
            return "Erreur: attraction ou visiteur invalide.";
        }
        if (type != TypeBillet.STANDARD && type != TypeBillet.FAST_PASS) {
            return "Type de ticket invalide. Choisissez Normal ou Fast Pass.";
        }
        double prix = type == TypeBillet.FAST_PASS 
            ? attraction.getPrixTicketFastPass() 
            : attraction.getPrixTicketNormal();
        if (visiteur.getArgent() < prix) {
            return String.format("Budget insuffisant. Prix: %.2f €, disponible: %.2f €.", 
                prix, visiteur.getArgent());
        }
        try {
            boolean fastPass = (type == TypeBillet.FAST_PASS);
            attraction.ajouterVisiteur((int) visiteur.getId(), fastPass);
            attraction.ajouterRevenu(prix);
            visiteur.payer(prix);
            statistiques.ajouterRevenus(prix);
            return String.format("Ticket %s acheté avec succès pour %.2f € !", 
                type.getLibelle(), prix);
        } catch (AttractionException e) {
            return "Impossible d'ajouter à la file: " + e.getMessage();
        }
    }
    
    /**
     * Retourne la liste des restaurants
     */
    public List<Restaurant> getRestaurants() {
        return Collections.unmodifiableList(restaurants);
    }
    
    /**
     * Retourne la liste des boutiques
     */
    public List<Boutique> getBoutiques() {
        return Collections.unmodifiableList(boutiques);
    }
    
    /**
     * Retourne la liste des employés
     */
    public List<Employe> getEmployes() {
        return Collections.unmodifiableList(employes);
    }
    
    /**
     * Ajoute un employé.
     * Ne démarre son thread que si le parc est ouvert (évite double start au chargement puis ouvrirParc).
     */
    public synchronized void ajouterEmploye(Employe employe) {
        employes.add(employe);
        if (parcOuvert) {
            employe.start();
        }
        Logger.logInfo("Employé ajouté: " + employe);
    }
    
    /**
     * Retire un employé (arrête son thread et le retire de la liste)
     */
    public synchronized void retirerEmploye(Employe employe) {
        if (!employes.contains(employe)) {
            return;
        }
        employe.arreter();
        employes.remove(employe);
        Logger.logInfo("Employé retiré: " + employe);
    }
    
    /**
     * Retourne l'horloge
     */
    public Horloge getHorloge() {
        return horloge;
    }
    
    public void setGestionnaireEvenements(GestionnaireEvenements ge) {
        this.gestionnaireEvenements = ge;
    }
    
    public GestionnaireEvenements getGestionnaireEvenements() {
        return gestionnaireEvenements;
    }
    
    /**
     * Retourne les statistiques
     */
    public Statistiques getStatistiques() {
        return statistiques;
    }
    
    /**
     * Retourne la météo actuelle
     */
    public Meteo getMeteoActuelle() {
        return meteoActuelle;
    }
    
    /**
     * Change la météo
     */
    public synchronized void setMeteoActuelle(Meteo meteo) {
        Meteo ancienneMeteo = this.meteoActuelle;
        this.meteoActuelle = meteo;
        
        Logger.logInfo("Météo changée: " + ancienneMeteo + " → " + meteo);
        
        // Si orage, évacuer le parc
        if (meteo.estUrgence()) {
            Logger.logError("ORAGE DETECTE - EVACUATION DU PARC");
            evacuerParc();
        } else if (meteo.fermeAttractionsExterieures()) {
            // Fermer attractions extérieures
            fermerAttractionsExterieures();
        } else if (ancienneMeteo.fermeAttractionsExterieures() && 
                   !meteo.fermeAttractionsExterieures()) {
            // Rouvrir attractions extérieures
            rouvrirAttractionsExterieures();
        }
    }
    
    /**
     * Évacue le parc (en cas d'orage)
     */
    private void evacuerParc() {
        SystemeNotifications.getInstance().ajouterNotification(
            TypeNotification.URGENCE, "ÉVACUATION - Orage");
        // Fermer toutes les attractions
        for (Attraction attraction : attractions) {
            attraction.fermer();
        }
        // Évacuer tous les visiteurs
        for (Visiteur visiteur : new ArrayList<>(visiteurs)) {
            visiteur.arreter();
        }
    }
    
    /**
     * Ferme les attractions extérieures (pluie)
     */
    private void fermerAttractionsExterieures() {
        boolean aucune = true;
        for (Attraction attraction : attractions) {
            if (attraction.estExterieure() && 
                attraction.getEtat() == EtatAttraction.OPERATIONNELLE) {
                attraction.fermer();
                Logger.logInfo("Attraction extérieure fermée: " + attraction.getNom());
                aucune = false;
            }
        }
        if (!aucune) {
            SystemeNotifications.getInstance().ajouterNotification(
                TypeNotification.ATTENTION, "Fermeture météo (attractions extérieures)");
        }
    }
    
    /**
     * Rouvre les attractions extérieures
     */
    private void rouvrirAttractionsExterieures() {
        for (Attraction attraction : attractions) {
            if (attraction.estExterieure() && 
                attraction.getEtat() == EtatAttraction.FERMEE) {
                attraction.ouvrir();
                Logger.logInfo("Attraction extérieure rouverte: " + attraction.getNom());
            }
        }
    }
    
    /**
     * Vérifie si le parc est ouvert
     */
    public boolean estOuvert() {
        return parcOuvert && (horloge == null || horloge.parcOuvert());
    }
    
    /**
     * Retourne le nombre total d'attractions
     */
    public int getNombreAttractions() {
        return attractions.size();
    }
    
    /**
     * Retourne le nombre d'attractions opérationnelles
     */
    public int getNombreAttractionsOperationnelles() {
        int count = 0;
        for (Attraction attraction : attractions) {
            if (attraction.getEtat() == EtatAttraction.OPERATIONNELLE) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Retourne un technicien disponible du pool
     * @return Technicien disponible ou null si aucun disponible
     */
    public Technicien getTechnicienDisponible() {
        for (Employe employe : employes) {
            if (employe instanceof Technicien) {
                Technicien technicien = (Technicien) employe;
                if (technicien.estDisponible() && !technicien.estEnIntervention()) {
                    return technicien;
                }
            }
        }
        return null;
    }
    
    /**
     * Retourne tous les techniciens
     */
    public List<Technicien> getTechniciens() {
        List<Technicien> techniciens = new ArrayList<>();
        for (Employe employe : employes) {
            if (employe instanceof Technicien) {
                techniciens.add((Technicien) employe);
            }
        }
        return techniciens;
    }
    
    /**
     * Retourne l'opérateur assigné à une attraction (diagramme maintenance).
     */
    public Operateur getOperateurPourAttraction(Attraction a) {
        for (Employe e : employes) {
            if (e instanceof Operateur) {
                Operateur op = (Operateur) e;
                if (op.getAttraction() == a) return op;
            }
        }
        return null;
    }
    
    /**
     * Demande un technicien en urgence pour réparer une attraction (diagramme maintenance).
     * Lance l'intervention en arrière-plan.
     */
    public void demanderTechnicienUrgence(Attraction attraction) {
        Technicien tech = getTechnicienDisponible();
        if (tech == null) {
            Logger.logWarning("Aucun technicien disponible pour " + attraction.getNom() + " – réparation différée");
            return;
        }
        new Thread(() -> {
            try {
                tech.intervenir(attraction);
                statistiques.mettreAJourDisponibilite(attraction.getNom(), true);
                SystemeNotifications.getInstance().ajouterNotification(
                    TypeNotification.INFO, "Attraction réparée: " + attraction.getNom());
            } catch (Exception ex) {
                Logger.logException("Intervention technicien " + attraction.getNom(), ex);
            }
        }, "ReparationUrgence-" + attraction.getNom()).start();
    }
    
    // ================= PERSISTANCE DES DONNÉES =================
    
    /**
     * Sauvegarde tous les visiteurs actuels en CSV
     */
    public synchronized void sauvegarderVisiteurs() {
        Logger.logInfo("Sauvegarde des visiteurs en CSV...");
        for (Visiteur visiteur : visiteurs) {
            CSVManager.sauvegarderVisiteur(
                (int) visiteur.getId(),
                visiteur.getNomVisiteur(),
                visiteur.getAge(),
                visiteur.getTaille(),
                visiteur.getProfil().name(),
                visiteur.getEtat().name(),
                visiteur.getSatisfaction(),
                visiteur.getArgent(),
                ""
            );
        }
        Logger.logInfo("Sauvegarde de " + visiteurs.size() + " visiteurs complétée");
    }
    
    /**
     * Sauvegarde toutes les attractions en CSV
     */
    public synchronized void sauvegarderAttractions() {
        Logger.logInfo("Sauvegarde des attractions en CSV...");
        for (Attraction attraction : attractions) {
            CSVManager.sauvegarderAttraction(
                attraction.getNom(),
                attraction.getType().name(),
                attraction.getEtat().name(),
                attraction.getCapacite(),
                attraction.getToursEffectues(),
                attraction.getVisiteursTotaux(),
                attraction.getAgeMin(),
                attraction.getTailleMin(),
                attraction.getIntensite().name(),
                attraction.getDureeTour()
            );
        }
        Logger.logInfo("Sauvegarde de " + attractions.size() + " attractions complétée");
    }
    
    /**
     * Sauvegarde tous les employés en CSV
     */
    public synchronized void sauvegarderEmployes() {
        Logger.logInfo("Sauvegarde des employés en CSV...");
        for (Employe employe : employes) {
            CSVManager.sauvegarderEmploye(
                (int) employe.getId(),
                employe.getNom(),
                employe.getPoste(),
                employe.getEtat().name(),
                employe.getFatigue()
            );
        }
        Logger.logInfo("Sauvegarde de " + employes.size() + " employés complétée");
    }
    
    /**
     * Sauvegarde une session complète du parc
     */
    private synchronized void sauvegarderSession() {
        if (dateOuvertureSession == null) {
            return;
        }
        
        LocalDateTime dateFermeture = LocalDateTime.now();
        double revenuBillets = CSVManager.calculerRevenuBillets();
        double revenuRestaurant = CSVManager.calculerRevenuRestaurant();
        double revenuSouvenirs = CSVManager.calculerRevenuSouvenirs();
        double revenuTotal = revenuBillets + revenuRestaurant + revenuSouvenirs;
        
        CSVManager.enregistrerSession(
            dateOuvertureSession,
            dateFermeture,
            visiteurs.size(),
            revenuTotal,
            revenuBillets,
            revenuRestaurant,
            revenuSouvenirs
        );
        
        Logger.logInfo("Session sauvegardée: Revenus totaux = " + revenuTotal);
    }
    
    /**
     * Sauvegarde toutes les données du parc (visiteurs, attractions, employés, session)
     */
    public synchronized void sauvegarderToutDonnees() {
        Logger.logInfo("═══════════════════════════════════════");
        Logger.logInfo("   SAUVEGARDE DE TOUTES LES DONNÉES");
        Logger.logInfo("═══════════════════════════════════════");
        
        sauvegarderVisiteurs();
        sauvegarderAttractions();
        sauvegarderEmployes();
        
        Logger.logInfo("Sauvegarde complète terminée");
    }
    
    /**
     * Charge les données depuis les fichiers CSV
     */
    public synchronized void chargerDonnees() {
        Logger.logInfo("═══════════════════════════════════════");
        Logger.logInfo("   CHARGEMENT DES DONNÉES");
        Logger.logInfo("═══════════════════════════════════════");
        
        // Charger les visiteurs (optionnel - dépend de la configuration)
        Map<Integer, String[]> visiteurCharges = CSVManager.chargerVisiteurs();
        Logger.logInfo("Visiteurs chargés: " + visiteurCharges.size());
        
        // Charger les attractions
        Map<String, String[]> attractionsChargees = CSVManager.chargerAttractions();
        Logger.logInfo("Attractions chargées: " + attractionsChargees.size());
        
        // Charger les employés
        Map<Integer, String[]> employesCharges = CSVManager.chargerEmployes();
        Logger.logInfo("Employés chargés: " + employesCharges.size());
        
        // Charger les sessions antérieures
        List<String[]> sessions = CSVManager.chargerSessions();
        Logger.logInfo("Sessions chargées: " + sessions.size());
        
        Logger.logInfo("Chargement des données complété");
    }
    
    /**
     * Enregistre une vente de billet dans le CSV et le TransactionManager
     */
    public void enregistrerVenteBillet(int visiteurId, String typeBillet, double prix, int ageVisiteur) {
        TransactionManager.enregistrerVenteBillet(visiteurId, typeBillet, prix, ageVisiteur);
        revenuSessionActuelle += prix;
    }
    
    /**
     * Enregistre une vente au restaurant dans le CSV et le TransactionManager
     */
    public void enregistrerVenteRestaurant(int visiteurId, String typeRepas, double prix) {
        TransactionManager.enregistrerVenteRestaurant(visiteurId, typeRepas, prix);
        revenuSessionActuelle += prix;
    }
    
    /**
     * Enregistre une vente de souvenir dans le CSV et le TransactionManager
     */
    public void enregistrerVenteSouvenir(int visiteurId, String article, double prix) {
        TransactionManager.enregistrerVenteSouvenir(visiteurId, article, prix);
        revenuSessionActuelle += prix;
    }
    
    /**
     * Retourne le revenu de la session actuelle
     */
    public double getRevenuSessionActuelle() {
        return revenuSessionActuelle;
    }
    
    // ================= TRANSACTION MANAGER =================
    
    /**
     * Retourne les statistiques financières du TransactionManager
     */
    public java.util.Map<String, Object> obtenirStatistiquesFinancieres() {
        return TransactionManager.obtenirStatistiques();
    }
    
    /**
     * Retourne le rapport financier complet
     */
    public String genererRapportFinancier() {
        return TransactionManager.genererRapportFinancier();
    }
    
    /**
     * Retourne le rapport détaillé des transactions
     */
    public String genererRapportDetaillé() {
        return TransactionManager.genererRapportDetaille();
    }
    
    /**
     * Réinitialise les statistiques du TransactionManager (nouvelle session)
     */
    public void reinitialiserStatistiquesTransactions() {
        TransactionManager.reinitialiserStatistiques();
    }

    
    /**
 * Active une réduction sur les tickets (pour événements)
 * @param tauxReduction Taux de réduction (0.20 = 20%)
 */
public synchronized void activerReduction(double tauxReduction) {
    this.tauxReductionActif = tauxReduction;
    Logger.logInfo("Réduction activée: " + (tauxReduction * 100) + "%");
}

/**
 * Désactive la réduction active
 */
public synchronized void desactiverReduction() {
    if (tauxReductionActif > 0) {
        Logger.logInfo("Réduction désactivée (était: " + (tauxReductionActif * 100) + "%)");
    }
    this.tauxReductionActif = 0.0;
}

/**
 * Retourne le taux de réduction actif
 */
public double getTauxReductionActif() {
    return tauxReductionActif;
}

/**
 * Vérifie si une réduction est active
 */
public boolean aReductionActive() {
    return tauxReductionActif > 0;
}

/**
 * Achète un ticket avec réduction si applicable
 */
public String acheterTicketAttraction(Attraction attraction, Visiteur visiteur, TypeBillet typeBillet) {
    if (attraction == null || visiteur == null || typeBillet == null) {
        return "Erreur: paramètres invalides";
    }
    
    // Calculer le prix de base
    double prixBase;
    if (typeBillet == TypeBillet.FAST_PASS) {
        prixBase = attraction.getPrixTicketFastPass();
    } else {
        prixBase = attraction.getPrixTicketNormal();
    }
    
    // Appliquer la réduction si active
    double prixFinal = prixBase;
    boolean reductionAppliquee = false;
    
    if (aReductionActive()) {
        prixFinal = prixBase * (1.0 - tauxReductionActif);
        reductionAppliquee = true;
    }
    
    // Vérifier les fonds
    if (visiteur.getArgent() < prixFinal) {
        return "Erreur: fonds insuffisants (prix: " + 
               String.format("%.2f€", prixFinal) + 
               ", disponible: " + String.format("%.2f€", visiteur.getArgent()) + ")";
    }
    
    // Effectuer l'achat
    visiteur.payer(prixFinal);
    attraction.ajouterRevenu(prixFinal);
    statistiques.ajouterRevenus(prixFinal);
    TransactionManager.enregistrerVenteBillet((int) visiteur.getId(), typeBillet.getLibelle(), prixFinal, visiteur.getAge());

    // Message de confirmation
    String message = "Ticket acheté avec succès!\n";
    message += "Type: " + typeBillet + "\n";
    message += "Prix de base: " + String.format("%.2f€", prixBase) + "\n";
    
    if (reductionAppliquee) {
        double economie = prixBase - prixFinal;
        message += "Réduction: -" + (int)(tauxReductionActif * 100) + "% (" + 
                  String.format("%.2f€", economie) + ")\n";
        message += "Prix payé: " + String.format("%.2f€", prixFinal) + "\n";
    } else {
        message += "Prix payé: " + String.format("%.2f€", prixFinal) + "\n";
    }
    
    message += "Solde restant: " + String.format("%.2f€", visiteur.getArgent());
    
    Logger.logInfo("Achat ticket: " + visiteur.getNomVisiteur() + 
                  " → " + attraction.getNom() + 
                  " (" + String.format("%.2f€", prixFinal) + ")");
    
    return message;
}

/**
 * Recherche un visiteur par son nom
 */
public Visiteur getVisiteurParNom(String nom) {
    if (nom == null || nom.isBlank()) {
        return null;
    }
    
    String nomRecherche = nom.trim().toLowerCase();
    
    for (Visiteur v : visiteurs) {
        if (v.getNomVisiteur().toLowerCase().contains(nomRecherche)) {
            return v;
        }
    }
    
    return null;
}
}

