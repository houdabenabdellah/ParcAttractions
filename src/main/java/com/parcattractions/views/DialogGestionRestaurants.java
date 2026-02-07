package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import main.java.com.parcattractions.models.services.Restaurant;
import main.java.com.parcattractions.models.services.ServiceManager;
import main.java.com.parcattractions.models.visiteurs.Visiteur;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.resources.styles.UIStyles;

/**
 * Dialog pour la gestion et consultation des restaurants
 */
public class DialogGestionRestaurants extends JDialog {
    
    private final ServiceManager serviceManager;
    private final GestionnaireParc gestionnaireParc;
    private JTable tableRestaurants;
    private DefaultTableModel modelRestaurants;

    public DialogGestionRestaurants(JFrame parent, ServiceManager serviceManager, 
                                     GestionnaireParc gestionnaireParc) {
        super(parent, "Gestion des Restaurants", true);
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

        JLabel title = new JLabel("GESTION DES RESTAURANTS");
        title.setFont(UIStyles.TITLE_FONT);
        title.setForeground(UIStyles.DUSK_BLUE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        modelRestaurants = new DefaultTableModel(
            new String[]{"RESTAURANT", "CAPACITÉ", "OCCUPÉ", "FILE", "TAUX", "REVENUS", "STATUT"}, 0);
        tableRestaurants = new JTable(modelRestaurants);
        tableRestaurants.setRowHeight(30);
        tableRestaurants.setFont(UIStyles.REGULAR_FONT);
        tableRestaurants.getTableHeader().setBackground(UIStyles.DUSK_BLUE);
        tableRestaurants.getTableHeader().setForeground(Color.WHITE);
        tableRestaurants.getTableHeader().setFont(UIStyles.HEADER_FONT);

        JScrollPane scroll = new JScrollPane(tableRestaurants);
        scroll.setBorder(UIStyles.createStyledBorder("État des Restaurants"));
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttons.setOpaque(false);

        JButton menuBtn = new JButton("Voir Menu");
        UIStyles.styleSecondaryButton(menuBtn);
        menuBtn.addActionListener(e -> logicAfficherMenu());

        JButton reserverBtn = new JButton("Réserver");
        UIStyles.stylePrimaryButton(reserverBtn);
        reserverBtn.addActionListener(e -> logicReserver());

        JButton revenusBtn = new JButton("Revenus Totaux");
        UIStyles.styleAccentButton(revenusBtn);
        revenusBtn.addActionListener(e -> afficherRecapitulatifRevenus());

        JButton close = new JButton("Fermer");
        UIStyles.stylePrimaryButton(close);
        close.addActionListener(e -> dispose());

        buttons.add(menuBtn);
        buttons.add(reserverBtn);
        buttons.add(revenusBtn);
        buttons.add(close);
        add(buttons, BorderLayout.SOUTH);
    }

    private void rafraichir() {
        modelRestaurants.setRowCount(0);
        for (Restaurant resto : serviceManager.getRestaurants()) {
            int capacite = resto.getCapacite();
            int occupes = resto.getClientsActuels();
            int file = resto.getTailleFile();
            double taux = resto.getTauxOccupation() * 100;
            double revenus = resto.getRevenus();
            
            String statut = resto.isEnService() ? "En service" : "Hors service";
            modelRestaurants.addRow(new Object[]{
                resto.getNom(),
                capacite,
                occupes,
                file,
                String.format("%.1f%%", taux),
                String.format("%.2f €", revenus),
                statut
            });
        }
    }

    private void logicAfficherMenu() {
        int r = tableRestaurants.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un restaurant", "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String nomResto = (String) modelRestaurants.getValueAt(r, 0);
        Restaurant resto = serviceManager.getRestaurantParNom(nomResto);
        if (resto != null) {
            String menu = resto.afficherMenu();
            JTextArea textArea = new JTextArea(menu);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            textArea.setEditable(false);
            JScrollPane scroll = new JScrollPane(textArea);
            scroll.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog(this, scroll, "Menu - " + nomResto, 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void logicReserver() {
        int r = tableRestaurants.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un restaurant", "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nomResto = (String) modelRestaurants.getValueAt(r, 0);
        Restaurant resto = serviceManager.getRestaurantParNom(nomResto);
        if (resto == null) return;
        if (!resto.isEnService()) {
            JOptionPane.showMessageDialog(this, "Le restaurant est hors service", "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (resto.estComplet()) {
            JOptionPane.showMessageDialog(this, "Le restaurant est plein", "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

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
            "Sélectionner un visiteur:", nomResto, JOptionPane.QUESTION_MESSAGE, null, 
            nomsVisiteurs, nomsVisiteurs[0]);

        if (nomVisiteur == null) return;

        Visiteur visiteur = gestionnaireParc.getVisiteurParNom1(nomVisiteur);
        if (visiteur == null) return;

        // Montant de la réservation
        String montantStr = JOptionPane.showInputDialog(this, 
            "Montant de la réservation (€):", "20.00");
        if (montantStr == null) return;

        try {
            double montant = Double.parseDouble(montantStr);
            String ordreCommande = JOptionPane.showInputDialog(this, 
                "Ordre de commande:", "Menu du jour");
            if (ordreCommande == null) ordreCommande = "Menu du jour";

            // Confirmation
            int conf = JOptionPane.showConfirmDialog(this, String.format(
                "Confirmer la réservation pour %s au %s pour %.2f € ?", visiteur.getNomVisiteur(), nomResto, montant),
                "Confirmer réservation", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION) return;

            String resultat = serviceManager.effectuerReservationRestaurant(
                visiteur, nomResto, montant, ordreCommande);

            JOptionPane.showMessageDialog(this, resultat, "Réservation", 
                JOptionPane.INFORMATION_MESSAGE);
            rafraichir();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Montant invalide", "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
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
