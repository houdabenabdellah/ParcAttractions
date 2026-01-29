package main.java.com.parcattractions.enums;

public enum Meteo {
    ENSOLEILLE("Ensoleille", 0.5, 1.2),
    NUAGEUX("Nuageux", 0.3, 1.0),
    PLUIE("Pluie", 0.15, 0.7),
    ORAGE("Orage", 0.05, 0.0);
    
    private final String description;
    private final double probabilite; // Probabilité d'occurrence
    private final double facteurAffluence; // Impact sur affluence (1.0 = normal)
    
    Meteo(String description, double probabilite, double facteurAffluence) {
        this.description = description;
        this.probabilite = probabilite;
        this.facteurAffluence = facteurAffluence;
    }
    
    public String getDescription() {
        return description;
    }
    
    public double getProbabilite() {
        return probabilite;
    }
    
    public double getFacteurAffluence() {
        return facteurAffluence;
    }
    
    /**
     * Vérifie si les attractions extérieures doivent être fermées
     */
    public boolean fermeAttractionsExterieures() {
        return this == PLUIE || this == ORAGE;
    }
    
    /**
     * Vérifie si c'est une urgence (évacuation nécessaire)
     */
    public boolean estUrgence() {
        return this == ORAGE;
    }
    
    /**
     * Retourne la météo suivante probable
     */
    public Meteo getMeteoSuivanteProbable() {
        double random = Math.random();
        
        switch (this) {
            case ENSOLEILLE:
                return random < 0.7 ? ENSOLEILLE : (random < 0.9 ? NUAGEUX : PLUIE);
            case NUAGEUX:
                return random < 0.4 ? ENSOLEILLE : (random < 0.8 ? NUAGEUX : PLUIE);
            case PLUIE:
                return random < 0.3 ? NUAGEUX : (random < 0.7 ? PLUIE : ORAGE);
            case ORAGE:
                return random < 0.6 ? PLUIE : NUAGEUX;
            default:
                return ENSOLEILLE;
        }
    }
    
    @Override
    public String toString() {
        return description;
    }
}
