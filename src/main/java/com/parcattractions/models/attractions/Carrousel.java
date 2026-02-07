package main.java.com.parcattractions.models.attractions;

import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.TypeAttraction;

public class Carrousel extends Attraction {
    
    public Carrousel() {
        super(
            "Carrousel Magique",
            30,                              // Capacité: 30 personnes
            240,                             // Durée: 4 minutes (240 secondes)
            TypeAttraction.ROMANTIQUE,
            NiveauIntensite.FAIBLE,
            0,                               // Âge minimum: tous âges
            0,                               // Pas de restriction de taille
            true,                             // Extérieure
            "/main/java/com/parcattractions/resources/images/carrousel.png",
            4.0, 6.0  // Ticket normal 4€, Fast Pass 6€
        );
    }
}