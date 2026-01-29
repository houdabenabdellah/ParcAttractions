package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.enums.ProfilVisiteur;
import main.java.com.parcattractions.models.visiteurs.Visiteur;
import main.java.com.parcattractions.models.visiteurs.VisiteurCouple;
import main.java.com.parcattractions.models.visiteurs.VisiteurEnfant;
import main.java.com.parcattractions.models.visiteurs.VisiteurExtreme;
import main.java.com.parcattractions.models.visiteurs.VisiteurFamille;
import main.java.com.parcattractions.models.visiteurs.VisiteurGroupe;
import main.java.com.parcattractions.utils.Logger;

/**
 * Dialog pour ajouter manuellement un visiteur
 */
public class DialogAjoutVisiteur extends JDialog {
    
    private JTextField nomField;
    private JSpinner ageSpinner;
    private JSpinner tailleSpinner;
    private JComboBox<ProfilVisiteur> profilCombo;
    private JSpinner detailsSpinner;  // Pour nombre enfants ou taille groupe
    private JLabel detailsLabel;
    private JButton ajouterBtn;
    private JButton annulerBtn;
    private boolean valide = false;
    
    public DialogAjoutVisiteur(JFrame parent) {
        super(parent, "➕ Ajouter un Visiteur", true);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_LIGHT);
        buildUI();
    }
    
    private void buildUI() {
        setLayout(new BorderLayout());
        
        // Titre
        JLabel titleLabel = new JLabel("Ajouter un Visiteur");
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(titleLabel, BorderLayout.NORTH);
        
        // Formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Nom
        JLabel nomLabel = new JLabel("Nom:");
        nomLabel.setFont(UIStyles.REGULAR_FONT);
        nomLabel.setForeground(UIStyles.TEXT_PRIMARY);
        nomField = new JTextField();
        nomField.setFont(UIStyles.REGULAR_FONT);
        nomField.setBackground(Color.WHITE);
        nomField.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1));
        formPanel.add(nomLabel);
        formPanel.add(nomField);
        
        // Âge
        JLabel ageLabel = new JLabel("Âge:");
        ageLabel.setFont(UIStyles.REGULAR_FONT);
        ageLabel.setForeground(UIStyles.TEXT_PRIMARY);
        ageSpinner = new JSpinner(new SpinnerNumberModel(18, 5, 100, 1));
        ageSpinner.setFont(UIStyles.REGULAR_FONT);
        formPanel.add(ageLabel);
        formPanel.add(ageSpinner);
        
        // Taille (cm)
        JLabel tailleLabel = new JLabel("Taille (cm):");
        tailleLabel.setFont(UIStyles.REGULAR_FONT);
        tailleLabel.setForeground(UIStyles.TEXT_PRIMARY);
        tailleSpinner = new JSpinner(new SpinnerNumberModel(170, 100, 220, 1));
        tailleSpinner.setFont(UIStyles.REGULAR_FONT);
        formPanel.add(tailleLabel);
        formPanel.add(tailleSpinner);
        
        // Profil
        JLabel profilLabel = new JLabel("Profil:");
        profilLabel.setFont(UIStyles.REGULAR_FONT);
        profilLabel.setForeground(UIStyles.TEXT_PRIMARY);
        profilCombo = new JComboBox<>(ProfilVisiteur.values());
        profilCombo.setFont(UIStyles.REGULAR_FONT);
        profilCombo.setBackground(Color.WHITE);
        profilCombo.addActionListener(e -> updateDetailsLabel());
        formPanel.add(profilLabel);
        formPanel.add(profilCombo);
        
        // Détails (enfants, taille groupe, etc)
        detailsLabel = new JLabel("Taille groupe:");
        detailsLabel.setFont(UIStyles.REGULAR_FONT);
        detailsLabel.setForeground(UIStyles.TEXT_PRIMARY);
        detailsSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        detailsSpinner.setFont(UIStyles.REGULAR_FONT);
        formPanel.add(detailsLabel);
        formPanel.add(detailsSpinner);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIStyles.BG_LIGHT);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        ajouterBtn = new JButton("Ajouter");
        ajouterBtn.setFont(UIStyles.HEADER_FONT);
        ajouterBtn.setBackground(UIStyles.SUCCESS_COLOR);
        ajouterBtn.setForeground(Color.WHITE);
        ajouterBtn.setFocusPainted(false);
        ajouterBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        ajouterBtn.addActionListener(e -> ajouterVisiteur());
        
        annulerBtn = new JButton("Annuler");
        annulerBtn.setFont(UIStyles.HEADER_FONT);
        annulerBtn.setBackground(UIStyles.DANGER_COLOR);
        annulerBtn.setForeground(Color.WHITE);
        annulerBtn.setFocusPainted(false);
        annulerBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        annulerBtn.addActionListener(e -> setVisible(false));
        
        buttonPanel.add(ajouterBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(annulerBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void updateDetailsLabel() {
        ProfilVisiteur profil = (ProfilVisiteur) profilCombo.getSelectedItem();
        switch (profil) {
            case FAMILLE -> detailsLabel.setText("Nombre d'enfants:");
            case GROUPE -> detailsLabel.setText("Taille groupe:");
            default -> {
                detailsLabel.setText("(Non applicable)");
                detailsSpinner.setEnabled(false);
            }
        }
    }
    
    private void ajouterVisiteur() {
        // Validation
        if (nomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un nom", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int age = (Integer) ageSpinner.getValue();
        int taille = (Integer) tailleSpinner.getValue();
        ProfilVisiteur profil = (ProfilVisiteur) profilCombo.getSelectedItem();
        int details = (Integer) detailsSpinner.getValue();
        
        try {
            // Créer visiteur selon profil
            Visiteur visiteur = creerVisiteurSelonProfil(age, taille, profil, details);
            
            if (visiteur != null) {
                // Ajouter au parc
                GestionnaireParc.getInstance().ajouterVisiteur(visiteur);
                
                JOptionPane.showMessageDialog(this, 
                    "Visiteur ajouté avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                valide = true;
                setVisible(false);
            }
        } catch (RuntimeException ex) {
            Logger.logException("Erreur ajout visiteur", ex);
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'ajout: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Crée un visiteur selon son profil
     */
    private Visiteur creerVisiteurSelonProfil(int age, int taille, ProfilVisiteur profil, int details) {
        return switch (profil) {
            case ENFANT -> {
                Visiteur accompagnateur = new VisiteurGroupe(age + 20, taille + 20);
                yield new VisiteurEnfant(age, taille, accompagnateur);
            }
            case COUPLE -> {
                Visiteur partenaire = new VisiteurCouple(age, taille, null);
                yield new VisiteurCouple(age, taille, partenaire);
            }
            case FAMILLE -> new VisiteurFamille(age, taille, details);
            case GROUPE -> new VisiteurGroupe(age, taille);
            case EXTREME -> new VisiteurExtreme(age, taille);
        };
    }
    
    public boolean estValide() {
        return valide;
    }
}

