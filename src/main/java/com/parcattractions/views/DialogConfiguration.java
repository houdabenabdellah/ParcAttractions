package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Dialog de configuration - Stylisée
 * (À implémenter selon besoins)
 */
public class DialogConfiguration extends JDialog {
    
    public DialogConfiguration(JFrame parent) {
        super(parent, "Configuration", true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_LIGHT);
        buildUI();
    }
    
    private void buildUI() {
        setLayout(new BorderLayout());
        
        // Titre
        JLabel titleLabel = new JLabel("Configuration de l'Application");
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(titleLabel, BorderLayout.NORTH);
        
        // Contenu
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel messageLabel = new JLabel("Les paramètres de configuration seront bientôt disponibles");
        messageLabel.setFont(UIStyles.REGULAR_FONT);
        messageLabel.setForeground(UIStyles.TEXT_SECONDARY);
        contentPanel.add(messageLabel);
        
        add(contentPanel, BorderLayout.CENTER);
    }
}
