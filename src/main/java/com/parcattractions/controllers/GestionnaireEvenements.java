package main.java.com.parcattractions.controllers;

import java.time.LocalTime;
import main.java.com.parcattractions.models.evenements.Evenement;
import main.java.com.parcattractions.models.evenements.HappyHour;
import main.java.com.parcattractions.models.evenements.Parade;
import main.java.com.parcattractions.models.evenements.SpectacleNocturne;
import main.java.com.parcattractions.utils.Logger;

/**
 * Gestionnaire des événements spéciaux du parc
 */
public class GestionnaireEvenements extends Thread {
    
    private final GestionnaireParc gestionnaireParc;
    private volatile boolean actif;
    private Evenement evenementActuel;
    
    // Horaires des événements
    private static final LocalTime HEURE_HAPPY_HOUR = LocalTime.of(14, 0);
    private static final LocalTime HEURE_PARADE = LocalTime.of(15, 0);
    private static final LocalTime HEURE_SPECTACLE = LocalTime.of(20, 0);
    
    /**
     * Constructeur
     */
    public GestionnaireEvenements(GestionnaireParc gestionnaireParc) {
        super("GestionnaireEvenements");
        this.gestionnaireParc = gestionnaireParc;
        this.actif = false;
        this.evenementActuel = null;
    }
    
    @Override
    public void run() {
        Logger.logThreadStart(getName());
    actif = true;
    
    // Mode manuel : le thread ne fait rien automatiquement
    try {
        while (actif) {
            Thread.sleep(1000); // Attend simplement
        }
    } catch (InterruptedException e) {
        Logger.logInfo("GestionnaireEvenements interrompu");
        Thread.currentThread().interrupt();
    } finally {
        actif = false;
        Logger.logThreadStop(getName());
    }
    }
    
    /**
     * Vérifie et déclenche les événements selon l'heure
     */
    private void verifierEvenements(LocalTime heure) {
        // Happy Hour (14h-16h)
        if (heure.getHour() == HEURE_HAPPY_HOUR.getHour() && 
            heure.getMinute() == 0 && 
            evenementActuel == null) {
            lancerHappyHour();
        } else if (heure.getHour() == 16 && heure.getMinute() == 0) {
            terminerEvenement();
        }
        
        // Parade (15h)
        if (heure.getHour() == HEURE_PARADE.getHour() && 
            heure.getMinute() == 0 && 
            evenementActuel == null) {
            lancerParade();
        } else if (heure.getHour() == HEURE_PARADE.getHour() && 
                   heure.getMinute() == 30) {
            terminerEvenement();
        }
        
        // Spectacle Nocturne (20h)
        if (heure.getHour() == HEURE_SPECTACLE.getHour() && 
            heure.getMinute() == 0 && 
            evenementActuel == null) {
            lancerSpectacleNocturne();
        } else if (heure.getHour() == 21 && heure.getMinute() == 0) {
            terminerEvenement();
        }
    }
    
    /**
     * Lance le Happy Hour (automatique ou manuel par le Manager)
     */
    private void lancerHappyHour() {
        evenementActuel = new HappyHour();
    
    //  ACTIVER LA RÉDUCTION DE 20%
    gestionnaireParc.activerReduction(0.20);
    
    Logger.logInfo("Happy Hour démarré ! Réduction de 20% sur les billets");
    SystemeNotifications.getInstance().ajouterNotification(
        main.java.com.parcattractions.enums.TypeNotification.INFO,
        " Happy Hour en cours - Réduction de 20% sur les billets !"
    );
    }
    
    /**
     * Lance la parade (automatique ou manuel par le Manager)
     */
    private void lancerParade() {
        evenementActuel = new Parade();
        Logger.logInfo("Parade démarrée !");
        SystemeNotifications.getInstance().ajouterNotification(
            main.java.com.parcattractions.enums.TypeNotification.INFO,
            "Parade en cours dans le parc - Venez admirer le spectacle !"
        );
    }
    
    /**
     * Lance le spectacle nocturne (automatique ou manuel par le Manager)
     */
    private void lancerSpectacleNocturne() {
        evenementActuel = new SpectacleNocturne();
        Logger.logInfo("Spectacle Nocturne démarré !");
        SystemeNotifications.getInstance().ajouterNotification(
            main.java.com.parcattractions.enums.TypeNotification.INFO,
            "Spectacle Nocturne en cours - Un moment magique à ne pas manquer !"
        );
    }
    
    /**
     * Lance le Happy Hour manuellement (UC15  Lancer Événement, Manager)
     */
    public void lancerHappyHourManuel() {
        terminerEvenement();
        lancerHappyHour();
    }
    
    /**
     * Lance la parade manuellement (UC15  Lancer Événement, Manager)
     */
    public void lancerParadeManuel() {
        terminerEvenement();
        lancerParade(); 
    }
    
    /**
     * Lance le spectacle nocturne manuellement (UC15 – Lancer Événement, Manager)
     */
    public void lancerSpectacleNocturneManuel() {
        terminerEvenement();
        lancerSpectacleNocturne();
    }
    
    /**
     * Termine l'événement actuel
     */
    private void terminerEvenement() {
        if (evenementActuel != null) {
        Logger.logInfo("Événement terminé: " + evenementActuel);
        
        //  DÉSACTIVER LA RÉDUCTION SI C'ÉTAIT UN HAPPY HOUR
        if (evenementActuel instanceof HappyHour) {
            gestionnaireParc.desactiverReduction();
            Logger.logInfo("Réduction Happy Hour désactivée");
            SystemeNotifications.getInstance().ajouterNotification(
                main.java.com.parcattractions.enums.TypeNotification.INFO,
                "Happy Hour terminé - Prix normaux rétablis"
            );
        }
        
        evenementActuel = null;
    }
    }
    public void terminerEvenementManuel() {
    if (evenementActuel != null) {
        String nomEvenement = evenementActuel.getNom();
        terminerEvenement();
        
        Logger.logInfo("Événement '" + nomEvenement + "' terminé manuellement");
        SystemeNotifications.getInstance().ajouterNotification(
            main.java.com.parcattractions.enums.TypeNotification.INFO,
            "Événement '" + nomEvenement + "' terminé"
        );
    }
}
    
    /**
     * Retourne l'événement actuel
     */
    public Evenement getEvenementActuel() {
        return evenementActuel;
    }
    
    /**
     * Arrête le gestionnaire
     */
    public void arreter() {
        actif = false;
        interrupt();
    }
}
