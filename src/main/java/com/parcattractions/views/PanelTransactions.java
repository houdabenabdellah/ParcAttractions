package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.utils.Logger;
import main.java.com.parcattractions.utils.TransactionManager;
import resources.styles.UIStyles;

/**
 * Panneau pour afficher les statistiques financières et transactions
 * Affichage en temps-réel des revenus et rapports
 */
public class PanelTransactions extends JPanel {
    
    private JLabel billetLabel;
    private JLabel restaurantLabel;
    private JLabel souvenirLabel;
    private JLabel totalLabel;
    private JLabel nbTransactionsLabel;
    
    private JTextArea rapportArea;
    private Timer updateTimer;
    
    public PanelTransactions() {
        setBackground(UIStyles.BG_LIGHT);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        buildUI();
        
        // Mise à jour automatique chaque seconde
        updateTimer = new Timer(1000, e -> updateAffichage());
        updateTimer.start();
    }
    
    private void buildUI() {
        // Panneau supérieur - Titre
        JLabel titleLabel = new JLabel("Transactions & Finances");
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIStyles.BG_LIGHT);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Panneau central - Statistiques et rapports
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBackground(UIStyles.BG_LIGHT);
        
        // Volet gauche - Statistiques
        JPanel statsPanel = createStatsPanel();
        centerPanel.add(statsPanel);
        
        // Volet droit - Rapport détaillé
        JPanel rapportPanel = createRapportPanel();
        centerPanel.add(rapportPanel);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    /**
     * Crée le panneau des statistiques
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel statsTitle = new JLabel("STATISTIQUES FINANCIÈRES");
        statsTitle.setFont(UIStyles.BOLD_FONT);
        statsTitle.setForeground(UIStyles.TEXT_PRIMARY);
        panel.add(statsTitle);
        panel.add(Box.createVerticalStrut(15));
        
        // Billets
        billetLabel = new JLabel("Billets: 0.00€ (0 transactions)");
        billetLabel.setFont(UIStyles.REGULAR_FONT);
        billetLabel.setForeground(UIStyles.INFO_COLOR);
        panel.add(billetLabel);
        panel.add(Box.createVerticalStrut(10));
        
        // Restaurant
        restaurantLabel = new JLabel("Restaurant: 0.00€ (0 transactions)");
        restaurantLabel.setFont(UIStyles.REGULAR_FONT);
        restaurantLabel.setForeground(UIStyles.WARNING_COLOR);
        panel.add(restaurantLabel);
        panel.add(Box.createVerticalStrut(10));
        
        // Souvenirs
        souvenirLabel = new JLabel("Souvenirs: 0.00€ (0 transactions)");
        souvenirLabel.setFont(UIStyles.REGULAR_FONT);
        souvenirLabel.setForeground(UIStyles.SUCCESS_COLOR);
        panel.add(souvenirLabel);
        panel.add(Box.createVerticalStrut(15));
        
        // Séparateur
        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
        panel.add(Box.createVerticalStrut(15));
        
        // Total
        totalLabel = new JLabel("TOTAL: 0.00€");
        totalLabel.setFont(UIStyles.HEADER_FONT);
        totalLabel.setForeground(UIStyles.PRIMARY_COLOR);
        panel.add(totalLabel);
        panel.add(Box.createVerticalStrut(10));
        
        // Nombre total de transactions
        nbTransactionsLabel = new JLabel("Transactions: 0");
        nbTransactionsLabel.setFont(UIStyles.REGULAR_FONT);
        nbTransactionsLabel.setForeground(UIStyles.TEXT_SECONDARY);
        panel.add(nbTransactionsLabel);
        
        panel.add(Box.createVerticalGlue());
        
        // Boutons d'action
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton exportBtn = new JButton("Exporter Rapport");
        exportBtn.setFont(UIStyles.HEADER_FONT);
        exportBtn.setBackground(UIStyles.INFO_COLOR);
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        exportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportBtn.addActionListener(e -> exporterRapport());
        buttonPanel.add(exportBtn);

        JButton rafraichirBtn = new JButton(" Rafraîchir");
rafraichirBtn.setFont(UIStyles.HEADER_FONT);
rafraichirBtn.setBackground(UIStyles.SUCCESS_COLOR);
rafraichirBtn.setForeground(Color.WHITE);
rafraichirBtn.setFocusPainted(false);
rafraichirBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
rafraichirBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
rafraichirBtn.addActionListener(e -> {
    updateAffichage();
    JOptionPane.showMessageDialog(this, 
        "Affichage rafraîchi!", 
        "Rafraîchissement", 
        JOptionPane.INFORMATION_MESSAGE);
});
        
        JButton reinitBtn = new JButton("Réinitialiser");
        reinitBtn.setFont(UIStyles.HEADER_FONT);
        reinitBtn.setBackground(UIStyles.DANGER_COLOR);
        reinitBtn.setForeground(Color.WHITE);
        reinitBtn.setFocusPainted(false);
        reinitBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        reinitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reinitBtn.addActionListener(e -> reinitialiserTransactions());
        buttonPanel.add(reinitBtn);
        
        panel.add(Box.createVerticalStrut(15));
        panel.add(buttonPanel);
        
        return panel;
    }
    
    /**
     * Crée le panneau du rapport détaillé
     */
    private JPanel createRapportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel rapportTitle = new JLabel("RAPPORT DÉTAILLÉ");
        rapportTitle.setFont(UIStyles.BOLD_FONT);
        rapportTitle.setForeground(UIStyles.TEXT_PRIMARY);
        panel.add(rapportTitle, BorderLayout.NORTH);
        
