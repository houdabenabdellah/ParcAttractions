package main.java.com.parcattractions.utils;

/**
 * Configuration des modes d'application
 */
public class ModeConfig {
    
    /**
     * Configuration pour le mode GESTION
     */
    public static class GestionConfig {
        public static final boolean ACTIVER_METEO = true;
        public static final boolean ACTIVER_EVENEMENTS = true;
        public static final boolean ACTIVER_GENERATEUR = false;
        public static final boolean PERMETTRE_AJOUT_MANUEL = true;
        public static final boolean PERMETTRE_SUPPRESSION = true;
        public static final boolean PERMETTRE_MODIFICATION = true;
    }

    /**
     * Affiche la configuration active
     */
    public static void afficherConfig(ModeApplication mode) {
        Logger.logInfo("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        Logger.logInfo("Configuration du Mode: " + mode.getNom());
        Logger.logInfo("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        Logger.logInfo("Meteo: ACTIVE");
        Logger.logInfo("Evenements: ACTIFS");
        Logger.logInfo("Generateur: INACTIF");
        Logger.logInfo("Ajout Manuel: AUTORISE");
        Logger.logInfo("Modification: AUTORISEE");
        
        Logger.logInfo("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}

