package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.utils.Horloge;
import resources.styles.UIStyles;

/**
 * Panel du tableau de bord stylisé
 */
public class PanelDashboard extends JPanel {
    
    private final GestionnaireParc gestionnaireParc;
    
    private JLabel labelHeure;
    private JLabel labelVisiteurs;
    private JLabel labelAttractions;
    private JLabel labelMeteo;
    
    private Timer refreshTimer;  // ADD THIS
    
    public PanelDashboard(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        
        setBackground(Color.WHITE);
        setBorder(UIStyles.createStyledBorder("Tableau de Bord"));
        setLayout(new GridLayout(2, 2, 15, 15));
        setBorder(BorderFactory.createCompoundBorder(
            UIStyles.createStyledBorder("Tableau de Bord"),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Heure
        add(createDashboardCard("Heure", "labelHeure"));
        labelHeure = new JLabel("--:--");
        
        // Visiteurs
        add(createDashboardCard("Visiteurs", "labelVisiteurs"));
        labelVisiteurs = new JLabel("0");
        
        // Attractions
        add(createDashboardCard("Attractions", "labelAttractions"));
        labelAttractions = new JLabel("0/8");
        
        // Météo
        add(createDashboardCard("Meteo", "labelMeteo"));
        labelMeteo = new JLabel("--");
        
        // Re-faire avec les bons labels créés
        removeAll();
        setLayout(new GridLayout(2, 2, 15, 15));
        
        add(createDashboardItem("Heure", labelHeure));
        add(createDashboardItem("Visiteurs", labelVisiteurs));
        add(createDashboardItem("Attractions", labelAttractions));
        add(createDashboardItem("Météo", labelMeteo));
        
        // START AUTO-REFRESH TIMER - ADD THIS
        startAutoRefresh();
    }
    
    /**
     * Démarre le rafraîchissement automatique
     */
    private void startAutoRefresh() {
        // Refresh every 500ms (0.5 seconds) for smooth updates
        refreshTimer = new Timer(500, e -> rafraichir());
        refreshTimer.start();
    }
    
    /**
     * Arrête le rafraîchissement automatique
     */
    public void stopAutoRefresh() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
    }
    
    /**
     * Crée une carte de tableau de bord stylisée
     */
    private JPanel createDashboardItem(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(5, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(UIStyles.PRIMARY_COLOR);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(UIStyles.SECONDARY_COLOR);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Crée une simple carte (pas utilisée mais possible)
     */
    private JPanel createDashboardCard(String title, String labelName) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1));
        return card;
    }
    
    /**
     * Rafraîchit les données affichées
     */
    public void rafraichir() {
        Horloge horloge = gestionnaireParc.getHorloge();
        if (horloge != null) {
            labelHeure.setText(horloge.getHeureFormatee());
        }
        
        labelVisiteurs.setText(String.valueOf(
            gestionnaireParc.getStatistiques().getNombreVisiteursActuels()));
        
        labelAttractions.setText(String.format("%d/%d", 
            gestionnaireParc.getNombreAttractionsOperationnelles(),
            gestionnaireParc.getNombreAttractions()));
        
        labelMeteo.setText(gestionnaireParc.getMeteoActuelle().toString());
    }
    /**
 * Active/désactive le rafraîchissement automatique
 */
public void setAutoRefresh(boolean enabled) {
    if (enabled && (refreshTimer == null || !refreshTimer.isRunning())) {
        startAutoRefresh();
    } else if (!enabled && refreshTimer != null) {
        stopAutoRefresh();
    }
}

/**
 * Retourne l'état du rafraîchissement automatique
 */
public boolean isAutoRefreshEnabled() {
    return refreshTimer != null && refreshTimer.isRunning();
}
}