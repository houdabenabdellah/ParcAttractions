package main.java.com.parcattractions.exceptions.attractions;

public class RestrictionException extends AttractionException {
    
    private final String typeRestriction; // "age" ou "taille"
    private final int valeurVisiteur;
    private final int valeurRequise;
    
    /**
     * Constructeur
     * @param attractionNom Nom de l'attraction
     * @param typeRestriction Type de restriction ("age" ou "taille")
     * @param valeurVisiteur Valeur du visiteur (âge ou taille en cm)
     * @param valeurRequise Valeur requise par l'attraction
     */
    public RestrictionException(String attractionNom, String typeRestriction, 
                                int valeurVisiteur, int valeurRequise) {
        super(
            String.format("Restriction %s non respectée pour %s: %d %s < %d %s requis", 
                typeRestriction, attractionNom, 
                valeurVisiteur, getUnite(typeRestriction),
                valeurRequise, getUnite(typeRestriction)),
            "ATTR_004",
            attractionNom
        );
        this.typeRestriction = typeRestriction;
        this.valeurVisiteur = valeurVisiteur;
        this.valeurRequise = valeurRequise;
    }
    
    /**
     * @return Type de restriction ("age" ou "taille")
     */
    public String getTypeRestriction() {
        return typeRestriction;
    }
    
    /**
     * @return Valeur du visiteur
     */
    public int getValeurVisiteur() {
        return valeurVisiteur;
    }
    
    /**
     * @return Valeur requise
     */
    public int getValeurRequise() {
        return valeurRequise;
    }
    
    /**
     * Retourne l'unité selon le type de restriction
     */
    private static String getUnite(String type) {
        return type.equalsIgnoreCase("age") ? "ans" : "cm";
    }
}
