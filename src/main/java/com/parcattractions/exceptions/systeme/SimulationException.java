package main.java.com.parcattractions.exceptions.systeme;

public class SimulationException extends SystemeException {
    
    /**
     * Constructeur avec message
     * @param message Description de l'erreur
     */
    public SimulationException(String message) {
        super(
            "Erreur de simulation: " + message,
            "SYS_002"
        );
    }
    
    /**
     * Constructeur avec message et cause
     * @param message Description de l'erreur
     * @param cause Exception originale
     */
    public SimulationException(String message, Throwable cause) {
        super(
            "Erreur de simulation: " + message,
            "SYS_002",
            cause
        );
    }
    
    /**
     * Constructeur pour erreur critique
     * @param message Description
     * @param estCritique Si vrai, marque comme critique
     */
    public SimulationException(String message, boolean estCritique) {
        super(
            (estCritique ? "ERREUR CRITIQUE - " : "") + "Simulation: " + message,
            estCritique ? "SYS_003" : "SYS_002"
        );
    }
}