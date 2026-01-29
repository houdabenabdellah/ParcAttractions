package main.java.com.parcattractions.exceptions.attractions;

public class AttractionFermeeException extends AttractionException {
    
    private final String raison;
    
    /**
     * Constructeur
     * @param attractionNom Nom de l'attraction
     * @param raison Raison de la fermeture (météo, panne, etc.)
     */
    public AttractionFermeeException(String attractionNom, String raison) {
        super(
            String.format("Attraction %s fermée: %s", attractionNom, raison),
            "ATTR_002",
            attractionNom
        );
        this.raison = raison;
    }
    
    /**
     * @return Raison de la fermeture
     */
    public String getRaison() {
        return raison;
    }
}
