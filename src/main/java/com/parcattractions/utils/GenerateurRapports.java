package main.java.com.parcattractions.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.models.attractions.Attraction;
import main.java.com.parcattractions.models.employes.Employe;
import main.java.com.parcattractions.models.services.Restaurant;

/**
 * Classe pour générer tous les rapports du parc
 */
public class GenerateurRapports {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    private final GestionnaireParc gestionnaireParc;
    private final Statistiques statistiques;
    
    public GenerateurRapports(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        this.statistiques = gestionnaireParc.getStatistiques();
    }
    
    /**
     * Génère un rapport financier
     */
    public String genererRapportFinancier() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║           RAPPORT FINANCIER DU PARC                        ║\n");
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Date/Heure : %-50s ║\n", LocalDateTime.now().format(FORMATTER)));
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        
        // Revenus totaux
        double revenusTotal = statistiques.getRevenusTotal();
        sb.append(String.format("║ Revenus totaux : %45.2f € ║\n", revenusTotal));
        
        // Revenus par visiteur
        int visiteursTotaux = statistiques.getNombreVisiteursTotal();
        double revenuParVisiteur = visiteursTotaux > 0 ? revenusTotal / visiteursTotaux : 0;
        sb.append(String.format("║ Revenu/visiteur : %43.2f € ║\n", revenuParVisiteur));
        
        // Revenus restaurants
        double revenusRestaurants = gestionnaireParc.getRestaurants().stream()
            .mapToDouble(Restaurant::getRevenus)
            .sum();
        sb.append(String.format("║ Revenus restaurants : %39.2f € ║\n", revenusRestaurants));
        
        // Revenus billets d'entrée
        double revenusBillets = revenusTotal - revenusRestaurants;
        sb.append(String.format("║ Revenus billets : %44.2f € ║\n", revenusBillets));
        
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append("║ Détail par Restaurant :                                    ║\n");
        sb.append("╟────────────────────────────────────────────────────────────╢\n");
        
        for (Restaurant resto : gestionnaireParc.getRestaurants()) {
            double revenus = resto.getRevenus();
            sb.append(String.format("║ %-40s : %12.2f € ║\n", resto.getNom(), revenus));
        }
        
