package main.java.com.parcattractions.exceptions.visiteurs;

public class RestrictionAgeException extends VisiteurException {
    
    private final int age;
    private final int ageMin;
    private final String service; // nom attraction/restaurant/etc
    
    /**
     * Constructeur
     * @param visiteurId ID du visiteur
     * @param age Âge du visiteur
     * @param ageMin Âge minimum requis
     * @param service Nom du service concerné
     */
    public RestrictionAgeException(int visiteurId, int age, int ageMin, String service) {
        super(
            String.format("Visiteur %d trop jeune pour %s: %d ans < %d ans requis", 
                visiteurId, service, age, ageMin),
            "VIS_003",
            visiteurId
        );
        this.age = age;
        this.ageMin = ageMin;
        this.service = service;
    }
    
    /**
     * @return Âge du visiteur
     */
    public int getAge() {
        return age;
    }
    
    /**
     * @return Âge minimum requis
     */
    public int getAgeMin() {
        return ageMin;
    }
    
    /**
     * @return Nom du service concerné
     */
    public String getService() {
        return service;
    }
    
    /**
     * @return Nombre d'années manquantes
     */
    public int getAnneesManquantes() {
        return ageMin - age;
    }
}