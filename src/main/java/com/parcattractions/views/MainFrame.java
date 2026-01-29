package main.java.com.parcattractions.views;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.ImageIcon;
import main.java.com.parcattractions.controllers.GenerateurVisiteurs;
import main.java.com.parcattractions.controllers.GestionnaireEvenements;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.exceptions.systeme.ParcFermeException;
import main.java.com.parcattractions.utils.Logger;
import java.awt.Color;
import java.awt.Font;

/**
 * Fenêtre principale de l'application
 */
public class MainFrame extends JFrame {
    
    private final GestionnaireParc gestionnaireParc;
    private final GenerateurVisiteurs generateurVisiteurs;
    private final GestionnaireEvenements gestionnaireEvenements;
    
    private PanelDashboard panelDashboard;
    private PanelAttractions panelAttractions;
    private PanelStatistiques panelStatistiques;
    private PanelNotifications panelNotifications;
    private PanelVueParc panelVueParc;
    
    // Toolbar controls
    private JToolBar toolBar;
    private JButton btnStart;
    private JButton btnPause;
    private JButton btnResume;
    private JButton btnStep;
    private JButton btnStop;
    // Speed control
    private JSlider speedSlider;
    
    // Secondary actions toolbar removed — now in PanelAttractions

    private boolean simulationEnCours;
    
    /**
     * Constructeur
     */
    public MainFrame(GestionnaireParc gestionnaireParc, GenerateurVisiteurs generateurVisiteurs,
            GestionnaireEvenements gestionnaireEvenements) {
        super("Parc d'Attractions - Simulation");
        
        this.gestionnaireParc = gestionnaireParc;
        this.generateurVisiteurs = generateurVisiteurs;
        this.gestionnaireEvenements = gestionnaireEvenements;
        this.simulationEnCours = false;
        
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
        // Toolbar
        creerToolBar();
        
        // Layout principal
        setLayout(new BorderLayout());
        
        // Panels
        panelDashboard = new PanelDashboard(gestionnaireParc);
        panelAttractions = new PanelAttractions(gestionnaireParc);
        panelStatistiques = new PanelStatistiques(gestionnaireParc);
        panelNotifications = new PanelNotifications();
        panelVueParc = new PanelVueParc(gestionnaireParc);
        
        // Organisation
        JPanel panelOuest = new JPanel(new BorderLayout());
        panelOuest.setBackground(UIStyles.BG_LIGHT);
        panelOuest.add(panelDashboard, BorderLayout.NORTH);
        panelOuest.add(panelStatistiques, BorderLayout.CENTER);
        
        JPanel panelEst = new JPanel(new BorderLayout());
        panelEst.setBackground(UIStyles.BG_LIGHT);
        panelEst.add(panelNotifications, BorderLayout.CENTER);
        panelEst.add(panelVueParc, BorderLayout.SOUTH);
        
        add(panelOuest, BorderLayout.WEST);
        add(panelAttractions, BorderLayout.CENTER);
        add(panelEst, BorderLayout.EAST);
    }

