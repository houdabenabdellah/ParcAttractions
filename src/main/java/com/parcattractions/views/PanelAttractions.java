package main.java.com.parcattractions.views;

import java.awt.*;
import java.net.URL;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.enums.EtatAttraction;
import main.java.com.parcattractions.models.attractions.Attraction;
import main.java.com.parcattractions.resources.styles.UIStyles;


public class PanelAttractions extends JPanel {
    private final GestionnaireParc gestionnaireParc;
    private final JFrame parentFrame;
    private final JLabel[] labelsAttractions;
    private final JLabel[] imageLabels;
    private final JComboBox<String> comboAttractions;
    private final JButton[] cardTicketButtons;
    
    public PanelAttractions(GestionnaireParc gestionnaireParc, JFrame parentFrame) {
        this.gestionnaireParc = gestionnaireParc;
        this.parentFrame = parentFrame;
        
        setBackground(UIStyles.BG_WINDOW); // Fond moderne gris clair
        setLayout(new BorderLayout(15, 15));
        setBorder(UIStyles.createStyledBorder("Flux des Attractions"));

        labelsAttractions = new JLabel[8];
        imageLabels = new JLabel[8];
        cardTicketButtons = new JButton[8];
        comboAttractions = new JComboBox<>();
        comboAttractions.setFont(UIStyles.REGULAR_FONT);
        comboAttractions.setBackground(Color.WHITE);

        // --- Grid des Attractions (Cards) ---
        JPanel grid = new JPanel(new GridLayout(4, 2, 15, 15));
        grid.setBackground(UIStyles.BG_WINDOW); // Le fond derrière les cartes est gris

        for (int i = 0; i < 8; i++) {
            grid.add(createAttractionCard(i));
        }

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIStyles.BG_WINDOW);

        // --- Panel de Contrôle Supérieur ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setOpaque(false); // Translucide pour voir le BG_WINDOW

        JButton btnOuvrir = new JButton("Ouvrir"); UIStyles.styleSecondaryButton(btnOuvrir); // Bronze
        // Les actions Fermer / En panne ont été déplacées dans la fenêtre de détails
        JButton btnDetails = new JButton("Gestion & Tickets"); UIStyles.stylePrimaryButton(btnDetails); // Blue

        btnOuvrir.addActionListener(e -> actionAttraction("ouvrir"));
        btnDetails.addActionListener(e -> ouvrirDetails());

        controlPanel.add(new JLabel("Sélection: "));
        controlPanel.add(comboAttractions);
        controlPanel.add(btnOuvrir);
        controlPanel.add(btnDetails);

        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Initialiser l'affichage des attractions
        var attractions = gestionnaireParc.getAttractions();
        comboAttractions.addItem("-- Choisir --");
        for (Attraction a : attractions) {
            comboAttractions.addItem(a.getNom());
        }
        // Ouvrir la fenêtre de détails automatiquement quand l'utilisateur choisit une attraction
        comboAttractions.addActionListener(e -> {
            Object sel = comboAttractions.getSelectedItem();
            if (sel != null && !sel.equals("-- Choisir --")) {
                ouvrirDetails();
            }
        });
        
