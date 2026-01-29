package main.java.com.parcattractions.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Gestionnaire centralisé des transactions du parc
 * Trackage automatique des ventes billets, restaurant et souvenirs
 * Permet de générer des rapports financiers en temps réel
 */
public class TransactionManager {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Listes en mémoire pour cache temps-réel
    private static final List<Transaction> transactions = Collections.synchronizedList(new ArrayList<>());
    
    // Statistiques cumulées
    private static double revenuBillets = 0.0;
    private static double revenuRestaurant = 0.0;
    private static double revenuSouvenirs = 0.0;
    private static double revenuTotal = 0.0;
    
    // Compteurs
    private static int nbTransactionsBillets = 0;
    private static int nbTransactionsRestaurant = 0;
    private static int nbTransactionsSouvenirs = 0;
    
    /**
     * Classe interne pour représenter une transaction
     */
    public static class Transaction {
        public long id;
        public int idVisiteur;
        public String type; // "BILLET", "RESTAURANT", "SOUVENIR"
        public String description; // Détail du billet/article
        public double montant;
        public LocalDateTime timestamp;
        
        public Transaction(int idVisiteur, String type, String description, double montant) {
            this.id = System.currentTimeMillis();
            this.idVisiteur = idVisiteur;
            this.type = type;
            this.description = description;
            this.montant = montant;
            this.timestamp = LocalDateTime.now();
        }
        
        @Override
        public String toString() {
            return String.format("[%s] %s - Visiteur %d: %.2f€ (%s)", 
                timestamp.format(DATE_FORMATTER), type, idVisiteur, montant, description);
        }
    }
    
    /**
     * Enregistre une vente de billet
     */
    public static void enregistrerVenteBillet(int idVisiteur, String typeBillet, double prix, int ageVisiteur) {
        Transaction t = new Transaction(idVisiteur, "BILLET", typeBillet, prix);
        transactions.add(t);
        revenuBillets += prix;
        revenuTotal += prix;
        nbTransactionsBillets++;
        
        // Sauvegarder en CSV
        CSVManager.enregistrerVenteBillet(idVisiteur, typeBillet, prix, ageVisiteur);
        
        Logger.logInfo("Vente billet enregistrée: " + t.toString());
    }
    
    /**
     * Enregistre une dépense restaurant
     */
    public static void enregistrerVenteRestaurant(int idVisiteur, String typeRepas, double prix) {
        Transaction t = new Transaction(idVisiteur, "RESTAURANT", typeRepas, prix);
        transactions.add(t);
        revenuRestaurant += prix;
        revenuTotal += prix;
        nbTransactionsRestaurant++;
        
        // Sauvegarder en CSV
        CSVManager.enregistrerVenteRestaurant(idVisiteur, typeRepas, prix);
        
        Logger.logInfo("Vente restaurant enregistrée: " + t.toString());
    }
    
    /**
     * Enregistre un achat de souvenir
     */
    public static void enregistrerVenteSouvenir(int idVisiteur, String article, double prix) {
        Transaction t = new Transaction(idVisiteur, "SOUVENIR", article, prix);
        transactions.add(t);
        revenuSouvenirs += prix;
        revenuTotal += prix;
        nbTransactionsSouvenirs++;
        
        // Sauvegarder en CSV
        CSVManager.enregistrerVenteSouvenir(idVisiteur, article, prix);
        
        Logger.logInfo("Vente souvenir enregistrée: " + t.toString());
    }
    
    // ================= REQUÊTES =================
    
    /**
     * Retourne toutes les transactions
     */
    public static List<Transaction> obtenirTransactions() {
        return new ArrayList<>(transactions);
    }
    
