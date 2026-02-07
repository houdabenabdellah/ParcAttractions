package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.resources.styles.UIStyles;
public class PanelVueParc extends JPanel {
    
    private final GestionnaireParc gestionnaireParc;
    private JLabel labelStatusBadge;
    private JLabel labelMeteo, labelPop;
    
    public PanelVueParc(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        
        setBackground(Color.WHITE); // Panel intérieur Blanc
        setLayout(new BorderLayout(10, 10));
        setBorder(UIStyles.createStyledBorder("Monitoring Principal"));
        
        // Badge d'état central
        labelStatusBadge = new JLabel("PARC FERMÉ", SwingConstants.CENTER);
        labelStatusBadge.setOpaque(true);
        labelStatusBadge.setFont(new Font("Segoe UI", Font.BOLD, 20));
        labelStatusBadge.setForeground(Color.WHITE);
        labelStatusBadge.setBackground(UIStyles.LIGHT_CORAL); // Coral = Fermé
        labelStatusBadge.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel statsFooter = new JPanel(new GridLayout(1, 2, 10, 0));
        statsFooter.setOpaque(false);
        labelMeteo = new JLabel("Ciel: --");
        labelPop = new JLabel("Population: 0");
        labelMeteo.setFont(UIStyles.HEADER_FONT);
        labelPop.setFont(UIStyles.HEADER_FONT);
        
        statsFooter.add(labelMeteo);
        statsFooter.add(labelPop);
        
        add(labelStatusBadge, BorderLayout.CENTER);
        add(statsFooter, BorderLayout.SOUTH);
    }
    
    public void rafraichir() {
        if (gestionnaireParc.estOuvert()) {
            labelStatusBadge.setText("SÉCURITÉ : PARC OUVERT");
            labelStatusBadge.setBackground(UIStyles.LIGHT_BRONZE); // Bronze = Succès/Ouvert
        } else {
            labelStatusBadge.setText("SÉCURITÉ : PARC FERMÉ");
            labelStatusBadge.setBackground(UIStyles.LIGHT_CORAL); // Coral = Fermé
        }
        labelMeteo.setText("MÉTÉO : " + gestionnaireParc.getMeteoActuelle());
        labelPop.setText("VISITEURS : " + gestionnaireParc.getVisiteurs().size());
    }
}