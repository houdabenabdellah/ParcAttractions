package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.resources.styles.UIStyles;
import main.java.com.parcattractions.utils.Horloge;


public class DialogConfiguration extends JDialog {
    
    private final GestionnaireParc gp;
    
    public DialogConfiguration(JFrame parent, GestionnaireParc gp) {
        super(parent, "Paramètres Système", true);
        this.gp = gp;
        setSize(500, 380);
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
        
        JPanel content = new JPanel(new GridLayout(2, 1, 0, 15));
        content.setBackground(UIStyles.BG_WINDOW);
        
        // Bloc Simulation (contrôle du temps)
        JPanel simPanel = new JPanel(new BorderLayout());
        simPanel.setBackground(Color.WHITE);
        simPanel.setBorder(UIStyles.createStyledBorder("Simulation temps réel"));
        JPanel simInner = new JPanel(new GridLayout(2, 1, 5, 10));
        simInner.setBackground(Color.WHITE);
        simInner.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel infoTime = new JLabel("Fermeture automatique du parc à " + Horloge.getHeureFermeture().toString() + ".");
        infoTime.setFont(UIStyles.REGULAR_FONT);
        JLabel speedLabel = new JLabel("Vitesse : minutes simulées par seconde réelle");
        speedLabel.setFont(UIStyles.REGULAR_FONT);
        Integer[] speeds = { 1, 5, 10, 30 };
        JComboBox<Integer> speedCombo = new JComboBox<>(speeds);
        speedCombo.setFont(UIStyles.REGULAR_FONT);
        Horloge h = gp.getHorloge();
        if (h != null && !h.isTempsReel()) {
            int v = h.getVitesseSimulation();
            speedCombo.setSelectedItem(v);
            if (speedCombo.getSelectedItem() == null) speedCombo.setSelectedIndex(0);
        }
        speedCombo.addActionListener(e -> {
            Horloge horloge = gp.getHorloge();
            if (horloge != null && !horloge.isTempsReel()) {
                Integer sel = (Integer) speedCombo.getSelectedItem();
                if (sel != null) horloge.setVitesseSimulation(sel);
            }
        });
        simInner.add(infoTime);
        JPanel rowSpeed = new JPanel(new BorderLayout(10, 0));
        rowSpeed.setOpaque(false);
        rowSpeed.add(speedLabel, BorderLayout.NORTH);
        rowSpeed.add(speedCombo, BorderLayout.CENTER);
        simInner.add(rowSpeed);
        simPanel.add(simInner, BorderLayout.CENTER);
        content.add(simPanel);
        
        // Bloc Météo / préférences
        JPanel prefPanel = new JPanel(new BorderLayout());
        prefPanel.setBackground(Color.WHITE);
        prefPanel.setBorder(UIStyles.createStyledBorder("Météo & Préférences"));
        JLabel msg = new JLabel("<html><center>Utilisez le menu <b>Météo → Changer la météo</b> pour modifier les conditions.<br>Le parc se ferme automatiquement à 16h00.</center></html>");
        msg.setFont(UIStyles.REGULAR_FONT);
        msg.setForeground(UIStyles.DUSTY_LAVENDER);
        msg.setHorizontalAlignment(SwingConstants.CENTER);
        msg.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        prefPanel.add(msg, BorderLayout.CENTER);
        content.add(prefPanel);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
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