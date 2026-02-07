package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.models.employes.*;
import main.java.com.parcattractions.resources.styles.UIStyles;
import main.java.com.parcattractions.utils.Logger;


public class DialogAjoutEmploye extends JDialog {
    
    private static final String[] POSTES = {"Opérateur", "Technicien", "Agent Sécurité", "Vendeur"};
    private JTextField nomField;
    private JComboBox<String> posteCombo;
    private JButton ajouterBtn, annulerBtn;
    private boolean valide = false;
    
    public DialogAjoutEmploye(JFrame parent) {
        super(parent, "Recrutement Personnel", true);
        setSize(500, 350);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_WINDOW);
        buildUI();
    }
    
    private void buildUI() {
        setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("NOUVEAU COLLABORATEUR");
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.DUSK_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel card = new JPanel(new GridLayout(2, 2, 20, 25));
        card.setBackground(Color.WHITE);
        card.setBorder(UIStyles.createStyledBorder("Contrat"));
        
        JLabel nl = new JLabel("Nom complet:"); nl.setFont(UIStyles.REGULAR_FONT);
        nomField = new JTextField();
        
        JLabel pl = new JLabel("Poste assigné:"); pl.setFont(UIStyles.REGULAR_FONT);
        posteCombo = new JComboBox<>(POSTES);
        
        card.add(nl); card.add(nomField);
        card.add(pl); card.add(posteCombo);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        wrapper.add(card, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
        
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttons.setOpaque(false);
        
        ajouterBtn = new JButton("Recruter");
        UIStyles.stylePrimaryButton(ajouterBtn); // Dusk Blue
        ajouterBtn.addActionListener(e -> logicAjouter());
        
        annulerBtn = new JButton("Annuler");
        UIStyles.styleAccentButton(annulerBtn); // Rosewood
        annulerBtn.addActionListener(e -> dispose());
        
        buttons.add(ajouterBtn);
        buttons.add(annulerBtn);
        add(buttons, BorderLayout.SOUTH);
    }
    
    private void logicAjouter() {
        if (nomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nom obligatoire"); return;
        }
        try {
            Employe emp = switch(posteCombo.getSelectedItem().toString()) {
                case "Technicien" -> new Technicien(nomField.getText());
                case "Agent Sécurité" -> new AgentSecurite(nomField.getText());
                default -> new Technicien(nomField.getText()); 
            };
            GestionnaireParc.getInstance().ajouterEmploye(emp);
            valide = true;
            dispose();
        } catch (Exception ex) {
            Logger.logException("Erreur RH", ex);
        }
    }
    
    public boolean estValide() { return valide; }
}