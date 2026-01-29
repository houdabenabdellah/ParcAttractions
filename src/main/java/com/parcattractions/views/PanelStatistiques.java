package main.java.com.parcattractions.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import main.java.com.parcattractions.controllers.GestionnaireParc;

/**
 * Panel des statistiques - Stylisé
 */
public class PanelStatistiques extends JPanel {
    
    private final GestionnaireParc gestionnaireParc;
    
    private JLabel labelRevenus;
    private JLabel labelSatisfaction;
    private JLabel labelTempsAttente;
    
    public PanelStatistiques(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        
        setBackground(Color.WHITE);
        setBorder(UIStyles.createStyledBorder("Statistiques"));
        setLayout(new GridLayout(3, 1, 10, 10));
        setBorder(BorderFactory.createCompoundBorder(
            UIStyles.createStyledBorder("Statistiques"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        labelRevenus = new JLabel("Revenus: 0.00 €");
        labelSatisfaction = new JLabel("Satisfaction: --");
        labelTempsAttente = new JLabel("Attente: --");
        
        // Style les labels
        Font statFont = new Font("Segoe UI", Font.BOLD, 14);
        for (JLabel label : new JLabel[]{labelRevenus, labelSatisfaction, labelTempsAttente}) {
            label.setFont(statFont);
            label.setForeground(UIStyles.PRIMARY_COLOR);
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            label.setOpaque(true);
            label.setBackground(UIStyles.BG_LIGHT);
        }
        
        add(labelRevenus);
        add(labelSatisfaction);
        add(labelTempsAttente);
    }
    
    /**
     * Rafraîchit les données affichées
     */
    public void rafraichir() {
        var stats = gestionnaireParc.getStatistiques();
        
        labelRevenus.setText("Revenus: " + stats.getRevenusTotalFormate());
        labelSatisfaction.setText("Satisfaction: " + stats.getSatisfactionFormatee());
        labelTempsAttente.setText("Attente: " + stats.getTempsAttenteMoyenFormate());
    }
}
