package main.java.com.parcattractions.models.attractions;

import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.TypeAttraction;

public class ChuteLibre extends Attraction {
    
    public ChuteLibre() {
        super(
            "Chute Libre Vertigineuse",
            4,                               // Capacité: 4 personnes
            120,                             // Durée: 2 minutes (120 secondes)
            TypeAttraction.SENSATIONS_FORTES,
            NiveauIntensite.EXTREME,
            16,                              // Âge minimum: 16 ans
            150,                             // Taille minimum: 150 cm
            true                             // Extérieure
        );
    }
}