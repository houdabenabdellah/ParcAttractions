package main.java.com.parcattractions.models.services;

public class MenuItem {
    
    private static int compteurId = 0;
    
    private final int id;
    private final String nom;
    private final TypePlat type;
    private final double prix;
    private final String description;
    private final boolean vegetarien;
    private final int tempsPreparation; // en secondes
    
    /**
     * Types de plats
     */
    public enum TypePlat {
        ENTREE("Entrée"),
        PLAT("Plat principal"),
        DESSERT("Dessert"),
        BOISSON("Boisson"),
        MENU("Menu complet");
        
        private final String libelle;
        
        TypePlat(String libelle) {
            this.libelle = libelle;
        }
        
        public String getLibelle() {
            return libelle;
        }
    }
    
    /**
     * Constructeur complet
     * @param nom Nom du plat
     * @param type Type de plat
     * @param prix Prix
     * @param description Description
     * @param vegetarien Vrai si végétarien
     * @param tempsPreparation Temps de préparation en secondes
     */
    public MenuItem(String nom, TypePlat type, double prix, String description, 
                   boolean vegetarien, int tempsPreparation) {
        this.id = ++compteurId;
        this.nom = nom;
        this.type = type;
        this.prix = prix;
        this.description = description;
        this.vegetarien = vegetarien;
        this.tempsPreparation = tempsPreparation;
    }
    
    /**
     * Constructeur simplifié
     * @param nom Nom du plat
     * @param type Type de plat
     * @param prix Prix
     */
    public MenuItem(String nom, TypePlat type, double prix) {
        this(nom, type, prix, "", false, 300); // 5 minutes par défaut
    }
    
    /**
     * @return ID unique du menu item
     */
    public int getId() {
        return id;
    }
    
    /**
     * @return Nom du plat
     */
    public String getNom() {
        return nom;
    }
    
    /**
     * @return Type de plat
     */
    public TypePlat getType() {
        return type;
    }
    
    /**
     * @return Prix
     */
    public double getPrix() {
        return prix;
    }
    
    /**
     * @return Description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @return Vrai si végétarien
     */
    public boolean estVegetarien() {
        return vegetarien;
    }
    
    /**
     * @return Temps de préparation en secondes
     */
    public int getTempsPreparation() {
        return tempsPreparation;
    }
    
    /**
     * @return Prix formaté
     */
    public String getPrixFormate() {
        return String.format("%.2f €", prix);
    }
    
    /**
     * @return Temps de préparation formaté
     */
    public String getTempsPreparationFormate() {
        int minutes = tempsPreparation / 60;
        int secondes = tempsPreparation % 60;
        if (secondes == 0) {
            return minutes + " min";
        }
        return String.format("%d min %d sec", minutes, secondes);
    }
    
    /**
     * @return Symbole végétarien si applicable
     */
    public String getSymboleVegetarien() {
        return vegetarien ? "🌱" : "";
    }
    
    /**
     * Représentation textuelle
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nom);
        if (vegetarien) {
            sb.append(" 🌱");
        }
        sb.append(" (").append(type.getLibelle()).append(")");
        sb.append(" - ").append(getPrixFormate());
        
        if (description != null && !description.isEmpty()) {
            sb.append(" - ").append(description);
        }
        
        return sb.toString();
    }
    
    /**
     * Génère une carte de menu formatée
     * @return Carte formatée
     */
    public String genererCarteMenu() {
        StringBuilder sb = new StringBuilder();
        sb.append("┌────────────────────────────────────┐\n");
        sb.append(String.format("│ %-34s │\n", nom.length() > 34 ? nom.substring(0, 34) : nom));
        if (vegetarien) {
            sb.append("│ 🌱 Végétarien                      │\n");
        }
        sb.append("├────────────────────────────────────┤\n");
        
        if (description != null && !description.isEmpty()) {
            // Découper la description en lignes de 34 caractères
            String[] mots = description.split(" ");
            StringBuilder ligne = new StringBuilder();
            for (String mot : mots) {
                if (ligne.length() + mot.length() + 1 <= 34) {
                    if (ligne.length() > 0) ligne.append(" ");
                    ligne.append(mot);
                } else {
                    sb.append(String.format("│ %-34s │\n", ligne.toString()));
                    ligne = new StringBuilder(mot);
                }
            }
            if (ligne.length() > 0) {
                sb.append(String.format("│ %-34s │\n", ligne.toString()));
            }
            sb.append("├────────────────────────────────────┤\n");
        }
        
        sb.append(String.format("│ Prix: %-28s │\n", getPrixFormate()));
        sb.append(String.format("│ Préparation: %-21s │\n", getTempsPreparationFormate()));
        sb.append("└────────────────────────────────────┘");
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MenuItem menuItem = (MenuItem) obj;
        return id == menuItem.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
