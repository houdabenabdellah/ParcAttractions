package main.java.com.parcattractions.exceptions.services;

import main.java.com.parcattractions.exceptions.ParcException;

public abstract class ServiceException extends ParcException {
    
    protected final String serviceNom;
    
    /**
     * Constructeur pour exception de service
     * @param message Description de l'erreur
     * @param code Code d'erreur
     * @param serviceNom Nom du service concerné
     */
    public ServiceException(String message, String code, String serviceNom) {
        super(message, code);
        this.serviceNom = serviceNom;
    }
    
    /**
     * Constructeur avec cause
     */
    public ServiceException(String message, String code, String serviceNom, Throwable cause) {
        super(message, code, cause);
        this.serviceNom = serviceNom;
    }
    
    /**
     * @return Nom du service concerné
     */
    public String getServiceNom() {
        return serviceNom;
    }
}
