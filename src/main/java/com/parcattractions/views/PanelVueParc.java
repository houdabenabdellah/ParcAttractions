package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import main.java.com.parcattractions.controllers.GestionnaireParc;

/**
 * Panel vue d'ensemble du parc - Stylisé
 */
public class PanelVueParc extends JPanel {
    
    private final GestionnaireParc gestionnaireParc;
    private JLabel labelEtat;
    
    public PanelVueParc(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        
        setBackground(Color.WHITE);
        setBorder(UIStyles.createStyledBorder("État du Parc"));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            UIStyles.createStyledBorder("État du Parc"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        labelEtat = new JLabel("🔴 Parc Fermé");
        labelEtat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelEtat.setForeground(UIStyles.DANGER_COLOR);
        labelEtat.setHorizontalAlignment(SwingConstants.CENTER);
        labelEtat.setOpaque(true);
        labelEtat.setBackground(UIStyles.BG_LIGHT);
        labelEtat.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        add(labelEtat, BorderLayout.CENTER);
    }
    
    /**
     * Rafraîchit les données affichées
     */
    public void rafraichir() {
        if (gestionnaireParc.estOuvert()) {
            labelEtat.setText("🟢 Parc Ouvert");
            labelEtat.setForeground(UIStyles.SUCCESS_COLOR);
        } else {
            labelEtat.setText("🔴 Parc Fermé");
            labelEtat.setForeground(UIStyles.DANGER_COLOR);
        }
    }
}
