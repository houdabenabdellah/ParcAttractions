package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import main.java.com.parcattractions.controllers.GestionnaireEvenements;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.exceptions.systeme.ParcFermeException;
import main.java.com.parcattractions.utils.ModeApplication;

/**
 * Fenêtre principale de l'application
 */
public class MainFrame extends JFrame {
    
    private final GestionnaireParc gestionnaireParc;
    private final GestionnaireEvenements gestionnaireEvenements;
    
    private PanelDashboard panelDashboard;
    private PanelAttractions panelAttractions;
    private PanelStatistiques panelStatistiques;
    private PanelNotifications panelNotifications;
    private PanelVueParc panelVueParc;
    private PanelGestion panelGestion;
    private PanelTransactions panelTransactions;
    
    private ModeApplication mode;
    
    /**
     * Constructeur
     */
    public MainFrame(GestionnaireParc gestionnaireParc,
            GestionnaireEvenements gestionnaireEvenements) {
        super("Parc d'Attractions - Mode Gestion");
        
        this.gestionnaireParc = gestionnaireParc;
        this.gestionnaireEvenements = gestionnaireEvenements;
        
        initialiserInterface();
        configurerFermeture();
    }
    
    /**
     * Initialise l'interface utilisateur
     */
    private void initialiserInterface() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        // Style global
        getContentPane().setBackground(UIStyles.BG_LIGHT);
        
        // Menu
        creerMenu();
        
        // Layout principal
        setLayout(new BorderLayout());
        
        // Panels
        panelDashboard = new PanelDashboard(gestionnaireParc);
        panelAttractions = new PanelAttractions(gestionnaireParc);
        panelStatistiques = new PanelStatistiques(gestionnaireParc);
        panelNotifications = new PanelNotifications();
        panelVueParc = new PanelVueParc(gestionnaireParc);
        panelGestion = new PanelGestion(gestionnaireParc);
        panelTransactions = new PanelTransactions();
        
        // Organisation avec onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(UIStyles.BG_LIGHT);
        
        // Onglet Tableau de bord
        JPanel dashboardTab = new JPanel(new BorderLayout());
        dashboardTab.setBackground(UIStyles.BG_LIGHT);
        JPanel panelOuest = new JPanel(new BorderLayout());
        panelOuest.setBackground(UIStyles.BG_LIGHT);
        panelOuest.add(panelDashboard, BorderLayout.NORTH);
        panelOuest.add(panelStatistiques, BorderLayout.CENTER);
        
        JPanel panelEst = new JPanel(new BorderLayout());
        panelEst.setBackground(UIStyles.BG_LIGHT);
        panelEst.add(panelNotifications, BorderLayout.CENTER);
        panelEst.add(panelVueParc, BorderLayout.SOUTH);
        
        dashboardTab.add(panelOuest, BorderLayout.WEST);
        dashboardTab.add(panelAttractions, BorderLayout.CENTER);
        dashboardTab.add(panelEst, BorderLayout.EAST);
        
        tabbedPane.addTab("Tableau de bord", dashboardTab);
        tabbedPane.addTab("Gestion", panelGestion);
        tabbedPane.addTab("Transactions", panelTransactions);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Crée la barre de menu
     */
    private void creerMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Parc (Ouvrir / Fermer le parc)
        JMenu menuParc = new JMenu("Parc");
        JMenuItem itemOuvrirParc = new JMenuItem("Ouvrir le parc");
        itemOuvrirParc.addActionListener(e -> ouvrirParc());
        JMenuItem itemFermerParc = new JMenuItem("Fermer le parc");
        itemFermerParc.addActionListener(e -> fermerParc());
        menuParc.add(itemOuvrirParc);
        menuParc.add(itemFermerParc);
        menuBar.add(menuParc);
        
        // Menu Manager (UC14 Gérer Personnel, UC15 Lancer Événement)
        JMenu menuManager = new JMenu("Manager");
        
        JMenuItem itemGérerPersonnel = new JMenuItem("Gérer le personnel");
        itemGérerPersonnel.addActionListener(e -> {
            DialogGestionPersonnel d = new DialogGestionPersonnel(this, gestionnaireParc);
            d.setVisible(true);
        });
        menuManager.add(itemGérerPersonnel);
        
