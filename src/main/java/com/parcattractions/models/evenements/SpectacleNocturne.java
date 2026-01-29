package main.java.com.parcattractions.models.evenements;

import java.time.LocalTime;

/**
 * Événement Spectacle Nocturne
 * Spectacle à 20h
 */
public class SpectacleNocturne extends Evenement {
    
    public SpectacleNocturne() {
        super("Spectacle Nocturne", LocalTime.of(20, 0), 60); // 20h-21h
    }
}
