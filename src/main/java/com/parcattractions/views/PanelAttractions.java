package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.enums.EtatAttraction;
import main.java.com.parcattractions.models.attractions.Attraction;

/**
 * Panel affichant les attractions - Stylisé
 */
public class PanelAttractions extends JPanel {
    
    private final GestionnaireParc gestionnaireParc;
    private JLabel[] labelsAttractions;
    private JComboBox<String> comboAttractions;
    
    public PanelAttractions(GestionnaireParc gestionnaireParc) {
        this.gestionnaireParc = gestionnaireParc;
        
        setBackground(Color.WHITE);
        setBorder(UIStyles.createStyledBorder("Attractions"));
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createCompoundBorder(
            UIStyles.createStyledBorder("Attractions"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        labelsAttractions = new JLabel[8];
        comboAttractions = new JComboBox<>();
        comboAttractions.addItem("-- Sélectionner --");
        comboAttractions.setFont(UIStyles.REGULAR_FONT);
        comboAttractions.setBackground(Color.WHITE);

        // Grid panel for labels
        JPanel grid = new JPanel(new GridLayout(4, 2, 10, 10));
        grid.setBackground(Color.WHITE);
        
        for (int i = 0; i < 8; i++) {
            labelsAttractions[i] = new JLabel("--");
            labelsAttractions[i].setFont(UIStyles.REGULAR_FONT);
            labelsAttractions[i].setForeground(UIStyles.TEXT_PRIMARY);
            labelsAttractions[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            labelsAttractions[i].setOpaque(true);
            labelsAttractions[i].setBackground(UIStyles.BG_LIGHT);
            grid.add(labelsAttractions[i]);
        }

        add(comboAttractions, BorderLayout.NORTH);
        add(grid, BorderLayout.CENTER);
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
            String texte = String.format("%s [%s] File: %d", 
                attraction.getNom(),
                getEtatCouleur(attraction.getEtat()),
                attraction.getTailleFileTotal());
            labelsAttractions[i].setText(texte);
            labelsAttractions[i].setForeground(UIStyles.getStateColor(getEtatCouleur(attraction.getEtat())));
        }
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
