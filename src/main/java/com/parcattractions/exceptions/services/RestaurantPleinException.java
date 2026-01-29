package main.java.com.parcattractions.exceptions.services;

public class RestaurantPleinException extends ServiceException {
    
    private final int capacite;
    private final int clientsActuels;
    
    /**
     * Constructeur
     * @param restaurantNom Nom du restaurant
     * @param capacite Capacité maximale
     * @param clientsActuels Nombre de clients actuels
     */
    public RestaurantPleinException(String restaurantNom, int capacite, int clientsActuels) {
        super(
            String.format("Restaurant %s complet: %d/%d places occupées", 
                restaurantNom, clientsActuels, capacite),
            "SRV_002",
            restaurantNom
        );
        this.capacite = capacite;
        this.clientsActuels = clientsActuels;
    }
    
    /**
     * @return Capacité maximale du restaurant
     */
    public int getCapacite() {
        return capacite;
    }
    
    /**
     * @return Nombre de clients actuels
     */
    public int getClientsActuels() {
        return clientsActuels;
    }
    
    /**
     * @return Vrai si le restaurant est vraiment plein
     */
    public boolean estComplet() {
        return clientsActuels >= capacite;
    }
}