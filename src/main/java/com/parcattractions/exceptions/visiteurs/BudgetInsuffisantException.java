package main.java.com.parcattractions.exceptions.visiteurs;

public class BudgetInsuffisantException extends VisiteurException {
    
    private final double argentActuel;
    private final double prixRequis;
    
    /**
     * Constructeur
     * @param visiteurId ID du visiteur
     * @param argentActuel Argent disponible
     * @param prixRequis Prix de l'article/service
     */
    public BudgetInsuffisantException(int visiteurId, double argentActuel, double prixRequis) {
        super(
            String.format("Visiteur %d: budget insuffisant (%.2f€ disponibles < %.2f€ requis)", 
                visiteurId, argentActuel, prixRequis),
            "VIS_001",
            visiteurId
        );
        this.argentActuel = argentActuel;
        this.prixRequis = prixRequis;
    }
    
    /**
     * @return Argent actuellement disponible
     */
    public double getArgentActuel() {
        return argentActuel;
    }
    
    /**
     * @return Prix requis pour l'achat
     */
    public double getPrixRequis() {
        return prixRequis;
    }
    
    /**
     * @return Montant manquant
     */
    public double getMontantManquant() {
        return prixRequis - argentActuel;
    }
}
