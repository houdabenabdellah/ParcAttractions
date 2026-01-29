package main.java.com.parcattractions.exceptions.attractions;

import main.java.com.parcattractions.exceptions.ParcException;

public abstract class AttractionException extends ParcException {
    
    protected final String attractionNom;
    
    /**
     * Constructeur pour exception d'attraction
     * @param message Description de l'erreur
     * @param code Code d'erreur
     * @param attractionNom Nom de l'attraction concernée
     */
    public AttractionException(String message, String code, String attractionNom) {
        super(message, code);
        this.attractionNom = attractionNom;
    }
    
    /**
     * Constructeur avec cause
     */
    public AttractionException(String message, String code, String attractionNom, Throwable cause) {
        super(message, code, cause);
        this.attractionNom = attractionNom;
    }
    
    /**
     * @return Nom de l'attraction concernée
     */
    public String getAttractionNom() {
        return attractionNom;
    }
}