        rapportArea = new JTextArea();
        rapportArea.setFont(UIStyles.MONOSPACE_FONT);
        rapportArea.setEditable(false);
        rapportArea.setBackground(UIStyles.BG_LIGHT);
        rapportArea.setForeground(UIStyles.TEXT_PRIMARY);
        rapportArea.setText("En attente de données...");
        
        JScrollPane scrollPane = new JScrollPane(rapportArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Met à jour l'affichage des statistiques
     */
    private void updateAffichage() {
        SwingUtilities.invokeLater(() -> {
            // Mise à jour des labels
            billetLabel.setText(String.format("Billets: %.2f€ (%d transactions)", 
                TransactionManager.getRevenuBillets(), 
                TransactionManager.getNbTransactionsBillets()));
            
            restaurantLabel.setText(String.format("Restaurant: %.2f€ (%d transactions)", 
                TransactionManager.getRevenuRestaurant(), 
                TransactionManager.getNbTransactionsRestaurant()));
            
            souvenirLabel.setText(String.format("Souvenirs: %.2f€ (%d transactions)", 
                TransactionManager.getRevenuSouvenirs(), 
                TransactionManager.getNbTransactionsSouvenirs()));
            
            totalLabel.setText(String.format("TOTAL: %.2f€", 
                TransactionManager.getRevenuTotal()));
            
            nbTransactionsLabel.setText(String.format("Transactions: %d", 
                TransactionManager.getNbTransactions()));
            
            // Mise à jour du rapport
            rapportArea.setText(TransactionManager.genererRapportFinancier() + 
                                TransactionManager.genererRapportDetaille());
            rapportArea.setCaretPosition(0);
        });
    }
    
    /**
     * Exporte le rapport dans une boîte de dialogue
     */
    private void exporterRapport() {
        String rapport = TransactionManager.genererRapportFinancier() + 
                        TransactionManager.genererRapportDetaille();
        
        JTextArea textArea = new JTextArea(rapport);
        textArea.setEditable(false);
        textArea.setFont(UIStyles.MONOSPACE_FONT);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Rapport Complet des Transactions", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Réinitialise les transactions (nouvelle session)
     */
    private void reinitialiserTransactions() {
        int result = JOptionPane.showConfirmDialog(this,
        " ATTENTION \n\n" +
        "Cette action va réinitialiser TOUTES les statistiques financières!\n\n" +
        "Les données suivantes seront perdues:\n" +
        "• Revenus des billets\n" +
        "• Revenus des restaurants\n" +
        "• Revenus des souvenirs\n" +
        "• Toutes les transactions enregistrées\n\n" +
        "Voulez-vous vraiment continuer?",
        "Confirmation de Réinitialisation", 
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);
    
    if (result == JOptionPane.YES_OPTION) {
        // Double confirmation pour une action critique
        int confirm2 = JOptionPane.showConfirmDialog(this,
            "Dernière confirmation:\n" +
            "Êtes-vous ABSOLUMENT sûr de vouloir réinitialiser?",
            "Confirmation finale", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm2 == JOptionPane.YES_OPTION) {
            TransactionManager.reinitialiserStatistiques();
            updateAffichage();
            JOptionPane.showMessageDialog(this, 
                "Statistiques réinitialisées avec succès!\n\n" +
                "Les compteurs sont remis à zéro.", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            Logger.logInfo("Statistiques réinitialisées par l'utilisateur en mode manuel");
        } else {
            Logger.logInfo("Réinitialisation annulée (2ème confirmation)");
        }
    } else {
        Logger.logInfo("Réinitialisation annulée");
    }
    }
    
    /**
     * Arrête la mise à jour automatique
     */
    public void arreterMiseAJour() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }
    /**
 * Arrête le timer automatique (mode manuel total)
 */
public void stopAutoUpdate() {
    if (updateTimer != null && updateTimer.isRunning()) {
        updateTimer.stop();
        Logger.logInfo("Mise à jour automatique des transactions arrêtée");
    }
}

/**
 * Démarre le timer automatique
 */
public void startAutoUpdate() {
    if (updateTimer != null && !updateTimer.isRunning()) {
        updateTimer.start();
        Logger.logInfo("Mise à jour automatique des transactions démarrée");
    }
}

/**
 * Bascule entre auto/manuel
 */
public void toggleAutoUpdate() {
    if (updateTimer != null) {
        if (updateTimer.isRunning()) {
            stopAutoUpdate();
        } else {
            startAutoUpdate();
        }
    }
}

/**
 * Rafraîchit manuellement (appel externe)
 */
public void rafraichirManuel() {
    updateAffichage();
}
}
