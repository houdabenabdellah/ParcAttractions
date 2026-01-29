package main.java.com.parcattractions.exceptions.employes;

public class TechnicienIndisponibleException extends EmployeException {
    
    private final int nombreTechniciensTotaux;
    private final int nombreTechniciensOccupes;
    
    /**
     * Constructeur simple
     */
    public TechnicienIndisponibleException() {
        super(
            "Aucun technicien disponible pour intervention",
            "EMP_001",
            -1
        );
        this.nombreTechniciensTotaux = 0;
        this.nombreTechniciensOccupes = 0;
    }
    
    /**
     * Constructeur avec détails
     * @param nombreTechniciensTotaux Nombre total de techniciens
     * @param nombreTechniciensOccupes Nombre de techniciens occupés
     */
    public TechnicienIndisponibleException(int nombreTechniciensTotaux, int nombreTechniciensOccupes) {
        super(
            String.format("Aucun technicien disponible: %d/%d techniciens occupés", 
                nombreTechniciensOccupes, nombreTechniciensTotaux),
            "EMP_001",
            -1
        );
        this.nombreTechniciensTotaux = nombreTechniciensTotaux;
        this.nombreTechniciensOccupes = nombreTechniciensOccupes;
    }
    
    /**
     * Constructeur avec ID technicien et message
     * @param technicienId ID du technicien indisponible
     * @param message Message décrivant la raison
     */
    public TechnicienIndisponibleException(int technicienId, String message) {
        super(
            String.format("Technicien #%d indisponible: %s", technicienId, message),
            "EMP_001",
            technicienId
        );
        this.nombreTechniciensTotaux = 0;
        this.nombreTechniciensOccupes = 0;
    }
    
    /**
     * @return Nombre total de techniciens
     */
    public int getNombreTechniciensTotaux() {
        return nombreTechniciensTotaux;
    }
    
    /**
     * @return Nombre de techniciens occupés
     */
    public int getNombreTechniciensOccupes() {
        return nombreTechniciensOccupes;
    }
}