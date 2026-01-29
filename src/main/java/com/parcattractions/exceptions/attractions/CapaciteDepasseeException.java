package main.java.com.parcattractions.exceptions.attractions;

public class CapaciteDepasseeException extends AttractionException {
    
    private final int capaciteMax;
    private final int visiteursTentes;
    
    /**
     * Constructeur
     * @param attractionNom Nom de l'attraction
     * @param capaciteMax Capacité maximale de la file
     * @param visiteursTentes Nombre de visiteurs tentant d'entrer
     */
    public CapaciteDepasseeException(String attractionNom, int capaciteMax, int visiteursTentes) {
        super(
            String.format("Capacité dépassée pour %s: %d visiteurs tentent d'entrer (max: %d)", 
                attractionNom, visiteursTentes, capaciteMax),
            "ATTR_001",
            attractionNom
        );
        this.capaciteMax = capaciteMax;
        this.visiteursTentes = visiteursTentes;
    }
    
    /**
     * @return Capacité maximale de la file
     */
    public int getCapaciteMax() {
        return capaciteMax;
    }
    
    /**
     * @return Nombre de visiteurs ayant tenté d'entrer
     */
    public int getVisiteursTentes() {
        return visiteursTentes;
    }
    
    /**
     * @return Nombre de visiteurs en excès
     */
    public int getExcedent() {
        return visiteursTentes - capaciteMax;
    }
}
