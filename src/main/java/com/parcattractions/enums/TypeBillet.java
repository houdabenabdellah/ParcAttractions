package main.java.com.parcattractions.enums;

public enum TypeBillet {
    STANDARD("Billet standard", 1.0),
    FAST_PASS("Fast-Pass", 1.6),
    PASS_JOURNEE("Pass journée", 2.0);
    
    private final String libelle;
    private final double multiplicateurPrix;
    
    TypeBillet(String libelle, double multiplicateurPrix) {
        this.libelle = libelle;
        this.multiplicateurPrix = multiplicateurPrix;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public double getMultiplicateurPrix() {
        return multiplicateurPrix;
    }
    
    /**
     * Vérifie si ce billet donne accès prioritaire
     */
    public boolean donnePriorite() {
        return this == FAST_PASS || this == PASS_JOURNEE;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}