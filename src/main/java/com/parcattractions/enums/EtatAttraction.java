package main.java.com.parcattractions.enums;

public enum EtatAttraction {
    /**
     * L'attraction fonctionne normalement
     */
    OPERATIONNELLE,
    
    /**
     * L'attraction est en maintenance programmée
     */
    MAINTENANCE,
    
    /**
     * L'attraction est en panne
     */
    PANNE,
    
    /**
     * L'attraction est fermée (météo, fin de journée, etc.)
     */
    FERMEE;
    
    /**
     * Retourne une description lisible de l'état
     */
    public String getDescription() {
        switch (this) {
            case OPERATIONNELLE:
                return "En service";
            case MAINTENANCE:
                return "Maintenance en cours";
            case PANNE:
                return "Hors service (panne)";
            case FERMEE:
                return "Fermée";
            default:
                return "État inconnu";
        }
    }
    
    /**
     * Vérifie si l'attraction peut accueillir des visiteurs
     */
    public boolean peutAccueillirVisiteurs() {
        return this == OPERATIONNELLE;
    }
}
