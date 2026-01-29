package main.java.com.parcattractions.enums;

public enum NiveauIntensite {
    FAIBLE(1, "Relaxant"),
    MODERE(2, "Modéré"),
    EXTREME(3, "Extrême");
    
    private final int valeur;
    private final String description;
    
    NiveauIntensite(int valeur, String description) {
        this.valeur = valeur;
        this.description = description;
    }
    
    public int getValeur() {
        return valeur;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Vérifie si ce niveau convient à un visiteur selon son profil
     */
    public boolean convientAuxEnfants() {
        return this == FAIBLE;
    }
    
    public boolean convientAuxAmateursExtreme() {
        return this == EXTREME || this == MODERE;
    }
    
    @Override
    public String toString() {
        return description + " (" + valeur + "/3)";
    }
}