package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.resources.styles.UIStyles;

public class PanelDashboard extends JPanel {
    private final GestionnaireParc gestionnaireParc;
    private JLabel labelHeure, labelVisiteurs, labelAttractions, labelMeteo, labelStatutParc, labelRevenus;

    public PanelDashboard(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        setBackground(UIStyles.BG_WINDOW); // Fond gris élégant
        setLayout(new GridLayout(2, 3, 15, 15));
        setBorder(UIStyles.createStyledBorder("Tableau de Bord"));

        labelHeure = new JLabel("--:--");
        labelVisiteurs = new JLabel("0");
        labelAttractions = new JLabel("0/0");
        labelMeteo = new JLabel("--");
        labelStatutParc = new JLabel("Fermé");
        labelRevenus = new JLabel("0,00 €");

        add(createCard("TEMPS RÉEL", labelHeure, UIStyles.DUSK_BLUE));
        add(createCard("FRÉQUENTATION", labelVisiteurs, UIStyles.DUSTY_LAVENDER));
        add(createCard("OPÉRATIONS", labelAttractions, UIStyles.ROSEWOOD));
        add(createCard("CONDITIONS MÉTÉO", labelMeteo, UIStyles.LIGHT_BRONZE));
        add(createCard("STATUT PARC", labelStatutParc, UIStyles.DUSK_BLUE));
        add(createCard("RECETTES", labelRevenus, UIStyles.LIGHT_BRONZE));

        rafraichir(); // Initialiser l'affichage au démarrage
        new Timer(1000, e -> rafraichir()).start();
    }

    private JPanel createCard(String title, JLabel valLabel, Color titleCol) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE); // Carte Blanche Fancy
        card.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(UIStyles.BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.setForeground(titleCol);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valLabel.setForeground(UIStyles.TEXT_PRIMARY);
        valLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(t, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    public void rafraichir() {
        if (gestionnaireParc.getHorloge() != null) {
            labelHeure.setText(gestionnaireParc.getHorloge().getHeureFormatee());
        } else {
            labelHeure.setText("--:--");
        }
        labelVisiteurs.setText(String.valueOf(gestionnaireParc.getStatistiques().getNombreVisiteursActuels()));
        labelAttractions.setText(gestionnaireParc.getNombreAttractionsOperationnelles() + "/" + gestionnaireParc.getNombreAttractions());
        labelMeteo.setText(gestionnaireParc.getMeteoActuelle().toString());
        boolean ouvert = gestionnaireParc.estOuvert();
        labelStatutParc.setText(ouvert ? "Ouvert" : "Fermé");
        labelStatutParc.setForeground(ouvert ? new Color(0, 120, 60) : UIStyles.LIGHT_CORAL);
        labelRevenus.setText(gestionnaireParc.getStatistiques().getRevenusTotalFormate());
    }
}