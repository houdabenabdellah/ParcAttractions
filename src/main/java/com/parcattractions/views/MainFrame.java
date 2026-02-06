package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import main.java.com.parcattractions.controllers.GestionnaireEvenements;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.exceptions.systeme.ParcFermeException;
import main.java.com.parcattractions.utils.ModeApplication;
import resources.styles.UIStyles;

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
    
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    private static final String CARD_DASHBOARD = "dashboard";
    private static final String CARD_GESTION = "gestion";
    private static final String CARD_TRANSACTIONS = "transactions";
    
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
        panelAttractions = new PanelAttractions(gestionnaireParc, this);
        panelStatistiques = new PanelStatistiques(gestionnaireParc);
        panelNotifications = new PanelNotifications();
        panelVueParc = new PanelVueParc(gestionnaireParc);
        panelGestion = new PanelGestion(gestionnaireParc);
        panelTransactions = new PanelTransactions();
        
        // Organisation avec CardLayout (navigation par menu)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIStyles.BG_LIGHT);
        
        // Vue Tableau de bord
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
        
        contentPanel.add(dashboardTab, CARD_DASHBOARD);
        contentPanel.add(panelGestion, CARD_GESTION);
        contentPanel.add(panelTransactions, CARD_TRANSACTIONS);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Vue initiale : Tableau de bord
        afficherVue(CARD_DASHBOARD);
    }
    
    /**
     * Affiche la vue sélectionnée (Tableau de bord, Gestion ou Transactions)
     */
    private void afficherVue(String cardName) {
        if (cardLayout != null && contentPanel != null) {
            cardLayout.show(contentPanel, cardName);
        }
    }
    
    /**
     * Crée la barre de menu
     */
    private void creerMenu() {
        JMenuBar menuBar = new JMenuBar();
    
    
    // ========== MENU PARC ==========
    JMenu menuParc = new JMenu("Parc");
    
    JMenuItem itemOuvrirParc = new JMenuItem("Ouvrir le parc");
    itemOuvrirParc.addActionListener(e -> ouvrirParc());
    
    JMenuItem itemFermerParc = new JMenuItem("Fermer le parc");
    itemFermerParc.addActionListener(e -> fermerParc());
    
    JMenuItem itemPauseSimulation = new JMenuItem("Pause");
    itemPauseSimulation.addActionListener(e -> pauseSimulation());
    
    JMenuItem itemReprendreSimulation = new JMenuItem("Reprendre");
    itemReprendreSimulation.addActionListener(e -> reprendreSimulation());
    
    menuParc.add(itemOuvrirParc);
    menuParc.add(itemFermerParc);
    menuParc.addSeparator();
    menuParc.add(itemPauseSimulation);
    menuParc.add(itemReprendreSimulation);
    
    menuBar.add(menuParc);
    
    // ========== MENU VISITEURS (NOUVEAU) ==========
    JMenu menuVisiteurs = new JMenu(" Visiteurs");
    
    JMenuItem itemAjouterVisiteur = new JMenuItem("Ajouter un visiteur");
    itemAjouterVisiteur.addActionListener(e -> ajouterVisiteurManuel());
    
    JMenuItem itemVoirVisiteurs = new JMenuItem("Voir tous les visiteurs");
    itemVoirVisiteurs.addActionListener(e -> visualiserVisiteurs());
    
    menuVisiteurs.add(itemAjouterVisiteur);
    menuVisiteurs.add(itemVoirVisiteurs);
    
    menuBar.add(menuVisiteurs);
    
    // ========== MENU MÉTÉO (NOUVEAU) ==========
    JMenu menuMeteo = new JMenu("Météo");
    
    for (main.java.com.parcattractions.enums.Meteo meteo : 
         main.java.com.parcattractions.enums.Meteo.values()) {
        JMenuItem itemMeteo = new JMenuItem(meteo.toString());
        itemMeteo.addActionListener(e -> changerMeteo(meteo));
        menuMeteo.add(itemMeteo);
    }
    
    menuBar.add(menuMeteo);
    
    // ========== MENU MANAGER ==========
    JMenu menuManager = new JMenu("Manager");
    
    JMenuItem itemGérerPersonnel = new JMenuItem("Gérer le personnel");
    itemGérerPersonnel.addActionListener(e -> {
        DialogGestionPersonnel d = new DialogGestionPersonnel(this, gestionnaireParc);
        d.setVisible(true);
        rafraichirTousLesPanels();
    });
    menuManager.add(itemGérerPersonnel);
    
    JMenu subLancerEvenement = new JMenu("Lancer un événement");
    
    JMenuItem itemHappyHour = new JMenuItem("Happy Hour");
    itemHappyHour.addActionListener(e -> {
        gestionnaireEvenements.lancerHappyHourManuel();
        JOptionPane.showMessageDialog(this, "Happy Hour lancé manuellement!", 
            "Événement", JOptionPane.INFORMATION_MESSAGE);
        rafraichirTousLesPanels();
    });
    
    JMenuItem itemParade = new JMenuItem("Parade");
    itemParade.addActionListener(e -> {
        gestionnaireEvenements.lancerParadeManuel();
        JOptionPane.showMessageDialog(this, "Parade lancée manuellement!", 
            "Événement", JOptionPane.INFORMATION_MESSAGE);
        rafraichirTousLesPanels();
    });
    
    JMenuItem itemSpectacle = new JMenuItem("Spectacle nocturne");
    itemSpectacle.addActionListener(e -> {
        gestionnaireEvenements.lancerSpectacleNocturneManuel();
        JOptionPane.showMessageDialog(this, "Spectacle nocturne lancé manuellement!", 
            "Événement", JOptionPane.INFORMATION_MESSAGE);
        rafraichirTousLesPanels();
    });
    
    subLancerEvenement.add(itemHappyHour);
    subLancerEvenement.add(itemParade);
    subLancerEvenement.add(itemSpectacle);
    menuManager.add(subLancerEvenement);
    
    menuBar.add(menuManager);
    
    // ========== MENU ATTRACTIONS (NOUVEAU) ==========
    JMenu menuAttractions = new JMenu("Attractions");
    
    JMenuItem itemOuvrirAttraction = new JMenuItem("Ouvrir une attraction");
    itemOuvrirAttraction.addActionListener(e -> ouvrirAttractionManuel());
    
    JMenuItem itemFermerAttraction = new JMenuItem("Fermer une attraction");
    itemFermerAttraction.addActionListener(e -> fermerAttractionManuel());
    
    JMenuItem itemMettreEnPanne = new JMenuItem("Simuler une panne");
    itemMettreEnPanne.addActionListener(e -> simulerPanneAttraction());
    
    menuAttractions.add(itemOuvrirAttraction);
    menuAttractions.add(itemFermerAttraction);
    menuAttractions.addSeparator();
    menuAttractions.add(itemMettreEnPanne);
    
    menuBar.add(menuAttractions);
    
    // ========== MENU AFFICHAGE ==========
    JMenu menuAffichage = new JMenu("Affichage");
    
    JMenuItem itemTableauBord = new JMenuItem("Tableau de bord");
    itemTableauBord.addActionListener(e -> afficherVue(CARD_DASHBOARD));
    
    JMenuItem itemGestion = new JMenuItem("Gestion");
    itemGestion.addActionListener(e -> afficherVue(CARD_GESTION));
    
    JMenuItem itemTransactions = new JMenuItem("Transactions");
    itemTransactions.addActionListener(e -> afficherVue(CARD_TRANSACTIONS));
    
    menuAffichage.add(itemTableauBord);
    menuAffichage.add(itemGestion);
    menuAffichage.add(itemTransactions);
    
    menuBar.add(menuAffichage);
    
    // ========== MENU RAPPORTS ==========
JMenu menuRapports = new JMenu(" Rapports");

JMenuItem itemAfficherRapports = new JMenuItem(" Afficher les rapports");
itemAfficherRapports.addActionListener(e -> {
    DialogRapports d = new DialogRapports(this, gestionnaireParc);
    d.setVisible(true);
});
menuRapports.add(itemAfficherRapports);

// NOUVEAU : Rafraîchir tous les panels
JMenuItem itemRafraichirTout = new JMenuItem(" Rafraîchir l'affichage");
itemRafraichirTout.addActionListener(e -> {
    rafraichirTousLesPanels();
    JOptionPane.showMessageDialog(this,
        " Tous les panneaux ont été rafraîchis!",
        "Rafraîchissement",
        JOptionPane.INFORMATION_MESSAGE);
});
menuRapports.add(itemRafraichirTout);

menuBar.add(menuRapports);
    
    // ========== MENU AIDE ==========
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
    /**
 * Pause la simulation
 */
private void pauseSimulation() {
    gestionnaireParc.pauseSimulation();
    JOptionPane.showMessageDialog(this, 
        "Simulation mise en pause", 
        "Pause", 
        JOptionPane.INFORMATION_MESSAGE);
    rafraichirTousLesPanels();
}

/**
 * Reprend la simulation
 */
private void reprendreSimulation() {
    gestionnaireParc.reprendreSimulation();
    JOptionPane.showMessageDialog(this, 
        "Simulation reprise", 
        "Reprise", 
        JOptionPane.INFORMATION_MESSAGE);
    rafraichirTousLesPanels();
}

/**
 * Ajoute un visiteur manuellement
 */
private void ajouterVisiteurManuel() {
    if (!gestionnaireParc.estOuvert()) {
        JOptionPane.showMessageDialog(this, 
            "Le parc doit être ouvert pour ajouter des visiteurs", 
            "Parc fermé", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    DialogAjoutVisiteur dialog = new DialogAjoutVisiteur(this);
    dialog.setVisible(true);
    
    if (dialog.estValide()) {
        rafraichirTousLesPanels();
        JOptionPane.showMessageDialog(this, 
            "Visiteur ajouté avec succès!", 
            "Succès", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}

/**
 * Visualise tous les visiteurs
 */
private void visualiserVisiteurs() {
    java.util.List<main.java.com.parcattractions.models.visiteurs.Visiteur> visiteurs = 
        gestionnaireParc.getVisiteurs();
    
    if (visiteurs.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Aucun visiteur actuellement dans le parc", 
            "Liste vide", 
            JOptionPane.INFORMATION_MESSAGE);
        return;
    }
    
    StringBuilder sb = new StringBuilder("Visiteurs actuels (" + visiteurs.size() + "):\n\n");
    for (main.java.com.parcattractions.models.visiteurs.Visiteur v : visiteurs) {
        sb.append(String.format("• %s - Âge: %d ans, Satisfaction: %d%%, État: %s\n",
            v.getNomVisiteur(), v.getAge(), v.getSatisfaction(), v.getEtat()));
    }
    
    javax.swing.JTextArea textArea = new javax.swing.JTextArea(sb.toString());
    textArea.setEditable(false);
    textArea.setFont(UIStyles.MONOSPACE_FONT);
    javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
    scrollPane.setPreferredSize(new java.awt.Dimension(600, 400));
    
    JOptionPane.showMessageDialog(this, scrollPane, 
        "Liste des Visiteurs", JOptionPane.INFORMATION_MESSAGE);
}

/**
 * Change la météo manuellement
 */
private void changerMeteo(main.java.com.parcattractions.enums.Meteo meteo) {
    gestionnaireParc.setMeteoActuelle(meteo);
    JOptionPane.showMessageDialog(this, 
        "Météo changée à : " + meteo, 
        "Météo", 
        JOptionPane.INFORMATION_MESSAGE);
    rafraichirTousLesPanels();
}

/**
 * Ouvre une attraction manuellement
 */
private void ouvrirAttractionManuel() {
    String[] attractionNames = gestionnaireParc.getAttractions().stream()
        .map(a -> a.getNom())
        .toArray(String[]::new);
    
    if (attractionNames.length == 0) {
        JOptionPane.showMessageDialog(this, 
            "Aucune attraction disponible", 
            "Erreur", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String selected = (String) JOptionPane.showInputDialog(
        this,
        "Sélectionnez l'attraction à ouvrir:",
        "Ouvrir Attraction",
        JOptionPane.QUESTION_MESSAGE,
        null,
        attractionNames,
        attractionNames[0]
    );
    
    if (selected != null) {
        main.java.com.parcattractions.models.attractions.Attraction attraction = 
            gestionnaireParc.getAttractionParNom(selected);
        if (attraction != null) {
            attraction.ouvrir();
            JOptionPane.showMessageDialog(this, 
                "Attraction '" + selected + "' ouverte", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            rafraichirTousLesPanels();
        }
    }
}

/**
 * Ferme une attraction manuellement
 */
private void fermerAttractionManuel() {
    String[] attractionNames = gestionnaireParc.getAttractions().stream()
        .map(a -> a.getNom())
        .toArray(String[]::new);
    
    if (attractionNames.length == 0) {
        JOptionPane.showMessageDialog(this, 
            "Aucune attraction disponible", 
            "Erreur", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String selected = (String) JOptionPane.showInputDialog(
        this,
        "Sélectionnez l'attraction à fermer:",
        "Fermer Attraction",
        JOptionPane.QUESTION_MESSAGE,
        null,
        attractionNames,
        attractionNames[0]
    );
    
    if (selected != null) {
        main.java.com.parcattractions.models.attractions.Attraction attraction = 
            gestionnaireParc.getAttractionParNom(selected);
        if (attraction != null) {
            attraction.fermer();
            JOptionPane.showMessageDialog(this, 
                "Attraction '" + selected + "' fermée", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            rafraichirTousLesPanels();
        }
    }
}

/**
 * Simule une panne sur une attraction
 */
private void simulerPanneAttraction() {
    String[] attractionNames = gestionnaireParc.getAttractions().stream()
        .map(a -> a.getNom())
        .toArray(String[]::new);
    
    if (attractionNames.length == 0) {
        JOptionPane.showMessageDialog(this, 
            "Aucune attraction disponible", 
            "Erreur", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    String selected = (String) JOptionPane.showInputDialog(
        this,
        "Sélectionnez l'attraction à mettre en panne:",
        "Simuler Panne",
        JOptionPane.WARNING_MESSAGE,
        null,
        attractionNames,
        attractionNames[0]
    );
    
    if (selected != null) {
        main.java.com.parcattractions.models.attractions.Attraction attraction = 
            gestionnaireParc.getAttractionParNom(selected);
        if (attraction != null) {
            attraction.mettrePanne();
            JOptionPane.showMessageDialog(this, 
                "Panne simulée sur '" + selected + "'!\nUn technicien sera appelé.", 
                "Panne", 
                JOptionPane.WARNING_MESSAGE);
            rafraichirTousLesPanels();
        }
    }
}
}
