package main.java.com.parcattractions.views;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.models.attractions.Attraction;
import main.java.com.parcattractions.models.employes.Employe;
import main.java.com.parcattractions.models.visiteurs.Visiteur;
import main.java.com.parcattractions.utils.Logger;
import resources.styles.UIStyles;

/**
 * Panneau de gestion des entités du parc
 * Permet d'ajouter/modifier/supprimer visiteurs, attractions, employés
 */
public class PanelGestion extends JPanel {
    
    private JButton ajouterVisiteurBtn;
    private JButton ajouterEmployeBtn;
    private JButton visualiserAttractionsBtn;
    private JButton sauvegarderBtn;
    private JButton chargerBtn;
    
    private JLabel statsLabel;
    private JTextArea logArea;
    
    private GestionnaireParc gestionnaireParc;
    
    public PanelGestion(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        setBackground(UIStyles.BG_LIGHT);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        buildUI();
    }
    
    private void buildUI() {
        // Panneau supérieur - Titre et stats
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(15, 15));
        topPanel.setBackground(UIStyles.BG_LIGHT);
        
        JLabel titleLabel = new JLabel("Gestion du Parc d'Attractions");
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.PRIMARY_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        statsLabel = new JLabel();
        statsLabel.setFont(UIStyles.REGULAR_FONT);
        statsLabel.setForeground(UIStyles.TEXT_SECONDARY);
        topPanel.add(statsLabel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Panneau central - Boutons d'actions
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(3, 2, 15, 15));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Ajouter visiteur
        ajouterVisiteurBtn = createButton("Ajouter Visiteur", UIStyles.INFO_COLOR);
        ajouterVisiteurBtn.addActionListener(e -> ajouterVisiteur());
        actionPanel.add(ajouterVisiteurBtn);
        
        // Visualiser visiteurs
        JButton visualiserVisiteurBtn = createButton("Voir Visiteurs", UIStyles.SECONDARY_COLOR);
        visualiserVisiteurBtn.addActionListener(e -> visualiserVisiteurs());
        actionPanel.add(visualiserVisiteurBtn);
        
        // Ajouter employé
        ajouterEmployeBtn = createButton("Ajouter Employé", UIStyles.INFO_COLOR);
        ajouterEmployeBtn.addActionListener(e -> ajouterEmploye());
        actionPanel.add(ajouterEmployeBtn);
        
        // Visualiser employés
        JButton visualiserEmployeBtn = createButton("Voir Employés", UIStyles.SECONDARY_COLOR);
        visualiserEmployeBtn.addActionListener(e -> visualiserEmployes());
        actionPanel.add(visualiserEmployeBtn);
        
        // Gestion attractions
        visualiserAttractionsBtn = createButton("Gestion Attractions", UIStyles.INFO_COLOR);
        visualiserAttractionsBtn.addActionListener(e -> visualiserAttractions());
        actionPanel.add(visualiserAttractionsBtn);
        
        // Sauvegarder/Charger
        sauvegarderBtn = createButton("Sauvegarder", UIStyles.SUCCESS_COLOR);
        sauvegarderBtn.addActionListener(e -> sauvegarderDonnees());
        actionPanel.add(sauvegarderBtn);
        
        chargerBtn = createButton("Charger", UIStyles.WARNING_COLOR);
        chargerBtn.addActionListener(e -> chargerDonnees());
        actionPanel.add(chargerBtn);

        

JButton rafraichirBtn = createButton("Rafraîchir", UIStyles.INFO_COLOR);
rafraichirBtn.addActionListener(e -> {
    updateStats();
    ajouterLog("Affichage rafraîchi manuellement");
});
actionPanel.add(rafraichirBtn);
        
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(UIStyles.BG_LIGHT);
        centerPanel.add(actionPanel, BorderLayout.NORTH);
        
        // Panneau inférieur - Logs
        JPanel logPanel = new JPanel(new BorderLayout(10, 10));
        logPanel.setBackground(Color.WHITE);
        logPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel logLabel = new JLabel("Activité récente:");
        logLabel.setFont(UIStyles.BOLD_FONT);
        logLabel.setForeground(UIStyles.TEXT_PRIMARY);
        logPanel.add(logLabel, BorderLayout.NORTH);
        
