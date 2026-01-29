package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.utils.ExporteurCSV;
import main.java.com.parcattractions.utils.GenerateurRapports;

/**
 * Dialog pour afficher tous les rapports du parc
 */
public class DialogRapports extends JDialog {
    
    private final GestionnaireParc gestionnaireParc;
    private final GenerateurRapports generateur;
    private final ExporteurCSV exporteur;
    
    public DialogRapports(JFrame parent, GestionnaireParc gestionnaireParc) {
        super(parent, "Rapports et Statistiques", true);
        
        this.gestionnaireParc = gestionnaireParc;
        this.generateur = new GenerateurRapports(gestionnaireParc);
        this.exporteur = new ExporteurCSV(gestionnaireParc);
        
        setSize(900, 650);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_LIGHT);
        
        buildUI();
    }
    
    private void buildUI() {
        setLayout(new BorderLayout());
        
        // Titre
        JPanel panelTitre = new JPanel();
        panelTitre.setBackground(UIStyles.PRIMARY_COLOR);
        JPanel panelLabelTitre = new JPanel();
        panelLabelTitre.setBackground(UIStyles.PRIMARY_COLOR);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIStyles.HEADER_FONT);
        tabbedPane.setBackground(Color.WHITE);
        
        // Onglet Résumé Financier
        tabbedPane.addTab("Financier", createRapportPanel(generateur.genererRapportFinancier()));
        
        // Onglet Occupation
        tabbedPane.addTab("Occupation", createRapportPanel(generateur.genererRapportOccupation()));
        
        // Onglet Satisfaction
        tabbedPane.addTab("Satisfaction", createRapportPanel(generateur.genererRapportSatisfaction()));
        
        // Onglet RH
        tabbedPane.addTab("Ressources Humaines", createRapportPanel(generateur.genererRapportRH()));
        
        // Onglet Complet
        tabbedPane.addTab("Rapport Complet", createRapportPanel(generateur.genererRapportComplet()));
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Boutons d'export
        JPanel panelBoutons = creerPanelBoutons();
        add(panelBoutons, BorderLayout.SOUTH);
    }
    
    /**
     * Crée un panel affichant un rapport dans un JTextArea scrollable
     */
    private JPanel createRapportPanel(String contenuRapport) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea textArea = new JTextArea();
        textArea.setText(contenuRapport);
        textArea.setEditable(false);
        textArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        textArea.setBackground(UIStyles.BG_LIGHT);
        textArea.setForeground(UIStyles.TEXT_PRIMARY);
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(false);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1));
        scrollPane.setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crée le panel des boutons d'export
     */
    private JPanel creerPanelBoutons() {
        JPanel panel = new JPanel();
        panel.setBackground(UIStyles.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setLayout(new GridLayout(1, 3, 10, 10));
        
        // Bouton Exporter CSV
        JButton btnExportCSV = new JButton("Exporter en CSV");
        btnExportCSV.addActionListener(e -> {
            String fichier = exporteur.exporterResume();
            if (fichier != null) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Rapport CSV exporté :\n" + fichier,
                    "Succès", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'export CSV",
                    "Erreur", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        UIStyles.stylePrimaryButton(btnExportCSV);
        panel.add(btnExportCSV);
        
        // Bouton Exporter HTML
        JButton btnExportHTML = new JButton("Exporter en HTML");
        btnExportHTML.addActionListener(e -> {
            String fichier = exporteur.exporterHTML();
            if (fichier != null) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Rapport HTML exporté :\n" + fichier,
                    "Succès", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'export HTML",
                    "Erreur", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        UIStyles.stylePrimaryButton(btnExportHTML);
        panel.add(btnExportHTML);
        
        // Bouton Fermer
        JButton btnFermer = new JButton("Fermer");
        btnFermer.addActionListener(e -> dispose());
        UIStyles.styleSecondaryButton(btnFermer);
        panel.add(btnFermer);
        
        return panel;
    }
}
