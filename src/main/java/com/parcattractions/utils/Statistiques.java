package main.java.com.parcattractions.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Classe pour gérer les statistiques du parc en temps réel
 * Thread-safe grâce aux AtomicInteger/AtomicLong
 */
public class Statistiques {
    
    // Compteurs atomiques pour thread-safety
    private final AtomicInteger nombreVisiteursTotal;
    private final AtomicInteger nombreVisiteursActuels;
    private final AtomicInteger nombreToursEffectues;
    private final AtomicLong revenusTotal; // en centimes pour éviter erreurs float
    private final AtomicInteger avisPositifs;
    private final AtomicInteger avisNegatifs;
    
    // File et disponibilité par attraction (nom -> valeur)
    private final Map<String, Integer> derniereTailleFile;
    private final Map<String, Boolean> disponibiliteAttractions;
    
    // Statistiques calculées
    private volatile double tempsAttenteMoyen;
    private volatile double satisfactionMoyenne;
    
    /**
     * Constructeur
     */
    public Statistiques() {
        this.nombreVisiteursTotal = new AtomicInteger(0);
        this.nombreVisiteursActuels = new AtomicInteger(0);
        this.nombreToursEffectues = new AtomicInteger(0);
        this.revenusTotal = new AtomicLong(0);
        this.avisPositifs = new AtomicInteger(0);
        this.avisNegatifs = new AtomicInteger(0);
        this.derniereTailleFile = new ConcurrentHashMap<>();
        this.disponibiliteAttractions = new ConcurrentHashMap<>();
        this.tempsAttenteMoyen = 0.0;
        this.satisfactionMoyenne = 0.0;
    }
    
    /**
     * Incrémente le nombre de visiteurs totaux
     */
    public void ajouterVisiteur() {
        nombreVisiteursTotal.incrementAndGet();
        nombreVisiteursActuels.incrementAndGet();
        Logger.logDebug("Visiteur ajouté - Total: " + nombreVisiteursTotal.get());
    }
    
    /**
     * Décrémente le nombre de visiteurs actuels
     */
    public void retirerVisiteur() {
        nombreVisiteursActuels.decrementAndGet();
        Logger.logDebug("Visiteur retiré - Actuels: " + nombreVisiteursActuels.get());
    }
    
    /**
     * Incrémente le nombre de tours effectués
     */
    public void ajouterTour() {
        nombreToursEffectues.incrementAndGet();
    }
    
    /**
     * Ajoute des revenus
     * @param montant Montant en euros
     */
    public void ajouterRevenus(double montant) {
        long centimes = Math.round(montant * 100);
        revenusTotal.addAndGet(centimes);
    }
    
    /**
     * Met à jour la file d'attente d'une attraction (diagramme séquence).
     * @param nomAttraction Nom de l'attraction
     * @param tailleFile Taille actuelle de la file
     */
    public void mettreAJourFile(String nomAttraction, int tailleFile) {
        derniereTailleFile.put(nomAttraction, tailleFile);
    }
    
    /**
     * Met à jour la disponibilité d'une attraction (maintenance/panne réparée).
     * @param nomAttraction Nom de l'attraction
     * @param dispo true si opérationnelle, false si maintenance/panne
     */
    public void mettreAJourDisponibilite(String nomAttraction, boolean dispo) {
        disponibiliteAttractions.put(nomAttraction, dispo);
    }
    
    /**
     * Enregistre un avis visiteur en quittant (diagramme activité).
     * @param positif true si satisfaction > 70%, false sinon
     */
    public void enregistrerAvis(boolean positif) {
        if (positif) {
            avisPositifs.incrementAndGet();
        } else {
            avisNegatifs.incrementAndGet();
        }
    }
    
    public int getAvisPositifs() { return avisPositifs.get(); }
    public int getAvisNegatifs() { return avisNegatifs.get(); }
    
    public Map<String, Integer> getDerniereTailleFile() {
        return new ConcurrentHashMap<>(derniereTailleFile);
    }
    
    public Map<String, Boolean> getDisponibiliteAttractions() {
        return new ConcurrentHashMap<>(disponibiliteAttractions);
    }
    
    /**
     * @return Nombre total de visiteurs depuis l'ouverture
     */
    public int getNombreVisiteursTotal() {
        return nombreVisiteursTotal.get();
    }
    
    /**
     * @return Nombre de visiteurs actuellement dans le parc
     */
    public int getNombreVisiteursActuels() {
        return nombreVisiteursActuels.get();
    }
    
    /**
     * @return Nombre total de tours effectués
     */
    public int getNombreToursEffectues() {
        return nombreToursEffectues.get();
    }
    
