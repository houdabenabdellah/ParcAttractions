package main.java.com.parcattractions.models.attractions;

import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.TypeAttraction;

public class ManegeEnfants extends Attraction {
    
    public ManegeEnfants() {
        super(
            "Manège Enchanté",
            15,                              // Capacité: 15 personnes
            180,                             // Durée: 3 minutes (180 secondes)
            TypeAttraction.ENFANTS,
            NiveauIntensite.FAIBLE,
            3,                               // Âge minimum: 3 ans
            0,                               // Pas de restriction de taille
            false,                            // Intérieure
            "/main/java/com/parcattractions/resources/images/manegeenchante.png",
            3.0, 5.0  // Ticket normal 3€, Fast Pass 5€
        );
    }
}
