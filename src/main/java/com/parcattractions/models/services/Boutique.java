package main.java.com.parcattractions.models.services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import main.java.com.parcattractions.exceptions.services.StockEpuiseException;
import main.java.com.parcattractions.utils.Logger;

public class Boutique {
    
    private final String nom;
    private final Map<Produit, Integer> stock; // Produit -> Quantité
    private final AtomicLong revenus; // en centimes
    private final int seuilStockFaible;
    private volatile boolean ouverte = true;
    
    /**
     * Constructeur
     * @param nom Nom de la boutique
     */
    public Boutique(String nom) {
        this.nom = nom;
        this.stock = new HashMap<>();
        this.revenus = new AtomicLong(0);
        this.seuilStockFaible = 5;
        
        // Initialiser stock par défaut
        initialiserStock();
        
        Logger.logInfo("Boutique créée: " + nom);
    }
    
    /**
     * Initialise le stock avec des produits par défaut
     */
    private void initialiserStock() {
        // Souvenirs
        ajouterProduit(new Produit("Peluche Mascotte", "Souvenirs", 15.00, 
            "Peluche officielle du parc"), 50);
        ajouterProduit(new Produit("Porte-clés", "Souvenirs", 5.00, 
            "Porte-clés métal"), 100);
        ajouterProduit(new Produit("Mug Logo Parc", "Souvenirs", 10.00, 
            "Mug céramique 350ml"), 30);
        ajouterProduit(new Produit("Casquette", "Souvenirs", 12.00, 
            "Casquette brodée"), 40);
        
        // Vêtements
        ajouterProduit(new Produit("T-Shirt Adulte", "Vêtements", 25.00, 
            "100% coton, tailles S-XXL"), 60);
        ajouterProduit(new Produit("T-Shirt Enfant", "Vêtements", 18.00, 
            "100% coton, tailles 4-14 ans"), 40);
        ajouterProduit(new Produit("Sweat à capuche", "Vêtements", 40.00, 
            "Coton/Polyester"), 25);
        
        // Photos
        ajouterProduit(new Produit("Photo Souvenir", "Photos", 15.00, 
            "Photo imprimée 20x30cm"), 200);
        ajouterProduit(new Produit("Cadre Photo", "Photos", 8.00, 
            "Cadre bois 20x30cm"), 30);
        
        Logger.logInfo("Stock initialisé pour " + nom + " (" + stock.size() + " produits)");
    }
    
    /**
     * Ajoute un produit au stock
     * @param produit Produit à ajouter
     * @param quantite Quantité initiale
     */
    public synchronized void ajouterProduit(Produit produit, int quantite) {
        stock.put(produit, quantite);
    }
    
    /**
     * Vend un produit à un visiteur
     * @param produit Produit à vendre
     * @param visiteurId ID du visiteur
     * @return Vrai si vente réussie
     * @throws StockEpuiseException Si produit épuisé
     */
    public synchronized boolean vendre(Produit produit, int visiteurId) 
            throws StockEpuiseException {
        
        // Vérifier stock
        Integer quantite = stock.get(produit);
        
        if (quantite == null || quantite <= 0) {
            throw new StockEpuiseException(nom, produit.getNom());
        }
        
        // Diminuer stock
        stock.put(produit, quantite - 1);
        
        // Ajouter revenus
        ajouterRevenus(produit.getPrix());
        
        Logger.logInfo(String.format("Boutique %s: Vente %s à visiteur %d (%.2f€)", 
            nom, produit.getNom(), visiteurId, produit.getPrix()));
        
        // Vérifier si stock faible
        if (quantite - 1 <= seuilStockFaible && quantite - 1 > 0) {
            Logger.logWarning(String.format("Stock faible pour %s dans %s: %d restants", 
                produit.getNom(), nom, quantite - 1));
        } else if (quantite - 1 == 0) {
            Logger.logWarning(String.format("Stock épuisé pour %s dans %s", 
                produit.getNom(), nom));
        }
        
        return true;
    }
    
