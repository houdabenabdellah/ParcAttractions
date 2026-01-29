import main.java.com.parcattractions.controllers.GestionnaireEvenements;
import main.java.com.parcattractions.controllers.GestionnaireMeteo;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.controllers.GenerateurVisiteurs;
import main.java.com.parcattractions.views.MainFrame;
import main.java.com.parcattractions.utils.Logger;

/**
 * Point d'entrée principal de l'application
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            Logger.logInfo("═══════════════════════════════════════");
            Logger.logInfo("   PARC D'ATTRACTIONS - SIMULATION");
            Logger.logInfo("═══════════════════════════════════════");
            
            // Initialiser le gestionnaire du parc
            GestionnaireParc gestionnaireParc = GestionnaireParc.getInstance();
            
            // Créer et démarrer les gestionnaires
            GenerateurVisiteurs generateurVisiteurs = new GenerateurVisiteurs(gestionnaireParc);
            GestionnaireMeteo gestionnaireMeteo = new GestionnaireMeteo(gestionnaireParc);
            GestionnaireEvenements gestionnaireEvenements = 
                new GestionnaireEvenements(gestionnaireParc);
            gestionnaireParc.setGestionnaireEvenements(gestionnaireEvenements);
            
            // Démarrer les gestionnaires
            gestionnaireMeteo.start();
            gestionnaireEvenements.start();
            
            // Créer et afficher la fenêtre principale
            MainFrame mainFrame = new MainFrame(gestionnaireParc, generateurVisiteurs,
                gestionnaireEvenements);
            mainFrame.setVisible(true);
            
            Logger.logInfo("Application démarrée avec succès");
            
        } catch (Exception e) {
            Logger.logException("Erreur au démarrage", e);
            System.err.println("Erreur fatale: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
