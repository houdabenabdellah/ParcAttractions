package main.java.com.parcattractions.utils;

/**
 * Configuration des modes d'application
 */
public class ModeConfig {
    
    /**
     * Configuration pour le mode SIMULATION
     */
    public static class SimulationConfig {
        public static final int VISITEURS_PAR_MINUTE = 3;
        public static final int DUREE_SIMULATION_MINUTES = 480; // 8 heures
        public static final boolean ACTIVER_METEO = true;
        public static final boolean ACTIVER_EVENEMENTS = true;
        public static final boolean ACTIVER_GENERATEUR = true;
        public static final int VITESSE_SIMULATION = 1; // x1 temps réel
    }
    
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
     * Configuration pour le mode HYBRIDE
     */
    public static class HybrideConfig {
        public static final int VISITEURS_PAR_MINUTE = 1; // Moins agressif
        public static final boolean ACTIVER_METEO = true;
        public static final boolean ACTIVER_EVENEMENTS = true;
        public static final boolean ACTIVER_GENERATEUR = true;
        public static final boolean PERMETTRE_AJOUT_MANUEL = true;
        public static final boolean PERMETTRE_SUPPRESSION = true;
        public static final int VITESSE_SIMULATION = 2; // x2 temps réel
    }
    
    /**
     * Récupère la configuration selon le mode
     */
    public static Object getConfig(ModeApplication mode) {
        return switch (mode) {
            case SIMULATION -> SimulationConfig.class;
            case GESTION -> GestionConfig.class;
            case HYBRIDE -> HybrideConfig.class;
        };
    }
    
    /**
     * Affiche la configuration active
     */
    public static void afficherConfig(ModeApplication mode) {
        Logger.logInfo("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        Logger.logInfo("Configuration du Mode: " + mode.getNom());
        Logger.logInfo("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        switch (mode) {
            case SIMULATION -> {
                Logger.logInfo("Méteo: ACTIVE");
                Logger.logInfo("Événements: ACTIFS");
                Logger.logInfo("Générateur: ACTIF");
                Logger.logInfo("Visiteurs/min: " + SimulationConfig.VISITEURS_PAR_MINUTE);
                Logger.logInfo("Vitesse: x" + SimulationConfig.VITESSE_SIMULATION);
            }
            case GESTION -> {
                Logger.logInfo("Méteo: ACTIVE");
                Logger.logInfo("Événements: ACTIFS");
                Logger.logInfo("Générateur: INACTIF");
                Logger.logInfo("Ajout Manuel: AUTORISÉ");
                Logger.logInfo("Modification: AUTORISÉE");
            }
            case HYBRIDE -> {
                Logger.logInfo("Meteo: ACTIVE");
                Logger.logInfo("Evenements: ACTIFS");
                Logger.logInfo("Generateur: ACTIF");
                Logger.logInfo("Visiteurs/min: " + HybrideConfig.VISITEURS_PAR_MINUTE);
                Logger.logInfo("Ajout Manuel: AUTORISE");
                Logger.logInfo("Vitesse: x" + HybrideConfig.VITESSE_SIMULATION);
            }
        }
        
        Logger.logInfo("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
