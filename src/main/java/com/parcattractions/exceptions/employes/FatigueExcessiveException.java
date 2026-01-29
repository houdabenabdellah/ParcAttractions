package main.java.com.parcattractions.exceptions.employes;

public class FatigueExcessiveException extends EmployeException {
    
    private final int niveauFatigue;
    private final int seuilMax;
    
    /**
     * Constructeur
     * @param employeId ID de l'employé
     * @param niveauFatigue Niveau de fatigue actuel (0-100)
     */
    public FatigueExcessiveException(int employeId, int niveauFatigue) {
        this(employeId, niveauFatigue, 100);
    }
    
    /**
     * Constructeur avec seuil personnalisé
     * @param employeId ID de l'employé
     * @param niveauFatigue Niveau de fatigue actuel
     * @param seuilMax Seuil maximum acceptable
     */
    public FatigueExcessiveException(int employeId, int niveauFatigue, int seuilMax) {
        super(
            String.format("Employé %d trop fatigué: %d/%d (pause nécessaire)", 
                employeId, niveauFatigue, seuilMax),
            "EMP_002",
            employeId
        );
        this.niveauFatigue = niveauFatigue;
        this.seuilMax = seuilMax;
    }
    
    /**
     * @return Niveau de fatigue actuel
     */
    public int getNiveauFatigue() {
        return niveauFatigue;
    }
    
    /**
     * @return Seuil maximum acceptable
     */
    public int getSeuilMax() {
        return seuilMax;
    }
    
    /**
     * @return Excédent de fatigue
     */
    public int getExcedent() {
        return niveauFatigue - seuilMax;
    }
}