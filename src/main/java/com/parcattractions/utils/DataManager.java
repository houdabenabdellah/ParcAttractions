package main.java.com.parcattractions.utils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.enums.*;
import main.java.com.parcattractions.models.attractions.*;
import main.java.com.parcattractions.models.employes.AgentSecurite;
import main.java.com.parcattractions.models.employes.Employe;
import main.java.com.parcattractions.models.employes.Technicien;
import main.java.com.parcattractions.models.visiteurs.*;

/**
 * Gestionnaire de chargement/sauvegarde des données en CSV
 */
public class DataManager {
    
    private static final Path DATA_DIR = Paths.get("data");
    private static final String VISITEURS_FILE = "data/visiteurs.csv";
    private static final String ATTRACTIONS_FILE = "data/attractions.csv";
    private static final String EMPLOYES_FILE = "data/employes.csv";
    private static final String SESSIONS_FILE = "data/sessions.csv";
    
    private static final DateTimeFormatter CSV_DATETIME_FORMAT = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Crée la structure des répertoires
     */
    public static void initializeDataDirectory() {
        try {
            Files.createDirectories(DATA_DIR);
            Logger.logInfo("Répertoire data créé ou existant");
        } catch (IOException e) {
            Logger.logError("Erreur lors de la création du répertoire data: " + e.getMessage());
        }
    }
    
    /**
     * Charge toutes les données du parc
     */
    public static void loadAllData(GestionnaireParc gestionnaireParc) {
        Logger.logInfo("═══════════════════════════════════════");
        Logger.logInfo("   CHARGEMENT DES DONNÉES");
        Logger.logInfo("═══════════════════════════════════════");
        
        loadAttractions(gestionnaireParc);
        loadEmployes(gestionnaireParc);
        loadVisiteurs(gestionnaireParc);
        
        Logger.logInfo("Chargement des données terminé");
    }
    
    /** Nombre fixe d'attractions du parc (évite doublons au rechargement). */
    private static final int MAX_ATTRACTIONS = 8;

    /**
     * Charge les attractions depuis CSV.
     * Vide d'abord la liste pour éviter d'ajouter aux 8 par défaut, puis charge au maximum 8 lignes.
     */
    public static void loadAttractions(GestionnaireParc gestionnaireParc) {
        Path file = Paths.get(ATTRACTIONS_FILE);
        if (!Files.exists(file)) {
            Logger.logInfo("Fichier attractions.csv non trouvé - Utilisation des attractions par défaut");
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            reader.readLine(); // Skip header
            String line;
            int attractionsChargees = 0;
            gestionnaireParc.clearAttractions();
            while ((line = reader.readLine()) != null && attractionsChargees < MAX_ATTRACTIONS) {
                String[] parts = line.split(";");
                if (parts.length >= 2) {
                    String typeName = parts[1].trim();
                    Attraction attraction = createAttraction(typeName);
                    if (attraction != null) {
                        gestionnaireParc.ajouterAttraction(attraction);
                        attractionsChargees++;
                    }
                }
            }
            if (attractionsChargees == 0) {
                gestionnaireParc.initialiserAttractionsParDefaut();
            }
            Logger.logInfo("Attractions chargées du CSV: " + attractionsChargees + "/" + MAX_ATTRACTIONS);
        } catch (IOException e) {
            Logger.logError("Erreur lors du chargement des attractions: " + e.getMessage());
        }
    }
    
