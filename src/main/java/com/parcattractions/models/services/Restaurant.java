package main.java.com.parcattractions.models.services;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import main.java.com.parcattractions.exceptions.services.RestaurantPleinException;
import main.java.com.parcattractions.utils.FileAttente;
import main.java.com.parcattractions.utils.Logger;

public class Restaurant {
    
    private final String nom;
    private final int capacite;
    private final FileAttente<Integer> fileAttente; // IDs visiteurs
    private final List<MenuItem> menu;
    private final AtomicLong revenus; // en centimes
    private volatile int clientsActuels;
    
    /**
     * Constructeur
     * @param nom Nom du restaurant
     * @param capacite Nombre de places assises
     */
    public Restaurant(String nom, int capacite) {
        this.nom = nom;
        this.capacite = capacite;
        this.fileAttente = new FileAttente<>(100); // Max 100 en file
        this.menu = new ArrayList<>();
        this.revenus = new AtomicLong(0);
        this.clientsActuels = 0;
        
        // Initialiser menu par défaut
        initialiserMenu();
        
        Logger.logInfo("Restaurant créé: " + nom + " (capacité: " + capacite + ")");
    }
    
    /**
     * Initialise le menu avec des plats par défaut
     */
    private void initialiserMenu() {
        // Entrées
        menu.add(new MenuItem("Salade César", MenuItem.TypePlat.ENTREE, 8.50, 
            "Salade romaine, poulet grillé, parmesan", false, 180));
        menu.add(new MenuItem("Soupe du jour", MenuItem.TypePlat.ENTREE, 6.50, 
            "Soupe maison", true, 120));
        
        // Plats
        menu.add(new MenuItem("Burger Classique", MenuItem.TypePlat.PLAT, 15.00, 
            "Steak, cheddar, tomate, salade, frites", false, 600));
        menu.add(new MenuItem("Pizza Margherita", MenuItem.TypePlat.PLAT, 14.00, 
            "Tomate, mozzarella, basilic", true, 480));
        menu.add(new MenuItem("Pâtes Carbonara", MenuItem.TypePlat.PLAT, 13.50, 
            "Pâtes, lardons, crème, parmesan", false, 420));
        
        // Desserts
        menu.add(new MenuItem("Glace 3 boules", MenuItem.TypePlat.DESSERT, 5.50, 
            "Vanille, chocolat, fraise", true, 60));
        menu.add(new MenuItem("Gâteau au chocolat", MenuItem.TypePlat.DESSERT, 6.00, 
            "Fondant au chocolat", true, 180));
        
        // Boissons
        menu.add(new MenuItem("Soda", MenuItem.TypePlat.BOISSON, 3.50, 
            "Coca, Sprite, Fanta", true, 30));
        menu.add(new MenuItem("Eau minérale", MenuItem.TypePlat.BOISSON, 2.50, 
            "50cl", true, 10));
        
        // Menus
        menu.add(new MenuItem("Menu Enfant", MenuItem.TypePlat.MENU, 12.00, 
            "Nuggets + Frites + Boisson + Glace", false, 480));
        menu.add(new MenuItem("Menu du Jour", MenuItem.TypePlat.MENU, 20.00, 
            "Entrée + Plat + Dessert + Boisson", false, 900));
    }
    
    /**
     * Ajoute un visiteur à la file d'attente
     * @param visiteurId ID du visiteur
     * @throws RestaurantPleinException Si file pleine
     */
    public synchronized void ajouterFile(int visiteurId) throws RestaurantPleinException {
        if (fileAttente.estPleine()) {
            throw new RestaurantPleinException(nom, capacite, clientsActuels);
        }
        
        fileAttente.ajouter(visiteurId);
        Logger.logInfo("Visiteur " + visiteurId + " ajouté file restaurant " + nom);
    }
    
    /**
     * Sert un visiteur (simule le service)
     * @param visiteurId ID du visiteur
     * @param montant Montant de la commande
     * @return Temps de service en secondes
     * @throws RestaurantPleinException Si restaurant complet
     */
    public synchronized int servir(int visiteurId, double montant) 
            throws RestaurantPleinException {
        
        // Vérifier capacité
        if (clientsActuels >= capacite) {
            throw new RestaurantPleinException(nom, capacite, clientsActuels);
        }
        
        // Client prend une place
        clientsActuels++;
        
        Logger.logInfo(String.format("Restaurant %s: Service visiteur %d (%.2f€)", 
            nom, visiteurId, montant));
        
        // Ajouter revenus
        ajouterRevenus(montant);
        
        // Temps de service aléatoire (5-10 minutes)
        int tempsService = 300 + (int)(Math.random() * 300);
        
        return tempsService;
    }
    
    /**
     * Libère une place quand un client part
     * @param visiteurId ID du visiteur
     */
    public synchronized void libererPlace(int visiteurId) {
        if (clientsActuels > 0) {
            clientsActuels--;
            Logger.logDebug("Restaurant " + nom + ": Place libérée (clients: " + clientsActuels + ")");
        }
    }
    
    /**
     * Ajoute des revenus
     * @param montant Montant en euros
     */
    private void ajouterRevenus(double montant) {
        long centimes = Math.round(montant * 100);
        revenus.addAndGet(centimes);
    }
    
    /**
     * @return Nom du restaurant
     */
    public String getNom() {
        return nom;
    }
    
    /**
     * @return Capacité totale
     */
    public int getCapacite() {
        return capacite;
    }
    
    /**
     * @return Nombre de clients actuels
     */
    public synchronized int getClientsActuels() {
        return clientsActuels;
    }
    
    /**
     * @return Taille de la file d'attente
     */
    public int getTailleFile() {
        return fileAttente.getTaille();
    }
    
    /**
     * @return Revenus totaux en euros
     */
    public double getRevenus() {
        return revenus.get() / 100.0;
    }
    
    /**
     * @return Menu complet
     */
    public List<MenuItem> getMenu() {
        return new ArrayList<>(menu); // Copie défensive
    }
    
    /**
     * Ajoute un item au menu
     * @param item Item à ajouter
     */
    public void ajouterMenuItem(MenuItem item) {
        menu.add(item);
        Logger.logInfo("Item ajouté au menu de " + nom + ": " + item.getNom());
    }
    
    /**
     * @return Vrai si le restaurant est complet
     */
    public synchronized boolean estComplet() {
        return clientsActuels >= capacite;
    }
    
    /**
     * @return Taux d'occupation (0.0 à 1.0)
     */
    public synchronized double getTauxOccupation() {
        return (double) clientsActuels / capacite;
    }
    
    /**
     * @return Revenus formatés
     */
    public String getRevenusFormates() {
        return String.format("%.2f €", getRevenus());
    }
    
    /**
     * Affiche le menu complet
     * @return Menu formaté
     */
    public String afficherMenu() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("   MENU - ").append(nom.toUpperCase()).append("\n");
        sb.append("═══════════════════════════════════════\n\n");
        
        // Grouper par type
        for (MenuItem.TypePlat type : MenuItem.TypePlat.values()) {
            List<MenuItem> items = menu.stream()
                .filter(item -> item.getType() == type)
                .toList();
            
            if (!items.isEmpty()) {
                sb.append("--- ").append(type.getLibelle().toUpperCase()).append(" ---\n");
                for (MenuItem item : items) {
                    sb.append("• ").append(item.toString()).append("\n");
                }
                sb.append("\n");
            }
        }
        
        sb.append("═══════════════════════════════════════");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("Restaurant %s [%d/%d places, File: %d, Revenus: %s]", 
            nom, clientsActuels, capacite, getTailleFile(), getRevenusFormates());
    }
}