    /**
     * Retourne les transactions d'un visiteur spécifique
     */
    public static List<Transaction> obtenirTransactionsVisiteur(int idVisiteur) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.idVisiteur == idVisiteur) {
                result.add(t);
            }
        }
        return result;
    }
    
    /**
     * Retourne les transactions d'un type spécifique
     */
    public static List<Transaction> obtenirTransactionsType(String type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.type.equals(type)) {
                result.add(t);
            }
        }
        return result;
    }
    
    /**
     * Retourne les transactions d'une période
     */
    public static List<Transaction> obtenirTransactionsPeriode(LocalDateTime debut, LocalDateTime fin) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (!t.timestamp.isBefore(debut) && !t.timestamp.isAfter(fin)) {
                result.add(t);
            }
        }
        return result;
    }
    
    // ================= STATISTIQUES =================
    
    /**
     * Retourne les statistiques financières
     */
    public static Map<String, Object> obtenirStatistiques() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("revenuBillets", revenuBillets);
        stats.put("revenuRestaurant", revenuRestaurant);
        stats.put("revenuSouvenirs", revenuSouvenirs);
        stats.put("revenuTotal", revenuTotal);
        stats.put("nbTransactionsBillets", nbTransactionsBillets);
        stats.put("nbTransactionsRestaurant", nbTransactionsRestaurant);
        stats.put("nbTransactionsSouvenirs", nbTransactionsSouvenirs);
        stats.put("nbTransactionsTotal", transactions.size());
        stats.put("ticketMoyen", nbTransactionsBillets > 0 ? revenuBillets / nbTransactionsBillets : 0.0);
        return stats;
    }
    
    /**
     * Retourne le rapport financier sous forme de texte
     */
    public static String genererRapportFinancier() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════╗\n");
        sb.append("║         RAPPORT FINANCIER - PARC           ║\n");
        sb.append("╚════════════════════════════════════════════╝\n\n");
        
        sb.append("REVENUS PAR CATEGORIE\n");
        sb.append("├─ Billets:      ").append(String.format("%.2f€", revenuBillets)).append("\n");
        sb.append("├─ Restaurant:   ").append(String.format("%.2f€", revenuRestaurant)).append("\n");
        sb.append("├─ Souvenirs:    ").append(String.format("%.2f€", revenuSouvenirs)).append("\n");
        sb.append("└─ TOTAL:        ").append(String.format("%.2f€", revenuTotal)).append("\n\n");
        
        sb.append("NOMBRE DE TRANSACTIONS\n");
        sb.append("├─ Billets:      ").append(nbTransactionsBillets).append("\n");
        sb.append("├─ Restaurant:   ").append(nbTransactionsRestaurant).append("\n");
        sb.append("├─ Souvenirs:    ").append(nbTransactionsSouvenirs).append("\n");
        sb.append("└─ TOTAL:        ").append(transactions.size()).append("\n\n");
        
        sb.append("MOYENNES\n");
        sb.append("├─ Billet moyen:      ").append(String.format("%.2f€", nbTransactionsBillets > 0 ? revenuBillets / nbTransactionsBillets : 0.0)).append("\n");
        sb.append("├─ Repas moyen:       ").append(String.format("%.2f€", nbTransactionsRestaurant > 0 ? revenuRestaurant / nbTransactionsRestaurant : 0.0)).append("\n");
        sb.append("├─ Souvenir moyen:    ").append(String.format("%.2f€", nbTransactionsSouvenirs > 0 ? revenuSouvenirs / nbTransactionsSouvenirs : 0.0)).append("\n");
        sb.append("└─ Dépense moyenne:   ").append(String.format("%.2f€", transactions.size() > 0 ? revenuTotal / transactions.size() : 0.0)).append("\n\n");
        
        sb.append("DERNIERES TRANSACTIONS\n");
        int count = Math.min(5, transactions.size());
        for (int i = transactions.size() - count; i < transactions.size(); i++) {
            sb.append("└─ ").append(transactions.get(i).toString()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Génère un rapport détaillé par type
     */
    public static String genererRapportDetaille() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════════╗\n");
        sb.append("║      RAPPORT DÉTAILLÉ DES TRANSACTIONS     ║\n");
        sb.append("╚════════════════════════════════════════════╝\n\n");
        
        // Ventes billets
        List<Transaction> billets = obtenirTransactionsType("BILLET");
        sb.append("VENTES DE BILLETS (").append(billets.size()).append(")\n");
        if (billets.isEmpty()) {
            sb.append("└─ Aucune vente\n");
        } else {
            for (Transaction t : billets) {
                sb.append("├─ ").append(t.toString()).append("\n");
            }
        }
        sb.append("\n");
        
        // Ventes restaurant
        List<Transaction> repas = obtenirTransactionsType("RESTAURANT");
        sb.append("VENTES RESTAURANT (").append(repas.size()).append(")\n");
        if (repas.isEmpty()) {
            sb.append("└─ Aucune vente\n");
        } else {
            for (Transaction t : repas) {
                sb.append("├─ ").append(t.toString()).append("\n");
            }
        }
        sb.append("\n");
        
        // Ventes souvenirs
        List<Transaction> souvenirs = obtenirTransactionsType("SOUVENIR");
        sb.append("VENTES SOUVENIRS (").append(souvenirs.size()).append(")\n");
        if (souvenirs.isEmpty()) {
            sb.append("└─ Aucune vente\n");
        } else {
            for (Transaction t : souvenirs) {
                sb.append("├─ ").append(t.toString()).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Réinitialise toutes les statistiques (nouvelle session)
     */
    public static void reinitialiserStatistiques() {
        transactions.clear();
        revenuBillets = 0.0;
        revenuRestaurant = 0.0;
        revenuSouvenirs = 0.0;
        revenuTotal = 0.0;
        nbTransactionsBillets = 0;
        nbTransactionsRestaurant = 0;
        nbTransactionsSouvenirs = 0;
        Logger.logInfo("Statistiques réinitialisées");
    }
    
    /**
     * Getters pour l'affichage en temps-réel
     */
    public static double getRevenuBillets() {
        return revenuBillets;
    }
    
    public static double getRevenuRestaurant() {
        return revenuRestaurant;
    }
    
    public static double getRevenuSouvenirs() {
        return revenuSouvenirs;
    }
    
    public static double getRevenuTotal() {
        return revenuTotal;
    }
    
    public static int getNbTransactions() {
        return transactions.size();
    }
    
    public static int getNbTransactionsBillets() {
        return nbTransactionsBillets;
    }
    
    public static int getNbTransactionsRestaurant() {
        return nbTransactionsRestaurant;
    }
    
    public static int getNbTransactionsSouvenirs() {
        return nbTransactionsSouvenirs;
    }
}
