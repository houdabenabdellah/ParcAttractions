package main.java.com.parcattractions.models.attractions;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.controllers.SystemeNotifications;
import main.java.com.parcattractions.enums.EtatAttraction;
import main.java.com.parcattractions.enums.NiveauIntensite;
import main.java.com.parcattractions.enums.TypeAttraction;
import main.java.com.parcattractions.enums.TypeNotification;
import main.java.com.parcattractions.exceptions.attractions.AttractionException;
import main.java.com.parcattractions.exceptions.attractions.AttractionFermeeException;
import main.java.com.parcattractions.exceptions.attractions.CapaciteDepasseeException;
import main.java.com.parcattractions.exceptions.attractions.MaintenanceEnCoursException;
import main.java.com.parcattractions.models.employes.Operateur;
import main.java.com.parcattractions.models.employes.Technicien;
import main.java.com.parcattractions.utils.FileAttente;
import main.java.com.parcattractions.utils.Logger;

public abstract class Attraction extends Thread {
    
    // Constantes
    protected static final int CAPACITE_MAX_FILE = 150;
    protected static final int TOURS_AVANT_MAINTENANCE = 10;
    protected static final double PROBABILITE_PANNE = 0.05; // 5%
    
    
    // Attributs de base
    protected final String nom;
    protected final int capacite;
    protected final int dureeTour; // en secondes
    protected final TypeAttraction type;
    protected final NiveauIntensite intensite;
    protected final int ageMin;
    protected final int tailleMin; // en cm
    protected final boolean estExterieure;
    protected  final String imagePath;
    
    // Files d'attente
    protected final FileAttente<Integer> fileNormale; // IDs visiteurs
    protected final FileAttente<Integer> fileFastPass;
    
    // État
    protected volatile EtatAttraction etat;
    protected final AtomicInteger toursEffectues;
    protected final AtomicInteger visiteursTotaux;
    
    // Synchronisation
    protected final ReentrantLock lock;
    protected volatile boolean parcOuvert;
    
    // Temps de maintenance restant
    protected volatile int tempsMaintenanceRestant;
    
    // Tarification par attraction (ticket normal et fast pass)
    protected final double prixTicketNormal;
    protected final double prixTicketFastPass;
    protected volatile double revenuTotal;
    
    /**
     * Constructeur pour initialiser une attraction
     * @param prixTicketNormal Prix du ticket normal (€)
     * @param prixTicketFastPass Prix du fast pass (€), doit être > prixTicketNormal
     */
    protected Attraction(String nom, int capacite, int dureeTour, 
                        TypeAttraction type, NiveauIntensite intensite,
                        int ageMin, int tailleMin, boolean estExterieure,
                        String imagePath, double prixTicketNormal, double prixTicketFastPass) {
        super("Thread-" + nom);
        this.nom = nom;
        this.capacite = capacite;
        this.dureeTour = dureeTour;
        this.type = type;
        this.intensite = intensite;
        this.ageMin = ageMin;
        this.tailleMin = tailleMin;
        this.estExterieure = estExterieure;
        this.imagePath = imagePath;
        this.prixTicketNormal = prixTicketNormal;
        this.prixTicketFastPass = Math.max(prixTicketFastPass, prixTicketNormal);
        this.revenuTotal = 0.0;
        
        this.fileNormale = new FileAttente<>(CAPACITE_MAX_FILE);
        this.fileFastPass = new FileAttente<>(CAPACITE_MAX_FILE / 2);
        
        this.etat = EtatAttraction.OPERATIONNELLE;
        this.toursEffectues = new AtomicInteger(0);
        this.visiteursTotaux = new AtomicInteger(0);
        
        this.lock = new ReentrantLock(true);
        this.parcOuvert = true;
        this.tempsMaintenanceRestant = 0;
    }