        logArea = new JTextArea(8, 50);
        logArea.setFont(UIStyles.MONOSPACE_FONT);
        logArea.setEditable(false);
        logArea.setBackground(Color.WHITE);
        logArea.setForeground(UIStyles.TEXT_PRIMARY);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1));
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        centerPanel.add(logPanel, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Mettre à jour les stats
        updateStats();
    }
    
    /**
     * Crée un bouton stylisé
     */
    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(UIStyles.HEADER_FONT);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    /**
     * Met à jour les statistiques affichées
     */
    private void updateStats() {
        int nbVisiteurs = gestionnaireParc.getVisiteurs().size();
        int nbEmployes = gestionnaireParc.getEmployes().size();
        int nbAttractionsOp = gestionnaireParc.getNombreAttractionsOperationnelles();
        int nbAttractionsTot = gestionnaireParc.getNombreAttractions();
        String nbAttractions = nbAttractionsOp + "/" + nbAttractionsTot;
        
        statsLabel.setText(String.format("%d visiteurs | %d employés | %s attractions",
            nbVisiteurs, nbEmployes, nbAttractions));
    }
    
    /**
     * Ajoute un visiteur
     */
    private void ajouterVisiteur() {
        if (!gestionnaireParc.estOuvert()) {
        int reponse = JOptionPane.showConfirmDialog(this, 
            "Le parc est fermé. Voulez-vous l'ouvrir d'abord?", 
            "Parc fermé", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (reponse == JOptionPane.YES_OPTION) {
            try {
                gestionnaireParc.ouvrirParc();
                ajouterLog("Parc ouvert automatiquement");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'ouverture du parc: " + ex.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            return;
        }
    }
    
    DialogAjoutVisiteur dialog = new DialogAjoutVisiteur(
        (JFrame) SwingUtilities.getWindowAncestor(this));
    dialog.setVisible(true);
    
    if (dialog.estValide()) {
        ajouterLog("Visiteur ajouté manuellement");
        updateStats();
    }
    }
    
    /**
     * Ajoute un employé
     */
    private void ajouterEmploye() {
        DialogAjoutEmploye dialog = new DialogAjoutEmploye((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        
        if (dialog.estValide()) {
            logArea.append("[" + java.time.LocalTime.now() + "] Employé ajouté\n");
            updateStats();
        }
    }
    
    /**
     * Visualise les visiteurs actuels
     */
    private void visualiserVisiteurs() {
        List<Visiteur> visiteurs = gestionnaireParc.getVisiteurs();
        
        if (visiteurs.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun visiteur actuellement dans le parc", 
                "Liste vide", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("Visiteurs actuels:\n\n");
        for (Visiteur v : visiteurs) {
            sb.append(String.format("%s - Age: %d, Satisfaction: %d%%, État: %s\n",
                v.getNomVisiteur(), v.getAge(), v.getSatisfaction(), v.getEtat()));
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(UIStyles.MONOSPACE_FONT);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Liste des Visiteurs (" + visiteurs.size() + ")", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Visualise les employés actuels
     */
    private void visualiserEmployes() {
        List<Employe> employes = gestionnaireParc.getEmployes();
        
        if (employes.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun employé enregistré", 
                "Liste vide", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("Employés du parc:\n\n");
        for (Employe e : employes) {
            sb.append(String.format("%s (%s) - État: %s, Fatigue: %d%%\n",
                e.getNom(), e.getPoste(), e.getEtat(), e.getFatigue()));
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(UIStyles.MONOSPACE_FONT);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Liste des Employés (" + employes.size() + ")", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Visualise et gère les attractions
     */
    private void visualiserAttractions() {
        List<Attraction> attractions = gestionnaireParc.getAttractions();
        
        if (attractions.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucune attraction disponible", 
                "Liste vide", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder("Attractions du parc:\n\n");
        for (Attraction a : attractions) {
            sb.append(String.format("%s (%s) - État: %s, Tours: %d, Visiteurs: %d, Temps attente: %s\n",
                a.getNom(), a.getType(), a.getEtat(), a.getToursEffectues(), 
                a.getVisiteursTotaux(), a.getTempsAttenteFormate()));
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(UIStyles.MONOSPACE_FONT);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Liste des Attractions (" + attractions.size() + ")", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Sauvegarde toutes les données en CSV
     */
    private void sauvegarderDonnees() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir sauvegarder toutes les données actuelles?\n" +
            "Cette action va écrire dans les fichiers CSV.", 
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                gestionnaireParc.sauvegarderToutDonnees();
                logArea.append("[" + java.time.LocalTime.now() + "] Données sauvegardées\n");
                JOptionPane.showMessageDialog(this, 
                    "Données sauvegardées avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (RuntimeException ex) {
                Logger.logException("Erreur sauvegarde", ex);
                JOptionPane.showMessageDialog(this, 
                    "Erreur: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Charge les données depuis les fichiers CSV
     */
    private void chargerDonnees() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir charger les données depuis les fichiers CSV?\n" +
            "Les données actuelles non sauvegardées seront perdues.", 
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                gestionnaireParc.chargerDonnees();
                logArea.append("[" + java.time.LocalTime.now() + "] Données chargées\n");
                updateStats();
                JOptionPane.showMessageDialog(this, 
                    "Données chargées avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (RuntimeException ex) {
                Logger.logException("Erreur chargement", ex);
                JOptionPane.showMessageDialog(this, 
                    "Erreur: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Ajoute un message au log
     */
    public void ajouterLog(String message) {
        logArea.append("[" + java.time.LocalTime.now() + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength()); // Scroll to bottom
    }
}
