package main.java.com.parcattractions.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.models.attractions.Attraction;
import main.java.com.parcattractions.models.employes.Employe;

/**
 * Classe pour exporter les données du parc en CSV
 */
public class ExporteurCSV {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    private final GestionnaireParc gestionnaireParc;
    private final Statistiques statistiques;
    
    public ExporteurCSV(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        this.statistiques = gestionnaireParc.getStatistiques();
    }
    
    /**
     * Exporte un résumé complet du parc en CSV
     */
    public String exporterResume() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String filename = "logs/Rapport_" + timestamp + ".csv";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("RÉSUMÉ FINAL DU PARC D'ATTRACTIONS\n");
            writer.write("Date;Heure;Catégorie;Valeur;Unité\n");
            
            LocalDateTime now = LocalDateTime.now();
            String date = now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String heure = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            
            // Statistiques générales
            writer.write(String.format("%s;%s;Visiteurs Total;%d;visiteurs\n", 
                date, heure, statistiques.getNombreVisiteursTotal()));
            writer.write(String.format("%s;%s;Visiteurs Actuels;%d;visiteurs\n", 
                date, heure, statistiques.getNombreVisiteursActuels()));
            writer.write(String.format("%s;%s;Tours Effectués;%d;tours\n", 
                date, heure, statistiques.getNombreToursEffectues()));
            
            // Revenus
            writer.write(String.format("%s;%s;Revenus Total;%.2f;€\n", 
                date, heure, statistiques.getRevenusTotal()));
            writer.write(String.format("%s;%s;Revenu Moyen Par Visiteur;%.2f;€\n", 
                date, heure, statistiques.getRevenuMoyenParVisiteur()));
            
            // Satisfaction
            writer.write(String.format("%s;%s;Satisfaction Moyenne;%.1f;%%\n", 
                date, heure, statistiques.getSatisfactionMoyenne()));
            writer.write(String.format("%s;%s;Avis Positifs;%d;avis\n", 
                date, heure, statistiques.getAvisPositifs()));
            writer.write(String.format("%s;%s;Avis Négatifs;%d;avis\n", 
                date, heure, statistiques.getAvisNegatifs()));
            
            // Temps d'attente
            writer.write(String.format("%s;%s;Temps Attente Moyen;%.0f;secondes\n", 
                date, heure, statistiques.getTempsAttenteMoyen()));
            
            writer.write("\n");
            writer.write("DÉTAIL PAR ATTRACTION\n");
            writer.write("Attraction;Visiteurs;File;Temps Attente;État\n");
            
            for (Attraction attraction : gestionnaireParc.getAttractions()) {
                writer.write(String.format("%s;%d;%d;%d;%s\n",
                    attraction.getNom(),
                    attraction.getVisiteursTotaux(),
                    attraction.getTailleFileTotal(),
                    attraction.getTempsAttenteEstime(),
                    attraction.getEtat().name()
                ));
            }
            
            writer.write("\n");
            writer.write("DÉTAIL DES EMPLOYÉS\n");
            writer.write("Nom;Poste;État;Fatigue\n");
            
            for (Employe employe : gestionnaireParc.getEmployes()) {
                writer.write(String.format("%s;%s;%s;%.1f%%\n",
                    employe.getNom(),
                    employe.getPoste(),
                    employe.getEtat().getDescription(),
                    employe.getFatigue()
                ));
            }
            
            writer.write("\n");
            writer.write("DÉTAIL DES RESTAURANTS\n");
            writer.write("Restaurant;Clients Actuels;Capacité;Taux Occupation;Revenus\n");
            
            for (var resto : gestionnaireParc.getRestaurants()) {
                try {
                    writer.write(String.format("%s;%d;%d;%.1f%%;%.2f€\n",
                        resto.getNom(),
                        resto.getClientsActuels(),
                        resto.getCapacite(),
                        resto.getTauxOccupation() * 100,
                        resto.getRevenus()
                    ));
                } catch (IOException e) {
                    Logger.logError("Erreur lors de l'export CSV du restaurant: " + e.getMessage());
                }
            }
            
            writer.write("\n");
            writer.write("DÉTAIL DES BOUTIQUES\n");
            writer.write("Boutique;Stock Disponible;Revenus\n");
            
            for (var boutique : gestionnaireParc.getBoutiques()) {
                try {
                    writer.write(String.format("%s;%d;%.2f€\n",
                        boutique.getNom(),
                        boutique.getStock(),
                        boutique.getRevenus()
                    ));
                } catch (IOException e) {
                    Logger.logError("Erreur lors de l'export CSV de la boutique: " + e.getMessage());
                }
            }
            
