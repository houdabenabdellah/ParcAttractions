package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.resources.styles.UIStyles;
import main.java.com.parcattractions.utils.TransactionManager;

public class PanelTransactions extends JPanel {
    
    private JLabel billetLabel, restaurantLabel, souvenirLabel, totalLabel, nbTransactionsLabel;
    private JTextArea rapportArea;
    private Timer updateTimer;
    
    public PanelTransactions() {
        setBackground(UIStyles.BG_WINDOW);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        buildUI();
        updateTimer = new Timer(1000, e -> updateAffichage());
        updateTimer.start();
    }
    
    private void buildUI() {
        // --- Titre Fancy ---
        JLabel title = new JLabel("MONITEUR FINANCIER");
        title.setFont(UIStyles.TITLE_FONT);
        title.setForeground(UIStyles.DUSK_BLUE);
        add(title, BorderLayout.NORTH);
        
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 25, 0));
        mainContent.setOpaque(false);

        // --- Colonne GAUCHE (Résumé) ---
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(Color.WHITE);
        leftColumn.setBorder(UIStyles.createStyledBorder("Résumé des Flux"));

        totalLabel = createValueRow(leftColumn, "RECETTES GLOBALES", UIStyles.DUSK_BLUE);
        leftColumn.add(new JSeparator());
        billetLabel = createValueRow(leftColumn, "Billeterie", UIStyles.DUSTY_LAVENDER);
        restaurantLabel = createValueRow(leftColumn, "Restauration", UIStyles.LIGHT_BRONZE);
        souvenirLabel = createValueRow(leftColumn, "Boutiques", UIStyles.ROSEWOOD);
        nbTransactionsLabel = createValueRow(leftColumn, "Volume Ventes", UIStyles.DUSK_BLUE);

        // Boutons Action
        JPanel btnGrid = new JPanel(new GridLayout(3, 1, 10, 10));
        btnGrid.setOpaque(false);
        JButton exp = new JButton("Exporter (.CSV)"); UIStyles.stylePrimaryButton(exp);
        JButton rfr = new JButton("Rafraîchir"); UIStyles.styleSecondaryButton(rfr);
        JButton rst = new JButton("Réinitialiser Session"); UIStyles.styleAccentButton(rst);

        leftColumn.add(Box.createVerticalGlue());
        leftColumn.add(btnGrid);
        btnGrid.add(exp); btnGrid.add(rfr); btnGrid.add(rst);

        // --- Colonne DROITE (Journal) ---
        JPanel rightColumn = new JPanel(new BorderLayout());
        rightColumn.setBackground(Color.WHITE);
        rightColumn.setBorder(UIStyles.createStyledBorder("Historique Détaillé"));

        rapportArea = new JTextArea();
        rapportArea.setFont(UIStyles.MONOSPACE_FONT);
        rapportArea.setEditable(false);
        rapportArea.setBackground(new Color(252, 252, 252));
        
        JScrollPane scroll = new JScrollPane(rapportArea);
        scroll.setBorder(null);
        rightColumn.add(scroll, BorderLayout.CENTER);

        mainContent.add(leftColumn);
        mainContent.add(rightColumn);
        add(mainContent, BorderLayout.CENTER);
    }

    private JLabel createValueRow(JPanel p, String title, Color col) {
        JLabel lTitle = new JLabel(title);
        lTitle.setFont(UIStyles.REGULAR_FONT);
        lTitle.setForeground(new Color(128, 128, 128));
        
        JLabel lVal = new JLabel("0.00 €");
        lVal.setFont(UIStyles.HEADER_FONT);
        lVal.setForeground(col);
        
        p.add(lTitle); p.add(lVal);
        p.add(Box.createVerticalStrut(15));
        return lVal;
    }

    private void updateAffichage() {
        totalLabel.setText(String.format("%.2f €", TransactionManager.getRevenuTotal()));
        billetLabel.setText(String.format("%.2f €", TransactionManager.getRevenuBillets()));
        restaurantLabel.setText(String.format("%.2f €", TransactionManager.getRevenuRestaurant()));
        souvenirLabel.setText(String.format("%.2f €", TransactionManager.getRevenuSouvenirs()));
        nbTransactionsLabel.setText(TransactionManager.getNbTransactions() + " opérations");
        rapportArea.setText(TransactionManager.genererRapportDetaille());
    }
}