    /**
     * @return Revenus totaux en euros
     */
    public double getRevenusTotal() {
        return revenusTotal.get() / 100.0;
    }
    
    /**
     * @return Revenus formatés
     */
    public String getRevenusTotalFormate() {
        return String.format("%.2f €", getRevenusTotal());
    }
    
    /**
     * Met à jour le temps d'attente moyen
     * @param tempsAttente Temps d'attente en secondes
     */
    public synchronized void mettreAJourTempsAttente(double tempsAttente) {
        // Moyenne mobile simple (à améliorer)
        this.tempsAttenteMoyen = (this.tempsAttenteMoyen + tempsAttente) / 2.0;
    }
    
    /**
     * @return Temps d'attente moyen en secondes
     */
    public synchronized double getTempsAttenteMoyen() {
        return tempsAttenteMoyen;
    }
    
    /**
     * @return Temps d'attente moyen formaté (ou "—" si jamais mis à jour)
     */
    public synchronized String getTempsAttenteMoyenFormate() {
        if (tempsAttenteMoyen <= 0 && nombreToursEffectues.get() == 0) {
            return "—";
        }
        int minutes = (int) (tempsAttenteMoyen / 60);
        int secondes = (int) (tempsAttenteMoyen % 60);
        return String.format("%d min %d sec", minutes, secondes);
    }
    
    /**
     * Met à jour la satisfaction moyenne
     * @param satisfaction Satisfaction (0-100)
     */
    public synchronized void mettreAJourSatisfaction(double satisfaction) {
        // Moyenne mobile simple
        this.satisfactionMoyenne = (this.satisfactionMoyenne + satisfaction) / 2.0;
    }
    
    /**
     * @return Satisfaction moyenne (0-100)
     */
    public synchronized double getSatisfactionMoyenne() {
        return satisfactionMoyenne;
    }
    
    /**
     * @return Satisfaction formatée (ou "—" si aucun avis)
     */
    public synchronized String getSatisfactionFormatee() {
        int totalAvis = avisPositifs.get() + avisNegatifs.get();
        if (totalAvis == 0) {
            return "— (aucun avis)";
        }
        String indicateur;
        if (satisfactionMoyenne >= 80) {
            indicateur = "[Excellent]";
        } else if (satisfactionMoyenne >= 60) {
            indicateur = "[Bon]";
        } else if (satisfactionMoyenne >= 40) {
            indicateur = "[Moyen]";
        } else {
            indicateur = "[Mauvais]";
        }
        return String.format("%.1f%% %s", satisfactionMoyenne, indicateur);
    }
    
    /**
     * Calcule le revenu moyen par visiteur
     * @return Revenu moyen en euros
     */
    public double getRevenuMoyenParVisiteur() {
        int total = nombreVisiteursTotal.get();
        if (total == 0) {
            return 0.0;
        }
        return getRevenusTotal() / total;
    }
    
    /**
     * Réinitialise toutes les statistiques
     */
    public synchronized void reinitialiser() {
        nombreVisiteursTotal.set(0);
        nombreVisiteursActuels.set(0);
        nombreToursEffectues.set(0);
        revenusTotal.set(0);
        avisPositifs.set(0);
        avisNegatifs.set(0);
        derniereTailleFile.clear();
        disponibiliteAttractions.clear();
        tempsAttenteMoyen = 0.0;
        satisfactionMoyenne = 0.0;
        Logger.logInfo("Statistiques réinitialisées");
    }
    
    /**
     * Génère un rapport textuel des statistiques
     * @return Rapport formaté
     */
    public synchronized String genererRapport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== STATISTIQUES DU PARC ===\n");
        sb.append("Visiteurs total: ").append(nombreVisiteursTotal.get()).append("\n");
        sb.append("Visiteurs actuels: ").append(nombreVisiteursActuels.get()).append("\n");
        sb.append("Tours effectués: ").append(nombreToursEffectues.get()).append("\n");
        sb.append("Revenus: ").append(getRevenusTotalFormate()).append("\n");
        sb.append("Revenu moyen/visiteur: ").append(String.format("%.2f €", getRevenuMoyenParVisiteur())).append("\n");
        sb.append("Temps attente moyen: ").append(getTempsAttenteMoyenFormate()).append("\n");
        sb.append("Satisfaction: ").append(getSatisfactionFormatee()).append("\n");
        sb.append("Avis positifs: ").append(avisPositifs.get()).append(" | Négatifs: ").append(avisNegatifs.get()).append("\n");
        sb.append("============================");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return genererRapport();
    }
}
