import javax.swing.*;
import main.java.com.parcattractions.controllers.GenerateurVisiteurs;
import main.java.com.parcattractions.controllers.GestionnaireEvenements;
import main.java.com.parcattractions.controllers.GestionnaireMeteo;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.utils.DataManager;
import main.java.com.parcattractions.utils.Logger;
import main.java.com.parcattractions.utils.ModeApplication;
import main.java.com.parcattractions.utils.ModeConfig;
import main.java.com.parcattractions.views.MainFrame;

/**
 * Point d'entrée principal de l'application
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            Logger.logInfo("═══════════════════════════════════════");
            Logger.logInfo("   PARC D'ATTRACTIONS");
            Logger.logInfo("═══════════════════════════════════════");
            
            // Initialiser les répertoires de données
            DataManager.initializeDataDirectory();
            
            // Sélectionner le mode de fonctionnement
            ModeApplication mode = selectMode();
            Logger.logInfo("Mode sélectionné: " + mode.getNom());
            
            // Afficher la configuration
            ModeConfig.afficherConfig(mode);
            
            // Initialiser le gestionnaire du parc
            GestionnaireParc gestionnaireParc = GestionnaireParc.getInstance();
            
            // Charger les données existantes
            DataManager.loadAllData(gestionnaireParc);
            
            // Créer les gestionnaires
            GenerateurVisiteurs generateurVisiteurs = new GenerateurVisiteurs(gestionnaireParc);
            GestionnaireMeteo gestionnaireMeteo = new GestionnaireMeteo(gestionnaireParc);
            GestionnaireEvenements gestionnaireEvenements = 
                new GestionnaireEvenements(gestionnaireParc);
            gestionnaireParc.setGestionnaireEvenements(gestionnaireEvenements);
            
            // Démarrer les services selon le mode
            gestionnaireMeteo.start();
            gestionnaireEvenements.start();
            
            if (mode == ModeApplication.SIMULATION || mode == ModeApplication.HYBRIDE) {
                generateurVisiteurs.start();
                Logger.logInfo("Générateur de visiteurs activé");
            }
            
            // Créer et afficher la fenêtre principale
            MainFrame mainFrame = new MainFrame(gestionnaireParc, generateurVisiteurs,
                gestionnaireEvenements);
            mainFrame.setMode(mode);
            mainFrame.setVisible(true);
            
            // Configurer la sauvegarde au fermeture
            mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    Logger.logInfo("Sauvegarde des données en cours...");
                    DataManager.saveAllData(gestionnaireParc);
                    Logger.logInfo("Application fermée");
                    System.exit(0);
                }
            });
            
            Logger.logInfo("Application démarrée avec succès");
            
        } catch (Exception e) {
            Logger.logException("Erreur au démarrage", e);
            System.err.println("Erreur fatale: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Affiche un dialogue pour sélectionner le mode de fonctionnement
     */
    private static ModeApplication selectMode() {
        Object[] modes = ModeApplication.values();
        Object selectedMode = JOptionPane.showInputDialog(
            null,
            "Sélectionnez le mode de fonctionnement:",
            "Parc d'Attractions",
            JOptionPane.QUESTION_MESSAGE,
            null,
            modes,
            modes[0]
        );
        
        if (selectedMode == null) {
            Logger.logWarning("Aucun mode sélectionné, utilisation du mode GESTION par défaut");
            return ModeApplication.GESTION;
        }
        
        return (ModeApplication) selectedMode;
    }
}
