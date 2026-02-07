package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.resources.styles.UIStyles;
import main.java.com.parcattractions.utils.*;


public class DialogRapports extends JDialog {
    
    private final GestionnaireParc gestionnaireParc;
    private final GenerateurRapports generateur;
    private final ExporteurCSV exporteur;
    
    public DialogRapports(JFrame parent, GestionnaireParc gestionnaireParc) {
        super(parent, "Rapports & Analystiques", true);
        this.gestionnaireParc = gestionnaireParc;
        this.generateur = new GenerateurRapports(gestionnaireParc);
        this.exporteur = new ExporteurCSV(gestionnaireParc);
        
        setSize(950, 700);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIStyles.BG_WINDOW);
        buildUI();
    }
    
    private void buildUI() {
        setLayout(new BorderLayout(15, 15));

        JLabel title = new JLabel("REPRÉSENTATION DES PERFORMANCES DU PARC");
        title.setFont(UIStyles.TITLE_FONT);
        title.setForeground(UIStyles.DUSK_BLUE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIStyles.HEADER_FONT);
        tabs.setBackground(Color.WHITE);

        tabs.addTab(" Finance ", createReportPanel(generateur.genererRapportFinancier(), UIStyles.LIGHT_BRONZE));
        tabs.addTab(" Fréquentation ", createReportPanel(generateur.genererRapportOccupation(), UIStyles.DUSTY_LAVENDER));
        tabs.addTab(" Satisfaction ", createReportPanel(generateur.genererRapportSatisfaction(), UIStyles.ROSEWOOD));
        tabs.addTab(" Rapport Global ", createReportPanel(generateur.genererRapportComplet(), UIStyles.DUSK_BLUE));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        wrapper.add(tabs, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        // --- Action Panel ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        actionPanel.setOpaque(false);

        JButton csvBtn = new JButton("Exporter (.CSV)");
        UIStyles.styleSecondaryButton(csvBtn);
        csvBtn.addActionListener(e -> exporteur.exporterResume());

        JButton htmlBtn = new JButton("Aperçu Web (.HTML)");
        UIStyles.stylePrimaryButton(htmlBtn);
        htmlBtn.addActionListener(e -> exporteur.exporterHTML());

        JButton quit = new JButton("Fermer");
        UIStyles.styleAccentButton(quit);
        quit.addActionListener(e -> dispose());

        actionPanel.add(csvBtn); actionPanel.add(htmlBtn); actionPanel.add(quit);
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createReportPanel(String text, Color borderColor) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, borderColor),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(UIStyles.MONOSPACE_FONT);
        area.setBackground(new Color(250, 250, 252));
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        p.add(sp, BorderLayout.CENTER);

        return p;
    }
}