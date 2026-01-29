package main.java.com.parcattractions.models.visiteurs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.ProfilVisiteur;
import main.java.com.parcattractions.enums.TypeAttraction;
import main.java.com.parcattractions.models.attractions.Attraction;

public class VisiteurGroupe extends Visiteur {
    
    private final List<Visiteur> membres;
    private final Map<TypeAttraction, Integer> preferences;
    
    public VisiteurGroupe(int age, int taille) {
        super("Groupe", age, taille, ProfilVisiteur.GROUPE);
        this.membres = new ArrayList<>();
        
        // Définir les préférences
        this.preferences = new HashMap<>();
        preferences.put(TypeAttraction.SENSATIONS_FORTES, 80);
        preferences.put(TypeAttraction.FAMILIALE, 60);
        preferences.put(TypeAttraction.AQUATIQUE, 50);
        preferences.put(TypeAttraction.ROMANTIQUE, 30);
        preferences.put(TypeAttraction.ENFANTS, 20);
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
     * Calcule le score selon préférences groupe
     */
    protected double calculerScore(Attraction attraction) {
        double score = 0;
        
        // Préférences type
        score += preferences.getOrDefault(attraction.getType(), 45);
        
        // Variété d'intensités appréciée
        if (attraction.getIntensite() == NiveauIntensite.EXTREME ||
            attraction.getIntensite() == NiveauIntensite.MODERE) {
            score += 25;
        }
        
        // Tolérance moyenne au temps d'attente
        int tempsAttente = attraction.getTempsAttenteEstime();
        score -= tempsAttente * 0.35;
        
        // Pénalité si déjà visitée
        if (attractionsVisitees.contains(attraction)) {
            score -= 50;
        }
        
        // Facteur aléatoire
        score += (Math.random() * 25) - 12;
        
        return score;
    }
    
    /**
     * Ajoute un membre au groupe
     */
    public void ajouterMembre(Visiteur visiteur) {
        membres.add(visiteur);
    }
    
    public List<Visiteur> getMembres() {
        return new ArrayList<>(membres);
    }
    
    public int getTailleGroupe() {
        return membres.size() + 1; // +1 pour ce visiteur
    }
}