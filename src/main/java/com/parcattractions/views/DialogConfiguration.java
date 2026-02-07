package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import main.java.com.parcattractions.resources.styles.UIStyles;


public class DialogConfiguration extends JDialog {
    
    public DialogConfiguration(JFrame parent) {
        super(parent, "Paramètres Système", true);
        setSize(500, 350);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_WINDOW);
        buildUI();
    }
    
    private void buildUI() {
        setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("CONFIGURATION");
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.DUSK_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(UIStyles.createStyledBorder("Maintenance & Préférences"));
        
        JLabel msg = new JLabel("<html><center>Le module de configuration avancée est<br>en cours de maintenance technique.</center></html>");
        msg.setFont(UIStyles.REGULAR_FONT);
        msg.setForeground(UIStyles.DUSTY_LAVENDER);
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(msg, BorderLayout.CENTER);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
        wrapper.add(content, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Fermer");
        UIStyles.stylePrimaryButton(closeBtn);
        closeBtn.addActionListener(e -> dispose());
        
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }
}