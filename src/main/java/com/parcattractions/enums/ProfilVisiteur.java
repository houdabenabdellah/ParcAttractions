package main.java.com.parcattractions.enums;

public enum ProfilVisiteur {
    FAMILLE("Famille", 100, 150.0),
    EXTREME("Amateur sensations fortes", 60, 80.0),
    ENFANT("Enfant", 40, 30.0),
    COUPLE("Couple", 120, 180.0),
    GROUPE("Groupe d'amis", 80, 120.0);
    
    private final String libelle;
    private final int patienceInitiale; // en unités de temps
    private final double budgetMoyen; // en euros
    
    ProfilVisiteur(String libelle, int patienceInitiale, double budgetMoyen) {
        this.libelle = libelle;
        this.patienceInitiale = patienceInitiale;
        this.budgetMoyen = budgetMoyen;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public int getPatienceInitiale() {
        return patienceInitiale;
    }
    
    public double getBudgetMoyen() {
        return budgetMoyen;
    }
    
    /**
     * Retourne le budget avec variation aléatoire (+/- 20%)
     */
    public double getBudgetAleatoire() {
        double variation = 0.8 + (Math.random() * 0.4); // entre 0.8 et 1.2
        return budgetMoyen * variation;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}
