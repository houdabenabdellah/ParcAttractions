package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.enums.ProfilVisiteur;
import main.java.com.parcattractions.models.visiteurs.*;
import main.java.com.parcattractions.resources.styles.UIStyles;
import main.java.com.parcattractions.utils.DataManager; // Import crucial !
import main.java.com.parcattractions.utils.Logger;


public class DialogAjoutVisiteur extends JDialog {
    private JTextField nomField;
    private JSpinner ageSpinner, tailleSpinner, detailsSpinner;
    private JComboBox<ProfilVisiteur> profilCombo;
    private JLabel detailsLabel;
    private JButton ajouterBtn, annulerBtn;
    private boolean valide = false;

    public DialogAjoutVisiteur(JFrame parent) {
        super(parent, "Ajouter un Visiteur", true);
        setSize(500, 480);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_WINDOW); // Nettoyage fond Swing
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("NOUVEAU VISITEUR");
        titleLabel.setFont(UIStyles.TITLE_FONT);
        titleLabel.setForeground(UIStyles.DUSK_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(5, 2, 15, 20));
        form.setBackground(UIStyles.BG_CARD); // Carte Blanche
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Styling des labels et champs
        String[] labels = {"Nom du Visiteur:", "Âge (ans):", "Taille (cm):", "Profil:", "Options:"};
        nomField = new JTextField();
        ageSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 99, 1));
        tailleSpinner = new JSpinner(new SpinnerNumberModel(170, 50, 230, 1));
        profilCombo = new JComboBox<>(ProfilVisiteur.values());
        detailsLabel = new JLabel("Taille groupe:");
        detailsSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));

        form.add(new JLabel(labels[0])); form.add(nomField);
        form.add(new JLabel(labels[1])); form.add(ageSpinner);
        form.add(new JLabel(labels[2])); form.add(tailleSpinner);
        form.add(new JLabel(labels[3])); form.add(profilCombo);
        form.add(detailsLabel); form.add(detailsSpinner);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(UIStyles.BG_WINDOW);
        ajouterBtn = new JButton("Enregistrer");
        annulerBtn = new JButton("Fermer");
        UIStyles.stylePrimaryButton(ajouterBtn);
        UIStyles.styleSecondaryButton(annulerBtn);
        
        ajouterBtn.addActionListener(e -> logicAjouter());
        annulerBtn.addActionListener(e -> dispose());
        
        buttons.add(ajouterBtn); buttons.add(annulerBtn);
        add(buttons, BorderLayout.SOUTH);
    }

    private void logicAjouter() {
        if(nomField.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Saisissez un nom"); return;
        }
        
        try {
            Visiteur v = creerVisiteurSelonProfil(nomField.getText(), (int)ageSpinner.getValue(), (int)tailleSpinner.getValue(), 
                                                (ProfilVisiteur)profilCombo.getSelectedItem(), (int)detailsSpinner.getValue());
            
            // MÉMOIRE + SAUVEGARDE CSV
            GestionnaireParc.getInstance().ajouterVisiteur(v);
            DataManager.saveVisiteurs(GestionnaireParc.getInstance()); // CORRECTION ICI
            
            JOptionPane.showMessageDialog(this, "Visiteur ajouté et sauvegardé dans le CSV !");
            valide = true;
            dispose();
        } catch(Exception e) {
            Logger.logException("Erreur Sauvegarde", e);
        }
    }
    
    private Visiteur creerVisiteurSelonProfil(String n, int a, int t, ProfilVisiteur p, int d) {
        return switch (p) {
            case FAMILLE -> new VisiteurFamille(n, a, t, d);
            case GROUPE -> new VisiteurGroupe(n, a, t);
            case EXTREME -> new VisiteurExtreme(n, a, t);
            default -> new VisiteurExtreme(n, a, t);
        };
    }
    public boolean estValide() { return valide; }
}