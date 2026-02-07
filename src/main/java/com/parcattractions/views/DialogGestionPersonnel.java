package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.models.employes.*;
import main.java.com.parcattractions.resources.styles.UIStyles;


public class DialogGestionPersonnel extends JDialog {

    private final GestionnaireParc gestionnaireParc;
    private JTable tableEmployes;
    private DefaultTableModel modelEmployes;

    public DialogGestionPersonnel(JFrame parent, GestionnaireParc gestionnaireParc) {
        super(parent, "Gérer les Effectifs", true);
        this.gestionnaireParc = gestionnaireParc;
        setSize(700, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_WINDOW);
        buildUI();
        rafraichir();
    }

    private void buildUI() {
        setLayout(new BorderLayout(20, 20));
        
        JLabel title = new JLabel("REGISTRE DU PERSONNEL");
        title.setFont(UIStyles.TITLE_FONT);
        title.setForeground(UIStyles.DUSK_BLUE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        // --- Table Section ---
        modelEmployes = new DefaultTableModel(new String[]{"NOM", "POSTE", "STATUT"}, 0);
        tableEmployes = new JTable(modelEmployes);
        tableEmployes.setRowHeight(30);
        tableEmployes.setFont(UIStyles.REGULAR_FONT);
        tableEmployes.getTableHeader().setBackground(UIStyles.DUSK_BLUE);
        tableEmployes.getTableHeader().setForeground(Color.WHITE);
        tableEmployes.getTableHeader().setFont(UIStyles.HEADER_FONT);
        
        JScrollPane scroll = new JScrollPane(tableEmployes);
        scroll.setBorder(UIStyles.createStyledBorder("Liste des Collaborateurs"));
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        // --- Button Bar ---
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        buttons.setOpaque(false);

        JButton addBtn = new JButton("Nouveau Contrat");
        UIStyles.styleSecondaryButton(addBtn); // Light Bronze
        addBtn.addActionListener(e -> logicAjouter());

        JButton delBtn = new JButton("Retirer du Registre");
        UIStyles.styleAccentButton(delBtn); // Rosewood
        delBtn.addActionListener(e -> logicRetirer());

        JButton close = new JButton("Quitter");
        UIStyles.stylePrimaryButton(close); // Dusk Blue
        close.addActionListener(e -> dispose());

        buttons.add(addBtn); buttons.add(delBtn); buttons.add(close);
        add(buttons, BorderLayout.SOUTH);
    }

    private void rafraichir() {
        modelEmployes.setRowCount(0);
        for (Employe e : gestionnaireParc.getEmployes()) {
            modelEmployes.addRow(new Object[]{e.getNom(), e.getPoste(), e.getEtat().getDescription()});
        }
    }

    private void logicAjouter() {
        // ... Logique d'ajout existante révisée avec styles modernes ...
        String n = JOptionPane.showInputDialog(this, "Nom du technicien :");
        if(n != null && !n.isBlank()){
            gestionnaireParc.ajouterEmploye(new Technicien(n));
            rafraichir();
        }
    }

    private void logicRetirer() {
        int r = tableEmployes.getSelectedRow();
        if(r < 0) return;
        String nom = (String) modelEmployes.getValueAt(r, 0);
        Employe emp = gestionnaireParc.getEmployes().stream().filter(e->e.getNom().equals(nom)).findFirst().orElse(null);
        if(emp != null) {
            gestionnaireParc.retirerEmploye(emp);
            rafraichir();
        }
    }
}