package main.java.com.parcattractions.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Gestionnaire pour la persistance des données en fichiers CSV
 * Permet de sauvegarder et charger : visiteurs, attractions, employés, transactions, sessions
 */
public class CSVManager {
    
    private static final String DATA_DIR = "data";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Fichiers CSV
    private static final String VISITEURS_FILE = DATA_DIR + "/visiteurs.csv";
    private static final String ATTRACTIONS_FILE = DATA_DIR + "/attractions.csv";
    private static final String EMPLOYES_FILE = DATA_DIR + "/employes.csv";
    private static final String VENTES_BILLETS_FILE = DATA_DIR + "/ventes_billets.csv";
    private static final String VENTES_RESTAURANT_FILE = DATA_DIR + "/ventes_restaurant.csv";
    private static final String VENTES_SOUVENIRS_FILE = DATA_DIR + "/ventes_souvenirs.csv";
    private static final String SESSIONS_FILE = DATA_DIR + "/sessions.csv";
    
    // Délimiteur CSV
    private static final String DELIMITER = ";";
    
    static {
        // Créer le répertoire data/ s'il n'existe pas
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Logger.logInfo("Répertoire data/ vérifiée/créée");
        } catch (IOException e) {
            Logger.logException("Erreur création répertoire data/", e);
        }
    }
    
    /**
     * Initialise les fichiers CSV avec en-têtes s'ils n'existent pas
     */
    public static void initialiserFichiers() {
        initFichierVisiteurs();
        initFichierAttractions();
        initFichierEmployes();
        initFichierVentesBillets();
        initFichierVentesRestaurant();
        initFichierVentesSouvenirs();
        initFichierSessions();
    }
    
    // ================= VISITEURS =================
    
    private static void initFichierVisiteurs() {
        try {
            if (!Files.exists(Paths.get(VISITEURS_FILE))) {
                String header = "ID" + DELIMITER + "Nom" + DELIMITER + "Age" + DELIMITER + "Taille" + DELIMITER 
                    + "Profil" + DELIMITER + "Etat" + DELIMITER + "Satisfaction" + DELIMITER + "ArgentRestant" 
                    + DELIMITER + "DateArrivee" + DELIMITER + "DateDepart" + DELIMITER + "AttractionsVisitees";
                Files.write(Paths.get(VISITEURS_FILE), header.getBytes(StandardCharsets.UTF_8));
                Logger.logInfo("Fichier visiteurs.csv créé");
            }
        } catch (IOException e) {
            Logger.logException("Erreur initialisation visiteurs.csv", e);
        }
    }
    
    /**
     * Sauvegarde un visiteur dans le fichier CSV
     */
    public static void sauvegarderVisiteur(int id, String nom, int age, int taille, String profil, 
                                            String etat, int satisfaction, double argent, String attractionsVisitees) {
        try {
            String ligne = String.format("%d%s%s%s%d%s%d%s%s%s%s%s%d%s%.2f%s%s%s%s%s%s",
                id,
                DELIMITER, nom,
                DELIMITER, age,
                DELIMITER, taille,
                DELIMITER, profil,
                DELIMITER, etat,
                DELIMITER, satisfaction,
                DELIMITER, argent,
                DELIMITER, LocalDateTime.now().format(DATE_FORMATTER),
                DELIMITER, "N/A",
                DELIMITER, attractionsVisitees
            );
            
            Files.write(
                Paths.get(VISITEURS_FILE),
                (ligne + "\n").getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            Logger.logException("Erreur sauvegarde visiteur " + id, e);
        }
    }
    
    /**
     * Charge tous les visiteurs depuis le CSV
     */
    public static Map<Integer, String[]> chargerVisiteurs() {
        Map<Integer, String[]> visiteurs = new HashMap<>();
        try {
            if (Files.exists(Paths.get(VISITEURS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(VISITEURS_FILE), StandardCharsets.UTF_8);
                for (int i = 1; i < lines.size(); i++) { // Sauter l'en-tête
                    String[] parts = lines.get(i).split(DELIMITER, -1);
                    if (parts.length >= 8) {
                        int id = Integer.parseInt(parts[0]);
                        visiteurs.put(id, parts);
                    }
                }
                Logger.logInfo("Chargement de " + (visiteurs.size()) + " visiteurs");
            }
        } catch (IOException e) {
            Logger.logException("Erreur chargement visiteurs.csv", e);
        }
        return visiteurs;
    }
    
    /**
     * Met à jour la date de départ d'un visiteur
     */
    public static void mettreAJourDateDepartVisiteur(int visiteurId, LocalDateTime dateDepart) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(VISITEURS_FILE), StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(DELIMITER, -1);
                if (Integer.parseInt(parts[0]) == visiteurId) {
                    parts[9] = dateDepart.format(DATE_FORMATTER);
                    lines.set(i, String.join(DELIMITER, parts));
                    Files.write(Paths.get(VISITEURS_FILE), lines, StandardCharsets.UTF_8);
                    return;
                }
            }
        } catch (IOException e) {
            Logger.logException("Erreur mise à jour date départ visiteur", e);
        }
    }

    /**
     * Met à jour le montant d'argent restant d'un visiteur dans le CSV
     * @param visiteurId ID du visiteur
     * @param argent Nouveau solde (en euros)
     */
    public static void mettreAJourArgentVisiteur(int visiteurId, double argent) {
        try {
            Path path = Paths.get(VISITEURS_FILE);
            if (!Files.exists(path)) return;
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(DELIMITER, -1);
                if (parts.length > 7 && Integer.parseInt(parts[0]) == visiteurId) {
                    parts[7] = String.format("%.2f", argent);
                    lines.set(i, String.join(DELIMITER, parts));
                    Files.write(path, lines, StandardCharsets.UTF_8);
                    Logger.logInfo("Mise à jour solde visiteur #" + visiteurId + " -> " + argent + "€");
                    return;
                }
            }
        } catch (IOException e) {
            Logger.logException("Erreur mise à jour solde visiteur", e);
        } catch (NumberFormatException ex) {
            Logger.logException("Erreur parsing ID visiteur dans CSV", ex);
        }
    }
    
    // ================= ATTRACTIONS =================
    
    private static void initFichierAttractions() {
        try {
            if (!Files.exists(Paths.get(ATTRACTIONS_FILE))) {
                String header = "Nom" + DELIMITER + "Type" + DELIMITER + "Etat" + DELIMITER + "CapaciteMax" 
                    + DELIMITER + "ToursEffectues" + DELIMITER + "VisiteursTotaux" + DELIMITER + "TempsMaintenanceRestant"
                    + DELIMITER + "AgeMin" + DELIMITER + "TailleMin" + DELIMITER + "Intensite" + DELIMITER + "DureeTour";
                Files.write(Paths.get(ATTRACTIONS_FILE), header.getBytes(StandardCharsets.UTF_8));
                Logger.logInfo("Fichier attractions.csv créé");
            }
        } catch (IOException e) {
            Logger.logException("Erreur initialisation attractions.csv", e);
        }
    }
    
    /**
     * Sauvegarde une attraction dans le fichier CSV
     */
    public static void sauvegarderAttraction(String nom, String type, String etat, 
                                              int capacite, int toursEffectues, int visiteursTotaux,
                                              int ageMin, int tailleMin, String intensite, int dureeTour) {
        try {
            String ligne = String.format("%s%s%s%s%s%s%d%s%d%s%d%s%d%s%d%s%d%s%s%s%d",
                nom,
                DELIMITER, type,
                DELIMITER, etat,
                DELIMITER, capacite,
                DELIMITER, toursEffectues,
                DELIMITER, visiteursTotaux,
                DELIMITER, 0,
                DELIMITER, ageMin,
                DELIMITER, tailleMin,
                DELIMITER, intensite,
                DELIMITER, dureeTour
            );
            
            Files.write(
                Paths.get(ATTRACTIONS_FILE),
                (ligne + "\n").getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            Logger.logException("Erreur sauvegarde attraction " + nom, e);
        }
    }
    
    /**
     * Charge tous les attractions depuis le CSV
     */
    public static Map<String, String[]> chargerAttractions() {
        Map<String, String[]> attractions = new HashMap<>();
        try {
            if (Files.exists(Paths.get(ATTRACTIONS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(ATTRACTIONS_FILE), StandardCharsets.UTF_8);
                for (int i = 1; i < lines.size(); i++) { // Sauter l'en-tête
                    String[] parts = lines.get(i).split(DELIMITER, -1);
                    if (parts.length >= 6) {
                        String nom = parts[0];
                        attractions.put(nom, parts);
                    }
                }
                Logger.logInfo("Chargement de " + attractions.size() + " attractions");
            }
        } catch (IOException e) {
            Logger.logException("Erreur chargement attractions.csv", e);
        }
        return attractions;
    }
    
    /**
     * Met à jour l'état et les statistiques d'une attraction
     */
    public static void mettreAJourAttraction(String nomAttraction, String etat, 
                                               int toursEffectues, int visiteursTotaux) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(ATTRACTIONS_FILE), StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(DELIMITER, -1);
                if (parts[0].equals(nomAttraction)) {
                    parts[2] = etat;
                    parts[4] = String.valueOf(toursEffectues);
                    parts[5] = String.valueOf(visiteursTotaux);
                    lines.set(i, String.join(DELIMITER, parts));
                    Files.write(Paths.get(ATTRACTIONS_FILE), lines, StandardCharsets.UTF_8);
                    return;
                }
            }
        } catch (IOException e) {
            Logger.logException("Erreur mise à jour attraction", e);
        }
    }
    
    // ================= EMPLOYES =================
    
    private static void initFichierEmployes() {
        try {
            if (!Files.exists(Paths.get(EMPLOYES_FILE))) {
                String header = "ID" + DELIMITER + "Nom" + DELIMITER + "Poste" + DELIMITER + "Etat" + DELIMITER + "NiveauFatigue" + DELIMITER + "DateEmbauche";
                Files.write(Paths.get(EMPLOYES_FILE), header.getBytes(StandardCharsets.UTF_8));
                Logger.logInfo("Fichier employes.csv créé");
            }
        } catch (IOException e) {
            Logger.logException("Erreur initialisation employes.csv", e);
        }
    }
    
    /**
     * Sauvegarde un employé dans le fichier CSV
     */
    public static void sauvegarderEmploye(int id, String nom, String poste, String etat, int fatigue) {
        try {
            String ligne = String.format("%d%s%s%s%s%s%s%s%d%s%s",
                id,
                DELIMITER, nom,
                DELIMITER, poste,
                DELIMITER, etat,
                DELIMITER, fatigue,
                DELIMITER, LocalDateTime.now().format(DATE_FORMATTER)
            );
            
            Files.write(
                Paths.get(EMPLOYES_FILE),
                (ligne + "\n").getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            Logger.logException("Erreur sauvegarde employé " + id, e);
        }
    }
    
    /**
     * Charge tous les employés depuis le CSV
     */
    public static Map<Integer, String[]> chargerEmployes() {
        Map<Integer, String[]> employes = new HashMap<>();
        try {
            if (Files.exists(Paths.get(EMPLOYES_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(EMPLOYES_FILE), StandardCharsets.UTF_8);
                for (int i = 1; i < lines.size(); i++) { // Sauter l'en-tête
                    String[] parts = lines.get(i).split(DELIMITER, -1);
                    if (parts.length >= 3) {
                        int id = Integer.parseInt(parts[0]);
                        employes.put(id, parts);
                    }
                }
                Logger.logInfo("Chargement de " + employes.size() + " employés");
            }
        } catch (IOException e) {
            Logger.logException("Erreur chargement employes.csv", e);
        }
        return employes;
    }
    
    /**
     * Met à jour la fatigue d'un employé
     */
    public static void mettreAJourFatigueEmploye(int employeId, int fatigue) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(EMPLOYES_FILE), StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                String[] parts = lines.get(i).split(DELIMITER, -1);
                if (Integer.parseInt(parts[0]) == employeId) {
                    parts[4] = String.valueOf(fatigue);
                    lines.set(i, String.join(DELIMITER, parts));
                    Files.write(Paths.get(EMPLOYES_FILE), lines, StandardCharsets.UTF_8);
                    return;
                }
            }
        } catch (IOException e) {
            Logger.logException("Erreur mise à jour fatigue employé", e);
        }
    }
    
    // ================= TRANSACTIONS - BILLETS =================
    
    private static void initFichierVentesBillets() {
        try {
            if (!Files.exists(Paths.get(VENTES_BILLETS_FILE))) {
                String header = "ID" + DELIMITER + "IdVisiteur" + DELIMITER + "TypeBillet" + DELIMITER + "PrixPaye" + DELIMITER + "AgeVisiteur" + DELIMITER + "DateTime";
                Files.write(Paths.get(VENTES_BILLETS_FILE), header.getBytes(StandardCharsets.UTF_8));
                Logger.logInfo("Fichier ventes_billets.csv créé");
            }
        } catch (IOException e) {
            Logger.logException("Erreur initialisation ventes_billets.csv", e);
        }
    }
    
    /**
     * Enregistre une vente de billet
     */
    public static void enregistrerVenteBillet(int visiteurId, String typeBillet, double prix, int ageVisiteur) {
        try {
            long id = System.currentTimeMillis();
            String ligne = String.format("%d%s%d%s%s%s%.2f%s%d%s%s",
                id,
                DELIMITER, visiteurId,
                DELIMITER, typeBillet,
                DELIMITER, prix,
                DELIMITER, ageVisiteur,
                DELIMITER, LocalDateTime.now().format(DATE_FORMATTER)
            );
            
            Files.write(
                Paths.get(VENTES_BILLETS_FILE),
                (ligne + "\n").getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            Logger.logException("Erreur enregistrement vente billet", e);
        }
    }
    
    /**
     * Charge toutes les ventes de billets
     */
    public static List<String[]> chargerVentesBillets() {
        List<String[]> ventes = new ArrayList<>();
        try {
            if (Files.exists(Paths.get(VENTES_BILLETS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(VENTES_BILLETS_FILE), StandardCharsets.UTF_8);
                for (int i = 1; i < lines.size(); i++) {
                    ventes.add(lines.get(i).split(DELIMITER, -1));
                }
            }
        } catch (IOException e) {
            Logger.logException("Erreur chargement ventes_billets.csv", e);
        }
        return ventes;
    }
    
    // ================= TRANSACTIONS - RESTAURANT =================
    
    private static void initFichierVentesRestaurant() {
        try {
            if (!Files.exists(Paths.get(VENTES_RESTAURANT_FILE))) {
                String header = "ID" + DELIMITER + "IdVisiteur" + DELIMITER + "TypeRepas" + DELIMITER + "PrixPaye" + DELIMITER + "DateTime";
                Files.write(Paths.get(VENTES_RESTAURANT_FILE), header.getBytes(StandardCharsets.UTF_8));
                Logger.logInfo("Fichier ventes_restaurant.csv créé");
            }
        } catch (IOException e) {
            Logger.logException("Erreur initialisation ventes_restaurant.csv", e);
        }
    }
    
    /**
     * Enregistre une vente au restaurant
     */
    public static void enregistrerVenteRestaurant(int visiteurId, String typeRepas, double prix) {
        try {
            long id = System.currentTimeMillis();
            String ligne = String.format("%d%s%d%s%s%s%.2f%s%s",
                id,
                DELIMITER, visiteurId,
                DELIMITER, typeRepas,
                DELIMITER, prix,
                DELIMITER, LocalDateTime.now().format(DATE_FORMATTER)
            );
            
            Files.write(
                Paths.get(VENTES_RESTAURANT_FILE),
                (ligne + "\n").getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            Logger.logException("Erreur enregistrement vente restaurant", e);
        }
    }
    
    /**
     * Charge toutes les ventes restaurant
     */
    public static List<String[]> chargerVentesRestaurant() {
        List<String[]> ventes = new ArrayList<>();
        try {
            if (Files.exists(Paths.get(VENTES_RESTAURANT_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(VENTES_RESTAURANT_FILE), StandardCharsets.UTF_8);
                for (int i = 1; i < lines.size(); i++) {
                    ventes.add(lines.get(i).split(DELIMITER, -1));
                }
            }
        } catch (IOException e) {
            Logger.logException("Erreur chargement ventes_restaurant.csv", e);
        }
        return ventes;
    }
    
    // ================= TRANSACTIONS - SOUVENIRS =================
    
    private static void initFichierVentesSouvenirs() {
        try {
            if (!Files.exists(Paths.get(VENTES_SOUVENIRS_FILE))) {
                String header = "ID" + DELIMITER + "IdVisiteur" + DELIMITER + "Article" + DELIMITER + "PrixPaye" + DELIMITER + "DateTime";
                Files.write(Paths.get(VENTES_SOUVENIRS_FILE), header.getBytes(StandardCharsets.UTF_8));
                Logger.logInfo("Fichier ventes_souvenirs.csv créé");
            }
        } catch (IOException e) {
            Logger.logException("Erreur initialisation ventes_souvenirs.csv", e);
        }
    }
    
    /**
     * Enregistre une vente de souvenir
     */
    public static void enregistrerVenteSouvenir(int visiteurId, String article, double prix) {
        try {
            long id = System.currentTimeMillis();
            String ligne = String.format("%d%s%d%s%s%s%.2f%s%s",
                id,
                DELIMITER, visiteurId,
                DELIMITER, article,
                DELIMITER, prix,
                DELIMITER, LocalDateTime.now().format(DATE_FORMATTER)
            );
            
            Files.write(
                Paths.get(VENTES_SOUVENIRS_FILE),
                (ligne + "\n").getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            Logger.logException("Erreur enregistrement vente souvenir", e);
        }
    }
    
    /**
     * Charge toutes les ventes souvenirs
     */
    public static List<String[]> chargerVentesSouvenirs() {
        List<String[]> ventes = new ArrayList<>();
        try {
            if (Files.exists(Paths.get(VENTES_SOUVENIRS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(VENTES_SOUVENIRS_FILE), StandardCharsets.UTF_8);
                for (int i = 1; i < lines.size(); i++) {
                    ventes.add(lines.get(i).split(DELIMITER, -1));
                }
            }
        } catch (IOException e) {
            Logger.logException("Erreur chargement ventes_souvenirs.csv", e);
        }
        return ventes;
    }
    
    // ================= SESSIONS =================
    
    private static void initFichierSessions() {
        try {
            if (!Files.exists(Paths.get(SESSIONS_FILE))) {
                String header = "DateDebut" + DELIMITER + "DateFin" + DELIMITER + "NbVisiteursTotal" + DELIMITER + "RevenuTotal" + DELIMITER + "RevenuBillets" + DELIMITER + "RevenuRestaurant" + DELIMITER + "RevenuSouvenirs";
                Files.write(Paths.get(SESSIONS_FILE), header.getBytes(StandardCharsets.UTF_8));
                Logger.logInfo("Fichier sessions.csv créé");
            }
        } catch (IOException e) {
            Logger.logException("Erreur initialisation sessions.csv", e);
        }
    }
    
    /**
     * Enregistre une session de parc
     */
    public static void enregistrerSession(LocalDateTime dateDebut, LocalDateTime dateFin, 
                                          int nbVisiteurs, double revenuTotal, 
                                          double revenuBillets, double revenuRestaurant, double revenuSouvenirs) {
        try {
            String ligne = String.format("%s%s%s%s%d%s%.2f%s%.2f%s%.2f%s%.2f",
                dateDebut.format(DATE_FORMATTER),
                DELIMITER, dateFin.format(DATE_FORMATTER),
                DELIMITER, nbVisiteurs,
                DELIMITER, revenuTotal,
                DELIMITER, revenuBillets,
                DELIMITER, revenuRestaurant,
                DELIMITER, revenuSouvenirs
            );
            
            Files.write(
                Paths.get(SESSIONS_FILE),
                (ligne + "\n").getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND
            );
            Logger.logInfo("Session enregistrée : " + nbVisiteurs + " visiteurs, Revenu: " + revenuTotal);
        } catch (IOException e) {
            Logger.logException("Erreur enregistrement session", e);
        }
    }
    
    /**
     * Charge toutes les sessions
     */
    public static List<String[]> chargerSessions() {
        List<String[]> sessions = new ArrayList<>();
        try {
            if (Files.exists(Paths.get(SESSIONS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(SESSIONS_FILE), StandardCharsets.UTF_8);
                for (int i = 1; i < lines.size(); i++) {
                    sessions.add(lines.get(i).split(DELIMITER, -1));
                }
            }
        } catch (IOException e) {
            Logger.logException("Erreur chargement sessions.csv", e);
        }
        return sessions;
    }

    /**
     * Enregistre un rapport textuel des services (restaurants + boutiques)
     * Le fichier est créé sous data/rapport_services_<timestamp>.txt
     */
    public static void enregistrerRapportServices(String contenu) {
        try {
            String fileName = "rapport_services_" + System.currentTimeMillis() + ".txt";
            Path out = Paths.get(DATA_DIR, fileName);
            Files.write(out, Collections.singletonList(contenu), StandardCharsets.UTF_8);
            Logger.logInfo("Rapport services enregistré: " + out.toString());
        } catch (IOException e) {
            Logger.logException("Erreur enregistrement rapport services", e);
        }
    }
    
    // ================= UTILITAIRES =================
    
    /**
     * Calcule le revenu total depuis les ventes de billets
     */
    public static double calculerRevenuBillets() {
        double total = 0;
        for (String[] vente : chargerVentesBillets()) {
            if (vente.length >= 4) {
                try {
                    total += Double.parseDouble(vente[3]);
                } catch (NumberFormatException e) {
                    // Ignorer les lignes mal formées
                }
            }
        }
        return total;
    }
    
    /**
     * Calcule le revenu total depuis les ventes restaurant
     */
    public static double calculerRevenuRestaurant() {
        double total = 0;
        for (String[] vente : chargerVentesRestaurant()) {
            if (vente.length >= 4) {
                try {
                    total += Double.parseDouble(vente[3]);
                } catch (NumberFormatException e) {
                    // Ignorer les lignes mal formées
                }
            }
        }
        return total;
    }
    
    /**
     * Calcule le revenu total depuis les ventes souvenirs
     */
    public static double calculerRevenuSouvenirs() {
        double total = 0;
        for (String[] vente : chargerVentesSouvenirs()) {
            if (vente.length >= 4) {
                try {
                    total += Double.parseDouble(vente[3]);
                } catch (NumberFormatException e) {
                    // Ignorer les lignes mal formées
                }
            }
        }
        return total;
    }
    
    /**
     * Retourne le chemin absolu du répertoire de données
     */
    public static String getDataDirectory() {
        return DATA_DIR;
    }
    
    /**
     * Nettoie/réinitialise tous les fichiers (usage caution!)
     */
    public static void reinitialiserTousFichiers() {
        try {
            Files.deleteIfExists(Paths.get(VISITEURS_FILE));
            Files.deleteIfExists(Paths.get(ATTRACTIONS_FILE));
            Files.deleteIfExists(Paths.get(EMPLOYES_FILE));
            Files.deleteIfExists(Paths.get(VENTES_BILLETS_FILE));
            Files.deleteIfExists(Paths.get(VENTES_RESTAURANT_FILE));
            Files.deleteIfExists(Paths.get(VENTES_SOUVENIRS_FILE));
            Files.deleteIfExists(Paths.get(SESSIONS_FILE));
            
            initialiserFichiers();
            Logger.logInfo("Tous les fichiers CSV ont été réinitialisés");
        } catch (IOException e) {
            Logger.logException("Erreur réinitialisation fichiers CSV", e);
        }
    }
}
