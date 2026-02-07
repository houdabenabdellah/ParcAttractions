package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.enums.TypeBillet;
import main.java.com.parcattractions.models.attractions.Attraction;
import main.java.com.parcattractions.models.visiteurs.Visiteur;
import main.java.com.parcattractions.resources.styles.UIStyles;
import main.java.com.parcattractions.utils.Tarification;

public class DialogDetailsAttraction extends JDialog {
    
    private final Attraction attraction;
    private final GestionnaireParc gestionnaireParc;
    private JTextField nomVisiteurField;
    private JComboBox<TypeBillet> typeTicketCombo;
    private JLabel revenuLabel;
    private JButton prendreTicketBtn;
    
    private static final TypeBillet[] TYPES_TICKET = { TypeBillet.STANDARD, TypeBillet.FAST_PASS };
    
    public DialogDetailsAttraction(JFrame parent, Attraction attraction, GestionnaireParc gestionnaireParc) {
        super(parent, "Détails Attraction", true);
        this.attraction = attraction;
        this.gestionnaireParc = gestionnaireParc;
        
        setSize(550, 600);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_WINDOW); // Fond Modern
        buildUI();
        mettreAJourRevenu();
    }
    
    private void buildUI() {
        setLayout(new BorderLayout(15, 15));
        
        // --- Header Fancy ---
        JLabel titleLabel = new JLabel(attraction.getNom().toUpperCase());
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.DUSK_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 5, 10));
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 10, 25));

        // --- Card Détails ---
        JPanel cardInfo = new JPanel();
        cardInfo.setLayout(new BoxLayout(cardInfo, BoxLayout.Y_AXIS));
        cardInfo.setBackground(Color.WHITE);
        cardInfo.setBorder(UIStyles.createStyledBorder("Infos & Statistiques"));

        JLabel descLabel = new JLabel("<html><p style='width:300px;'>" + attraction.getDescription() + "</p></html>");
        descLabel.setFont(UIStyles.REGULAR_FONT);
        descLabel.setForeground(UIStyles.TEXT_PRIMARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JPanel gridPrices = new JPanel(new GridLayout(3, 2, 10, 10));
        gridPrices.setOpaque(false);
        gridPrices.add(new JLabel("Prix Standard :")); 
        gridPrices.add(new JLabel(Tarification.formaterPrix(attraction.getPrixTicketNormal())));
        gridPrices.add(new JLabel("Prix FastPass :")); 
        gridPrices.add(new JLabel(Tarification.formaterPrix(attraction.getPrixTicketFastPass())));
        gridPrices.add(new JLabel("Recettes générées :"));
        revenuLabel = new JLabel();
        revenuLabel.setFont(UIStyles.HEADER_FONT);
        revenuLabel.setForeground(UIStyles.LIGHT_BRONZE);
        gridPrices.add(revenuLabel);

        cardInfo.add(descLabel);
        cardInfo.add(gridPrices);
        mainPanel.add(cardInfo);
        mainPanel.add(Box.createVerticalStrut(20));

        // --- Card Vente ---
        JPanel cardAchat = new JPanel(new GridBagLayout());
        cardAchat.setBackground(Color.WHITE);
        cardAchat.setBorder(UIStyles.createStyledBorder("Billeterie Express"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; cardAchat.add(new JLabel("Visiteur :"), gbc);
        gbc.gridx = 1; nomVisiteurField = new JTextField(15); cardAchat.add(nomVisiteurField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; cardAchat.add(new JLabel("Billet :"), gbc);
        gbc.gridx = 1; typeTicketCombo = new JComboBox<>(TYPES_TICKET); cardAchat.add(typeTicketCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        prendreTicketBtn = new JButton("Vendre le Billet");
        UIStyles.stylePrimaryButton(prendreTicketBtn); // Dusk Blue
        prendreTicketBtn.addActionListener(e -> onPrendreTicket());
        cardAchat.add(prendreTicketBtn, gbc);

        mainPanel.add(cardAchat);
        add(mainPanel, BorderLayout.CENTER);

        // --- Footer ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setOpaque(false);

        JButton fermerAttraction = new JButton("Fermer Attraction");
        UIStyles.styleAccentButton(fermerAttraction);
        fermerAttraction.addActionListener(e -> {
            try {
                attraction.fermer();
                mettreAJourRevenu();
                JOptionPane.showMessageDialog(this, attraction.getNom() + " est maintenant FERMÉE", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton mettrePanne = new JButton("Mettre en panne");
        UIStyles.styleAccentButton(mettrePanne);
        mettrePanne.addActionListener(e -> {
            try {
                attraction.mettrePanne();
                mettreAJourRevenu();
                JOptionPane.showMessageDialog(this, attraction.getNom() + " est signalée EN PANNE.", "Panne déclenchée", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton close = new JButton("Fermer");
        UIStyles.styleAccentButton(close); // Rosewood
        close.addActionListener(e -> dispose());

        footer.add(fermerAttraction);
        footer.add(mettrePanne);
        footer.add(close);
        add(footer, BorderLayout.SOUTH);
    }
    
    private void mettreAJourRevenu() {
        revenuLabel.setText(Tarification.formaterPrix(attraction.getRevenuTotal()));
    }
    
    private void onPrendreTicket() {
        String nom = nomVisiteurField.getText().trim();
        if (nom.isEmpty()) { JOptionPane.showMessageDialog(this, "Saisissez un nom !"); return; }
        
        Visiteur v = gestionnaireParc.getVisiteurParNom(nom);
        if (v == null) { JOptionPane.showMessageDialog(this, "Visiteur non inscrit."); return; }
        
        String res = gestionnaireParc.acheterTicketAttraction(attraction, v, (TypeBillet)typeTicketCombo.getSelectedItem());
        if (res.contains("succès")) {
            mettreAJourRevenu();
            JOptionPane.showMessageDialog(this, res, "Succès", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, res, "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}