package main.java.com.parcattractions.models.attractions;

import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.TypeAttraction;

public class MaisonHantee extends Attraction {
    
    public MaisonHantee() {
        super(
            "Maison Hantée des Ombres",
            12,                              // Capacité: 12 personnes
            360,                             // Durée: 6 minutes (360 secondes)
            TypeAttraction.SENSATIONS_FORTES,
            NiveauIntensite.MODERE,
            10,                              // Âge minimum: 10 ans
            0,                               // Pas de restriction de taille
            false                            // Intérieure
        );
    }
}