package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.TypeAttraction;
import main.java.com.parcattractions.resources.styles.UIStyles;

public class DialogAjoutAttraction extends JDialog {
    
    private JTextField nomField;
    private JComboBox<TypeAttraction> typeCombo;
    private JComboBox<NiveauIntensite> intensiteCombo;
    private JSpinner capaciteSpinner, ageMinSpinner, tailleMinSpinner, dureeToursSpinner;
    private JCheckBox exterieureCheck;
    private JButton okBtn, annulerBtn;
    private boolean valide = false;
    
    public DialogAjoutAttraction(JFrame parent) {
        super(parent, "Configuration Attraction", true);
        setSize(600, 600);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_WINDOW); // Fond moderne
        buildUI();
    }
    
    private void buildUI() {
        setLayout(new BorderLayout());
        
        // Titre Stylisé
        JLabel titleLabel = new JLabel("PARAMÈTRES DE L'ATTRACTION");
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.DUSK_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Formulaire (Carte Blanche)
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(UIStyles.createStyledBorder("Informations Techniques"));
        
        // Ajout des composants avec style
        addFormField(formPanel, "Nom:", nomField = new JTextField());
        nomField.setEditable(false);
        
        addFormField(formPanel, "Catégorie:", typeCombo = new JComboBox<>(TypeAttraction.values()));
        addFormField(formPanel, "Intensité:", intensiteCombo = new JComboBox<>(NiveauIntensite.values()));
        addFormField(formPanel, "Capacité Max:", capaciteSpinner = new JSpinner(new SpinnerNumberModel(20, 5, 100, 1)));
        addFormField(formPanel, "Âge Minimum:", ageMinSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 100, 1)));
        addFormField(formPanel, "Taille Min (cm):", tailleMinSpinner = new JSpinner(new SpinnerNumberModel(100, 50, 220, 1)));
        addFormField(formPanel, "Durée (sec):", dureeToursSpinner = new JSpinner(new SpinnerNumberModel(60, 10, 600, 10)));
        
        JLabel extLabel = new JLabel("Zone Extérieure:");
        extLabel.setFont(UIStyles.REGULAR_FONT);
        formPanel.add(extLabel);
        exterieureCheck = new JCheckBox("Oui");
        exterieureCheck.setBackground(Color.WHITE);
        formPanel.add(exterieureCheck);
        
        // Centrage du formulaire dans un conteneur avec marge
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        wrapper.add(formPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
        
        // Zone Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setOpaque(false);
        
        okBtn = new JButton("Appliquer");
        UIStyles.styleSecondaryButton(okBtn); // Bronze soft
        okBtn.addActionListener(e -> { valide = true; setVisible(false); });
        
        annulerBtn = new JButton("Annuler");
        UIStyles.styleAccentButton(annulerBtn); // Rosewood
        annulerBtn.addActionListener(e -> setVisible(false));
        
        buttonPanel.add(okBtn);
        buttonPanel.add(annulerBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel l = new JLabel(labelText);
        l.setFont(UIStyles.REGULAR_FONT);
        l.setForeground(UIStyles.TEXT_PRIMARY);
        panel.add(l);
        panel.add(field);
    }
    
    public boolean estValide() { return valide; }
    
    public void chargerAttraction(String nom, String type, String intensite, int cap, int age, int taille, int dur, boolean ext) {
        nomField.setText(nom);
        typeCombo.setSelectedItem(TypeAttraction.valueOf(type));
        intensiteCombo.setSelectedItem(NiveauIntensite.valueOf(intensite));
        capaciteSpinner.setValue(cap);
        ageMinSpinner.setValue(age);
        tailleMinSpinner.setValue(taille);
        dureeToursSpinner.setValue(dur);
        exterieureCheck.setSelected(ext);
    }
}