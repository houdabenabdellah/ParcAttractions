package main.java.com.parcattractions.enums;

import java.awt.Color;

public enum TypeNotification {
    URGENCE("URGENCE", 3, new Color(220, 53, 69)),
    ATTENTION("ATTENTION", 2, new Color(255, 193, 7)),
    INFO("INFO", 1, new Color(40, 167, 69));
    
    private final String prefixe;
    private final int priorite;
    private final Color couleur;
    
    TypeNotification(String prefixe, int priorite, Color couleur) {
        this.prefixe = prefixe;
        this.priorite = priorite;
        this.couleur = couleur;
    }
    
    public String getPrefixe() {
        return prefixe;
    }
    
    public int getPriorite() {
        return priorite;
    }
    
    public Color getCouleur() {
        return couleur;
    }
    
    /**
     * Formate un message avec le préfixe approprié
     */
    public String formaterMessage(String message) {
        return prefixe + " " + message;
    }
    
    /**
     * Vérifie si c'est une notification critique
     */
    public boolean estCritique() {
        return this == URGENCE;
    }
    
    @Override
    public String toString() {
        return prefixe;
    }
}