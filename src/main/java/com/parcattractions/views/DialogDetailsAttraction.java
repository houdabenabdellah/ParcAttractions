package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.enums.TypeBillet;
import main.java.com.parcattractions.models.attractions.Attraction;
import main.java.com.parcattractions.models.visiteurs.Visiteur;
import main.java.com.parcattractions.utils.Tarification;
import resources.styles.UIStyles;

/**
 * Dialog affichant les détails d'une attraction et permettant l'achat de tickets
 * Vérifie l'existence du visiteur avant l'achat
 */
public class DialogDetailsAttraction extends JDialog {
    
    private final Attraction attraction;
    private final GestionnaireParc gestionnaireParc;
    
    private JTextField nomVisiteurField;
    private JComboBox<TypeBillet> typeTicketCombo;
    private JLabel revenuLabel;
    private JButton prendreTicketBtn;
    
    /** Types de ticket proposés (Normal et Fast Pass uniquement) */
    private static final TypeBillet[] TYPES_TICKET = { TypeBillet.STANDARD, TypeBillet.FAST_PASS };
    
    public DialogDetailsAttraction(JFrame parent, Attraction attraction, GestionnaireParc gestionnaireParc) {
        super(parent, "Détails - " + attraction.getNom(), true);
        this.attraction = attraction;
        this.gestionnaireParc = gestionnaireParc;
        
        setSize(500, 520);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_LIGHT);
        buildUI();
        mettreAJourRevenu();
    }
    
    private void buildUI() {
        JPanel content = (JPanel) getContentPane();
        content.setLayout(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // ========== Section Détails de l'attraction ==========
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Nom
        JLabel nomLabel = new JLabel(attraction.getNom());
        nomLabel.setFont(UIStyles.TITLE_FONT);
        nomLabel.setForeground(UIStyles.PRIMARY_COLOR);
        detailsPanel.add(nomLabel);
        detailsPanel.add(Box.createVerticalStrut(8));
        
        // Description
        JLabel descLabel = new JLabel("<html>" + attraction.getDescription().replaceAll("\n", "<br>") + "</html>");
        descLabel.setFont(UIStyles.REGULAR_FONT);
        descLabel.setForeground(UIStyles.TEXT_SECONDARY);
        descLabel.setMaximumSize(new Dimension(450, 80));
        detailsPanel.add(descLabel);
        detailsPanel.add(Box.createVerticalStrut(12));
        
        // Prix et revenu
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 8, 6));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(createInfoLabel("Prix ticket Normal:"));
        infoPanel.add(createValueLabel(Tarification.formaterPrix(attraction.getPrixTicketNormal())));
        infoPanel.add(createInfoLabel("Prix ticket Fast Pass:"));
        infoPanel.add(createValueLabel(Tarification.formaterPrix(attraction.getPrixTicketFastPass())));
        infoPanel.add(createInfoLabel("Revenu total:"));
        revenuLabel = createValueLabel(Tarification.formaterPrix(attraction.getRevenuTotal()));
        infoPanel.add(revenuLabel);
        
        detailsPanel.add(infoPanel);
        content.add(detailsPanel, BorderLayout.NORTH);
        
        // ========== Section Achat de ticket ==========
        JPanel achatPanel = new JPanel();
        achatPanel.setLayout(new GridBagLayout());
        achatPanel.setBackground(Color.WHITE);
        achatPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1),
                " Prendre un ticket "),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nom du visiteur
        gbc.gridx = 0; gbc.gridy = 0;
        achatPanel.add(new JLabel("Nom du visiteur:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        nomVisiteurField = new JTextField(20);
        nomVisiteurField.setFont(UIStyles.REGULAR_FONT);
        achatPanel.add(nomVisiteurField, gbc);
        
        // Type de ticket
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = 0; gbc.weightx = 0;
        achatPanel.add(new JLabel("Type de ticket:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        typeTicketCombo = new JComboBox<>(TYPES_TICKET);
        typeTicketCombo.setFont(UIStyles.REGULAR_FONT);
        typeTicketCombo.setBackground(Color.WHITE);
        achatPanel.add(typeTicketCombo, gbc);
        
        // Bouton Prendre un ticket
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.insets = new Insets(15, 5, 5, 5);
        prendreTicketBtn = new JButton("Prendre un ticket");
        prendreTicketBtn.setFont(UIStyles.HEADER_FONT);
        prendreTicketBtn.setBackground(UIStyles.SUCCESS_COLOR);
        prendreTicketBtn.setForeground(Color.WHITE);
        prendreTicketBtn.setFocusPainted(false);
        prendreTicketBtn.addActionListener(e -> onPrendreTicket());
        achatPanel.add(prendreTicketBtn, gbc);
        
        content.add(achatPanel, BorderLayout.CENTER);
        
        // Bouton Fermer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(UIStyles.BG_LIGHT);
        JButton fermerBtn = new JButton("Fermer");
        fermerBtn.setFont(UIStyles.REGULAR_FONT);
        fermerBtn.addActionListener(e -> dispose());
        footerPanel.add(fermerBtn);
        content.add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIStyles.REGULAR_FONT);
        l.setForeground(UIStyles.TEXT_SECONDARY);
        return l;
    }
    
    private JLabel createValueLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIStyles.BOLD_FONT);
        l.setForeground(UIStyles.TEXT_PRIMARY);
        return l;
    }
    
    private void mettreAJourRevenu() {
        revenuLabel.setText(Tarification.formaterPrix(attraction.getRevenuTotal()));
    }
    
    /**
     * Gère l'achat de ticket: vérifie le visiteur puis appelle la logique métier
     */
    private void onPrendreTicket() {
        String nom = nomVisiteurField.getText();
        if (nom == null || nom.isBlank()) {
            JOptionPane.showMessageDialog(this,
                "Veuillez saisir le nom du visiteur.",
                "Champ requis",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Visiteur visiteur = gestionnaireParc.getVisiteurParNom(nom.trim());
        if (visiteur == null) {
            JOptionPane.showMessageDialog(this,
                "Visiteur non trouvé. Veuillez vous inscrire.",
                "Visiteur introuvable",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        TypeBillet type = (TypeBillet) typeTicketCombo.getSelectedItem();
        if (type == null) {
            type = TypeBillet.STANDARD;
        }
        
        String resultat = gestionnaireParc.acheterTicketAttraction(attraction, visiteur, type);
        
        if (resultat.contains("succès")) {
            mettreAJourRevenu();
            JOptionPane.showMessageDialog(this, resultat, "Succès", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, resultat, "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