    @Override
    public void run() {
        Logger.logThreadStart(nom);
        Logger.logInfo(nom + " : Attraction démarrée");
        
        try {
            while (parcOuvert && !isInterrupted()) {
                
                // Vérifier si on peut faire un tour
                if (etat == EtatAttraction.OPERATIONNELLE && peutDemarrerTour()) {
                    executerTour();
                    
                    // Vérifier maintenance programmée
                    verifierMaintenanceProgrammee();
                    
                    // Vérifier panne aléatoire
                    verifierPanneAleatoire();
                }
                
                // Attendre un peu avant de vérifier à nouveau
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Logger.logInfo(nom + " : Thread interrompu");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            Logger.logException(nom + " : Erreur critique", e);
        } finally {
            evacuerFiles();
            Logger.logThreadStop(nom);
        }
    }
    
    /**
     * Vérifie si on peut démarrer un tour
     */
    private boolean peutDemarrerTour() {
        return getTailleFileTotal() >= capacite;
    }
    
    /**
     * Exécute un tour complet
     */
    private void executerTour() throws InterruptedException {
        lock.lock();
        try {
            Logger.logInfo(String.format("%s : Démarrage tour #%d", 
                nom, toursEffectues.get() + 1));
            
            // Charger les visiteurs
            int visiteurs = chargerVisiteurs();
            
            if (visiteurs == 0) {
                return; // Pas assez de visiteurs finalement
            }
            
            // Simuler le tour
            Logger.logInfo(String.format("%s : Tour en cours avec %d visiteurs...", 
                nom, visiteurs));
            Thread.sleep(dureeTour * 1000L);
            
            // Tour terminé
            toursEffectues.incrementAndGet();
            visiteursTotaux.addAndGet(visiteurs);
            GestionnaireParc.getInstance().getStatistiques().ajouterTour();
            
            Logger.logInfo(String.format("%s : Tour #%d terminé (Total: %d tours, %d visiteurs)", 
                nom, toursEffectues.get(), toursEffectues.get(), visiteursTotaux.get()));
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Charge les visiteurs pour le tour
     * @return Nombre de visiteurs chargés
     */
    private int chargerVisiteurs() {
        int charge = 0;
        
        // Priorité aux Fast-Pass
        while (charge < capacite && !fileFastPass.estVide()) {
            try {
                fileFastPass.retirerAvecTimeout(100);
                charge++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        // Compléter avec file normale
        while (charge < capacite && !fileNormale.estVide()) {
            try {
                fileNormale.retirerAvecTimeout(100);
                charge++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        return charge;
    }
    
    /**
     * Ajoute un visiteur à la file appropriée
     */
    public void ajouterVisiteur(int visiteurId, boolean fastPass) 
            throws AttractionException {
        
        // Vérifier état
        if (etat == EtatAttraction.FERMEE) {
            throw new AttractionFermeeException(nom, "Attraction fermée");
        }
        
        if (etat == EtatAttraction.MAINTENANCE) {
            throw new MaintenanceEnCoursException(nom, tempsMaintenanceRestant);
        }
        
        if (etat == EtatAttraction.PANNE) {
            throw new AttractionFermeeException(nom, "Attraction en panne");
        }
        
        // Vérifier capacité file
        FileAttente<Integer> file = fastPass ? fileFastPass : fileNormale;
        
        if (file.estPleine()) {
            throw new CapaciteDepasseeException(nom, CAPACITE_MAX_FILE, 
                getTailleFileTotal() + 1);
        }
        
        file.ajouter(visiteurId);
        int taille = getTailleFileTotal();
        GestionnaireParc.getInstance().getStatistiques().mettreAJourFile(nom, taille);
        
        if (taille > 100) {
            SystemeNotifications.getInstance().ajouterNotification(
                TypeNotification.ATTENTION, "File saturée (>100): " + nom);
        }
        
        Logger.logDebug(String.format("%s : Visiteur %d ajouté (FastPass: %b, File: %d)", 
            nom, visiteurId, fastPass, taille));
    }
    
    /**
     * Vérifie les restrictions pour un visiteur
     */
    public boolean accepteVisiteur(int age, int taille) {
        return age >= ageMin && taille >= tailleMin;
    }
    
    /**
     * Vérifie si maintenance programmée nécessaire
     */
    private void verifierMaintenanceProgrammee() {
        if (toursEffectues.get() > 0 && 
            toursEffectues.get() % TOURS_AVANT_MAINTENANCE == 0) {
            lancerMaintenance();
        }
    }
    
    /**
     * Vérifie panne aléatoire
     */
    private void verifierPanneAleatoire() {
        if (Math.random() < PROBABILITE_PANNE) {
            declencherPanne();
        }
    }
    
    /**
     * Lance la maintenance programmée (diagramme : Technicien si dispo, sinon attente).
     */
    private void lancerMaintenance() {
        etat = EtatAttraction.MAINTENANCE;
        tempsMaintenanceRestant = 30;
        GestionnaireParc.getInstance().getStatistiques().mettreAJourDisponibilite(nom, false);
        Logger.logWarning(nom + " : Maintenance programmée démarrée");
        SystemeNotifications.getInstance().ajouterNotification(
            TypeNotification.INFO, "Maintenance programmée: " + nom);
        
        Technicien tech = GestionnaireParc.getInstance().getTechnicienDisponible();
        if (tech != null) {
            new Thread(() -> {
                try {
                    tech.effectuerMaintenance(Attraction.this);
                    SystemeNotifications.getInstance().ajouterNotification(
                        TypeNotification.INFO, "Maintenance terminée: " + nom);
                } catch (Exception e) {
                    Logger.logException("Maintenance " + nom, e);
                    finishMaintenanceFallback();
                }
            }, "Maintenance-" + nom).start();
        } else {
            new Thread(() -> {
                try {
                    while (tempsMaintenanceRestant > 0 && parcOuvert) {
                        Thread.sleep(1000);
                        tempsMaintenanceRestant--;
                    }
                    if (parcOuvert) finishMaintenanceFallback();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Maintenance-" + nom).start();
        }
    }
    
    private void finishMaintenanceFallback() {
        etat = EtatAttraction.OPERATIONNELLE;
        toursEffectues.set(0);
        GestionnaireParc.getInstance().getStatistiques().mettreAJourDisponibilite(nom, true);
        Logger.logInfo(nom + " : Maintenance terminée");
        SystemeNotifications.getInstance().ajouterNotification(
            TypeNotification.INFO, "Maintenance terminée: " + nom);
    }
    
    /**
     * Déclenche manuellement une panne (pour simulation/UI).
     */
    public void mettrePanne() {
        declencherPanne();
    }
    
    /**
     * Déclenche une panne (diagramme : opérateur, notif, technicien).
     */
    private void declencherPanne() {
        etat = EtatAttraction.PANNE;
        GestionnaireParc.getInstance().getStatistiques().mettreAJourDisponibilite(nom, false);
        Logger.logError(nom + " : PANNE détectée !");
        
        SystemeNotifications.getInstance().ajouterNotification(
            TypeNotification.URGENCE, "PANNE - Intervention requise: " + nom);
        SystemeNotifications.getInstance().alerterManager("Panne " + nom);
        
        Operateur op = GestionnaireParc.getInstance().getOperateurPourAttraction(this);
        if (op != null) op.notifierPanne();
        
        int evacues = evacuerFiles();
        Logger.logWarning(nom + " : " + evacues + " visiteurs évacués des files");
        if (evacues > 0) {
            SystemeNotifications.getInstance().ajouterNotification(
                TypeNotification.INFO, "Compensation Fast-Pass proposée aux visiteurs évacués (" + nom + ")");
        }
        
        GestionnaireParc.getInstance().demanderTechnicienUrgence(this);
    }
    
    /**
     * Évacue toutes les files
     * @return Nombre de visiteurs évacués
     */
    private int evacuerFiles() {
        int evacues = 0;
        
        while (!fileNormale.estVide()) {
            try {
                fileNormale.retirerAvecTimeout(100);
                evacues++;
            } catch (InterruptedException e) {
                break;
            }
        }
        
        while (!fileFastPass.estVide()) {
            try {
                fileFastPass.retirerAvecTimeout(100);
                evacues++;
            } catch (InterruptedException e) {
                break;
            }
        }
        
        return evacues;
    }
    
    /**
     * Ferme l'attraction (météo, évacuation, etc.)
     */
    public void fermer() {
        etat = EtatAttraction.FERMEE;
        GestionnaireParc.getInstance().getStatistiques().mettreAJourDisponibilite(nom, false);
        Logger.logInfo(nom + " : Attraction fermée");
    }
    
    /**
     * Ouvre l'attraction (depuis FERMEE, MAINTENANCE ou PANNE).
     */
    public void ouvrir() {
        EtatAttraction prev = etat;
        if (prev == EtatAttraction.FERMEE || prev == EtatAttraction.MAINTENANCE 
                || prev == EtatAttraction.PANNE) {
            etat = EtatAttraction.OPERATIONNELLE;
            if (prev == EtatAttraction.MAINTENANCE) {
                toursEffectues.set(0);
            }
            GestionnaireParc.getInstance().getStatistiques().mettreAJourDisponibilite(nom, true);
            Logger.logInfo(nom + " : Attraction ouverte");
        }
    }
    
    /**
     * Arrête proprement l'attraction
     */
    public void arreter() {
        parcOuvert = false;
        interrupt();
    }
    
    // ========== GETTERS ==========
    
    public String getNom() {
        return nom;
    }
    
    public int getCapacite() {
        return capacite;
    }
    
    public int getDureeTour() {
        return dureeTour;
    }
    
    public TypeAttraction getType() {
        return type;
    }
    
    public NiveauIntensite getIntensite() {
        return intensite;
    }
    
    public int getAgeMin() {
        return ageMin;
    }
    
    public int getTailleMin() {
        return tailleMin;
    }
    
    public boolean estExterieure() {
        return estExterieure;
    }
    
    public EtatAttraction getEtat() {
        return etat;
    }
    
    public int getToursEffectues() {
        return toursEffectues.get();
    }
    
    public int getVisiteursTotaux() {
        return visiteursTotaux.get();
    }
    
    public int getTailleFileNormale() {
        return fileNormale.getTaille();
    }
    
    public int getTailleFileFastPass() {
        return fileFastPass.getTaille();
    }
    
    public int getTailleFileTotal() {
        return getTailleFileNormale() + getTailleFileFastPass();
    }

    public String getImagePath() {
        return imagePath;
    }
    
    /** Prix du ticket normal (€) */
    public double getPrixTicketNormal() {
        return prixTicketNormal;
    }
    
    /** Prix du fast pass (€) */
    public double getPrixTicketFastPass() {
        return prixTicketFastPass;
    }
    
    /** Revenu total généré par l'attraction (€) */
    public double getRevenuTotal() {
        return revenuTotal;
    }
    
    /**
     * Ajoute un montant au revenu de l'attraction (appelé lors d'un achat de ticket)
     */
    public synchronized void ajouterRevenu(double montant) {
        this.revenuTotal += montant;
    }
    
    /**
     * Retourne la description de l'attraction
     */
    public String getDescription() {
        return String.format("%s - %s, intensité %s. Âge min: %d ans, Taille min: %d cm. %s.",
            type.getLibelle(), nom, intensite, ageMin, tailleMin,
            estExterieure ? "Attraction extérieure" : "Attraction couverte");
    }
    
    /**
     * Calcule le temps d'attente estimé en secondes
     */
    public int getTempsAttenteEstime() {
        int totalFile = getTailleFileTotal();
        if (totalFile == 0) return 0;
        
        int toursNecessaires = (int) Math.ceil((double) totalFile / capacite);
        return toursNecessaires * dureeTour;
    }
    
    /**
     * Retourne le temps d'attente formaté
     */
    public String getTempsAttenteFormate() {
        int secondes = getTempsAttenteEstime();
        int minutes = secondes / 60;
        int secs = secondes % 60;
        return String.format("%d min %d sec", minutes, secs);
    }
    
    @Override
    public String toString() {
        return String.format("%s [%s] - État: %s - File: %d/%d - Tours: %d", 
            nom, type.getLibelle(), etat.getDescription(), 
            getTailleFileTotal(), CAPACITE_MAX_FILE, toursEffectues.get());
    }
}