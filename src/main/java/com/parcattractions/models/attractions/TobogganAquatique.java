package main.java.com.parcattractions.models.attractions;

import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.TypeAttraction;

public class TobogganAquatique extends Attraction {
    
    public TobogganAquatique() {
        super(
            "Toboggan Aquatique Splash",
            10,                              // Capacité: 10 personnes
            180,                             // Durée: 3 minutes (180 secondes)
            TypeAttraction.AQUATIQUE,
            NiveauIntensite.MODERE,
            8,                               // Âge minimum: 8 ans
            120,                             // Taille minimum: 120 cm
            true,                                // Extérieure
            "resources/images/tobogganaquatique.png",
            5.0, 8.0  // Ticket normal 5€, Fast Pass 8€
        );
    }
}