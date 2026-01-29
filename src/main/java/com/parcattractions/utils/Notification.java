package main.java.com.parcattractions.utils;

import  java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import main.java.com.parcattractions.enums.TypeNotification;

/**
 * Représente une notification du système
 */
public class Notification {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private final TypeNotification type;
    private final String message;
    private final LocalDateTime timestamp;
    private final int priorite;
    
    /**
     * Constructeur
     * @param type Type de notification
     * @param message Message de la notification
     */
    public Notification(TypeNotification type, String message) {
        this.type = type;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.priorite = type.getPriorite();
    }
    
    /**
     * @return Type de notification
     */
    public TypeNotification getType() {
        return type;
    }
    
    /**
     * @return Message de la notification
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * @return Timestamp de création
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * @return Priorité (1-3)
     */
    public int getPriorite() {
        return priorite;
    }
    
    /**
     * @return Timestamp formaté
     */
    public String getTimestampFormate() {
        return timestamp.format(FORMATTER);
    }
    
    /**
     * @return Message complet formaté avec préfixe et timestamp
     */
    public String getMessageComplet() {
        return String.format("[%s] %s %s", 
            getTimestampFormate(), 
            type.getPrefixe(), 
            message
        );
    }
    
    /**
     * @return Vrai si c'est une notification critique
     */
    public boolean estCritique() {
        return type.estCritique();
    }
    
    /**
     * Représentation textuelle
     */
    @Override
    public String toString() {
        return getMessageComplet();
    }
    
    /**
     * Comparaison par priorité (pour tri)
     * @param other Autre notification
     * @return Comparaison par priorité décroissante
     */
    public int compareTo(Notification other) {
        // Priorité décroissante (3 avant 1)
        int prioriteComp = Integer.compare(other.priorite, this.priorite);
        if (prioriteComp != 0) {
            return prioriteComp;
        }
        // Si même priorité, plus récent d'abord
        return other.timestamp.compareTo(this.timestamp);
    }
    
    /**
     * Vérifie si la notification est récente (moins de X minutes)
     * @param minutes Nombre de minutes
     * @return Vrai si récente
     */
    public boolean estRecente(int minutes) {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(minutes);
        return timestamp.isAfter(limite);
    }
}