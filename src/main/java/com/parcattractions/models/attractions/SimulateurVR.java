package main.java.com.parcattractions.models.attractions;

import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.TypeAttraction;

public class SimulateurVR extends Attraction {
    
    public SimulateurVR() {
        super(
            "Simulateur VR Spatial",
            8,                               // Capacité: 8 personnes
            240,                             // Durée: 4 minutes (240 secondes)
            TypeAttraction.SENSATIONS_FORTES,
            NiveauIntensite.EXTREME,
            14,                              // Âge minimum: 14 ans
            0,                               // Pas de restriction de taille
            false,                            // Intérieure
            "/main/java/com/parcattractions/resources/images/simulateurvrspatial.png",
            7.0, 11.0  // Ticket normal 7€, Fast Pass 11€
        );
    }
}
