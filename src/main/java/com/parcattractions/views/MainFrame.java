package main.java.com.parcattractions.views;

import java.awt.*;
import javax.swing.*;
import main.java.com.parcattractions.controllers.*;
import main.java.com.parcattractions.exceptions.systeme.ParcFermeException;
import main.java.com.parcattractions.resources.styles.UIStyles;


public class MainFrame extends JFrame {
    private final GestionnaireParc gp;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private PanelDashboard dashboardPanel;

    public MainFrame(GestionnaireParc gp, GestionnaireEvenements ge) {
        super("Grand Parc Simulator 2026");
        this.gp = gp;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        
        creerBarreMenuFancy();
        initialiserInterface();
        setLocationRelativeTo(null);
    }

    private void creerBarreMenuFancy() {
        JMenuBar mb = new JMenuBar();
        mb.setBackground(UIStyles.DUSK_BLUE);
        mb.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // === MENU PARC ===
        JMenu mParc = creerMenuSimple("Le Parc");
        JMenuItem iOuvrir = new JMenuItem("Ouvrir le Parc");
        iOuvrir.addActionListener(e -> {
            try {
                gp.ouvrirParc();
                rafraichirTousLesPanels();
            } catch (ParcFermeException ex) {
                JOptionPane.showMessageDialog(MainFrame.this, 
                    "Impossible d'ouvrir le parc: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JMenuItem iFermer = new JMenuItem("Fermer le Parc");
        iFermer.addActionListener(e -> {
            try {
                gp.fermerParc();
                rafraichirTousLesPanels();
                JOptionPane.showMessageDialog(MainFrame.this, "Parc fermé avec succès", "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "Erreur fermeture: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JMenuItem iPause = new JMenuItem("Pause/Reprendre");
        iPause.addActionListener(e -> {
            try {
                if (gp.estSimulationEnPause()) {
                    gp.reprendreSimulation();
                    JOptionPane.showMessageDialog(MainFrame.this, "Simulation reprise", "Info", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    gp.pauseSimulation();
                    JOptionPane.showMessageDialog(MainFrame.this, "Simulation en pause", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
                rafraichirTousLesPanels();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JMenuItem iAvancerPas = new JMenuItem("Avancer le temps (1 pas)");
        iAvancerPas.addActionListener(e -> {
            try {
                if (gp.getHorloge() != null && gp.getHorloge().estEnPause()) {
                    gp.getHorloge().avancerUnPas();
                    rafraichirTousLesPanels();
                } else {
                    JOptionPane.showMessageDialog(MainFrame.this, "Mettez la simulation en pause pour avancer d'un pas.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        mParc.add(iOuvrir);
        mParc.addSeparator();
        mParc.add(iFermer);
        mParc.add(iPause);
        mParc.add(iAvancerPas);
        mb.add(mParc);
        
        // === MENU VISITEURS ===
        JMenu mVisiteurs = creerMenuSimple("Visiteurs");
        JMenuItem iAjouterVisiteur = new JMenuItem("Ajouter un visiteur");
        iAjouterVisiteur.addActionListener(e -> {
            DialogAjoutVisiteur dialog = new DialogAjoutVisiteur(MainFrame.this);
            dialog.setVisible(true);
            rafraichirTousLesPanels();
        });
        
        JMenuItem iListeVisiteurs = new JMenuItem("Consulter les visiteurs");
        iListeVisiteurs.addActionListener(e -> {
            DialogGestionVisiteurs dialog = new DialogGestionVisiteurs(MainFrame.this, gp);
            dialog.setVisible(true);
            rafraichirTousLesPanels();
        });
        
        mVisiteurs.add(iAjouterVisiteur);
        mVisiteurs.add(iListeVisiteurs);
        mb.add(mVisiteurs);
        
        // === MENU ATTRACTIONS ===
        JMenu mAttractions = creerMenuSimple("Attractions");
        JMenuItem iOuvrirAttraction = new JMenuItem("Ouvrir");
        iOuvrirAttraction.addActionListener(e -> {
            JOptionPane.showMessageDialog(MainFrame.this, "Utilisez le panneau des attractions pour gérer", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        JMenuItem iFermerAttraction = new JMenuItem("Fermer");
        iFermerAttraction.addActionListener(e -> {
            JOptionPane.showMessageDialog(MainFrame.this, "Utilisez le panneau des attractions pour gérer", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        mAttractions.add(iOuvrirAttraction);
        mAttractions.add(iFermerAttraction);
        mb.add(mAttractions);
        
        // === MENU MÉTÉO ===
        JMenu mMeteo = creerMenuSimple("Météo");
        JMenuItem iChangerMeteo = new JMenuItem("Changer la météo");
        iChangerMeteo.addActionListener(e -> {
            new DialogConfiguration(MainFrame.this, gp).setVisible(true);
            rafraichirTousLesPanels();
        });
        mMeteo.add(iChangerMeteo);
        mb.add(mMeteo);
        
        // === MENU MANAGER ===
        JMenu mManager = creerMenuSimple("Manager");
        JMenuItem iEvenement = new JMenuItem("Lancer un événement");
        iEvenement.addActionListener(e -> {
            if (!gp.estOuvert()) {
                JOptionPane.showMessageDialog(MainFrame.this, "Le parc doit être ouvert", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] options = {"Happy Hour", "Parade", "Spectacle Nocturne", "Annuler"};
            int choice = JOptionPane.showOptionDialog(MainFrame.this, "Quel événement lancer?", "Événements",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (choice >= 0 && choice < 3) {
                JOptionPane.showMessageDialog(MainFrame.this, "Événement lancé!", "Info", JOptionPane.INFORMATION_MESSAGE);
                rafraichirTousLesPanels();
            }
        });
        
        JMenuItem iGestionPersonnel = new JMenuItem("Gérer le personnel");
        iGestionPersonnel.addActionListener(e -> {
            new DialogGestionPersonnel(MainFrame.this, gp).setVisible(true);
            rafraichirTousLesPanels();
        });
        
        mManager.add(iEvenement);
        mManager.add(iGestionPersonnel);
        mb.add(mManager);
        
        // === MENU SERVICES ===
        JMenu mServices = creerMenuSimple("Services");
        JMenuItem iGestionRestaurants = new JMenuItem("Gérer les restaurants");
        iGestionRestaurants.addActionListener(e -> {
            // Créer dialog avec ServiceManager depuis GestionnaireParc
            var sm = gp.getServiceManager();
            new DialogGestionRestaurants(MainFrame.this, sm, gp).setVisible(true);
            rafraichirTousLesPanels();
        });
        JMenuItem iGestionBoutiques = new JMenuItem("Gérer les boutiques");
        iGestionBoutiques.addActionListener(e -> {
            var sm = gp.getServiceManager();
            new DialogGestionBoutiques(MainFrame.this, sm, gp).setVisible(true);
            rafraichirTousLesPanels();
        });
        mServices.add(iGestionRestaurants);
        mServices.add(iGestionBoutiques);
        mb.add(mServices);
        
        // === MENU GESTION RH ===
        JMenu mGestionRH = creerMenuSimple("Gestion RH");
        JMenuItem iAjouterEmploye = new JMenuItem("Ajouter un employé");
        iAjouterEmploye.addActionListener(e -> {
            new DialogAjoutEmploye(MainFrame.this).setVisible(true);
            rafraichirTousLesPanels();
        });
        JMenuItem iGererRH = new JMenuItem("Gérer les employés");
        iGererRH.addActionListener(e -> {
            new DialogGestionPersonnel(MainFrame.this, gp).setVisible(true);
            rafraichirTousLesPanels();
        });
        mGestionRH.add(iAjouterEmploye);
        mGestionRH.add(iGererRH);
        mb.add(mGestionRH);
        
        // === MENU AFFICHAGE ===
        JMenu mAffichage = creerMenuSimple("Affichage");
        JMenuItem iDashboard = new JMenuItem("Tableau de Bord");
        iDashboard.addActionListener(e -> cardLayout.show(contentPanel, "dashboard"));
        JMenuItem iNotifications = new JMenuItem("Notifications & Alertes");
        iNotifications.addActionListener(e -> cardLayout.show(contentPanel, "notifications"));
        JMenuItem iTransactions = new JMenuItem("Moniteur Financier");
        iTransactions.addActionListener(e -> cardLayout.show(contentPanel, "transactions"));
        JMenuItem iRapports = new JMenuItem("Rapports & Statistiques");
        iRapports.addActionListener(e -> cardLayout.show(contentPanel, "rapports"));
        
        mAffichage.add(iDashboard);
        mAffichage.addSeparator();
        mAffichage.add(iNotifications);
        mAffichage.add(iTransactions);
        mAffichage.add(iRapports);
        mb.add(mAffichage);
        
        setJMenuBar(mb);
    }

    private JMenu creerMenuSimple(String title) {
        JMenu m = new JMenu(title);
        m.setForeground(Color.WHITE);
        m.setFont(UIStyles.HEADER_FONT);
        return m;
    }

    private void initialiserInterface() {
        getContentPane().setBackground(UIStyles.BG_WINDOW); // Fond global gris modern
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIStyles.BG_WINDOW);

        // === VUE TABLEAU DE BORD ===
        JPanel dashboardView = creerVueDashboard();
        contentPanel.add(dashboardView, "dashboard");
        
        // === VUE NOTIFICATIONS ===
        JPanel notificationsView = new JPanel(new BorderLayout());
        notificationsView.setBackground(UIStyles.BG_WINDOW);
        notificationsView.add(new PanelNotifications(), BorderLayout.CENTER);
        contentPanel.add(notificationsView, "notifications");
        
        // === VUE TRANSACTIONS ===
        JPanel transactionsView = new JPanel(new BorderLayout());
        transactionsView.setBackground(UIStyles.BG_WINDOW);
        transactionsView.add(new PanelTransactions(), BorderLayout.CENTER);
        contentPanel.add(transactionsView, "transactions");
        
        // === VUE RAPPORTS ===
        JPanel rapportsView = new JPanel(new BorderLayout());
        rapportsView.setBackground(UIStyles.BG_WINDOW);
        rapportsView.add(new PanelRapports(gp), BorderLayout.CENTER);
        contentPanel.add(rapportsView, "rapports");
        
        cardLayout.show(contentPanel, "dashboard");
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel creerVueDashboard() {
        JPanel mainView = new JPanel(new BorderLayout(20, 20));
        mainView.setBackground(UIStyles.BG_WINDOW);
        mainView.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        dashboardPanel = new PanelDashboard(gp);
        PanelStatistiques stats = new PanelStatistiques(gp);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setOpaque(false);
        leftPanel.add(dashboardPanel, BorderLayout.NORTH);
        leftPanel.add(stats, BorderLayout.CENTER);

        mainView.add(leftPanel, BorderLayout.WEST);
        mainView.add(new PanelAttractions(gp, this), BorderLayout.CENTER);

        return mainView;
    }

    private void rafraichirTousLesPanels() {
        if (dashboardPanel != null) {
            dashboardPanel.rafraichir();
        }
    }
}