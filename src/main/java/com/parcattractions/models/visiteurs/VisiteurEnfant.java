package main.java.com.parcattractions.models.visiteurs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.ProfilVisiteur;
import main.java.com.parcattractions.enums.TypeAttraction;
import main.java.com.parcattractions.models.attractions.Attraction;

public class VisiteurEnfant extends Visiteur {
    
    private final Visiteur accompagnateur;
    private final Map<TypeAttraction, Integer> preferences;
    
    public VisiteurEnfant(int age, int taille, Visiteur accompagnateur) {
        super("Enfant", age, taille, ProfilVisiteur.ENFANT);
        this.accompagnateur = accompagnateur;
        
        // Définir les préférences
        this.preferences = new HashMap<>();
        preferences.put(TypeAttraction.ENFANTS, 100);
        preferences.put(TypeAttraction.FAMILIALE, 60);
        preferences.put(TypeAttraction.AQUATIQUE, 50);
        preferences.put(TypeAttraction.ROMANTIQUE, 10);
        preferences.put(TypeAttraction.SENSATIONS_FORTES, 5);
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
     * Calcule le score selon préférences enfant
     */
    protected double calculerScore(Attraction attraction) {
        double score = 0;
        
        // Préférences type
        score += preferences.getOrDefault(attraction.getType(), 20);
        
        // Uniquement intensité faible
        if (attraction.getIntensite() == NiveauIntensite.FAIBLE) {
            score += 40;
        } else {
            score -= 60; // Enfants évitent sensations fortes
        }
        
        // Très sensible au temps d'attente (faible patience)
        int tempsAttente = attraction.getTempsAttenteEstime();
        score -= tempsAttente * 0.8;
        
        // Pénalité si déjà visitée
        if (attractionsVisitees.contains(attraction)) {
            score -= 40;
        }
        
        // Facteur aléatoire important (enfants imprévisibles)
        score += (Math.random() * 30) - 15;
        
        return score;
    }
    
    public Visiteur getAccompagnateur() {
        return accompagnateur;
    }
}