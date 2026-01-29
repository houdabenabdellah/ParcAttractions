package main.java.com.parcattractions.exceptions.systeme;

public class ParcFermeException extends SystemeException {
    
    /**
     * Constructeur simple
     */
    public ParcFermeException() {
        super(
            "Action impossible: le parc est actuellement fermé",
            "SYS_001"
        );
    }
    
    /**
     * Constructeur avec action précise
     * @param action Action tentée
     */
    public ParcFermeException(String action) {
        super(
            String.format("Action impossible: %s (le parc est fermé)", action),
            "SYS_001"
        );
    }
}
