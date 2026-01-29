package main.java.com.parcattractions.exceptions.attractions;

public class MaintenanceEnCoursException extends AttractionException {
    
    private final int tempsRestantSecondes;
    
    /**
     * Constructeur
     * @param attractionNom Nom de l'attraction
     * @param tempsRestantSecondes Temps restant de maintenance (en secondes)
     */
    public MaintenanceEnCoursException(String attractionNom, int tempsRestantSecondes) {
        super(
            String.format("Maintenance en cours sur %s (temps restant: %d secondes)", 
                attractionNom, tempsRestantSecondes),
            "ATTR_003",
            attractionNom
        );
        this.tempsRestantSecondes = tempsRestantSecondes;
    }
    
    /**
     * @return Temps restant de maintenance en secondes
     */
    public int getTempsRestantSecondes() {
        return tempsRestantSecondes;
    }
    
    /**
     * @return Temps restant formaté (minutes:secondes)
     */
    public String getTempsRestantFormate() {
        int minutes = tempsRestantSecondes / 60;
        int secondes = tempsRestantSecondes % 60;
        return String.format("%d min %d sec", minutes, secondes);
    }
}