    /**
     * Réapprovisionne un produit
     * @param produit Produit à réapprovisionner
     * @param quantite Quantité à ajouter
     */
    public synchronized void reapprovisionner(Produit produit, int quantite) {
        Integer stockActuel = stock.getOrDefault(produit, 0);
        stock.put(produit, stockActuel + quantite);
        
        Logger.logInfo(String.format("Boutique %s: Réapprovisionnement %s (+%d, total: %d)", 
            nom, produit.getNom(), quantite, stockActuel + quantite));
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
     * Méthode publique pour ajouter des revenus (ServiceManager)
     */
    public synchronized void ajouterRevenu(double montant) {
        ajouterRevenus(montant);
    }

    /**
     * Indique si la boutique est ouverte
     */
    public boolean isOuverte() {
        return ouverte;
    }

    /**
     * Définit le statut d'ouverture de la boutique
     */
    public void setOuverte(boolean ouverte) {
        this.ouverte = ouverte;
    }
    
    /**
     * @return Nom de la boutique
     */
    public String getNom() {
        return nom;
    }
    
    /**
     * @return Revenus totaux en euros
     */
    public double getRevenus() {
        return revenus.get() / 100.0;
    }
    
    /**
     * @return Revenus formatés
     */
    public String getRevenusFormates() {
        return String.format("%.2f €", getRevenus());
    }
    
    /**
     * Retourne la quantité en stock d'un produit
     * @param produit Produit
     * @return Quantité (0 si inexistant)
     */
    public synchronized int getQuantiteStock(Produit produit) {
        return stock.getOrDefault(produit, 0);
    }
    
    /**
     * Vérifie si un produit est disponible
     * @param produit Produit
     * @return Vrai si en stock
     */
    public synchronized boolean estDisponible(Produit produit) {
        return getQuantiteStock(produit) > 0;
    }
    
    /**
     * @return Copie du stock
     */
    public synchronized Map<Produit, Integer> getStock() {
        return new HashMap<>(stock);
    }
    
    /**
     * @return Liste des produits en stock faible
     */
    public synchronized Map<Produit, Integer> getProduitsStockFaible() {
        Map<Produit, Integer> stockFaible = new HashMap<>();
        
        for (Map.Entry<Produit, Integer> entry : stock.entrySet()) {
            if (entry.getValue() > 0 && entry.getValue() <= seuilStockFaible) {
                stockFaible.put(entry.getKey(), entry.getValue());
            }
        }
        
        return stockFaible;
    }
    
    /**
     * @return Liste des produits épuisés
     */
    public synchronized Map<Produit, Integer> getProduitsEpuises() {
        Map<Produit, Integer> epuises = new HashMap<>();
        
        for (Map.Entry<Produit, Integer> entry : stock.entrySet()) {
            if (entry.getValue() == 0) {
                epuises.put(entry.getKey(), 0);
            }
        }
        
        return epuises;
    }
    
    /**
     * @return Nombre total de produits différents
     */
    public synchronized int getNombreProduits() {
        return stock.size();
    }
    
    /**
     * @return Valeur totale du stock
     */
    public synchronized double getValeurStock() {
        double valeur = 0.0;
        
        for (Map.Entry<Produit, Integer> entry : stock.entrySet()) {
            valeur += entry.getKey().getPrix() * entry.getValue();
        }
        
        return valeur;
    }
    
    /**
     * Affiche le catalogue complet
     * @return Catalogue formaté
     */
    public synchronized String afficherCatalogue() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("   CATALOGUE - ").append(nom.toUpperCase()).append("\n");
        sb.append("═══════════════════════════════════════\n\n");
        
        // Grouper par catégorie
        Map<String, Map<Produit, Integer>> parCategorie = new HashMap<>();
        
        for (Map.Entry<Produit, Integer> entry : stock.entrySet()) {
            String categorie = entry.getKey().getCategorie();
            parCategorie.computeIfAbsent(categorie, k -> new HashMap<>())
                       .put(entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<String, Map<Produit, Integer>> catEntry : parCategorie.entrySet()) {
            sb.append("--- ").append(catEntry.getKey().toUpperCase()).append(" ---\n");
            
            for (Map.Entry<Produit, Integer> prodEntry : catEntry.getValue().entrySet()) {
                Produit p = prodEntry.getKey();
                int qte = prodEntry.getValue();
                
                sb.append(String.format("• %s - %s ", 
                    p.getNom(), p.getPrixFormate()));
                
                if (qte == 0) {
                    sb.append("[EPUISE]");
                } else if (qte <= seuilStockFaible) {
                    sb.append("[Stock faible] (").append(qte).append(")");
                } else {
                    sb.append("[En stock] (").append(qte).append(")");
                }
                
                sb.append("\n");
            }
            sb.append("\n");
        }
        
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("Valeur stock: %s | Revenus: %s\n", 
            String.format("%.2f €", getValeurStock()), 
            getRevenusFormates()));
        sb.append("═══════════════════════════════════════");
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("Boutique %s [%d produits, Stock: %.2f€, Revenus: %s]", 
            nom, getNombreProduits(), getValeurStock(), getRevenusFormates());
    }
}