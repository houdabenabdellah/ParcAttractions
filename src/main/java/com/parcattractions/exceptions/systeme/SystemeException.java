package main.java.com.parcattractions.exceptions.systeme;

import main.java.com.parcattractions.exceptions.ParcException;

public abstract class SystemeException extends ParcException {
    
    /**
     * Constructeur pour exception système
     * @param message Description de l'erreur
     * @param code Code d'erreur
     */
    public SystemeException(String message, String code) {
        super(message, code);
    }
    
    /**
     * Constructeur avec cause
     */
    public SystemeException(String message, String code, Throwable cause) {
        super(message, code, cause);
    }
}