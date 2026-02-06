package main.java.com.parcattractions.models.visiteurs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.ProfilVisiteur;
import main.java.com.parcattractions.enums.TypeAttraction;
import main.java.com.parcattractions.models.attractions.Attraction;

public class VisiteurCouple extends Visiteur {
    
    private final Visiteur partenaire;
    private final Map<TypeAttraction, Integer> preferences;
    
    public VisiteurCouple(int age, int taille, Visiteur partenaire) {
        this("Couple", age, taille, partenaire);
    }
    
    public VisiteurCouple(String nom, int age, int taille, Visiteur partenaire) {
        super(nom != null && !nom.isBlank() ? nom : "Couple", age, taille, ProfilVisiteur.COUPLE);
        this.partenaire = partenaire;
        
        // Définir les préférences
        this.preferences = new HashMap<>();
        preferences.put(TypeAttraction.ROMANTIQUE, 100);
        preferences.put(TypeAttraction.FAMILIALE, 60);
        preferences.put(TypeAttraction.SENSATIONS_FORTES, 40);
        preferences.put(TypeAttraction.AQUATIQUE, 50);
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
     * Calcule le score selon préférences couple
     */
    protected double calculerScore(Attraction attraction) {
        double score = 0;
        
        // Préférences type
        score += preferences.getOrDefault(attraction.getType(), 40);
        
        // Bonus pour intensité modérée
        if (attraction.getIntensite() == NiveauIntensite.MODERE) {
            score += 20;
        } else if (attraction.getIntensite() == NiveauIntensite.FAIBLE) {
            score += 30; // Préfère romantique
        }
        
        // Patience moyenne
        int tempsAttente = attraction.getTempsAttenteEstime();
        score -= tempsAttente * 0.4;
        
        // Pénalité si déjà visitée
        if (attractionsVisitees.contains(attraction)) {
            score -= 55;
        }
        
        // Facteur aléatoire
        score += (Math.random() * 20) - 10;
        
        return score;
    }
    
    public Visiteur getPartenaire() {
        return partenaire;
    }
}