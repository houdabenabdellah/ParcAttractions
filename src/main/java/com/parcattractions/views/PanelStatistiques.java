package main.java.com.parcattractions.views;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import resources.styles.UIStyles;

/**
 * Panel des statistiques - Stylisé
 * Rafraîchit automatiquement pour refléter les changements (visiteurs, revenus, etc.)
 */
public class PanelStatistiques extends JPanel {
    
    private final GestionnaireParc gestionnaireParc;
    
    private JLabel labelRevenus;
    private JLabel labelSatisfaction;
    private JLabel labelTempsAttente;
    
    private Timer refreshTimer;
    
    public PanelStatistiques(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        
        setBackground(Color.WHITE);
        setBorder(UIStyles.createStyledBorder("Statistiques"));
        setLayout(new GridLayout(4, 1, 10, 10));
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
        // Bouton de rafraîchissement manuel
JButton btnRafraichir = new JButton(" Rafraîchir Stats");
btnRafraichir.setFont(new Font("Segoe UI", Font.BOLD, 12));
btnRafraichir.setBackground(UIStyles.INFO_COLOR);
btnRafraichir.setForeground(Color.WHITE);
btnRafraichir.setFocusPainted(false);
btnRafraichir.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
    BorderFactory.createEmptyBorder(10, 10, 10, 10)
));
btnRafraichir.setCursor(new Cursor(Cursor.HAND_CURSOR));
btnRafraichir.addActionListener(e -> {
    rafraichir();
    // Feedback visuel temporaire
    btnRafraichir.setText(" Rafraîchi!");
    Timer resetTimer = new Timer(1000, evt -> 
        btnRafraichir.setText(" Rafraîchir Stats"));
    resetTimer.setRepeats(false);
    resetTimer.start();
});

add(btnRafraichir);
        
        // Rafraîchissement automatique (comme le Dashboard) pour refléter les changements
        refreshTimer = new Timer(500, e -> rafraichir());
        refreshTimer.start();
    }
    
    /**
     * Arrête le rafraîchissement automatique (ex: fermeture de l'app)
     */
    public void stopAutoRefresh() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
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
