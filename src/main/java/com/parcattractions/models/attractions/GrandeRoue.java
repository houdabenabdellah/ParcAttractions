package main.java.com.parcattractions.models.attractions;

import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.TypeAttraction;

public class GrandeRoue extends Attraction {
    
    public GrandeRoue() {
        super(
            "Grande Roue Panoramique",
            50,                              // Capacité: 50 personnes
            480,                             // Durée: 8 minutes (480 secondes)
            TypeAttraction.FAMILIALE,
            NiveauIntensite.FAIBLE,
            3,                               // Âge minimum: 3 ans
            0,                               // Pas de restriction de taille
            true,                             // Extérieure
            "/main/java/com/parcattractions/resources/images/granderoue.png",
            5.0, 8.0  // Ticket normal 5€, Fast Pass 8€
        );
    }
    }
