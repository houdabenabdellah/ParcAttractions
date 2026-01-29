package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.models.employes.AgentSecurite;
import main.java.com.parcattractions.models.employes.Employe;
import main.java.com.parcattractions.models.employes.Technicien;
import main.java.com.parcattractions.models.employes.Vendeur;
import main.java.com.parcattractions.utils.Logger;

/**
 * Dialog pour ajouter manuellement un employé
 */
public class DialogAjoutEmploye extends JDialog {
    
    private static final String[] POSTES = {
        "Opérateur", "Technicien", "Agent Sécurité", "Vendeur", "Autre"
    };
    
    private JTextField nomField;
    private JComboBox<String> posteCombo;
    private JButton ajouterBtn;
    private JButton annulerBtn;
    private boolean valide = false;
    
    public DialogAjoutEmploye(JFrame parent) {
        super(parent, "➕ Ajouter un Employé", true);
        setSize(500, 300);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_LIGHT);
        buildUI();
    }
    
    private void buildUI() {
        setLayout(new BorderLayout());
        
        // Titre
        JLabel titleLabel = new JLabel("Ajouter un Employé");
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(titleLabel, BorderLayout.NORTH);
        
        // Formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(2, 2, 15, 15));
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
        
        // Poste
        JLabel posteLabel = new JLabel("Poste:");
        posteLabel.setFont(UIStyles.REGULAR_FONT);
        posteLabel.setForeground(UIStyles.TEXT_PRIMARY);
        posteCombo = new JComboBox<>(POSTES);
        posteCombo.setFont(UIStyles.REGULAR_FONT);
        posteCombo.setBackground(Color.WHITE);
        formPanel.add(posteLabel);
        formPanel.add(posteCombo);
        
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
        ajouterBtn.addActionListener(e -> ajouterEmploye());
        
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
    
    private void ajouterEmploye() {
        // Validation
        if (nomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un nom", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String nom = nomField.getText().trim();
        String poste = (String) posteCombo.getSelectedItem();
        
        try {
            // Créer employé selon poste
            Employe employe = creerEmployeSelonPoste(nom, poste);
            
            if (employe != null) {
                // Ajouter au parc
                GestionnaireParc.getInstance().ajouterEmploye(employe);
                
                JOptionPane.showMessageDialog(this, 
                    "Employé '" + nom + "' (" + poste + ") ajouté avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                valide = true;
                setVisible(false);
            }
        } catch (Exception ex) {
            Logger.logException("Erreur ajout employé", ex);
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'ajout: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Crée un employé selon son poste
     */
    private Employe creerEmployeSelonPoste(String nom, String poste) {
        switch (poste) {
            case "Technicien":
                return new Technicien(nom);
            case "Agent Sécurité":
                return new AgentSecurite(nom);
            case "Vendeur":
                return new Vendeur(nom, GestionnaireParc.getInstance().getRestaurants().get(0));
            case "Opérateur":
            case "Autre":
            default:
                // Pour opérateur, il faut une attraction assignée
                // On affiche un message
                JOptionPane.showMessageDialog(this, 
                    "Le poste 'Opérateur' doit être assigné à une attraction spécifique.\n" +
                    "Cette fonctionnalité sera disponible prochainement.", 
                    "Info", JOptionPane.INFORMATION_MESSAGE);
                return null;
        }
    }
    
    public boolean estValide() {
        return valide;
    }
}
