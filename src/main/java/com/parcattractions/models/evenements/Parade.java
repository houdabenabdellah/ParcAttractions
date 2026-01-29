package main.java.com.parcattractions.models.evenements;

import java.time.LocalTime;

/**
 * Événement Parade
 * Parade dans le parc à 15h
 */
public class Parade extends Evenement {
    
    public Parade() {
        super("Parade", LocalTime.of(15, 0), 30); // 15h00-15h30
    }
}
