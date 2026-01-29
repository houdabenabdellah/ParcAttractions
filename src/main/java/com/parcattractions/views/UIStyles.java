package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;

/**
 * Classe pour gérer les styles et couleurs de l'interface utilisateur
 */
public class UIStyles {
    
    // Palette de couleurs modernes
    public static final Color PRIMARY_COLOR = new Color(26, 118, 188);      // Bleu professionnel
    public static final Color SECONDARY_COLOR = new Color(108, 52, 131);    // Violet
    public static final Color ACCENT_COLOR = new Color(255, 107, 53);       // Orange vif
    public static final Color SUCCESS_COLOR = new Color(46, 184, 92);       // Vert succès
    public static final Color WARNING_COLOR = new Color(243, 156, 18);      // Orange avertissement
    public static final Color DANGER_COLOR = new Color(231, 76, 60);        // Rouge danger
    public static final Color INFO_COLOR = new Color(52, 152, 219);         // Bleu info
    
    // Couleurs de background
    public static final Color BG_DARK = new Color(22, 26, 34);              // Fond très sombre
    public static final Color BG_CARD = new Color(33, 40, 54);              // Fond card
    public static final Color BG_LIGHT = new Color(248, 249, 250);          // Fond clair
    
    // Couleurs de texte
    public static final Color TEXT_PRIMARY = new Color(33, 40, 54);         // Texte principal
    public static final Color TEXT_SECONDARY = new Color(100, 120, 140);    // Texte secondaire
    public static final Color TEXT_LIGHT = Color.WHITE;                     // Texte sur fond sombre
    
    // Couleurs spéciales
    public static final Color GRID_COLOR = new Color(220, 224, 230);
    public static final Color BORDER_COLOR = new Color(200, 210, 220);
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 10);
    public static final Font MONOSPACE_FONT = new Font("Courier New", Font.PLAIN, 11);
    
    /**
     * Applique un style moderne à un JLabel pour les titres
     */
    public static void styleTitleLabel(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_COLOR);
    }
    
    /**
     * Applique un style à un JLabel pour les en-têtes
     */
    public static void styleHeaderLabel(JLabel label) {
        label.setFont(HEADER_FONT);
        label.setForeground(PRIMARY_COLOR);
    }
    
    /**
     * Applique un style à un JLabel pour les données principales
     */
    public static void styleDataLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
    }
    
    /**
     * Applique un style à un JLabel pour les petites données
     */
    public static void styleSmallLabel(JLabel label) {
        label.setFont(REGULAR_FONT);
        label.setForeground(TEXT_SECONDARY);
    }
    
    /**
     * Applique un style moderne à un JButton
     */
    public static void stylePrimaryButton(JButton button) {
        button.setFont(HEADER_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(TEXT_LIGHT);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }
    
    /**
     * Applique un style moderne à un JButton secondaire
     */
    public static void styleSecondaryButton(JButton button) {
        button.setFont(REGULAR_FONT);
        button.setBackground(BG_LIGHT);
        button.setForeground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
    }
    
    /**
     * Applique un style moderne à un JButton de couleur accent
     */
    public static void styleAccentButton(JButton button) {
        button.setFont(HEADER_FONT);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(TEXT_LIGHT);
        button.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
    }
    
    /**
     * Applique un style modern à un JPanel pour les cartes
     */
    public static void styleCardPanel(JPanel panel) {
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
    }
    
    /**
     * Applique un style modern à un JPanel pour les sections
     */
    public static void styleSectionPanel(JPanel panel) {
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    /**
     * Crée un border titré stylisé
     */
    public static javax.swing.border.Border createStyledBorder(String title) {
        javax.swing.border.TitledBorder titledBorder = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            title,
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            HEADER_FONT,
            PRIMARY_COLOR
        );
        return titledBorder;
    }
    
    /**
     * Applique un style moderne à un JPanel avec border titré
     */
    public static void stylePanel(JPanel panel, String title) {
        panel.setBackground(Color.WHITE);
        panel.setBorder(createStyledBorder(title));
    }
    
    /**
     * Retourne une couleur basée sur l'état (succès, warning, danger)
     */
    public static Color getStateColor(String state) {
        if (state.toLowerCase().contains("opérationnel") || state.toLowerCase().contains("ok")) {
            return SUCCESS_COLOR;
        } else if (state.toLowerCase().contains("maintenance") || state.toLowerCase().contains("warning")) {
            return WARNING_COLOR;
        } else if (state.toLowerCase().contains("panne") || state.toLowerCase().contains("error")) {
            return DANGER_COLOR;
        } else if (state.toLowerCase().contains("fermé") || state.toLowerCase().contains("closed")) {
            return TEXT_SECONDARY;
        }
        return INFO_COLOR;
    }
}
