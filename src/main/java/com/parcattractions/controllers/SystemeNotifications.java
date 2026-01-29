package main.java.com.parcattractions.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import main.java.com.parcattractions.enums.TypeNotification;
import main.java.com.parcattractions.utils.Logger;
import main.java.com.parcattractions.utils.Notification;

/**
 * Système de notifications centralisé
 * Gère toutes les notifications du parc avec priorités
 */
public class SystemeNotifications {
    
    private static SystemeNotifications instance;
    
    // Queue thread-safe pour notifications
    private final BlockingQueue<Notification> notifications;
    
    // Historique (limité à 1000 notifications)
    private final List<Notification> historique;
    private static final int MAX_HISTORIQUE = 1000;
    
    /**
     * Constructeur privé (Singleton)
     */
    private SystemeNotifications() {
        this.notifications = new LinkedBlockingQueue<>();
        this.historique = Collections.synchronizedList(new ArrayList<>());
    }
    
    /**
     * Retourne l'instance unique
     */
    public static synchronized SystemeNotifications getInstance() {
        if (instance == null) {
            instance = new SystemeNotifications();
        }
        return instance;
    }
    
    /**
     * Alerte le Manager (diagramme maintenance : notif -> alerterManager).
     * Envoie une notification URGENCE prioritaire.
     */
    public void alerterManager(String message) {
        ajouterNotification(TypeNotification.URGENCE, "Alerte Manager: " + message);
    }
    
    /**
     * Ajoute une notification
     */
    public void ajouterNotification(TypeNotification type, String message) {
        Notification notification = new Notification(type, message);
        
        try {
            notifications.put(notification);
            historique.add(notification);
            
            // Limiter l'historique
            if (historique.size() > MAX_HISTORIQUE) {
                historique.remove(0);
            }
            
            // Logger selon priorité
            switch (type) {
                case URGENCE:
                    Logger.logError(message);
                    break;
                case ATTENTION:
                    Logger.logWarning(message);
                    break;
                case INFO:
                    Logger.logInfo(message);
                    break;
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.logException("Erreur ajout notification", e);
        }
    }
    
    /**
     * Retire et retourne la prochaine notification (bloquant)
     */
    public Notification retirerNotification() throws InterruptedException {
        return notifications.take();
    }
    
    /**
     * Retire et retourne la prochaine notification (non bloquant)
     */
    public Notification retirerNotificationNonBloquant() {
        return notifications.poll();
    }
    
    /**
     * Retourne toutes les notifications en attente
     */
    public List<Notification> getNotificationsEnAttente() {
        List<Notification> result = new ArrayList<>();
        notifications.drainTo(result);
        return result;
    }
    
    /**
     * Retourne l'historique complet
     */
    public List<Notification> getHistorique() {
        return Collections.unmodifiableList(historique);
    }
    
    /**
     * Retourne les N dernières notifications
     */
    public List<Notification> getDernieresNotifications(int nombre) {
        int taille = historique.size();
        int debut = Math.max(0, taille - nombre);
        return new ArrayList<>(historique.subList(debut, taille));
    }
    
    /**
     * Retourne les notifications par type
     */
    public List<Notification> getNotificationsParType(TypeNotification type) {
        List<Notification> result = new ArrayList<>();
        for (Notification notif : historique) {
            if (notif.getType() == type) {
                result.add(notif);
            }
        }
        return result;
    }
    
    /**
     * Retourne les notifications critiques (URGENCE)
     */
    public List<Notification> getNotificationsCritiques() {
        return getNotificationsParType(TypeNotification.URGENCE);
    }
    
    /**
     * Vide l'historique
     */
    public void viderHistorique() {
        historique.clear();
        notifications.clear();
        Logger.logInfo("Historique notifications vidé");
    }
    
    /**
     * Retourne le nombre de notifications en attente
     */
    public int getNombreNotificationsEnAttente() {
        return notifications.size();
    }
    
    /**
     * Retourne le nombre total de notifications dans l'historique
     */
    public int getNombreTotalNotifications() {
        return historique.size();
    }
}
