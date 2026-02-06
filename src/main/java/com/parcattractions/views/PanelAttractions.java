package main.java.com.parcattractions.views;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.enums.EtatAttraction;
import main.java.com.parcattractions.models.attractions.Attraction;
import resources.styles.UIStyles;

/**
 * Panel affichant les attractions - Stylisé
 */
public class PanelAttractions extends JPanel {
    
    private final GestionnaireParc gestionnaireParc;
    private JFrame parentFrame;
    private JLabel[] labelsAttractions;
    private JLabel[] imageLabels;  // Add this for images
    private JComboBox<String> comboAttractions;
    
    public PanelAttractions(GestionnaireParc gestionnaireParc) {
        this(gestionnaireParc, null);
    }
    
    public PanelAttractions(GestionnaireParc gestionnaireParc, JFrame parentFrame) {
        this.gestionnaireParc = gestionnaireParc;
        this.parentFrame = parentFrame;
        
        setBackground(Color.WHITE);
        setBorder(UIStyles.createStyledBorder("Attractions"));
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createCompoundBorder(
            UIStyles.createStyledBorder("Attractions"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        labelsAttractions = new JLabel[8];
        imageLabels = new JLabel[8];  // Initialize image labels array
        comboAttractions = new JComboBox<>();
        comboAttractions.addItem("-- Sélectionner --");
        comboAttractions.setFont(UIStyles.REGULAR_FONT);
        comboAttractions.setBackground(Color.WHITE);

        // Grid panel for attraction panels: image on top, text below, full cell width
        JPanel grid = new JPanel(new GridLayout(4, 2, 10, 10));
        grid.setBackground(Color.WHITE);

        for (int i = 0; i < 8; i++) {
            JPanel attractionPanel = createAttractionPanel(i);
            grid.add(attractionPanel);
        }

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        
        // Panel de contrôle manuel
JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
controlPanel.setBackground(Color.WHITE);

JButton btnOuvrir = new JButton("Ouvrir");
btnOuvrir.setFont(UIStyles.SMALL_FONT);
btnOuvrir.setBackground(UIStyles.SUCCESS_COLOR);
btnOuvrir.setForeground(Color.WHITE);
btnOuvrir.setFocusPainted(false);
btnOuvrir.addActionListener(e -> {
    Attraction selected = getSelectedAttraction();
    if (selected != null) {
        selected.ouvrir();
        rafraichir();
        JOptionPane.showMessageDialog(this, 
            "Attraction ouverte: " + selected.getNom(), 
            "Succès", 
            JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, 
            "Veuillez sélectionner une attraction", 
            "Attention", 
            JOptionPane.WARNING_MESSAGE);
    }
});

JButton btnFermer = new JButton("Fermer");
btnFermer.setFont(UIStyles.SMALL_FONT);
btnFermer.setBackground(UIStyles.DANGER_COLOR);
btnFermer.setForeground(Color.WHITE);
btnFermer.setFocusPainted(false);
btnFermer.addActionListener(e -> {
    Attraction selected = getSelectedAttraction();
    if (selected != null) {
        selected.fermer();
        rafraichir();
        JOptionPane.showMessageDialog(this, 
            "Attraction fermée: " + selected.getNom(), 
            "Succès", 
            JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, 
            "Veuillez sélectionner une attraction", 
            "Attention", 
            JOptionPane.WARNING_MESSAGE);
    }
});

JButton btnPanne = new JButton("Panne");
btnPanne.setFont(UIStyles.SMALL_FONT);
btnPanne.setBackground(UIStyles.WARNING_COLOR);
btnPanne.setForeground(Color.WHITE);
btnPanne.setFocusPainted(false);
btnPanne.addActionListener(e -> {
    Attraction selected = getSelectedAttraction();
    if (selected != null) {
        selected.mettrePanne();
        rafraichir();
        JOptionPane.showMessageDialog(this, 
            "Panne simulée sur: " + selected.getNom(), 
            "Panne", 
            JOptionPane.WARNING_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, 
            "Veuillez sélectionner une attraction", 
            "Attention", 
            JOptionPane.WARNING_MESSAGE);
    }
});

JButton btnDetails = new JButton("Détails / Ticket");
btnDetails.setFont(UIStyles.SMALL_FONT);
btnDetails.setBackground(UIStyles.PRIMARY_COLOR);
btnDetails.setForeground(Color.WHITE);
btnDetails.setFocusPainted(false);
btnDetails.addActionListener(e -> {
    Attraction selected = getSelectedAttraction();
    if (selected == null) {
        JOptionPane.showMessageDialog(this,
            "Veuillez sélectionner une attraction",
            "Attention",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    try {
        JFrame frame = parentFrame;
        if (frame == null) {
            java.awt.Window win = javax.swing.SwingUtilities.getWindowAncestor(this);
            frame = (win instanceof JFrame) ? (JFrame) win : null;
        }
        if (frame == null) {
            for (java.awt.Frame f : java.awt.Frame.getFrames()) {
                if (f instanceof JFrame && f.isShowing()) {
                    frame = (JFrame) f;
                    break;
                }
            }
        }
        DialogDetailsAttraction dialog = new DialogDetailsAttraction(frame, selected, gestionnaireParc);
        dialog.setVisible(true);
        dialog.toFront();
        rafraichir();
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Erreur lors de l'ouverture des détails: " + ex.getMessage(),
            "Erreur",
            JOptionPane.ERROR_MESSAGE);
    }
});

JButton btnRafraichir = new JButton("Rafraîchir");
btnRafraichir.setFont(UIStyles.SMALL_FONT);
btnRafraichir.setBackground(UIStyles.INFO_COLOR);
btnRafraichir.setForeground(Color.WHITE);
btnRafraichir.setFocusPainted(false);
btnRafraichir.addActionListener(e -> rafraichir());

controlPanel.add(comboAttractions);
controlPanel.add(btnOuvrir);
controlPanel.add(btnFermer);
controlPanel.add(btnPanne);
controlPanel.add(btnDetails);
controlPanel.add(btnRafraichir);

add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /** Image area height (pixels) - image spans full card width */
    private static final int IMAGE_HEIGHT = 180;

    /** Minimum height of each attraction card in the grid */
    private static final int CARD_MIN_HEIGHT = 260;

    /**
     * Creates a panel for each attraction: image on top (full width), text below
     */
    private JPanel createAttractionPanel(int index) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(UIStyles.BG_LIGHT);
        panel.setPreferredSize(new Dimension(0, CARD_MIN_HEIGHT));
        panel.setMinimumSize(new Dimension(0, CARD_MIN_HEIGHT));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        // Image on top - takes full width of grid cell, scales when resized
        imageLabels[index] = new JLabel();
        imageLabels[index].setPreferredSize(new Dimension(0, IMAGE_HEIGHT));
        imageLabels[index].setMinimumSize(new Dimension(0, IMAGE_HEIGHT));
        imageLabels[index].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        imageLabels[index].setHorizontalAlignment(SwingConstants.CENTER);
        imageLabels[index].setVerticalAlignment(SwingConstants.CENTER);
        imageLabels[index].setBackground(Color.WHITE);
        imageLabels[index].setOpaque(true);
        final int idx = index;
        imageLabels[index].addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                scaleImageForLabel(imageLabels[idx]);
            }
        });
        panel.add(imageLabels[index], BorderLayout.NORTH);

        // Text below image
        labelsAttractions[index] = new JLabel("--");
        labelsAttractions[index].setFont(UIStyles.REGULAR_FONT);
        labelsAttractions[index].setForeground(UIStyles.TEXT_PRIMARY);
        labelsAttractions[index].setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelsAttractions[index], BorderLayout.CENTER);

