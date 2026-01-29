package main.java.com.parcattractions.exceptions.visiteurs;

public class PatienceEpuiseeException extends VisiteurException {
    
    /**
     * Constructeur
     * @param visiteurId ID du visiteur
     */
    public PatienceEpuiseeException(int visiteurId) {
        super(
            String.format("Visiteur %d: patience épuisée, quitte le parc", visiteurId),
            "VIS_002",
            visiteurId
        );
    }
    
    /**
     * Constructeur avec message personnalisé
     * @param visiteurId ID du visiteur
     * @param raison Raison de l'épuisement
     */
    public PatienceEpuiseeException(int visiteurId, String raison) {
        super(
            String.format("Visiteur %d: patience épuisée (%s)", visiteurId, raison),
            "VIS_002",
            visiteurId
        );
    }
}
