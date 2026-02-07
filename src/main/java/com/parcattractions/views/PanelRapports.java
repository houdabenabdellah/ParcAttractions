package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.resources.styles.UIStyles;
import main.java.com.parcattractions.utils.ExporteurCSV;
import main.java.com.parcattractions.utils.Logger;
import main.java.com.parcattractions.utils.TransactionManager;

public class PanelRapports extends JPanel {
    
    private final GestionnaireParc gestionnaireParc;
    private JTextArea rapportArea;
    private JComboBox<String> typeRapportCombo;
    private JLabel dateRapportLabel;
    
    public PanelRapports(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        
        setBackground(UIStyles.BG_WINDOW);
        setLayout(new BorderLayout(15, 15));
        setBorder(UIStyles.createStyledBorder("Rapports & Statistiques"));
        
        buildUI();
        rafraichir();
    }
    
    private void buildUI() {
        // --- Panel de Contrôle Supérieur ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setOpaque(false);
        
        JLabel typeLabel = new JLabel("Type de Rapport: ");
        typeLabel.setFont(UIStyles.REGULAR_FONT);
        
        String[] types = {
            "Résumé Session",
            "Visiteurs",
            "Finances",
            "Attractions",
            "Personnel",
            "Complet"
        };
        typeRapportCombo = new JComboBox<>(types);
        typeRapportCombo.setFont(UIStyles.REGULAR_FONT);
        typeRapportCombo.setBackground(Color.WHITE);
        typeRapportCombo.addActionListener(e -> rafraichir());
        
        JButton btnGenerer = new JButton("Générer Rapport");
        UIStyles.stylePrimaryButton(btnGenerer);
        btnGenerer.addActionListener(e -> genererRapport());
        
        JButton btnExporter = new JButton("Exporter (CSV)");
        UIStyles.styleSecondaryButton(btnExporter);
        btnExporter.addActionListener(e -> exporterCSV());
        
        JButton btnExporterHTML = new JButton("Exporter (HTML)");
        UIStyles.styleAccentButton(btnExporterHTML);
        btnExporterHTML.addActionListener(e -> exporterHTML());
        
        controlPanel.add(typeLabel);
        controlPanel.add(typeRapportCombo);
        controlPanel.add(btnGenerer);
        controlPanel.add(btnExporter);
        controlPanel.add(btnExporterHTML);
        
        // --- Panel d'Affichage ---
        JPanel displayPanel = new JPanel(new BorderLayout(10, 10));
        displayPanel.setBackground(Color.WHITE);
        displayPanel.setBorder(UIStyles.createStyledBorder("Rapport Actuel"));
        
        dateRapportLabel = new JLabel("Généré: --");
        dateRapportLabel.setFont(UIStyles.REGULAR_FONT);
        dateRapportLabel.setForeground(UIStyles.TEXT_PRIMARY);
        dateRapportLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        rapportArea = new JTextArea();
        rapportArea.setEditable(false);
        rapportArea.setLineWrap(true);
        rapportArea.setWrapStyleWord(true);
        rapportArea.setFont(UIStyles.MONOSPACE_FONT);
        rapportArea.setBackground(Color.WHITE);
        rapportArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(rapportArea);
        scrollPane.setBorder(new javax.swing.border.LineBorder(UIStyles.BORDER_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        displayPanel.add(dateRapportLabel, BorderLayout.NORTH);
        displayPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(controlPanel, BorderLayout.NORTH);
        add(displayPanel, BorderLayout.CENTER);
    }
    
    private void rafraichir() {
        String typeSelected = (String) typeRapportCombo.getSelectedItem();
        
        String rapport = switch (typeSelected) {
            case "Visiteurs" -> genererRapportVisiteurs();
            case "Finances" -> genererRapportFinances();
            case "Attractions" -> genererRapportAttractions();
            case "Personnel" -> genererRapportPersonnel();
            case "Complet" -> genererRapportComplet();
            default -> genererRapportResume();
        };
        
        rapportArea.setText(rapport);
        dateRapportLabel.setText("Généré: " + java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }
    
    private void genererRapport() {
        rafraichir();
        JOptionPane.showMessageDialog(this, "Rapport généré avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exporterCSV() {
        try {
            ExporteurCSV exporteur = new ExporteurCSV(gestionnaireParc);
            String fichier = exporteur.exporterResume();
            JOptionPane.showMessageDialog(this, "Rapport exporté: " + fichier, "Succès", JOptionPane.INFORMATION_MESSAGE);
            Logger.logInfo("Rapport CSV généré: " + fichier);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exporterHTML() {
        try {
            ExporteurCSV exporteur = new ExporteurCSV(gestionnaireParc);
            String fichier = exporteur.exporterHTML();
            JOptionPane.showMessageDialog(this, "Rapport exporté: " + fichier, "Succès", JOptionPane.INFORMATION_MESSAGE);
            Logger.logInfo("Rapport HTML généré: " + fichier);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String genererRapportResume() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║     RÉSUMÉ DE SESSION DU PARC         ║\n");
        sb.append("╚════════════════════════════════════════╝\n\n");
        
        sb.append("🏢 PARC D'ATTRACTIONS\n");
        sb.append("─────────────────────\n");
        sb.append("État: ").append(gestionnaireParc.estOuvert() ? "OUVERT ✓" : "FERMÉ ✗").append("\n");
        sb.append("Nombre d'attractions: ").append(gestionnaireParc.getNombreAttractions()).append("\n");
        sb.append("Attractions opérationnelles: ").append(gestionnaireParc.getNombreAttractionsOperationnelles()).append("\n\n");
        
        sb.append("👥 VISITEURS\n");
        sb.append("───────────\n");
        sb.append("Total visiteurs actuels: ").append(gestionnaireParc.getVisiteurs().size()).append("\n\n");
        
        sb.append("💼 PERSONNEL\n");
        sb.append("────────────\n");
        sb.append("Total employés: ").append(gestionnaireParc.getNombreEmployes()).append("\n\n");
        
        sb.append("💰 FINANCES\n");
        sb.append("────────────\n");
        sb.append("Statistiques: ").append(gestionnaireParc.getStatistiques().genererRapport()).append("\n");
        
        return sb.toString();
    }
    
    private String genererRapportVisiteurs() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║         RAPPORT VISITEURS             ║\n");
        sb.append("╚════════════════════════════════════════╝\n\n");
        
        sb.append("📊 STATISTIQUES VISITEURS\n");
        sb.append("─────────────────────────\n");
        sb.append("Nombre total: ").append(gestionnaireParc.getVisiteurs().size()).append("\n");
        
        if (gestionnaireParc.getVisiteurs().isEmpty()) {
            sb.append("\nAucun visiteur actuellement\n");
        } else {
            sb.append("\n👤 DÉTAIL DES VISITEURS:\n");
            for (var visiteur : gestionnaireParc.getVisiteurs()) {
                sb.append("  • ").append(visiteur.getNomVisiteur())
                  .append(" (").append(visiteur.getAge()).append(" ans")
                  .append(", Budget: ").append(String.format("%.2f", visiteur.getArgent())).append("€)")
                  .append("\n");
            }
        }
        
        return sb.toString();
    }
    
    private String genererRapportFinances() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║       RAPPORT FINANCIER               ║\n");
        sb.append("╚════════════════════════════════════════╝\n\n");
        
        sb.append("💰 RÉSUMÉ FINANCIER (Statistiques parc)\n");
        sb.append("──────────────────────────────────────\n");
        sb.append(gestionnaireParc.getStatistiques().genererRapport());
        sb.append("\n\n📈 DÉTAIL DES RECETTES (Transactions)\n");
        sb.append("──────────────────────────────────────\n");
        sb.append("  Billets:     ").append(String.format("%.2f €", TransactionManager.getRevenuBillets())).append("\n");
        sb.append("  Restauration: ").append(String.format("%.2f €", TransactionManager.getRevenuRestaurant())).append("\n");
        sb.append("  Souvenirs:   ").append(String.format("%.2f €", TransactionManager.getRevenuSouvenirs())).append("\n");
        sb.append("  TOTAL:       ").append(String.format("%.2f €", TransactionManager.getRevenuTotal())).append("\n");
        sb.append("  Opérations:  ").append(TransactionManager.getNbTransactions()).append(" ventes\n");
        
        return sb.toString();
    }
    
    private String genererRapportAttractions() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║      RAPPORT ATTRACTIONS              ║\n");
        sb.append("╚════════════════════════════════════════╝\n\n");
        
        sb.append("🎢 STATUS DES ATTRACTIONS\n");
        sb.append("──────────────────────────\n");
        sb.append("Total: ").append(gestionnaireParc.getNombreAttractions()).append("\n");
        sb.append("Opérationnelles: ").append(gestionnaireParc.getNombreAttractionsOperationnelles()).append("\n\n");
        
        sb.append("📋 DÉTAIL:\n");
        for (var attraction : gestionnaireParc.getAttractions()) {
            String etat = attraction.getEtat().toString();
            sb.append("  • ").append(attraction.getNom()).append(" [").append(etat).append("]\n");
        }
        
        return sb.toString();
    }
    
    private String genererRapportPersonnel() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║      RAPPORT PERSONNEL                ║\n");
        sb.append("╚════════════════════════════════════════╝\n\n");
        
        sb.append("👔 EFFECTIF\n");
        sb.append("──────────\n");
        sb.append("Total employés: ").append(gestionnaireParc.getNombreEmployes()).append("\n\n");
        
        sb.append("📋 LISTE DU PERSONNEL:\n");
        for (var employe : gestionnaireParc.getEmployes()) {
            sb.append("  • ").append(employe.getNom())
              .append(" (").append(employe.getClass().getSimpleName()).append(")")
              .append("\n");
        }
        
        return sb.toString();
    }
    
    private String genererRapportComplet() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════╗\n");
        sb.append("║     RAPPORT COMPLET DU PARC D'ATTRACTIONS         ║\n");
        sb.append("╚═══════════════════════════════════════════════════╝\n\n");
        
        sb.append(genererRapportResume()).append("\n");
        sb.append(genererRapportVisiteurs()).append("\n");
        sb.append(genererRapportFinances()).append("\n");
        sb.append(genererRapportAttractions()).append("\n");
        sb.append(genererRapportPersonnel()).append("\n");
        
        return sb.toString();
    }
}