    /**
     * Crée la barre d'outils avec boutons de contrôle
     */
    private void creerToolBar() {
        toolBar = new JToolBar();
        toolBar.setBackground(UIStyles.PRIMARY_COLOR);
        toolBar.setRollover(true);
        
        btnStart = new JButton(loadIcon("start"));
        btnStart.setToolTipText("Démarrer la simulation");
        btnStart.addActionListener(e -> demarrerSimulation());
        UIStyles.stylePrimaryButton(btnStart);
        toolBar.add(btnStart);

        btnPause = new JButton(loadIcon("pause"));
        btnPause.setToolTipText("Mettre en pause");
        btnPause.addActionListener(e -> {
            if (!simulationEnCours) {
                JOptionPane.showMessageDialog(this, "La simulation n'est pas démarrée", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            gestionnaireParc.pauseSimulation();
            updateToolbarState();
        });
        UIStyles.stylePrimaryButton(btnPause);
        toolBar.add(btnPause);

        btnResume = new JButton(loadIcon("resume"));
        btnResume.setToolTipText("Reprendre la simulation");
        btnResume.addActionListener(e -> {
            if (!simulationEnCours) {
                JOptionPane.showMessageDialog(this, "La simulation n'est pas démarrée", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            gestionnaireParc.reprendreSimulation();
            updateToolbarState();
        });
        UIStyles.stylePrimaryButton(btnResume);
        toolBar.add(btnResume);

        btnStep = new JButton(loadIcon("step"));
        btnStep.setToolTipText("Avancer d'un pas");
        btnStep.addActionListener(e -> {
            if (!simulationEnCours) {
                JOptionPane.showMessageDialog(this, "La simulation n'est pas démarrée", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            gestionnaireParc.stepSimulation();
        });
        UIStyles.stylePrimaryButton(btnStep);
        toolBar.add(btnStep);

        btnStop = new JButton(loadIcon("stop"));
        btnStop.setToolTipText("Arrêter la simulation");
        btnStop.addActionListener(e -> arreterSimulation());
        UIStyles.styleAccentButton(btnStop);
        toolBar.add(btnStop);

        // Speed slider (1..60, default 10)
        speedSlider = new JSlider(1, 60, 10);
        speedSlider.setToolTipText("Vitesse de simulation (minutes simulées par seconde)");
        speedSlider.setPaintTicks(true);
        speedSlider.setMajorTickSpacing(10);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setMaximumSize(new java.awt.Dimension(200, 50));
        speedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = speedSlider.getValue();
                if (gestionnaireParc != null && gestionnaireParc.getHorloge() != null) {
                    gestionnaireParc.getHorloge().setVitesseSimulation(value);
                }
            }
        });
        toolBar.add(speedSlider);

        add(toolBar, BorderLayout.NORTH);
        updateToolbarState();
    }

    /**
     * Crée la barre d'actions pour attractions / employés
     */
    // Secondary actions moved into PanelAttractions

    /**
     * Charge une icône depuis `src/resources/images/<name>.png` ou utilise un fallback embarqué.
     */
    private ImageIcon loadIcon(String name) {
        try {
            String path = "/resources/images/" + name + ".png";
            var res = getClass().getResource(path);
            Image img = null;
            if (res != null) {
                img = ImageIO.read(res);
            } else {
                // Fallback: tiny 1x1 PNG (transparent) as placeholder
                String b64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAASsJTYQAAAAASUVORK5CYII=";
                byte[] bytes = Base64.getDecoder().decode(b64);
                img = ImageIO.read(new ByteArrayInputStream(bytes));
            }
            if (img != null) {
                Image scaled = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
        } catch (IOException ignored) {
        }
        return new ImageIcon();
    }

    /**
     * Met à jour l'état activé/désactivé des boutons selon l'état de la simulation
     */
    private void updateToolbarState() {
        boolean started = simulationEnCours;
        boolean paused = gestionnaireParc != null && gestionnaireParc.estSimulationEnPause();

        btnStart.setEnabled(!started);
        btnPause.setEnabled(started && !paused);
        btnResume.setEnabled(started && paused);
        btnStep.setEnabled(started);
        btnStop.setEnabled(started);
    }
    
    /**
     * Crée le menu
     */
    private void creerMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Simulation
        JMenu menuSimulation = new JMenu("Simulation");
        
        JMenuItem itemDemarrer = new JMenuItem("Démarrer");
        itemDemarrer.addActionListener(e -> demarrerSimulation());
        menuSimulation.add(itemDemarrer);
        JMenuItem itemPause = new JMenuItem("Pause");
        itemPause.addActionListener(e -> {
            if (!simulationEnCours) {
                JOptionPane.showMessageDialog(this, "La simulation n'est pas démarrée", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            gestionnaireParc.pauseSimulation();
            updateToolbarState();
        });
        menuSimulation.add(itemPause);

        JMenuItem itemReprendre = new JMenuItem("Reprendre");
        itemReprendre.addActionListener(e -> {
            if (!simulationEnCours) {
                JOptionPane.showMessageDialog(this, "La simulation n'est pas démarrée", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            gestionnaireParc.reprendreSimulation();
            updateToolbarState();
        });
        menuSimulation.add(itemReprendre);

        JMenuItem itemPas = new JMenuItem("Pas");
        itemPas.addActionListener(e -> {
            if (!simulationEnCours) {
                JOptionPane.showMessageDialog(this, "La simulation n'est pas démarrée", "Information", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            gestionnaireParc.stepSimulation();
            updateToolbarState();
        });
        menuSimulation.add(itemPas);

        JMenuItem itemArreter = new JMenuItem("Arrêter");
        itemArreter.addActionListener(e -> arreterSimulation());
        menuSimulation.add(itemArreter);
        
        menuSimulation.addSeparator();
        
        JMenuItem itemQuitter = new JMenuItem("Quitter");
        itemQuitter.addActionListener(e -> quitter());
        menuSimulation.add(itemQuitter);
        
        menuBar.add(menuSimulation);
        
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
        
        // Menu Aide
        JMenu menuAide = new JMenu("Aide");
        JMenuItem itemAPropos = new JMenuItem("À propos");
        itemAPropos.addActionListener(e -> afficherAPropos());
        menuAide.add(itemAPropos);
        
        menuBar.add(menuAide);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Démarre la simulation
     */
    private void demarrerSimulation() {
        if (simulationEnCours) {
            JOptionPane.showMessageDialog(this, 
                "La simulation est déjà en cours", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            gestionnaireParc.ouvrirParc();
            // Apply speed slider value to horloge if available
            if (gestionnaireParc.getHorloge() != null) {
                gestionnaireParc.getHorloge().setVitesseSimulation(speedSlider.getValue());
            }
            generateurVisiteurs.start();
            simulationEnCours = true;
            
            // Démarrer rafraîchissement des panels
            demarrerRafraichissement();
            updateToolbarState();
            
            JOptionPane.showMessageDialog(this, 
                "Simulation démarrée avec succès !", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (ParcFermeException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Arrête la simulation
     */
    private void arreterSimulation() {
        if (!simulationEnCours) {
            return;
        }
        
        int confirmation = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir arrêter la simulation ?", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            generateurVisiteurs.arreter();
            gestionnaireParc.fermerParc();
            simulationEnCours = false;
                updateToolbarState();
            
            JOptionPane.showMessageDialog(this, 
                "Simulation arrêtée", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Démarre le rafraîchissement périodique des panels
     */
    private void demarrerRafraichissement() {
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            SwingUtilities.invokeLater(() -> {
                panelDashboard.rafraichir();
                panelAttractions.rafraichir();
                panelStatistiques.rafraichir();
                panelNotifications.rafraichir();
                panelVueParc.rafraichir();
            });
        });
        timer.start();
    }
    
    /**
     * Affiche la boîte À propos
     */
    private void afficherAPropos() {
        String message = "Parc d'Attractions - Simulation\n\n" +
            "Version 1.0\n" +
            "Simulation complète d'un parc d'attractions\n" +
            "avec gestion des visiteurs, attractions, employés,\n" +
            "météo et événements spéciaux.";
        
        JOptionPane.showMessageDialog(this, message, 
            "À propos", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Configure la fermeture de la fenêtre
     */
    private void configurerFermeture() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quitter();
            }
        });
    }
    
    /**
     * Quitte l'application
     */
    private void quitter() {
        if (simulationEnCours) {
            int confirmation = JOptionPane.showConfirmDialog(this, 
                "La simulation est en cours. Voulez-vous vraiment quitter ?", 
                "Confirmation", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }
            
            arreterSimulation();
        }
        
        Logger.close();
        System.exit(0);
    }
}
