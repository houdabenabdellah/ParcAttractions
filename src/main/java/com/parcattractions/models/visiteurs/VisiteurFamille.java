package main.java.com.parcattractions.models.visiteurs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.ProfilVisiteur;
import main.java.com.parcattractions.enums.TypeAttraction;
import main.java.com.parcattractions.models.attractions.Attraction;

public class VisiteurFamille extends Visiteur {
    
    private final int nombreEnfants;
    private final Map<TypeAttraction, Integer> preferences;
    
    public VisiteurFamille(int age, int taille, int nombreEnfants) {
        super("Famille", age, taille, ProfilVisiteur.FAMILLE);
        this.nombreEnfants = nombreEnfants;
        
        // Définir les préférences
        this.preferences = new HashMap<>();
        preferences.put(TypeAttraction.FAMILIALE, 100);
        preferences.put(TypeAttraction.ENFANTS, 80);
        preferences.put(TypeAttraction.ROMANTIQUE, 40);
        preferences.put(TypeAttraction.AQUATIQUE, 60);
        preferences.put(TypeAttraction.SENSATIONS_FORTES, 10);
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
     * Calcule le score d'une attraction selon les préférences familiales
     */
    protected double calculerScore(Attraction attraction) {
        double score = 0;
        
        // Bonus selon le type
        score += preferences.getOrDefault(attraction.getType(), 50);
        
        // Pénalité temps d'attente
        int tempsAttente = attraction.getTempsAttenteEstime();
        score -= tempsAttente * 0.3; // Familles moins patientes
        
        // Bonus si intensité faible
        if (attraction.getIntensite() == NiveauIntensite.FAIBLE) {
            score += 30;
        }
        
        // Pénalité si déjà visitée
        if (attractionsVisitees.contains(attraction)) {
            score -= 50;
        }
        
        // Pénalité si restrictions trop élevées
        if (attraction.getAgeMin() > 10) {
            score -= 40; // Difficile avec enfants
        }
        
        // Facteur aléatoire pour variété
        score += (Math.random() * 20) - 10;
        
        return score;
    }
    
    public int getNombreEnfants() {
        return nombreEnfants;
    }
}
