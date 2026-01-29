package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.models.attractions.Attraction;
import main.java.com.parcattractions.models.employes.AgentSecurite;
import main.java.com.parcattractions.models.employes.Employe;
import main.java.com.parcattractions.models.employes.Operateur;
import main.java.com.parcattractions.models.employes.Technicien;
import main.java.com.parcattractions.models.employes.Vendeur;
import main.java.com.parcattractions.models.services.Boutique;
import main.java.com.parcattractions.models.services.Restaurant;

/**
 * Dialogue Manager : Gérer Personnel (UC14) - Stylisé
 * Liste les employés, permet d'ajouter ou de retirer du personnel.
 */
public class DialogGestionPersonnel extends JDialog {

    private static final String[] TYPES = {
        "Opérateur", "Technicien", "Agent Sécurité", "Vendeur"
    };

    private final GestionnaireParc gestionnaireParc;
    private JTable tableEmployes;
    private DefaultTableModel modelEmployes;
    private List<Employe> employesList;

    public DialogGestionPersonnel(JFrame parent, GestionnaireParc gestionnaireParc) {
        super(parent, "👨‍💼 Gérer le personnel (UC14)", true);
        this.gestionnaireParc = gestionnaireParc;
        this.employesList = new ArrayList<>();
        setSize(620, 480);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_LIGHT);
        buildUI();
        rafraichir();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        modelEmployes = new DefaultTableModel(
            new String[] { "Nom", "Poste", "État" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tableEmployes = new JTable(modelEmployes);
        tableEmployes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableEmployes.getTableHeader().setReorderingAllowed(false);
        tableEmployes.setFont(UIStyles.REGULAR_FONT);
        tableEmployes.setRowHeight(25);
        tableEmployes.getTableHeader().setFont(UIStyles.HEADER_FONT);
        tableEmployes.getTableHeader().setBackground(UIStyles.PRIMARY_COLOR);
        tableEmployes.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scroll = new JScrollPane(tableEmployes);
        scroll.setBorder(BorderFactory.createLineBorder(UIStyles.BORDER_COLOR, 1));
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(scroll, BorderLayout.CENTER);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        add(centerPanel, BorderLayout.CENTER);

        JPanel boutons = new JPanel();
        boutons.setBackground(UIStyles.BG_LIGHT);
        boutons.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        JButton btnAjouter = new JButton("➕ Ajouter");
        btnAjouter.addActionListener(e -> ajouterEmploye());
        UIStyles.stylePrimaryButton(btnAjouter);
        
        JButton btnRetirer = new JButton("❌ Retirer");
        btnRetirer.addActionListener(e -> retirerEmploye());
        UIStyles.styleAccentButton(btnRetirer);
        
        JButton btnFermer = new JButton("🚪 Fermer");
        btnFermer.addActionListener(e -> dispose());
        UIStyles.styleSecondaryButton(btnFermer);
        
        boutons.add(btnAjouter);
        boutons.add(btnRetirer);
        boutons.add(btnFermer);
        add(boutons, BorderLayout.SOUTH);
    }

    private void rafraichir() {
        employesList.clear();
        employesList.addAll(gestionnaireParc.getEmployes());
        modelEmployes.setRowCount(0);
        for (Employe e : employesList) {
            modelEmployes.addRow(new Object[] {
                e.getNom(),
                e.getPoste(),
                e.getEtat().getDescription()
            });
        }
    }

    private void ajouterEmploye() {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JComboBox<String> comboType = new JComboBox<>(TYPES);
        JTextField nomField = new JTextField(20);
        JComboBox<String> comboExtra = new JComboBox<>();
        comboExtra.addItem("-- Sélectionner --");

        JLabel lblExtra = new JLabel("Attraction / Lieu");
        form.add(new JLabel("Type"));
        form.add(comboType);
        form.add(new JLabel("Nom"));
        form.add(nomField);
        form.add(lblExtra);
        form.add(comboExtra);

        Runnable majExtra = () -> {
            comboExtra.removeAllItems();
            comboExtra.addItem("-- Sélectionner --");
            String t = (String) comboType.getSelectedItem();
            if ("Opérateur".equals(t)) {
                for (Attraction a : gestionnaireParc.getAttractions()) {
                    comboExtra.addItem(a.getNom());
                }
                lblExtra.setText("Attraction");
                lblExtra.setVisible(true);
                comboExtra.setVisible(true);
            } else if ("Vendeur".equals(t)) {
                for (Boutique b : gestionnaireParc.getBoutiques()) {
                    comboExtra.addItem("Boutique:" + b.getNom());
                }
                for (Restaurant r : gestionnaireParc.getRestaurants()) {
                    comboExtra.addItem("Restaurant:" + r.getNom());
                }
                lblExtra.setText("Lieu (boutique ou restaurant)");
                lblExtra.setVisible(true);
                comboExtra.setVisible(true);
            } else {
                lblExtra.setVisible(false);
                comboExtra.setVisible(false);
            }
        };

        comboType.addActionListener(ev -> majExtra.run());
        majExtra.run();

        int rep = JOptionPane.showConfirmDialog(this, form, "Ajouter un employé",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (rep != JOptionPane.OK_OPTION) {
            return;
        }

        String nom = nomField.getText().trim();
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un nom.", "Attention",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String type = (String) comboType.getSelectedItem();
        Employe nouvelEmploye = null;

        switch (type) {
            case "Opérateur": {
                Object sel = comboExtra.getSelectedItem();
                if (sel == null || "-- Sélectionner --".equals(sel.toString())) {
                    JOptionPane.showMessageDialog(this, "Veuillez sélectionner une attraction.",
                        "Attention", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Attraction att = gestionnaireParc.getAttractionParNom(sel.toString());
                if (att != null) {
                    nouvelEmploye = new Operateur(nom, att);
                }
                break;
            }
            case "Technicien":
                nouvelEmploye = new Technicien(nom);
                break;
            case "Agent Sécurité":
                nouvelEmploye = new AgentSecurite(nom);
                break;
            case "Vendeur": {
                Object sel = comboExtra.getSelectedItem();
                if (sel == null || "-- Sélectionner --".equals(sel.toString())) {
                    JOptionPane.showMessageDialog(this, "Veuillez sélectionner un lieu (boutique ou restaurant).",
                        "Attention", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String s = sel.toString();
                if (s.startsWith("Boutique:")) {
                    String bn = s.substring("Boutique:".length());
                    for (Boutique b : gestionnaireParc.getBoutiques()) {
                        if (b.getNom().equals(bn)) {
                            nouvelEmploye = new Vendeur(nom, b);
                            break;
                        }
                    }
                } else if (s.startsWith("Restaurant:")) {
                    String rn = s.substring("Restaurant:".length());
                    for (Restaurant r : gestionnaireParc.getRestaurants()) {
                        if (r.getNom().equals(rn)) {
                            nouvelEmploye = new Vendeur(nom, r);
                            break;
                        }
                    }
                }
                break;
            }
            default:
                break;
        }

        if (nouvelEmploye == null) {
            JOptionPane.showMessageDialog(this, "Impossible de créer l'employé. Vérifiez les choix.",
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        gestionnaireParc.ajouterEmploye(nouvelEmploye);
        rafraichir();
        JOptionPane.showMessageDialog(this, "Employé ajouté : " + nom + " (" + type + ").",
            "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    private void retirerEmploye() {
        int row = tableEmployes.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un employé à retirer.",
                "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (row >= employesList.size()) {
            return;
        }
        Employe e = employesList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Retirer l'employé « " + e.getNom() + " » (" + e.getPoste() + ") ?",
            "Confirmer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        gestionnaireParc.retirerEmploye(e);
        rafraichir();
    }
}
