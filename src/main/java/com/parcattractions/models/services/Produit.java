package main.java.com.parcattractions.models.services;

public class Produit {
    
    private static int compteurId = 0;
    
    private final int id;
    private final String nom;
    private final String categorie;
    private final double prix;
    private final String description;
    
    /**
     * Constructeur complet
     * @param nom Nom du produit
     * @param categorie Catégorie (souvenirs, vêtements, photos, etc.)
     * @param prix Prix unitaire
     * @param description Description du produit
     */
    public Produit(String nom, String categorie, double prix, String description) {
        this.id = ++compteurId;
        this.nom = nom;
        this.categorie = categorie;
        this.prix = prix;
        this.description = description;
    }
    
    /**
     * Constructeur simplifié sans description
     * @param nom Nom du produit
     * @param categorie Catégorie
     * @param prix Prix
     */
    public Produit(String nom, String categorie, double prix) {
        this(nom, categorie, prix, "");
    }
    
    /**
     * @return ID unique du produit
     */
    public int getId() {
        return id;
    }
    
    /**
     * @return Nom du produit
     */
    public String getNom() {
        return nom;
    }
    
    /**
     * @return Catégorie du produit
     */
    public String getCategorie() {
        return categorie;
    }
    
    /**
     * @return Prix unitaire
     */
    public double getPrix() {
        return prix;
    }
    
    /**
     * @return Description du produit
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @return Prix formaté
     */
    public String getPrixFormate() {
        return String.format("%.2f €", prix);
    }
    
    /**
     * Vérifie si le produit a une description
     * @return Vrai si description non vide
     */
    public boolean aDescription() {
        return description != null && !description.isEmpty();
    }
    
    /**
     * Calcule le prix TTC avec TVA
     * @param tauxTVA Taux de TVA (ex: 0.20 pour 20%)
     * @return Prix TTC
     */
    public double getPrixTTC(double tauxTVA) {
        return prix * (1 + tauxTVA);
    }
    
    /**
     * Calcule le prix pour une quantité donnée
     * @param quantite Quantité
     * @return Prix total
     */
    public double calculerPrixTotal(int quantite) {
        return prix * quantite;
    }
    
    /**
     * Représentation textuelle du produit
     */
    @Override
    public String toString() {
        if (aDescription()) {
            return String.format("%s (%s) - %s - %s", 
                nom, categorie, getPrixFormate(), description);
        } else {
            return String.format("%s (%s) - %s", 
                nom, categorie, getPrixFormate());
        }
    }
    
    /**
     * Génère une étiquette de prix
     * @return Étiquette formatée
     */
    public String genererEtiquette() {
        StringBuilder sb = new StringBuilder();
        sb.append("┌─────────────────────────┐\n");
        sb.append(String.format("│ %-23s │\n", nom.length() > 23 ? nom.substring(0, 23) : nom));
        sb.append("├─────────────────────────┤\n");
        sb.append(String.format("│ Prix: %-17s │\n", getPrixFormate()));
        sb.append(String.format("│ Catégorie: %-12s │\n", categorie));
        sb.append("└─────────────────────────┘");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Produit produit = (Produit) obj;
        return id == produit.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
