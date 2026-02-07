package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import main.java.com.parcattractions.models.services.Boutique;
import main.java.com.parcattractions.models.services.Produit;
import main.java.com.parcattractions.models.services.ServiceManager;
import main.java.com.parcattractions.models.visiteurs.Visiteur;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.resources.styles.UIStyles;

/**
 * Dialog pour la gestion et consultation des boutiques
 */
public class DialogGestionBoutiques extends JDialog {
    
    private final ServiceManager serviceManager;
    private final GestionnaireParc gestionnaireParc;
    private JTable tableBoutiques;
    private DefaultTableModel modelBoutiques;

    public DialogGestionBoutiques(JFrame parent, ServiceManager serviceManager, 
                                   GestionnaireParc gestionnaireParc) {
        super(parent, "Gestion des Boutiques", true);
        this.serviceManager = serviceManager;
        this.gestionnaireParc = gestionnaireParc;
        setSize(900, 600);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_WINDOW);
        buildUI();
        rafraichir();
    }

    private void buildUI() {
        setLayout(new BorderLayout(20, 20));

        JLabel title = new JLabel("GESTION DES BOUTIQUES DE SOUVENIRS");
        title.setFont(UIStyles.TITLE_FONT);
        title.setForeground(UIStyles.DUSK_BLUE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        modelBoutiques = new DefaultTableModel(
            new String[]{"BOUTIQUE", "PRODUITS", "STOCK FAIBLE", "ÉPUISÉS", "REVENUS", "STATUT"}, 0);
        tableBoutiques = new JTable(modelBoutiques);
        tableBoutiques.setRowHeight(30);
        tableBoutiques.setFont(UIStyles.REGULAR_FONT);
        tableBoutiques.getTableHeader().setBackground(UIStyles.DUSK_BLUE);
        tableBoutiques.getTableHeader().setForeground(Color.WHITE);
        tableBoutiques.getTableHeader().setFont(UIStyles.HEADER_FONT);

        JScrollPane scroll = new JScrollPane(tableBoutiques);
        scroll.setBorder(UIStyles.createStyledBorder("État des Boutiques"));
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttons.setOpaque(false);

        JButton stockBtn = new JButton("Voir Stock");
        UIStyles.styleSecondaryButton(stockBtn);
        stockBtn.addActionListener(e -> logicAfficherStock());

        JButton acheterBtn = new JButton("Acheter Produit");
        UIStyles.stylePrimaryButton(acheterBtn);
        acheterBtn.addActionListener(e -> logicAcheter());

        JButton revenusBtn = new JButton("Revenus Totaux");
        UIStyles.styleAccentButton(revenusBtn);
        revenusBtn.addActionListener(e -> afficherRecapitulatifRevenus());

        JButton close = new JButton("Fermer");
        UIStyles.stylePrimaryButton(close);
        close.addActionListener(e -> dispose());

        buttons.add(stockBtn);
        buttons.add(acheterBtn);
        buttons.add(revenusBtn);
        buttons.add(close);
        add(buttons, BorderLayout.SOUTH);
    }

    private void rafraichir() {
        modelBoutiques.setRowCount(0);
        for (Boutique boutique : serviceManager.getBoutiques()) {
            int totalProduits = boutique.getStock().size();
            int stockFaible = boutique.getProduitsStockFaible().size();
            int epuises = boutique.getProduitsEpuises().size();
            double revenus = boutique.getRevenus();
            String statut = (epuises < totalProduits) ? "Ouverte" : "Fermée (épuisée)";
            
            modelBoutiques.addRow(new Object[]{
                boutique.getNom(),
                totalProduits,
                stockFaible,
                epuises,
                String.format("%.2f €", revenus),
                statut
            });
        }
    }

    private void logicAfficherStock() {
        int r = tableBoutiques.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une boutique", "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nomBoutique = (String) modelBoutiques.getValueAt(r, 0);
        Boutique boutique = serviceManager.getBoutiqueParNom(nomBoutique);
        if (boutique == null) return;
        if (!boutique.isOuverte()) {
            JOptionPane.showMessageDialog(this, "La boutique est fermée", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("   STOCK - ").append(nomBoutique.toUpperCase()).append("\n");
        sb.append("═══════════════════════════════════════════\n\n");

        boutique.getStock().forEach((produit, quantite) -> {
            String status = quantite <= 0 ? "[ÉPUISÉ]" : 
                           quantite <= 5 ? "[STOCK FAIBLE]" : "[OK]";
            sb.append(String.format("%-30s %3d unités  %s\n", 
                produit.getNom(), quantite, status));
        });

        sb.append("\n═══════════════════════════════════════════");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(this, scroll, "Stock - " + nomBoutique, 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void logicAcheter() {
        int r = tableBoutiques.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une boutique", "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nomBoutique = (String) modelBoutiques.getValueAt(r, 0);
        Boutique boutique = serviceManager.getBoutiqueParNom(nomBoutique);
        if (boutique == null) return;

        // Sélectionner visiteur
        java.util.List<Visiteur> visiteurs = gestionnaireParc.getVisiteurs();
        if (visiteurs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun visiteur en parc", "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] nomsVisiteurs = visiteurs.stream()
            .map(Visiteur::getNomVisiteur)
            .toArray(String[]::new);

        String nomVisiteur = (String) JOptionPane.showInputDialog(this, 
            "Sélectionner un visiteur:", nomBoutique, JOptionPane.QUESTION_MESSAGE, null, 
            nomsVisiteurs, nomsVisiteurs[0]);

        if (nomVisiteur == null) return;

        Visiteur visiteur = gestionnaireParc.getVisiteurParNom1(nomVisiteur);
        if (visiteur == null) return;

        // Sélectionner produit
        java.util.Map<Produit, Integer> stock = boutique.getStock();
        java.util.List<Produit> produitsDisponibles = stock.entrySet()
            .stream()
            .filter(e -> e.getValue() > 0)
            .map(java.util.Map.Entry::getKey)
            .toList();

        if (produitsDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun produit disponible", "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Produit[] produits = produitsDisponibles.toArray(new Produit[0]);
        Produit selectedProduit = (Produit) JOptionPane.showInputDialog(this, 
            "Sélectionner un produit:", "Produits disponibles", JOptionPane.QUESTION_MESSAGE, 
            null, produits, produits[0]);

        if (selectedProduit == null) return;

        // Confirmation
        int conf = JOptionPane.showConfirmDialog(this, String.format(
            "Confirmer l'achat de %s pour %s (%.2f €) ?", selectedProduit.getNom(), visiteur.getNomVisiteur(), selectedProduit.getPrix()),
            "Confirmer achat", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        String resultat = serviceManager.effectuerAchatBoutique(
            visiteur, nomBoutique, selectedProduit);

        JOptionPane.showMessageDialog(this, resultat, "Achat", 
            JOptionPane.INFORMATION_MESSAGE);
        rafraichir();
    }

    private void afficherRecapitulatifRevenus() {
        String recap = serviceManager.genererRecapitulatifRevenus();
        JTextArea textArea = new JTextArea(recap);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(600, 300));
        JOptionPane.showMessageDialog(this, scroll, "Récapitulatif des Revenus", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