    /**
     * Charge les employés depuis CSV
     */
    public static void loadEmployes(GestionnaireParc gestionnaireParc) {
        Path file = Paths.get(EMPLOYES_FILE);
        if (!Files.exists(file)) {
            Logger.logInfo("Fichier employes.csv non trouvé");
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    String nom = parts[1];
                    String poste = parts[2].trim();
                    Employe employe = createEmploye(nom, poste);
                    if (employe != null) {
                        gestionnaireParc.ajouterEmploye(employe);
                    }
                }
            }
            Logger.logInfo("Employés chargés: " + gestionnaireParc.getEmployes().size());
        } catch (IOException e) {
            Logger.logError("Erreur lors du chargement des employés: " + e.getMessage());
        }
    }
    
    /**
     * Charge les visiteurs depuis CSV
     */
    public static void loadVisiteurs(GestionnaireParc gestionnaireParc) {
        Path file = Paths.get(VISITEURS_FILE);
        if (!Files.exists(file)) {
            Logger.logInfo("Fichier visiteurs.csv non trouvé");
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            reader.readLine(); // Skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 6) {
                    int age = Integer.parseInt(parts[2]);
                    int taille = (int) Double.parseDouble(parts[3]);
                    ProfilVisiteur profil;
                    try {
                        profil = ProfilVisiteur.valueOf(parts[4].trim());
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                    Visiteur visiteur = createVisiteur(profil, age, taille);
                    if (visiteur != null) {
                        gestionnaireParc.ajouterVisiteur(visiteur);
                    }
                }
            }
            Logger.logInfo("Visiteurs chargés: " + gestionnaireParc.getVisiteurs().size());
        } catch (IOException e) {
            Logger.logError("Erreur lors du chargement des visiteurs: " + e.getMessage());
        }
    }
    
    /**
     * Sauvegarde toutes les données du parc
     */
    public static void saveAllData(GestionnaireParc gestionnaireParc) {
        Logger.logInfo("═══════════════════════════════════════");
        Logger.logInfo("   SAUVEGARDE DES DONNÉES");
        Logger.logInfo("═══════════════════════════════════════");
        
        saveAttractions(gestionnaireParc);
        saveEmployes(gestionnaireParc);
        saveVisiteurs(gestionnaireParc);
        saveSession(gestionnaireParc);
        
        Logger.logInfo("Sauvegarde des données terminée");
    }
    
    /**
     * Sauvegarde les attractions en CSV
     */
    public static void saveAttractions(GestionnaireParc gestionnaireParc) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ATTRACTIONS_FILE))) {
            writer.write("Nom;Type;État;CapacitéMax;VisiteursTotaux;TempsAttente;Revenus\n");
            
            for (Attraction attraction : gestionnaireParc.getAttractions()) {
                writer.write(String.format("%s;%s;%s;%d;%d;%d;%.2f€\n",
                    attraction.getNom(),
                    getAttractionType(attraction),
                    attraction.getEtat().name(),
                    attraction.getCapacite(),
                    attraction.getVisiteursTotaux(),
                    attraction.getTempsAttenteEstime(),
                    0.0
                ));
            }
            Logger.logInfo("Attractions sauvegardées: " + ATTRACTIONS_FILE);
        } catch (IOException e) {
            Logger.logError("Erreur lors de la sauvegarde des attractions: " + e.getMessage());
        }
    }
    
    /**
     * Sauvegarde les employés en CSV
     */
    public static void saveEmployes(GestionnaireParc gestionnaireParc) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EMPLOYES_FILE))) {
            writer.write("ID;Nom;Poste;État;Fatigue;SalaireJour\n");
            
            for (Employe employe : gestionnaireParc.getEmployes()) {
                writer.write(String.format("%d;%s;%s;%s;%d%%;%.2f€\n",
                    employe.getId(),
                    employe.getNom(),
                    employe.getPoste(),
                    employe.getEtat().name(),
                    employe.getFatigue(),
                    50.0 // Salaire journalier standard
                ));
            }
            Logger.logInfo("Employés sauvegardés: " + EMPLOYES_FILE);
        } catch (IOException e) {
            Logger.logError("Erreur lors de la sauvegarde des employés: " + e.getMessage());
        }
    }
    
    /**
     * Sauvegarde les visiteurs en CSV
     */
    public static void saveVisiteurs(GestionnaireParc gestionnaireParc) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VISITEURS_FILE))) {
            writer.write("ID;Nom;Age;Taille;Profil;État;Satisfaction;Tours\n");
            
            for (Visiteur visiteur : gestionnaireParc.getVisiteurs()) {
                writer.write(String.format("%d;%s;%d;%d;%s;%s;%d%%;%d\n",
                    visiteur.getId(),
                    visiteur.getNomVisiteur(),
                    visiteur.getAge(),
                    visiteur.getTaille(),
                    getVisiteurProfil(visiteur),
                    visiteur.getEtat().name(),
                    visiteur.getSatisfaction(),
                    visiteur.getAttractionsVisitees().size()
                ));
            }
            Logger.logInfo("Visiteurs sauvegardés: " + VISITEURS_FILE);
        } catch (IOException e) {
            Logger.logError("Erreur lors de la sauvegarde des visiteurs: " + e.getMessage());
        }
    }
    
    /**
     * Sauvegarde une session de parc
     */
    public static void saveSession(GestionnaireParc gestionnaireParc) {
        Path path = Paths.get(SESSIONS_FILE);
        boolean writeHeader = !Files.exists(path);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSIONS_FILE, true))) {
            if (writeHeader) {
                writer.write("DateDébut;DateFin;NombreVisiteurs;RevenuTotal;DuréeHeures\n");
            }
            LocalDateTime now = LocalDateTime.now();
            writer.write(String.format("%s;%s;%d;%.2f€;8\n",
                now.format(CSV_DATETIME_FORMAT),
                now.plusHours(8).format(CSV_DATETIME_FORMAT),
                gestionnaireParc.getStatistiques().getNombreVisiteursTotal(),
                gestionnaireParc.getStatistiques().getRevenusTotal()
            ));
        } catch (IOException e) {
            Logger.logError("Erreur lors de la sauvegarde de la session: " + e.getMessage());
        }
    }
    
    /**
     * Crée une attraction selon son type (nom de classe)
     */
    private static Attraction createAttraction(String typeName) {
        return switch (typeName) {
            case "MontagnesRusses" -> new MontagnesRusses();
            case "MaisonHantee" -> new MaisonHantee();
            case "GrandeRoue" -> new GrandeRoue();
            case "Carrousel" -> new Carrousel();
            case "TobogganAquatique" -> new TobogganAquatique();
            case "ChuteLibre" -> new ChuteLibre();
            case "SimulateurVR" -> new SimulateurVR();
            case "ManegeEnfants" -> new ManegeEnfants();
            default -> null;
        };
    }
    
    /**
     * Crée un employé selon le poste (Technicien ou Agent Sécurité uniquement)
     */
    private static Employe createEmploye(String nom, String poste) {
        String p = poste.toLowerCase();
        if (p.contains("technicien")) return new Technicien(nom);
        if (p.contains("sécurité") || p.contains("securite") || p.contains("agent")) return new AgentSecurite(nom);
        return null;
    }
    
    /**
     * Crée un visiteur selon son profil (EXTREME, GROUPE, FAMILLE uniquement)
     */
    private static Visiteur createVisiteur(ProfilVisiteur profil, int age, int taille) {
        return switch (profil) {
            case EXTREME -> new VisiteurExtreme(age, taille);
            case GROUPE -> new VisiteurGroupe(age, taille);
            case FAMILLE -> new VisiteurFamille(age, taille, 2);
            case COUPLE, ENFANT -> null;
        };
    }
    
    /**
     * Obtient le type d'une attraction (nom de classe)
     */
    private static String getAttractionType(Attraction attraction) {
        return attraction.getClass().getSimpleName();
    }
    
    /**
     * Obtient le profil d'un visiteur
     */
    private static String getVisiteurProfil(Visiteur visiteur) {
        if (visiteur instanceof VisiteurCouple) return ProfilVisiteur.COUPLE.name();
        if (visiteur instanceof VisiteurFamille) return ProfilVisiteur.FAMILLE.name();
        if (visiteur instanceof VisiteurGroupe) return ProfilVisiteur.GROUPE.name();
        if (visiteur instanceof VisiteurEnfant) return ProfilVisiteur.ENFANT.name();
        if (visiteur instanceof VisiteurExtreme) return ProfilVisiteur.EXTREME.name();
        return ProfilVisiteur.FAMILLE.name();
    }
}
