package main.java.com.parcattractions.models.attractions;

import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.TypeAttraction;

public class MontagnesRusses extends Attraction {
    
    public MontagnesRusses() {
        super(
            "Montagnes Russes Extrême",
            20,                              // Capacité: 20 personnes
            300,                             // Durée: 5 minutes (300 secondes)
            TypeAttraction.SENSATIONS_FORTES,
            NiveauIntensite.EXTREME,
            12,                              // Âge minimum: 12 ans
            140,                             // Taille minimum: 140 cm
            true                             // Extérieure
        );
    }
}