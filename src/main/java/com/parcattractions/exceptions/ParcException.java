package main.java.com.parcattractions.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class ParcException extends Exception {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final String code;
    private final LocalDateTime timestamp;
    
    /**
     * Constructeur avec message et code d'erreur
     * @param message Description de l'erreur
     * @param code Code unique de l'erreur (ex: "ATTR_001")
     */
    public ParcException(String message, String code) {
        super(message);
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructeur avec message, code et cause
     * @param message Description de l'erreur
     * @param code Code unique de l'erreur
     * @param cause Exception originale
     */
    public ParcException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * @return Code d'erreur unique
     */
    public String getCode() {
        return code;
    }
    
    /**
     * @return Timestamp de l'erreur
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Retourne le message formaté pour les logs
     * @return Message formaté avec timestamp et code
     */
    public String getFormattedMessage() {
        return String.format("[%s] %s (Code: %s)", 
            timestamp.format(FORMATTER), 
            getMessage(), 
            code
        );
    }
    
    /**
     * Retourne une représentation textuelle complète de l'exception
     */
    @Override
    public String toString() {
        return getFormattedMessage();
    }
}
