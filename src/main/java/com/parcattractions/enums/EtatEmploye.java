package main.java.com.parcattractions.enums;

public enum EtatEmploye {
    DISPONIBLE("Disponible"),
    OCCUPE("Occupé"),
    EN_PAUSE("En pause"),
    EN_DEPLACEMENT("En déplacement");
    
    private final String description;
    
    EtatEmploye(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Vérifie si l'employé peut être assigné à une tâche
     */
    public boolean peutEtreAssigne() {
        return this == DISPONIBLE;
    }
    
    @Override
    public String toString() {
        return description;
    }
}