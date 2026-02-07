package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.resources.styles.UIStyles;

public class PanelGestion extends JPanel {
    private final GestionnaireParc gestionnaireParc;
    private JLabel statsLabel;
    private JTextArea logArea;

    public PanelGestion(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        setBackground(UIStyles.BG_WINDOW); // Fond Modern
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        buildUI();
    }

    private void buildUI() {
        // --- Header Section ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("CENTRE DE COMMANDEMENT");
        title.setFont(UIStyles.TITLE_FONT);
        title.setForeground(UIStyles.DUSK_BLUE);
        
        statsLabel = new JLabel("Prêt pour la gestion...");
        statsLabel.setFont(UIStyles.HEADER_FONT);
        statsLabel.setForeground(UIStyles.DUSTY_LAVENDER);

        header.add(title, BorderLayout.WEST);
        header.add(statsLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- Grille d'actions (Flat & Fancy) ---
        JPanel actionPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        actionPanel.setOpaque(false);

        JButton btnV = new JButton("Ajouter un Visiteur"); UIStyles.stylePrimaryButton(btnV);
        JButton btnE = new JButton("Recruter un Employé"); UIStyles.stylePrimaryButton(btnE);
        JButton btnSave = new JButton("Synchroniser (Save CSV)"); UIStyles.styleSecondaryButton(btnSave);
        JButton btnLoad = new JButton("Importer Données (Load)"); UIStyles.styleSecondaryButton(btnLoad);
        JButton btnRefresh = new JButton("Actualiser la Vue"); UIStyles.styleAccentButton(btnRefresh);

        btnV.addActionListener(e -> ajouterVisiteur());
        btnSave.addActionListener(e -> gestionnaireParc.sauvegarderToutDonnees());
        
        actionPanel.add(btnV); actionPanel.add(btnE);
        actionPanel.add(btnSave); actionPanel.add(btnLoad);
        actionPanel.add(btnRefresh);

        add(actionPanel, BorderLayout.CENTER);

        // --- Journal de bord (Log Area Fancy) ---
        JPanel logPanel = new JPanel(new BorderLayout(5, 5));
        logPanel.setBackground(Color.WHITE); // Carte Blanche
        logPanel.setBorder(UIStyles.createStyledBorder("Journal d'activité du Manager"));

        logArea = new JTextArea(8, 0);
        logArea.setFont(UIStyles.MONOSPACE_FONT);
        logArea.setBackground(new Color(252, 252, 252));
        logArea.setEditable(false);
        
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(null);
        logPanel.add(scroll, BorderLayout.CENTER);

        add(logPanel, BorderLayout.SOUTH);
    }

    private void ajouterVisiteur() { /* Logique identique utilisant DialogAjoutVisiteur */ }
    private void updateStats() { /* Met à jour statsLabel */ }
}