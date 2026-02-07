package main.java.com.parcattractions.resources.styles;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class UIStyles {
    // Nouvelle palette Fancy
    public static final Color DUSK_BLUE = new Color(0x355070);      // Couleur Primaire
    public static final Color DUSTY_LAVENDER = new Color(0x6D597A); // Secondaire
    public static final Color ROSEWOOD = new Color(0xB56576);       // Accent (Boutons action)
    public static final Color LIGHT_CORAL = new Color(0xE56B6F);    // Alertes / Panne
    public static final Color LIGHT_BRONZE = new Color(0xEAAC8B);   // Succès / Bronze soft

    // Backgrounds & Textes
    public static final Color BG_WINDOW = new Color(0xF2F4F7);      // Fond de fenêtre gris très clair
    public static final Color BG_CARD = Color.WHITE;                // Fond des cartes
    public static final Color TEXT_PRIMARY = new Color(0x2B2D42);
    public static final Color BORDER_COLOR = new Color(0xD8E2DC);

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font MONOSPACE_FONT = new Font("Consolas", Font.PLAIN, 12);

    /** Bordure Fancy pour les panneaux */
    public static Border createStyledBorder(String title) {
        Border line = new LineBorder(BORDER_COLOR, 1, true); // Bords arrondis
        TitledBorder titled = BorderFactory.createTitledBorder(
            line, "  " + title.toUpperCase() + "  ", 
            TitledBorder.LEFT, TitledBorder.TOP, 
            HEADER_FONT, DUSTY_LAVENDER
        );
        return BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createCompoundBorder(titled, BorderFactory.createEmptyBorder(10, 10, 10, 10))
        );
    }

    public static void stylePrimaryButton(JButton b) {
        b.setFont(HEADER_FONT); b.setBackground(DUSK_BLUE); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleSecondaryButton(JButton b) {
        b.setFont(HEADER_FONT); b.setBackground(LIGHT_BRONZE); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    public static void styleAccentButton(JButton b) {
        b.setFont(HEADER_FONT); b.setBackground(ROSEWOOD); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }
    
    public static Color getStateColor(String state) {
        String s = state.toLowerCase();
        if (s.contains("opérationnel") || s.contains("ouvert")) return LIGHT_BRONZE;
        if (s.contains("panne") || s.contains("fermé")) return LIGHT_CORAL;
        return DUSTY_LAVENDER;
    }
}