        return panel;
    }
    
    /**
     * Rafraîchit les données affichées
     */
    public void rafraichir() {
        var attractions = gestionnaireParc.getAttractions();
        // Update combo box
        comboAttractions.removeAllItems();
        comboAttractions.addItem("-- Sélectionner --");
        for (Attraction a : attractions) {
            comboAttractions.addItem(a.getNom());
        }
        
        for (int i = 0; i < Math.min(attractions.size(), labelsAttractions.length); i++) {
            Attraction attraction = attractions.get(i);
            
            // Update text
            String texte = String.format("%s [%s] File: %d", 
                attraction.getNom(),
                getEtatCouleur(attraction.getEtat()),
                attraction.getTailleFileTotal());
            labelsAttractions[i].setText(texte);
            labelsAttractions[i].setForeground(UIStyles.getStateColor(getEtatCouleur(attraction.getEtat())));
            
            // Update image
            loadAttractionImage(attraction, imageLabels[i]);
        }
    }
    
    private static final String KEY_IMAGE = "attractionImage";

    /** Essaie plusieurs chemins pour trouver la ressource image */
    private URL findResource(ClassLoader cl, String... paths) {
        if (cl == null) return null;
        for (String p : paths) {
            if (p == null || p.isEmpty()) continue;
            URL u = cl.getResource(p);
            if (u != null) return u;
        }
        return null;
    }

    /**
     * Loads the attraction image from classpath and stores it; scales to label size
     * Essaie plusieurs chemins pour compatibilité avec différentes configs de classpath
     */
    private void loadAttractionImage(Attraction attraction, JLabel imageLabel) {
        try {
            String imagePath = attraction.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                ClassLoader cl = getClass().getClassLoader();
                if (cl == null) cl = Thread.currentThread().getContextClassLoader();
                // Extraire le nom du fichier pour essayer des chemins alternatifs
                String fileName = imagePath.contains("/") ? imagePath.substring(imagePath.lastIndexOf('/') + 1) : imagePath;
                String pathImages = "images/" + fileName;
                String pathResources = "resources/images/" + fileName;
                URL url = findResource(cl, imagePath, pathImages, pathResources);
                if (url != null) {
                    ImageIcon icon = new ImageIcon(url);
                    if (icon.getIconWidth() > 0) {
                        imageLabel.putClientProperty(KEY_IMAGE, icon.getImage());
                        imageLabel.setText(null);
                        scaleImageForLabel(imageLabel);
                    } else {
                        imageLabel.putClientProperty(KEY_IMAGE, null);
                        imageLabel.setIcon(null);
                        imageLabel.setText("No Image");
                        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    }
                } else {
                    imageLabel.putClientProperty(KEY_IMAGE, null);
                    imageLabel.setIcon(null);
                    imageLabel.setText("No Image");
                    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                }
            } else {
                imageLabel.putClientProperty(KEY_IMAGE, null);
                imageLabel.setIcon(null);
                imageLabel.setText("No Image");
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
        } catch (Exception e) {
            imageLabel.putClientProperty(KEY_IMAGE, null);
            imageLabel.setIcon(null);
            imageLabel.setText("Error");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    /** Default width when label not yet laid out (e.g. off-screen in scroll) */
    private static final int FALLBACK_IMAGE_WIDTH = 280;

    /** Scales the stored image to the label's current size (full width of grid cell) */
    private void scaleImageForLabel(JLabel imageLabel) {
        Image img = (Image) imageLabel.getClientProperty(KEY_IMAGE);
        if (img == null) return;
        int w = imageLabel.getWidth();
        int h = imageLabel.getHeight();
        // Labels not yet laid out (e.g. below fold in scroll) have size 0; use fallback so icon is set
        if (w <= 0 || h <= 0) {
            w = FALLBACK_IMAGE_WIDTH;
            h = IMAGE_HEIGHT;
        }
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));
    }

    /**
     * Retourne l'attraction sélectionnée ou null si aucune sélection valide
     */
    public Attraction getSelectedAttraction() {
        Object sel = comboAttractions.getSelectedItem();
        if (sel == null) return null;
        String name = sel.toString();
        if (name.equals("-- Sélectionner --")) return null;
        return gestionnaireParc.getAttractionParNom(name);
    }
    
    /**
     * Retourne l'état avec emoji
     */
    private String getEtatCouleur(EtatAttraction etat) {
        switch (etat) {
            case OPERATIONNELLE:
                return "Opérationnelle";
            case MAINTENANCE:
                return "Maintenance";
            case PANNE:
                return "Panne";
            case FERMEE:
                return "Fermée";
            default:
                return etat.toString();
        }
    }
}