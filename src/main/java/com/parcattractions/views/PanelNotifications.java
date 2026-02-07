package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.SystemeNotifications;
import main.java.com.parcattractions.resources.styles.UIStyles;
import main.java.com.parcattractions.utils.Notification;

public class PanelNotifications extends JPanel {
    private final JTextArea textArea;
    private final SystemeNotifications systemeNotifications;

    public PanelNotifications() {
        this.systemeNotifications = SystemeNotifications.getInstance();
        
        setBackground(UIStyles.BG_WINDOW);
        setLayout(new BorderLayout(10, 10));
        setBorder(UIStyles.createStyledBorder("Système d'Alertes"));

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setFont(UIStyles.MONOSPACE_FONT);
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(new javax.swing.border.LineBorder(UIStyles.BORDER_COLOR));
        scroll.getViewport().setBackground(Color.WHITE);

        // --- Contrôles de Notifications ---
        JPanel control = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        control.setOpaque(false);

        JButton clearBtn = new JButton("Effacer Log");
        UIStyles.styleAccentButton(clearBtn); // Couleur Rosewood moderne
        clearBtn.addActionListener(e -> { 
            systemeNotifications.viderHistorique(); 
            rafraichir(); 
        });

        control.add(new JLabel("Mise à jour en direct"));
        control.add(clearBtn);

        add(control, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        
        rafraichir();
    }

    public void rafraichir() {
        var notes = systemeNotifications.getDernieresNotifications(20);
        if (notes.isEmpty()) {
            textArea.setText("\n   >> Système opérationnel. Aucune alerte active.");
            textArea.setForeground(UIStyles.TEXT_PRIMARY);
        } else {
            StringBuilder sb = new StringBuilder();
            for (Notification n : notes) {
                sb.append(" [!] ").append(n.getMessageComplet()).append("\n");
            }
            textArea.setText(sb.toString());
            textArea.setForeground(UIStyles.DUSTY_LAVENDER);
        }
    }
}