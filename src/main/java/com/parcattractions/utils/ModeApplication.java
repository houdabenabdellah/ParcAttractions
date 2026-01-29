package main.java.com.parcattractions.utils;

/**
 * Énumération des modes de fonctionnement de l'application
 */
public enum ModeApplication {
    SIMULATION("Simulation automatique", "Générateurs automatiques de visiteurs"),
    GESTION("Gestion manuelle", "Ajout/suppression manuel des visiteurs et attractions"),
    HYBRIDE("Mode hybride", "Combinaison de simulation et gestion manuelle");
    
    private final String nom;
    private final String description;
    
    ModeApplication(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }
    
    public String getNom() {
        return nom;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return nom + " - " + description;
    }
}
