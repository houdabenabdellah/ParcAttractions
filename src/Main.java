import javax.swing.*;
import main.java.com.parcattractions.controllers.GestionnaireEvenements;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.utils.DataManager;
import main.java.com.parcattractions.utils.Logger;
import main.java.com.parcattractions.utils.ModeApplication;
import main.java.com.parcattractions.utils.ModeConfig;
import main.java.com.parcattractions.views.MainFrame;

/**
 * Point d'entrée principal de l'application - MODE MANUEL
 * Aucune génération automatique de visiteurs
 * Contrôle total par le gestionnaire
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            Logger.logInfo("═══════════════════════════════════════");
            Logger.logInfo("   PARC D'ATTRACTIONS - MODE MANUEL");
            Logger.logInfo("═══════════════════════════════════════");
            
            // Initialiser les répertoires de données
            DataManager.initializeDataDirectory();
            
            // MODE MANUEL FORCÉ (pas de sélection)
            ModeApplication mode = ModeApplication.GESTION;
            Logger.logInfo("Mode: MANUEL (Contrôle Total)");
            
            // Afficher la configuration
            ModeConfig.afficherConfig(mode);
            
            // Initialiser le gestionnaire du parc
            GestionnaireParc gestionnaireParc = GestionnaireParc.getInstance();
            
            // Charger les données existantes
            DataManager.loadAllData(gestionnaireParc);
            
            // Créer le gestionnaire d'événements (MAIS NE PAS LE DÉMARRER)
            GestionnaireEvenements gestionnaireEvenements = 
                new GestionnaireEvenements(gestionnaireParc);
            gestionnaireParc.setGestionnaireEvenements(gestionnaireEvenements);
            
            // NE PAS démarrer le gestionnaire d'événements automatiquement
            // gestionnaireEvenements.start(); // ← LIGNE COMMENTÉE POUR MODE MANUEL
            Logger.logInfo("Gestionnaire d'événements créé (mode manuel - non démarré)");
            
            // ═══════════════════════════════════════════════════════════════
            // IMPORTANT: PAS DE GenerateurVisiteurs en mode manuel
            // Les visiteurs seront ajoutés manuellement via l'interface
            // ═══════════════════════════════════════════════════════════════
            Logger.logInfo("Mode manuel: Pas de génération automatique de visiteurs");
            Logger.logInfo("Utilisez le menu 'Visiteurs → Ajouter un visiteur' pour ajouter des visiteurs");
            
            // Créer et afficher la fenêtre principale
            MainFrame mainFrame = new MainFrame(gestionnaireParc, gestionnaireEvenements);
            mainFrame.setVisible(true);
            
            // NE PAS ouvrir le parc automatiquement au démarrage
            // Le gestionnaire doit l'ouvrir manuellement via le menu
            Logger.logInfo("═══════════════════════════════════════");
            Logger.logInfo("   MODE MANUEL ACTIVÉ");
            Logger.logInfo("   Le parc est FERMÉ au démarrage");
            Logger.logInfo("   Utilisez 'Menu Parc → Ouvrir le parc'");
            Logger.logInfo("═══════════════════════════════════════");

            // Configurer la sauvegarde à la fermeture
            mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    Logger.logInfo("═══════════════════════════════════════");
                    Logger.logInfo("   FERMETURE DE L'APPLICATION");
                    Logger.logInfo("═══════════════════════════════════════");
                    Logger.logInfo("Sauvegarde des données en cours...");
                    DataManager.saveAllData(gestionnaireParc);
                    Logger.logInfo("Données sauvegardées avec succès");
                    Logger.logInfo("Application fermée");
                    Logger.logInfo("═══════════════════════════════════════");
                    System.exit(0);
                }
            });
            
            Logger.logInfo("Application démarrée avec succès en MODE MANUEL");
            Logger.logInfo("Aucun générateur de visiteurs actif");
            Logger.logInfo("Aucune simulation automatique");
            
        } catch (Exception e) {
            Logger.logException("Erreur fatale au démarrage", e);
            System.err.println("╔════════════════════════════════════════╗");
            System.err.println("║   ERREUR FATALE AU DÉMARRAGE           ║");
            System.err.println("╚════════════════════════════════════════╝");
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            
            // Afficher un message d'erreur à l'utilisateur
            JOptionPane.showMessageDialog(null,
                "ERREUR FATALE\n\n" +
                "L'application n'a pas pu démarrer.\n\n" +
                "Erreur: " + e.getMessage() + "\n\n" +
                "Consultez les logs pour plus de détails.",
                "Erreur de Démarrage",
                JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
        }
    }
    
}