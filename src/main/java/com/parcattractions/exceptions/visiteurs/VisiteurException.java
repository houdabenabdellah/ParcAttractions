package main.java.com.parcattractions.exceptions.visiteurs;

import main.java.com.parcattractions.exceptions.ParcException;

public abstract class VisiteurException extends ParcException {
    
    protected final int visiteurId;
    
    /**
     * Constructeur pour exception de visiteur
     * @param message Description de l'erreur
     * @param code Code d'erreur
     * @param visiteurId ID du visiteur concerné
     */
    public VisiteurException(String message, String code, int visiteurId) {
        super(message, code);
        this.visiteurId = visiteurId;
    }
    
    /**
     * Constructeur avec cause
     */
    public VisiteurException(String message, String code, int visiteurId, Throwable cause) {
        super(message, code, cause);
        this.visiteurId = visiteurId;
    }
    
    /**
     * @return ID du visiteur concerné
     */
    public int getVisiteurId() {
        return visiteurId;
    }
}
