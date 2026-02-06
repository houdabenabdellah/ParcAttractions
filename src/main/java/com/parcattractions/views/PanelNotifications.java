package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import main.java.com.parcattractions.controllers.SystemeNotifications;
import main.java.com.parcattractions.utils.Notification;
import resources.styles.UIStyles;

/**
 * Panel des notifications - Stylisé
 */
public class PanelNotifications extends JPanel {
    
    private final JTextArea textArea;
    private final SystemeNotifications systemeNotifications;
    
    public PanelNotifications() {
        this.systemeNotifications = SystemeNotifications.getInstance();
        
        setBackground(Color.WHITE);
        setBorder(UIStyles.createStyledBorder("Notifications"));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            UIStyles.createStyledBorder("Notifications"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        textArea = new JTextArea(15, 30);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        textArea.setBackground(UIStyles.BG_LIGHT);
        textArea.setForeground(UIStyles.TEXT_PRIMARY);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1));
        scrollPane.setBackground(UIStyles.BG_LIGHT);

        // Panel de contrôle
JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
controlPanel.setBackground(Color.WHITE);

JButton btnRafraichir = new JButton("Rafraichir");
btnRafraichir.setToolTipText("Rafraîchir les notifications");
btnRafraichir.setFont(new Font("Segoe UI", Font.BOLD, 10));
btnRafraichir.setBackground(UIStyles.INFO_COLOR);
btnRafraichir.setForeground(Color.WHITE);
btnRafraichir.setFocusPainted(false);
btnRafraichir.setPreferredSize(new Dimension(40, 25));
btnRafraichir.addActionListener(e -> rafraichir());

JButton btnEffacer = new JButton("Effacer");
btnEffacer.setToolTipText("Effacer toutes les notifications");
btnEffacer.setFont(new Font("Segoe UI", Font.BOLD, 10));
btnEffacer.setBackground(UIStyles.DANGER_COLOR);
btnEffacer.setForeground(Color.WHITE);
btnEffacer.setFocusPainted(false);
btnEffacer.setPreferredSize(new Dimension(40, 25));
btnEffacer.addActionListener(e -> {
    int result = JOptionPane.showConfirmDialog(this,
        "Voulez-vous effacer toutes les notifications?",
        "Confirmation",
        JOptionPane.YES_NO_OPTION);
    if (result == JOptionPane.YES_OPTION) {
        systemeNotifications.viderHistorique();
        rafraichir();
    }
});

JLabel labelCount = new JLabel("(20 dernières)");
labelCount.setFont(new Font("Segoe UI", Font.ITALIC, 9));
labelCount.setForeground(UIStyles.TEXT_SECONDARY);

controlPanel.add(btnRafraichir);
controlPanel.add(btnEffacer);
controlPanel.add(Box.createHorizontalStrut(10));
controlPanel.add(labelCount);

add(controlPanel, BorderLayout.NORTH);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
   /**
 * Rafraîchit les notifications affichées - MODE MANUEL
 */
public void rafraichir() {
    var notifications = systemeNotifications.getDernieresNotifications(20);
    
    if (notifications.isEmpty()) {
        textArea.setText(" Aucune notification pour le moment.\n\n" +
                        "Les événements du parc apparaîtront ici.");
        textArea.setForeground(UIStyles.TEXT_SECONDARY);
    } else {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== %d notification(s) ===\n\n", 
                               notifications.size()));
        
        for (Notification notif : notifications) {
            sb.append(notif.getMessageComplet()).append("\n");
        }
        
        textArea.setText(sb.toString());
        textArea.setForeground(UIStyles.TEXT_PRIMARY);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
}