        sb.append("╚════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }
    
    /**
     * Génère un rapport d'occupation
     */
    public String genererRapportOccupation() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║        RAPPORT D'OCCUPATION DES ATTRACTIONS               ║\n");
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Date/Heure : %-50s ║\n", LocalDateTime.now().format(FORMATTER)));
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        
        // Occupation générale
        int visiteursTotaux = statistiques.getNombreVisiteursTotal();
        int visiteurActuels = statistiques.getNombreVisiteursActuels();
        sb.append(String.format("║ Visiteurs totaux : %44d ║\n", visiteursTotaux));
        sb.append(String.format("║ Visiteurs actuels : %43d ║\n", visiteurActuels));
        sb.append(String.format("║ Taux occupation global : %38.1f%% ║\n", 
            visiteursTotaux > 0 ? (visiteurActuels * 100.0 / visiteursTotaux) : 0));
        
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append("║ Occupation par Attraction :                                ║\n");
        sb.append("╟────────────────────────────────────────────────────────────╢\n");
        
        for (Attraction attraction : gestionnaireParc.getAttractions()) {
            int fileTotal = attraction.getTailleFileTotal();
            int visiteurs = attraction.getVisiteursTotaux();
            double tauxOcc = visiteurs > 0 ? (fileTotal * 100.0 / (visiteurs + fileTotal)) : 0;
            
            sb.append(String.format("║ %-35s : File:%4d, Taux:%5.1f%% ║\n", 
                attraction.getNom(), fileTotal, tauxOcc));
        }
        
        sb.append("╚════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }
    
    /**
     * Génère un rapport de satisfaction
     */
    public String genererRapportSatisfaction() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║          RAPPORT DE SATISFACTION                           ║\n");
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Date/Heure : %-50s ║\n", LocalDateTime.now().format(FORMATTER)));
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        
        // Satisfaction générale
        double satisfaction = statistiques.getSatisfactionMoyenne();
        int avisPositifs = statistiques.getAvisPositifs();
        int avisNegatifs = statistiques.getAvisNegatifs();
        int totalAvis = avisPositifs + avisNegatifs;
        
        String emoji;
        if (satisfaction >= 80) emoji = "[Excellent]";
        else if (satisfaction >= 60) emoji = "[Bon]";
        else if (satisfaction >= 40) emoji = "[Moyen]";
        else emoji = "[Mauvais]";
        
        sb.append(String.format("║ Satisfaction moyenne : %37.1f%% %s ║\n", satisfaction, emoji));
        sb.append(String.format("║ Avis positifs : %45d ║\n", avisPositifs));
        sb.append(String.format("║ Avis négatifs : %45d ║\n", avisNegatifs));
        
        if (totalAvis > 0) {
            double pctPositif = (avisPositifs * 100.0) / totalAvis;
            sb.append(String.format("║ Taux satisfaction : %39.1f%% ║\n", pctPositif));
        }
        
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append("║ Attractions les plus populaires :                          ║\n");
        sb.append("╟────────────────────────────────────────────────────────────╢\n");
        
        var attractionsTop = gestionnaireParc.getAttractions().stream()
            .sorted((a, b) -> Integer.compare(b.getVisiteursTotaux(), a.getVisiteursTotaux()))
            .limit(5)
            .collect(Collectors.toList());
        
        for (Attraction a : attractionsTop) {
            sb.append(String.format("║ %-35s : %6d visiteurs ║\n", a.getNom(), a.getVisiteursTotaux()));
        }
        
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append("║ Attractions moins visitées :                               ║\n");
        sb.append("╟────────────────────────────────────────────────────────────╢\n");
        
        var attractionsBottom = gestionnaireParc.getAttractions().stream()
            .sorted(Comparator.comparingInt(Attraction::getVisiteursTotaux))
            .limit(5)
            .collect(Collectors.toList());
        
        for (Attraction a : attractionsBottom) {
            sb.append(String.format("║ %-35s : %6d visiteurs ║\n", a.getNom(), a.getVisiteursTotaux()));
        }
        
        sb.append("╚════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }
    
    /**
     * Génère un rapport RH
     */
    public String genererRapportRH() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║           RAPPORT RESSOURCES HUMAINES                      ║\n");
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Date/Heure : %-50s ║\n", LocalDateTime.now().format(FORMATTER)));
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        
        List<Employe> employes = gestionnaireParc.getEmployes();
        sb.append(String.format("║ Nombre total d'employés : %41d ║\n", employes.size()));
        
        // Calculer statistiques de fatigue
        double fatigueMoyenne = employes.stream()
            .mapToDouble(e -> e.getFatigue())
            .average()
            .orElse(0);
        
        long employesFatigues = employes.stream()
            .filter(e -> e.getFatigue() > 70)
            .count();
        
        sb.append(String.format("║ Fatigue moyenne : %45.1f%% ║\n", fatigueMoyenne));
        sb.append(String.format("║ Employés fatigués (>70%%) : %38d ║\n", employesFatigues));
        
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append("║ Détail des employés :                                      ║\n");
        sb.append("╟────────────────────────────────────────────────────────────╢\n");
        
        for (Employe employe : employes) {
            String etat = employe.getEtat().getDescription();
            double fatigue = employe.getFatigue();
            sb.append(String.format("║ %-30s : Fatigue: %5.1f%% [%s] ║\n", 
                employe.getNom(), fatigue, etat));
        }
        
        sb.append("╚════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }
    
    /**
     * Génère un rapport complet
     */
    public String genererRapportComplet() {
        StringBuilder sb = new StringBuilder();
        sb.append(genererRapportFinancier());
        sb.append("\n\n");
        sb.append(genererRapportOccupation());
        sb.append("\n\n");
        sb.append(genererRapportSatisfaction());
        sb.append("\n\n");
        sb.append(genererRapportRH());
        return sb.toString();
    }
}