        JMenu subLancerEvenement = new JMenu("Lancer un événement");
        JMenuItem itemHappyHour = new JMenuItem("Happy Hour");
        itemHappyHour.addActionListener(e -> {
            gestionnaireEvenements.lancerHappyHourManuel();
            JOptionPane.showMessageDialog(this, "Happy Hour lancé.", "Événement",
                JOptionPane.INFORMATION_MESSAGE);
        });
        JMenuItem itemParade = new JMenuItem("Parade");
        itemParade.addActionListener(e -> {
            gestionnaireEvenements.lancerParadeManuel();
            JOptionPane.showMessageDialog(this, "Parade lancée.", "Événement",
                JOptionPane.INFORMATION_MESSAGE);
        });
        JMenuItem itemSpectacle = new JMenuItem("Spectacle nocturne");
        itemSpectacle.addActionListener(e -> {
            gestionnaireEvenements.lancerSpectacleNocturneManuel();
            JOptionPane.showMessageDialog(this, "Spectacle nocturne lancé.", "Événement",
                JOptionPane.INFORMATION_MESSAGE);
        });
        subLancerEvenement.add(itemHappyHour);
        subLancerEvenement.add(itemParade);
        subLancerEvenement.add(itemSpectacle);
        menuManager.add(subLancerEvenement);
        
        menuBar.add(menuManager);
        
        // Menu Rapports
        JMenu menuRapports = new JMenu("Rapports");
        JMenuItem itemAfficherRapports = new JMenuItem("Afficher les rapports");
        itemAfficherRapports.addActionListener(e -> {
            DialogRapports d = new DialogRapports(this, gestionnaireParc);
            d.setVisible(true);
        });
        menuRapports.add(itemAfficherRapports);
        
        menuBar.add(menuRapports);
        
        // Menu Aide
        JMenu menuAide = new JMenu("Aide");
        JMenuItem itemAPropos = new JMenuItem("À propos");
        itemAPropos.addActionListener(e -> afficherAPropos());
        menuAide.add(itemAPropos);
        
        JMenuItem itemQuitter = new JMenuItem("Quitter");
        itemQuitter.addActionListener(e -> quitter());
        menuAide.addSeparator();
        menuAide.add(itemQuitter);
        
        menuBar.add(menuAide);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Ouvre le parc (menu Parc)
     */
    private void ouvrirParc() {
        try {
            gestionnaireParc.ouvrirParc();
            JOptionPane.showMessageDialog(this, "Le parc est ouvert.", "Parc",
                JOptionPane.INFORMATION_MESSAGE);
            rafraichirTousLesPanels();
        } catch (ParcFermeException ex) {
            JOptionPane.showMessageDialog(this,
                "Le parc est déjà ouvert.", "Parc", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Ferme le parc (menu Parc)
     */
    private void fermerParc() {
        gestionnaireParc.fermerParc();
        JOptionPane.showMessageDialog(this, "Le parc est fermé.", "Parc",
            JOptionPane.INFORMATION_MESSAGE);
        rafraichirTousLesPanels();
    }
    
    /**
     * Rafraîchit l'affichage de tous les panels (état du parc, dashboard, etc.)
     * Appelable depuis l'extérieur (ex. après ouverture du parc au démarrage).
     */
    public void rafraichirAffichage() {
        rafraichirTousLesPanels();
    }
    
    private void rafraichirTousLesPanels() {
        if (panelVueParc != null) panelVueParc.rafraichir();
        if (panelDashboard != null) panelDashboard.rafraichir();
        if (panelAttractions != null) panelAttractions.rafraichir();
        if (panelStatistiques != null) panelStatistiques.rafraichir();
        if (panelNotifications != null) panelNotifications.rafraichir();
    }
    
    /**
     * Affiche la boîte À propos
     */
    private void afficherAPropos() {
        String message = "Parc d'Attractions - Mode Gestion\n\n" +
            "Version 1.0\n" +
            "Gestion manuelle d'un parc d'attractions\n" +
            "avec ajout/suppression de visiteurs, attractions,\n" +
            "employés, gestion météo et événements.";
        
        JOptionPane.showMessageDialog(this, message, 
            "À propos", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Configure la fermeture de l'application
     */
    private void configurerFermeture() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                quitter();
            }
        });
    }
    
    /**
     * Quitte l'application
     */
    private void quitter() {
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir quitter ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void setMode(ModeApplication mode) {
        this.mode = mode;
    }
}
