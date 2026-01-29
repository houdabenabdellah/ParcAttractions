package main.java.com.parcattractions.utils;

import  java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Système de logging centralisé pour tout le projet.
 * Gère les logs dans la console et dans un fichier.
 */
public class Logger {
    
    private static final String LOG_DIR = "logs/";
    private static final String LOG_FILE = "parc_attractions.log";
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private static PrintWriter logWriter;
    private static boolean initialized = false;
    
    /**
     * Niveaux de log
     */
    public enum LogLevel {
        INFO("INFO", "\u001B[32m"),      // Vert
        WARNING("WARNING", "\u001B[33m"), // Jaune
        ERROR("ERROR", "\u001B[31m"),     // Rouge
        CRITICAL("CRITICAL", "\u001B[35m"); // Magenta
        
        private final String label;
        private final String colorCode;
        
        LogLevel(String label, String colorCode) {
            this.label = label;
            this.colorCode = colorCode;
        }
        
        public String getLabel() {
            return label;
        }
        
        public String getColorCode() {
            return colorCode;
        }
    }
    
    // Code de réinitialisation de couleur
    private static final String RESET = "\u001B[0m";
    
    /**
     * Initialise le système de logging
     */
    private static synchronized void init() {
        if (initialized) {
            return;
        }
        
        try {
            // Créer le dossier logs s'il n'existe pas
            File logDirectory = new File(LOG_DIR);
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }
            
            // Créer/ouvrir le fichier de log en mode append
            File logFile = new File(LOG_DIR + LOG_FILE);
            logWriter = new PrintWriter(new FileWriter(logFile, true), true);
            
            // Marquer le début d'une nouvelle session
            String separator = "=".repeat(80);
            logWriter.println();
            logWriter.println(separator);
            logWriter.println("NOUVELLE SESSION - " + LocalDateTime.now().format(FORMATTER));
            logWriter.println(separator);
            
            initialized = true;
            
            // Message de confirmation
            System.out.println("[Logger] Système de logging initialisé");
            System.out.println("[Logger] Fichier: " + logFile.getAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'initialisation du logger: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Méthode générique de log
     * @param level Niveau de log
     * @param message Message à logger
     */
    private static synchronized void log(LogLevel level, String message) {
        // Initialiser si nécessaire
        if (!initialized) {
            init();
        }
        
        // Timestamp
        String timestamp = LocalDateTime.now().format(FORMATTER);
        
        // Format du message
        String logMessage = String.format("[%s] [%s] %s", timestamp, level.getLabel(), message);
        
        // Console avec couleur
        String consoleMessage = String.format("%s[%s] [%s]%s %s", 
            level.getColorCode(), timestamp, level.getLabel(), RESET, message);
        
        // Afficher dans la console selon le niveau
        if (level == LogLevel.ERROR || level == LogLevel.CRITICAL) {
            System.err.println(consoleMessage);
        } else {
            System.out.println(consoleMessage);
        }
        
        // Écrire dans le fichier
        if (logWriter != null) {
            logWriter.println(logMessage);
        }
    }
    
    /**
     * Log niveau INFO
     * @param message Message informatif
     */
    public static void logInfo(String message) {
        log(LogLevel.INFO, message);
    }
    
    /**
     * Log niveau WARNING
     * @param message Message d'avertissement
     */
    public static void logWarning(String message) {
        log(LogLevel.WARNING, message);
    }
    
    /**
     * Log niveau ERROR
     * @param message Message d'erreur
     */
    public static void logError(String message) {
        log(LogLevel.ERROR, message);
    }
    
    /**
     * Log niveau CRITICAL
     * @param message Message critique
     */
    public static void logCritical(String message) {
        log(LogLevel.CRITICAL, message);
    }
    
    /**
     * Log une exception complète avec stack trace
     * @param e Exception à logger
     */
    public static void logException(Exception e) {
        logError("Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        
        if (logWriter != null) {
            e.printStackTrace(logWriter);
            logWriter.println(); // Ligne vide après la stack trace
        }
        
        // Aussi dans la console pour débogage
        e.printStackTrace();
    }
    
    /**
     * Log une exception avec message personnalisé
     * @param message Message de contexte
     * @param e Exception
     */
    public static void logException(String message, Exception e) {
        logError(message + " - " + e.getMessage());
        
        if (logWriter != null) {
            logWriter.println("Contexte: " + message);
            e.printStackTrace(logWriter);
            logWriter.println();
        }
    }
    
    /**
     * Log un message de démarrage de thread
     * @param threadName Nom du thread
     */
    public static void logThreadStart(String threadName) {
        logInfo("Thread démarré: " + threadName);
    }
    
    /**
     * Log un message d'arrêt de thread
     * @param threadName Nom du thread
     */
    public static void logThreadStop(String threadName) {
        logInfo("Thread arrêté: " + threadName);
    }
    
    /**
     * Log un message de débogage (seulement si mode debug activé)
     * @param message Message de debug
     */
    public static void logDebug(String message) {
        // Vous pouvez ajouter un flag DEBUG pour activer/désactiver
        // Pour l'instant, on utilise INFO
        if (isDebugEnabled()) {
            log(LogLevel.INFO, "[DEBUG] " + message);
        }
    }
    
    /**
     * Vérifie si le mode debug est activé
     * @return true si debug activé
     */
    private static boolean isDebugEnabled() {
        // Vous pouvez lire depuis une propriété système
        // Pour l'instant, retourne false
        return Boolean.getBoolean("parc.debug");
    }
    
    /**
     * Ferme proprement le système de logging
     */
    public static synchronized void close() {
        if (logWriter != null) {
            String separator = "=".repeat(80);
            logWriter.println();
            logWriter.println(separator);
            logWriter.println("FIN DE SESSION - " + LocalDateTime.now().format(FORMATTER));
            logWriter.println(separator);
            logWriter.println();
            
            logWriter.close();
            logWriter = null;
            initialized = false;
            
            System.out.println("[Logger] Système de logging fermé");
        }
    }
    
    /**
     * Ajoute un shutdown hook pour fermer le logger proprement
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close();
        }));
    }
}
