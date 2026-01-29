package main.java.com.parcattractions.exceptions.services;

public class StockEpuiseException extends ServiceException {
    
    private final String produitNom;
    
    /**
     * Constructeur
     * @param serviceNom Nom du service (boutique)
     * @param produitNom Nom du produit épuisé
     */
    public StockEpuiseException(String serviceNom, String produitNom) {
        super(
            String.format("Stock épuisé dans %s: %s", serviceNom, produitNom),
            "SRV_001",
            serviceNom
        );
        this.produitNom = produitNom;
    }
    
    /**
     * @return Nom du produit épuisé
     */
    public String getProduitNom() {
        return produitNom;
    }
}
