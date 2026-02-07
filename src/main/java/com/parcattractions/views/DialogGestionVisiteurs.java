package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.models.visiteurs.Visiteur;
import main.java.com.parcattractions.resources.styles.UIStyles;


public class DialogGestionVisiteurs extends JDialog {

    private final GestionnaireParc gestionnaireParc;
    private JTable tableVisiteurs;
    private DefaultTableModel modelVisiteurs;

    public DialogGestionVisiteurs(JFrame parent, GestionnaireParc gestionnaireParc) {
        super(parent, "Gérer les Visiteurs", true);
        this.gestionnaireParc = gestionnaireParc;
        setSize(800, 500);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_WINDOW);
        buildUI();
        rafraichir();
    }

    private void buildUI() {
        setLayout(new BorderLayout(20, 20));

        JLabel title = new JLabel("REGISTRE DES VISITEURS");
        title.setFont(UIStyles.TITLE_FONT);
        title.setForeground(UIStyles.DUSK_BLUE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        modelVisiteurs = new DefaultTableModel(new String[]{"NOM", "AGE", "TAILLE", "PROFIL", "ETAT"}, 0);
        tableVisiteurs = new JTable(modelVisiteurs);
        tableVisiteurs.setRowHeight(30);
        tableVisiteurs.setFont(UIStyles.REGULAR_FONT);
        tableVisiteurs.getTableHeader().setBackground(UIStyles.DUSK_BLUE);
        tableVisiteurs.getTableHeader().setForeground(Color.WHITE);
        tableVisiteurs.getTableHeader().setFont(UIStyles.HEADER_FONT);

        JScrollPane scroll = new JScrollPane(tableVisiteurs);
        scroll.setBorder(UIStyles.createStyledBorder("Liste des Visiteurs"));
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        buttons.setOpaque(false);

        JButton addBtn = new JButton("Ajouter Visiteur");
        UIStyles.styleSecondaryButton(addBtn);
        addBtn.addActionListener(e -> logicAjouter());

        JButton delBtn = new JButton("Retirer Visiteur");
        UIStyles.styleAccentButton(delBtn);
        delBtn.addActionListener(e -> logicRetirer());

        JButton detailsBtn = new JButton("Voir Détails");
        UIStyles.stylePrimaryButton(detailsBtn);
        detailsBtn.addActionListener(e -> logicDetails());

        JButton close = new JButton("Quitter");
        UIStyles.stylePrimaryButton(close);
        close.addActionListener(e -> dispose());

        buttons.add(addBtn); buttons.add(delBtn); buttons.add(detailsBtn); buttons.add(close);
        add(buttons, BorderLayout.SOUTH);
    }

    private void rafraichir() {
        modelVisiteurs.setRowCount(0);
        for (Visiteur v : gestionnaireParc.getVisiteurs()) {
            modelVisiteurs.addRow(new Object[]{v.getNomVisiteur(), v.getAge(), v.getTaille(), v.getProfil().getLibelle(), v.getEtat().getDescription()});
        }
    }

    private void logicAjouter() {
        DialogAjoutVisiteur dialog = new DialogAjoutVisiteur((JFrame) getParent());
        dialog.setVisible(true);
        rafraichir();
    }

    private void logicRetirer() {
        int r = tableVisiteurs.getSelectedRow();
        if (r < 0) return;
        String nom = (String) modelVisiteurs.getValueAt(r, 0);
        Visiteur vis = gestionnaireParc.getVisiteurs().stream().filter(v -> v.getNomVisiteur().equals(nom)).findFirst().orElse(null);
        if (vis != null) {
            vis.arreter();
            gestionnaireParc.retirerVisiteur(vis);
            rafraichir();
        }
    }

    private void logicDetails() {
        int r = tableVisiteurs.getSelectedRow();
        if (r < 0) return;
        String nom = (String) modelVisiteurs.getValueAt(r, 0);
        Visiteur vis = gestionnaireParc.getVisiteurs().stream().filter(v -> v.getNomVisiteur().equals(nom)).findFirst().orElse(null);
        if (vis != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Nom: ").append(vis.getNomVisiteur()).append("\n");
            sb.append("Age: ").append(vis.getAge()).append("\n");
            sb.append("Taille: ").append(vis.getTaille()).append(" cm\n");
            sb.append("Profil: ").append(vis.getProfil().getLibelle()).append("\n");
            sb.append("Etat: ").append(vis.getEtat().getDescription()).append("\n");
            sb.append("Argent restant: ").append(String.format("%.2f €", vis.getArgent())).append("\n");
            sb.append("Satisfaction: ").append(vis.getSatisfaction()).append("%\n");
            JOptionPane.showMessageDialog(this, sb.toString(), "Détails Visiteur", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
