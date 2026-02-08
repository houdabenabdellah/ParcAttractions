package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.resources.styles.UIStyles;

public class PanelStatistiques extends JPanel {
    
    private final GestionnaireParc gestionnaireParc;
    private JLabel labelRevenus, labelSatisfaction, labelTempsAttente;
    private Timer refreshTimer;
    
    public PanelStatistiques(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        
        setBackground(UIStyles.BG_WINDOW); // Fond Modern Gris-Bleuté
        setLayout(new BorderLayout(15, 15));
        setBorder(UIStyles.createStyledBorder("Analytiques en Direct"));

        // Container des cartes
        JPanel cardsContainer = new JPanel(new GridLayout(3, 1, 10, 10));
        cardsContainer.setOpaque(false);

        // Création des cartes métriques
        labelRevenus = new JLabel("0.00 €");
        labelSatisfaction = new JLabel("0%");
        labelTempsAttente = new JLabel("0 sec");

        cardsContainer.add(createStatCard("Recettes Totales", labelRevenus, UIStyles.LIGHT_BRONZE));
        cardsContainer.add(createStatCard("Indice Satisfaction", labelSatisfaction, UIStyles.DUSTY_LAVENDER));
        cardsContainer.add(createStatCard("Attente Estimée", labelTempsAttente, UIStyles.ROSEWOOD));

        add(cardsContainer, BorderLayout.CENTER);

        // Bouton Fancy
        JButton btnRafraichir = new JButton("Actualiser les données");
        UIStyles.stylePrimaryButton(btnRafraichir); // Dusk Blue
        btnRafraichir.addActionListener(e -> rafraichir());
        add(btnRafraichir, BorderLayout.SOUTH);
        
        rafraichir(); // Initialiser l'affichage au démarrage
        refreshTimer = new Timer(500, e -> rafraichir());
        refreshTimer.start();
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor), // Barre colorée latérale
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(new Font("Segoe UI", Font.BOLD, 11));
        t.setForeground(UIStyles.TEXT_PRIMARY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(UIStyles.TEXT_PRIMARY);

        card.add(t, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void rafraichir() {
        var stats = gestionnaireParc.getStatistiques();
        labelRevenus.setText(stats.getRevenusTotalFormate());
        labelSatisfaction.setText(stats.getSatisfactionFormatee());
        labelTempsAttente.setText(stats.getTempsAttenteMoyenFormate());
    }
}