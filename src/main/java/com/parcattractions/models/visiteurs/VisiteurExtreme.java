package main.java.com.parcattractions.models.visiteurs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.ProfilVisiteur;
import main.java.com.parcattractions.enums.TypeAttraction;
import main.java.com.parcattractions.models.attractions.Attraction;

public class VisiteurExtreme extends Visiteur {
    
    private final int seuilIntensite;
    private final Map<TypeAttraction, Integer> preferences;
    
    public VisiteurExtreme(int age, int taille) {
        super("Extreme", age, taille, ProfilVisiteur.EXTREME);
        this.seuilIntensite = 2; // Minimum modéré
        
        // Définir les préférences
        this.preferences = new HashMap<>();
        preferences.put(TypeAttraction.SENSATIONS_FORTES, 100);
        preferences.put(TypeAttraction.AQUATIQUE, 70);
        preferences.put(TypeAttraction.FAMILIALE, 20);
        preferences.put(TypeAttraction.ENFANTS, 5);
        preferences.put(TypeAttraction.ROMANTIQUE, 15);
    }
    
    @Override
    protected Attraction choisirAttraction() {
        main.java.com.parcattractions.controllers.GestionnaireParc gestionnaire = 
            main.java.com.parcattractions.controllers.GestionnaireParc.getInstance();
        
        List<Attraction> disponibles = gestionnaire.getAttractionsDisponibles();
        
        // Filtrer attractions déjà visitées et vérifier restrictions
        List<Attraction> candidates = new ArrayList<>();
        for (Attraction attraction : disponibles) {
            if (!attractionsVisitees.contains(attraction) && 
                attraction.accepteVisiteur(age, taille)) {
                candidates.add(attraction);
            }
        }
        
        if (candidates.isEmpty()) {
            return null;
        }
        
        // Trouver la meilleure attraction selon score
        Attraction meilleure = null;
        double meilleurScore = Double.NEGATIVE_INFINITY;
        
        for (Attraction attraction : candidates) {
            double score = calculerScore(attraction);
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleure = attraction;
            }
        }
        
        return meilleure;
    }
    
    /**
     * Calcule le score selon préférences extrêmes
     */
    protected double calculerScore(Attraction attraction) {
        double score = 0;
        
        // Bonus fort pour sensations
        score += preferences.getOrDefault(attraction.getType(), 30);
        
        // Bonus intensité élevée
        if (attraction.getIntensite() == NiveauIntensite.EXTREME) {
            score += 50;
        } else if (attraction.getIntensite() == NiveauIntensite.MODERE) {
            score += 20;
        } else {
            score -= 30; // Pénalité si trop calme
        }
        
        // Moins sensible au temps d'attente
        int tempsAttente = attraction.getTempsAttenteEstime();
        score -= tempsAttente * 0.2;
        
        // Pénalité si déjà visitée
        if (attractionsVisitees.contains(attraction)) {
            score -= 60;
        }
        
        // Facteur aléatoire
        score += (Math.random() * 15) - 7;
        
        return score;
    }
    
    public int getSeuilIntensite() {
        return seuilIntensite;
    }
}