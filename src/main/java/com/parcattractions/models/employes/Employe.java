package main.java.com.parcattractions.models.employes;

import main.java.com.parcattractions.enums.EtatEmploye;
import main.java.com.parcattractions.exceptions.employes.FatigueExcessiveException;
import main.java.com.parcattractions.utils.Logger;

/**
 * Classe abstraite représentant un employé du parc
 */
public abstract class Employe extends Thread {
    
    private static int compteurId = 0;
    
    protected final int id;
    protected final String nom;
    protected final String poste;
    
    protected volatile EtatEmploye etat;
    protected volatile int fatigue; // 0-100
    protected volatile boolean actif;
    
    // Seuils
    private static final int SEUIL_FATIGUE_MAX = 100;
    private static final int DUREE_PAUSE = 10; // minutes
    private static final int INCREMENT_FATIGUE = 1; // par minute
    
    /**
     * Constructeur
     */
    protected Employe(String nom, String poste) {
        super("Employe-" + (compteurId + 1));
        this.id = ++compteurId;
        this.nom = nom;
        this.poste = poste;
        this.etat = EtatEmploye.DISPONIBLE;
        this.fatigue = 0;
        this.actif = true;
    }
    
    @Override
    public void run() {
        Logger.logThreadStart(getName());
        
        try {
            while (actif && !isInterrupted()) {
                // Gérer fatigue
                gererFatigue();
                
                // Travail spécifique (méthode abstraite)
                if (etat == EtatEmploye.DISPONIBLE) {
                    travailler();
                }
                
                // Attendre 1 minute simulée (6 secondes réelles)
                Thread.sleep(6000);
            }
        } catch (InterruptedException e) {
            Logger.logInfo(getName() + " interrompu");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Logger.logException(getName() + " : Erreur", e);
        } finally {
            Logger.logThreadStop(getName());
        }
    }
    
    /**
     * Méthode abstraite : travail spécifique de l'employé
     */
    protected abstract void travailler() throws InterruptedException;
    
    /**
     * Gère la fatigue de l'employé
     */
    private void gererFatigue() throws InterruptedException {
        // Incrémenter fatigue si occupé
        if (etat == EtatEmploye.OCCUPE) {
            fatigue += INCREMENT_FATIGUE;
            
            // Si fatigue excessive, forcer pause
            if (fatigue >= SEUIL_FATIGUE_MAX) {
                Logger.logWarning(String.format("%s : Fatigue excessive (%d), pause obligatoire", 
                    nom, fatigue));
                prendrePause();
            }
        }
        
        // Si en pause, décrémenter fatigue
        if (etat == EtatEmploye.EN_PAUSE) {
            fatigue = Math.max(0, fatigue - 2); // Récupération plus rapide
            
            if (fatigue <= 20) {
                // Fin de pause
                etat = EtatEmploye.DISPONIBLE;
                Logger.logInfo(nom + " : Fin de pause, retour au travail");
            }
        }
    }
    
    /**
     * Prend une pause
     */
    public synchronized void prendrePause() throws InterruptedException {
        if (etat == EtatEmploye.EN_PAUSE) {
            return; // Déjà en pause
        }
        
        EtatEmploye ancienEtat = etat;
        etat = EtatEmploye.EN_PAUSE;
        
        Logger.logInfo(String.format("%s : Pause (fatigue: %d)", nom, fatigue));
        
        // Attendre durée pause (10 minutes = 60 secondes réelles)
        Thread.sleep(60000);
        
        // Retour disponible
        etat = EtatEmploye.DISPONIBLE;
        Logger.logInfo(nom + " : Retour disponible après pause");
    }
    
    /**
     * Marque l'employé comme occupé
     */
    public synchronized void marquerOccupe() throws FatigueExcessiveException {
        if (fatigue >= SEUIL_FATIGUE_MAX) {
            throw new FatigueExcessiveException(id, fatigue);
        }
        
        if (etat == EtatEmploye.DISPONIBLE) {
            etat = EtatEmploye.OCCUPE;
        }
    }
    
    /**
     * Marque l'employé comme disponible
     */
    public synchronized void marquerDisponible() {
        if (etat == EtatEmploye.OCCUPE) {
            etat = EtatEmploye.DISPONIBLE;
        }
    }
    
    /**
     * Arrête l'employé
     */
    public void arreter() {
        actif = false;
        interrupt();
    }
    
    // ========== GETTERS ==========
    
    @Override
    public long getId() {
        return id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public String getPoste() {
        return poste;
    }
    
    public EtatEmploye getEtat() {
        return etat;
    }
    
    public int getFatigue() {
        return fatigue;
    }
    
    public boolean estDisponible() {
        return etat == EtatEmploye.DISPONIBLE && fatigue < SEUIL_FATIGUE_MAX;
    }
    
    @Override
    public String toString() {
        return String.format("%s [%s] - État: %s - Fatigue: %d%%", 
            nom, poste, etat.getDescription(), fatigue);
    }
}
