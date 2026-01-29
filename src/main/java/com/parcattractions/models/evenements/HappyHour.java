package main.java.com.parcattractions.models.evenements;

import java.time.LocalTime;

/**
 * Événement Happy Hour
 * Réduction de 20% sur les billets de 14h à 16h
 */
public class HappyHour extends Evenement {
    
    public HappyHour() {
        super("Happy Hour", LocalTime.of(14, 0), 120); // 14h-16h = 2h
    }
    
    /**
     * Retourne le taux de réduction
     */
    public double getTauxReduction() {
        return 0.20; // 20%
    }
}
