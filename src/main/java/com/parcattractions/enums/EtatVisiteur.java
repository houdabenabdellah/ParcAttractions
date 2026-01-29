package main.java.com.parcattractions.enums;

public enum EtatVisiteur {
    EN_FILE("Dans la file d'attente"),
    EN_ATTRACTION("Sur l'attraction"),
    EN_PAUSE("En pause (restaurant/toilettes)"),
    EN_DEPLACEMENT("En déplacement"),
    QUITTE("A quitté le parc");
    
    private final String description;
    
    EtatVisiteur(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Vérifie si le visiteur est actif dans le parc
     */
    public boolean estActif() {
        return this != QUITTE;
    }
    
    /**
     * Vérifie si le visiteur est occupé (ne peut pas changer d'activité)
     */
    public boolean estOccupe() {
        return this == EN_FILE || this == EN_ATTRACTION || this == EN_PAUSE;
    }
    
    @Override
    public String toString() {
        return description;
    }
}