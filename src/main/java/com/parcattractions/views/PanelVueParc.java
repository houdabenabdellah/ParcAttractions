package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import resources.styles.UIStyles;

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
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createCompoundBorder(
            UIStyles.createStyledBorder("État du Parc"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        labelEtat = new JLabel("Parc Fermé");
        labelEtat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelEtat.setForeground(UIStyles.DANGER_COLOR);
        labelEtat.setHorizontalAlignment(SwingConstants.CENTER);
        labelEtat.setOpaque(true);
        labelEtat.setBackground(UIStyles.BG_LIGHT);
        labelEtat.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        // Panel d'infos supplémentaires
JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
infoPanel.setBackground(Color.WHITE);

JLabel labelMeteo = new JLabel("Météo: " + gestionnaireParc.getMeteoActuelle());
labelMeteo.setFont(UIStyles.REGULAR_FONT);
labelMeteo.setForeground(UIStyles.TEXT_SECONDARY);
labelMeteo.setHorizontalAlignment(SwingConstants.CENTER);

JLabel labelVisiteurs = new JLabel("Visiteurs: " + 
    gestionnaireParc.getVisiteurs().size());
labelVisiteurs.setFont(UIStyles.REGULAR_FONT);
labelVisiteurs.setForeground(UIStyles.TEXT_SECONDARY);
labelVisiteurs.setHorizontalAlignment(SwingConstants.CENTER);

infoPanel.add(labelMeteo);
infoPanel.add(labelVisiteurs);

add(infoPanel, BorderLayout.SOUTH);
        
        add(labelEtat, BorderLayout.CENTER);
    }
    
    /**
 * Rafraîchit les données affichées - MODE MANUEL
 */
public void rafraichir() {
    if (gestionnaireParc.estOuvert()) {
        labelEtat.setText(" PARC OUVERT");
        labelEtat.setForeground(UIStyles.SUCCESS_COLOR);
        labelEtat.setBackground(new Color(200, 255, 200));
    } else {
        labelEtat.setText(" PARC FERMÉ");
        labelEtat.setForeground(UIStyles.DANGER_COLOR);
        labelEtat.setBackground(new Color(255, 200, 200));
    }
    
    // Mise à jour des infos si elles existent
    Component[] components = getComponents();
    for (Component c : components) {
        if (c instanceof JPanel && c != labelEtat) {
            JPanel panel = (JPanel) c;
            Component[] labels = panel.getComponents();
            if (labels.length >= 2) {
                if (labels[0] instanceof JLabel) {
                    ((JLabel) labels[0]).setText(" Météo: " + 
                        gestionnaireParc.getMeteoActuelle());
                }
                if (labels[1] instanceof JLabel) {
                    ((JLabel) labels[1]).setText(" Visiteurs: " + 
                        gestionnaireParc.getVisiteurs().size());
                }
            }
        }
    }
}
}
