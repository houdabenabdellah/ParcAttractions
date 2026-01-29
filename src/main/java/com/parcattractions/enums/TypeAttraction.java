package main.java.com.parcattractions.enums;

public enum TypeAttraction {
    SENSATIONS_FORTES("Sensations fortes", 3),
    FAMILIALE("Familiale", 1),
    ENFANTS("Pour enfants", 0),
    ROMANTIQUE("Romantique", 0),
    AQUATIQUE("Aquatique", 2);
    
    private final String libelle;
    private final int niveauRisque; // 0 = aucun risque, 3 = risque élevé
    
    TypeAttraction(String libelle, int niveauRisque) {
        this.libelle = libelle;
        this.niveauRisque = niveauRisque;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public int getNiveauRisque() {
        return niveauRisque;
    }
    
    /**
     * Vérifie si ce type est compatible avec un profil visiteur
     */
    public boolean estCompatibleAvecEnfants() {
        return this == ENFANTS || this == FAMILIALE;
    }
    
    @Override
    public String toString() {
        return libelle;
    }
}
