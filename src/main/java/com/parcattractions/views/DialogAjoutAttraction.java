package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.TypeAttraction;
import resources.styles.UIStyles;

/**
 * Dialog pour ajouter/visualiser une attraction
 * Note: Les attractions sont prédéfinies et créées au démarrage.
 * Ce dialog permet de visualiser et configurer les attractions existantes.
 */
public class DialogAjoutAttraction extends JDialog {
    
    private JTextField nomField;
    private JComboBox<TypeAttraction> typeCombo;
    private JComboBox<NiveauIntensite> intensiteCombo;
    private JSpinner capaciteSpinner;
    private JSpinner ageMinSpinner;
    private JSpinner tailleMinSpinner;
    private JSpinner dureeToursSpinner;
    private JCheckBox exterieureCheck;
    private JButton okBtn;
    private JButton annulerBtn;
    private boolean valide = false;
    
    public DialogAjoutAttraction(JFrame parent) {
        super(parent, "Gestion Attractions", true);
        setSize(600, 550);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_LIGHT);
        buildUI();
    }
    
    private void buildUI() {
        setLayout(new BorderLayout());
        
        // Titre
        JLabel titleLabel = new JLabel("Gestion des Attractions");
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(titleLabel, BorderLayout.NORTH);
        
        // Formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(8, 2, 15, 15));
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
        nomField.setEditable(false); // Lecture seule
        formPanel.add(nomLabel);
        formPanel.add(nomField);
        
        // Type
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(UIStyles.REGULAR_FONT);
        typeLabel.setForeground(UIStyles.TEXT_PRIMARY);
        typeCombo = new JComboBox<>(TypeAttraction.values());
        typeCombo.setFont(UIStyles.REGULAR_FONT);
        typeCombo.setBackground(Color.WHITE);
        formPanel.add(typeLabel);
        formPanel.add(typeCombo);
        
        // Intensité
        JLabel intensiteLabel = new JLabel("Intensité:");
        intensiteLabel.setFont(UIStyles.REGULAR_FONT);
        intensiteLabel.setForeground(UIStyles.TEXT_PRIMARY);
        intensiteCombo = new JComboBox<>(NiveauIntensite.values());
        intensiteCombo.setFont(UIStyles.REGULAR_FONT);
        intensiteCombo.setBackground(Color.WHITE);
        formPanel.add(intensiteLabel);
        formPanel.add(intensiteCombo);
        
        // Capacité
        JLabel capaciteLabel = new JLabel("Capacité:");
        capaciteLabel.setFont(UIStyles.REGULAR_FONT);
        capaciteLabel.setForeground(UIStyles.TEXT_PRIMARY);
        capaciteSpinner = new JSpinner(new SpinnerNumberModel(20, 5, 100, 1));
        capaciteSpinner.setFont(UIStyles.REGULAR_FONT);
        formPanel.add(capaciteLabel);
        formPanel.add(capaciteSpinner);
        
        // Âge min
        JLabel ageMinLabel = new JLabel("Âge min:");
        ageMinLabel.setFont(UIStyles.REGULAR_FONT);
        ageMinLabel.setForeground(UIStyles.TEXT_PRIMARY);
        ageMinSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 100, 1));
        ageMinSpinner.setFont(UIStyles.REGULAR_FONT);
        formPanel.add(ageMinLabel);
        formPanel.add(ageMinSpinner);
        
        // Taille min
        JLabel tailleMinLabel = new JLabel("Taille min (cm):");
        tailleMinLabel.setFont(UIStyles.REGULAR_FONT);
        tailleMinLabel.setForeground(UIStyles.TEXT_PRIMARY);
        tailleMinSpinner = new JSpinner(new SpinnerNumberModel(100, 50, 220, 1));
        tailleMinSpinner.setFont(UIStyles.REGULAR_FONT);
        formPanel.add(tailleMinLabel);
        formPanel.add(tailleMinSpinner);
        
        // Durée tour
        JLabel dureeLabel = new JLabel("Durée tour (sec):");
        dureeLabel.setFont(UIStyles.REGULAR_FONT);
        dureeLabel.setForeground(UIStyles.TEXT_PRIMARY);
        dureeToursSpinner = new JSpinner(new SpinnerNumberModel(60, 10, 600, 10));
        dureeToursSpinner.setFont(UIStyles.REGULAR_FONT);
        formPanel.add(dureeLabel);
        formPanel.add(dureeToursSpinner);
        
        // Extérieure
        JLabel exterieureLabel = new JLabel("Extérieure:");
        exterieureLabel.setFont(UIStyles.REGULAR_FONT);
        exterieureLabel.setForeground(UIStyles.TEXT_PRIMARY);
        exterieureCheck = new JCheckBox("Oui");
        exterieureCheck.setFont(UIStyles.REGULAR_FONT);
        exterieureCheck.setBackground(Color.WHITE);
        formPanel.add(exterieureLabel);
        formPanel.add(exterieureCheck);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(UIStyles.BG_LIGHT);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        okBtn = new JButton("OK");
        okBtn.setFont(UIStyles.HEADER_FONT);
        okBtn.setBackground(UIStyles.SUCCESS_COLOR);
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        okBtn.addActionListener(e -> {
            valide = true;
            setVisible(false);
        });
        
        annulerBtn = new JButton("Annuler");
        annulerBtn.setFont(UIStyles.HEADER_FONT);
        annulerBtn.setBackground(UIStyles.DANGER_COLOR);
        annulerBtn.setForeground(Color.WHITE);
        annulerBtn.setFocusPainted(false);
        annulerBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        annulerBtn.addActionListener(e -> setVisible(false));
        
        buttonPanel.add(okBtn);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(annulerBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public boolean estValide() {
        return valide;
    }
    
    /**
     * Charge les données d'une attraction dans le formulaire
     */
    public void chargerAttraction(String nom, String type, String intensite, 
                                   int capacite, int ageMin, int tailleMin, 
                                   int dureeTour, boolean exterieure) {
        nomField.setText(nom);
        typeCombo.setSelectedItem(TypeAttraction.valueOf(type));
        intensiteCombo.setSelectedItem(NiveauIntensite.valueOf(intensite));
        capaciteSpinner.setValue(capacite);
        ageMinSpinner.setValue(ageMin);
        tailleMinSpinner.setValue(tailleMin);
        dureeToursSpinner.setValue(dureeTour);
        exterieureCheck.setSelected(exterieure);
    }
}
