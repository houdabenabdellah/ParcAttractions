package main.java.com.parcattractions.exceptions.employes;

import main.java.com.parcattractions.exceptions.ParcException;

public abstract class EmployeException extends ParcException {
    
    protected final int employeId;
    
    /**
     * Constructeur pour exception d'employé
     * @param message Description de l'erreur
     * @param code Code d'erreur
     * @param employeId ID de l'employé concerné (-1 si général)
     */
    public EmployeException(String message, String code, int employeId) {
        super(message, code);
        this.employeId = employeId;
    }
    
    /**
     * Constructeur avec cause
     */
    public EmployeException(String message, String code, int employeId, Throwable cause) {
        super(message, code, cause);
        this.employeId = employeId;
    }
    
    /**
     * @return ID de l'employé concerné (-1 si non applicable)
     */
    public int getEmployeId() {
        return employeId;
    }
}
