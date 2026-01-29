package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import main.java.com.parcattractions.controllers.SystemeNotifications;
import main.java.com.parcattractions.utils.Notification;

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
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Rafraîchit les notifications affichées
     */
    public void rafraichir() {
        var notifications = systemeNotifications.getDernieresNotifications(20);
        
        StringBuilder sb = new StringBuilder();
        for (Notification notif : notifications) {
            sb.append(notif.getMessageComplet()).append("\n");
        }
        
        textArea.setText(sb.toString());
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