        for (int i = 0; i < Math.min(attractions.size(), 8); i++) {
            Attraction att = attractions.get(i);
            String etatNom = getEtatTexte(att.getEtat());
            labelsAttractions[i].setText(att.getNom().toUpperCase() + " | " + etatNom);
            labelsAttractions[i].setForeground(UIStyles.getStateColor(etatNom));
            chargerImageAttraction(imageLabels[i], att);
            // activer le bouton de la carte
            if (cardTicketButtons[i] != null) cardTicketButtons[i].setEnabled(true);
        }
    }

    /** Charge et affiche l'image d'une attraction dans le JLabel (via classpath). */
    private void chargerImageAttraction(JLabel label, Attraction att) {
        String path = att.getImagePath();
        if (path == null || path.isEmpty()) {
            label.setIcon(null);
            label.setText("Pas d'image");
            return;
        }
        URL url = getClass().getResource(path);
        if (url == null) {
            label.setIcon(null);
            label.setText("Image non disponible");
            return;
        }
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage();
        if (img != null) {
            Image scaled = img.getScaledInstance(-1, 160, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
            label.setText(null);
        } else {
            label.setIcon(null);
            label.setText("Erreur chargement");
        }
    }

    private JPanel createAttractionCard(int index) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE); // Carte Blanche Fancy
        panel.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(UIStyles.BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        imageLabels[index] = new JLabel("Chargement...");
        imageLabels[index].setPreferredSize(new Dimension(0, 160));
        imageLabels[index].setBackground(new Color(250, 250, 250));
        imageLabels[index].setOpaque(true);
        imageLabels[index].setHorizontalAlignment(SwingConstants.CENTER);
        
        labelsAttractions[index] = new JLabel("--");
        labelsAttractions[index].setFont(UIStyles.REGULAR_FONT.deriveFont(Font.BOLD));
        labelsAttractions[index].setHorizontalAlignment(SwingConstants.CENTER);
        // bottom area contains label + action button
        JPanel bottom = new JPanel(new BorderLayout(5, 5));
        bottom.setOpaque(false);
        bottom.add(labelsAttractions[index], BorderLayout.CENTER);

        JButton btnTicket = new JButton("Prendre Ticket");
        UIStyles.stylePrimaryButton(btnTicket);
        btnTicket.setEnabled(false); // activé si une attraction est chargée
        int idx = index;
        btnTicket.addActionListener(e -> {
            var list = gestionnaireParc.getAttractions();
            if (idx < list.size()) {
                Attraction att = list.get(idx);
                DialogDetailsAttraction dialog = new DialogDetailsAttraction((JFrame) parentFrame, att, gestionnaireParc);
                dialog.setVisible(true);
                rafraichir();
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Aucune attraction à cet emplacement", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        cardTicketButtons[index] = btnTicket;
        bottom.add(btnTicket, BorderLayout.SOUTH);

        panel.add(imageLabels[index], BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // --- Méthodes de logique identiques, mais avec UI Refresh ---
    public void rafraichir() {
        var attractions = gestionnaireParc.getAttractions();
        comboAttractions.removeAllItems();
        comboAttractions.addItem("-- Choisir --");
        for (Attraction a : attractions) comboAttractions.addItem(a.getNom());
        
        for (int i = 0; i < Math.min(attractions.size(), 8); i++) {
            Attraction att = attractions.get(i);
            String etatNom = getEtatTexte(att.getEtat());
            labelsAttractions[i].setText(att.getNom().toUpperCase() + " | " + etatNom);
            labelsAttractions[i].setForeground(UIStyles.getStateColor(etatNom));
            chargerImageAttraction(imageLabels[i], att);
            if (cardTicketButtons[i] != null) cardTicketButtons[i].setEnabled(true);
        }
        // désactiver boutons pour emplacements vides
        for (int i = attractions.size(); i < 8; i++) if (cardTicketButtons[i] != null) cardTicketButtons[i].setEnabled(false);
    }

    private String getEtatTexte(EtatAttraction e) {
        return switch(e) {
            case OPERATIONNELLE -> "OUVERT";
            case FERMEE -> "FERMÉ";
            case PANNE -> "PANNE";
            default -> "MAINTENANCE";
        };
    }
    
    private void actionAttraction(String action) {
        Object selectedItem = comboAttractions.getSelectedItem();
        if (selectedItem == null || selectedItem.equals("-- Choisir --")) {
            JOptionPane.showMessageDialog(parentFrame, "Veuillez sélectionner une attraction", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Attraction attraction = getSelectedAttraction();
        if (attraction == null) {
            JOptionPane.showMessageDialog(parentFrame, "Attraction introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            switch (action) {
                case "ouvrir" -> {
                    attraction.ouvrir();
                    JOptionPane.showMessageDialog(parentFrame, attraction.getNom() + " est maintenant OUVERTE", "Succès", JOptionPane.INFORMATION_MESSAGE);
                }
                case "fermer" -> {
                    attraction.fermer();
                    JOptionPane.showMessageDialog(parentFrame, attraction.getNom() + " est maintenant FERMÉE", "Succès", JOptionPane.INFORMATION_MESSAGE);
                }
                case "panne" -> {
                    attraction.mettrePanne();
                    JOptionPane.showMessageDialog(parentFrame, attraction.getNom() + " est signalée EN PANNE. Intervention requise.", "Panne déclenchée", JOptionPane.WARNING_MESSAGE);
                }
                default -> { }
            }
            rafraichir(); // Rafraîchir l'affichage après l'action
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentFrame, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void ouvrirDetails() {
        Object selectedItem = comboAttractions.getSelectedItem();
        if (selectedItem == null || selectedItem.equals("-- Choisir --")) {
            JOptionPane.showMessageDialog(parentFrame, "Veuillez sélectionner une attraction", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Attraction attraction = getSelectedAttraction();
        if (attraction == null) {
            JOptionPane.showMessageDialog(parentFrame, "Attraction introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        DialogDetailsAttraction dialog = new DialogDetailsAttraction((JFrame) parentFrame, attraction, gestionnaireParc);
        dialog.setVisible(true);
        rafraichir(); // Rafraîchir après la fermeture du dialogue
    }
    private Attraction getSelectedAttraction() {
        Object selectedItem = comboAttractions.getSelectedItem();
        if (selectedItem == null || selectedItem.equals("-- Choisir --")) {
            return null;
        }
        return gestionnaireParc.getAttractionParNom((String) selectedItem);
    }
}