            Logger.logInfo("Rapport CSV généré : " + filename);
            return filename;
            
        } catch (IOException e) {
            Logger.logError("Erreur lors de la génération du CSV: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Exporte les données sous format HTML (plus lisible)
     */
    public String exporterHTML() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String filename = "logs/Rapport_" + timestamp + ".html";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html lang='fr'>\n");
            writer.write("<head>\n");
            writer.write("  <meta charset='UTF-8'>\n");
            writer.write("  <title>Rapport Parc d'Attractions</title>\n");
            writer.write("  <style>\n");
            writer.write("    body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n");
            writer.write("    h1 { color: #1a76bc; text-align: center; }\n");
            writer.write("    h2 { color: #6c3483; margin-top: 30px; border-bottom: 2px solid #1a76bc; padding-bottom: 10px; }\n");
            writer.write("    table { width: 100%; border-collapse: collapse; background-color: white; margin-bottom: 20px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }\n");
            writer.write("    th { background-color: #1a76bc; color: white; padding: 12px; text-align: left; }\n");
            writer.write("    td { padding: 10px; border-bottom: 1px solid #ddd; }\n");
            writer.write("    tr:hover { background-color: #f9f9f9; }\n");
            writer.write("    .stat-box { display: inline-block; width: 23%; margin: 1%; padding: 15px; background-color: white; border-left: 4px solid #1a76bc; }\n");
            writer.write("    .stat-value { font-size: 24px; font-weight: bold; color: #1a76bc; }\n");
            writer.write("    .stat-label { color: #666; font-size: 12px; margin-top: 5px; }\n");
            writer.write("  </style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            
            writer.write("<h1>Rapport Final du Parc d'Attractions</h1>\n");
            writer.write(String.format("<p style='text-align: center; color: #666;'>Généré le %s</p>\n", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm:ss"))));
            
            // Statistiques clés
            writer.write("<h2>Statistiques Cles</h2>\n");
            writer.write("<div class='stat-box'>\n");
            writer.write("  <div class='stat-value'>" + statistiques.getNombreVisiteursTotal() + "</div>\n");
            writer.write("  <div class='stat-label'>Visiteurs Total</div>\n");
            writer.write("</div>\n");
            writer.write("<div class='stat-box'>\n");
            writer.write(String.format("  <div class='stat-value'>%.2f€</div>\n", statistiques.getRevenusTotal()));
            writer.write("  <div class='stat-label'>Revenus Total</div>\n");
            writer.write("</div>\n");
            writer.write("<div class='stat-box'>\n");
            writer.write(String.format("  <div class='stat-value'>%.1f%%</div>\n", statistiques.getSatisfactionMoyenne()));
            writer.write("  <div class='stat-label'>Satisfaction Moyenne</div>\n");
            writer.write("</div>\n");
            writer.write("<div style='clear: both;'></div>\n");
            
            // Attractions
            writer.write("<h2>Attractions</h2>\n");
            writer.write("<table>\n");
            writer.write("  <tr><th>Nom</th><th>Visiteurs</th><th>File</th><th>Temps Attente</th><th>État</th></tr>\n");
            for (Attraction a : gestionnaireParc.getAttractions()) {
                writer.write(String.format("  <tr><td>%s</td><td>%d</td><td>%d</td><td>%d sec</td><td>%s</td></tr>\n",
                    a.getNom(), a.getVisiteursTotaux(), a.getTailleFileTotal(), 
                    a.getTempsAttenteEstime(), a.getEtat().name()));
            }
            writer.write("</table>\n");
            
            // Restaurants
            writer.write("<h2>Restaurants</h2>\n");
            writer.write("<table>\n");
            writer.write("  <tr><th>Nom</th><th>Occupation</th><th>Revenus</th></tr>\n");
            for (var resto : gestionnaireParc.getRestaurants()) {
                writer.write(String.format("  <tr><td>%s</td><td>%d/%d (%.1f%%)</td><td>%.2f€</td></tr>\n",
                    resto.getNom(), resto.getClientsActuels(), resto.getCapacite(),
                    resto.getTauxOccupation() * 100, resto.getRevenus()));
            }
            writer.write("</table>\n");
            
            // Employés
            writer.write("<h2>Employes</h2>\n");
            writer.write("<table>\n");
            writer.write("  <tr><th>Nom</th><th>Poste</th><th>État</th><th>Fatigue</th></tr>\n");
            for (Employe e : gestionnaireParc.getEmployes()) {
                writer.write(String.format("  <tr><td>%s</td><td>%s</td><td>%s</td><td>%.1f%%</td></tr>\n",
                    e.getNom(), e.getPoste(), e.getEtat().getDescription(), e.getFatigue()));
            }
            writer.write("</table>\n");
            
            writer.write("</body>\n");
            writer.write("</html>\n");
            
            Logger.logInfo("Rapport HTML généré : " + filename);
            return filename;
            
        } catch (IOException e) {
            Logger.logError("Erreur lors de la génération du HTML: " + e.getMessage());
            return null;
        }
    }
}
