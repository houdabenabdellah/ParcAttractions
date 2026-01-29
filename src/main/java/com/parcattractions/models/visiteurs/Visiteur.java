package main.java.com.parcattractions.models.visiteurs;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import main.java.com.parcattractions.controllers.GestionnaireParc;
import main.java.com.parcattractions.enums.EtatVisiteur;
import main.java.com.parcattractions.enums.ProfilVisiteur;
import main.java.com.parcattractions.enums.TypeBillet;
import main.java.com.parcattractions.exceptions.attractions.AttractionException;
import main.java.com.parcattractions.exceptions.attractions.AttractionFermeeException;
import main.java.com.parcattractions.exceptions.attractions.CapaciteDepasseeException;
import main.java.com.parcattractions.exceptions.attractions.RestrictionException;
import main.java.com.parcattractions.exceptions.visiteurs.BudgetInsuffisantException;
import main.java.com.parcattractions.exceptions.visiteurs.PatienceEpuiseeException;
import main.java.com.parcattractions.models.attractions.Attraction;
import main.java.com.parcattractions.models.services.Billet;
import main.java.com.parcattractions.utils.Logger;
import main.java.com.parcattractions.utils.Tarification;

public abstract class Visiteur extends Thread {
    
    // Compteur statique pour IDs uniques
    private static int compteurId = 0;
    
    // Identité
    protected final int id;
    protected final String nom;
    protected final int age;
    protected final int taille; // en cm
    
    // Profil
    protected final ProfilVisiteur profil;
    
    // État
    protected volatile EtatVisiteur etat;
    protected int patience;
    protected int satisfaction;
    protected double argent;
    
    // Billet
    protected Billet billet;
    
    // Historique
    protected LocalTime heureArrivee;
    protected final List<Attraction> attractionsVisitees;
    
    // Besoins
    protected int faim;
    protected boolean besoinToilettes;
    protected boolean souvenirsAchetes;
    
    // Contrôle
    protected volatile boolean actif;
    private long derniereSatisfactionEventMs;
    
    /**
     * Constructeur pour initialiser un visiteur
     */
    protected Visiteur(String nom, int age, int taille, ProfilVisiteur profil) {
        super("Visiteur-" + (compteurId + 1));
        this.id = ++compteurId;
        this.nom = nom;
        this.age = age;
        this.taille = taille;
        this.profil = profil;
        
        // État initial
        this.etat = EtatVisiteur.EN_DEPLACEMENT;
        this.patience = profil.getPatienceInitiale();
        this.satisfaction = 50; // Neutre au départ
        this.argent = profil.getBudgetAleatoire();
        
        this.attractionsVisitees = new ArrayList<>();
        this.faim = 0;
        this.besoinToilettes = false;
        this.souvenirsAchetes = false;
        this.actif = true;
    }
    
    @Override
    public void run() {
        Logger.logThreadStart(getName());
        heureArrivee = LocalTime.now();
        
        try {
            // Acheter le billet
            acheterBillet();
            
            Logger.logInfo(String.format("Visiteur %d (%s) arrive - Budget: %.2f€, Patience: %d", 
                id, profil.getLibelle(), argent, patience));
            
            // Boucle principale du visiteur (diagramme activité : Heure < 22h)
            while (actif && patience > 0 && satisfaction > 20) {
                if (!GestionnaireParc.getInstance().estOuvert()) break;
                
                // Événement en cours (Parade, Spectacle, Happy Hour) – max 1 fois/min
                var ge = GestionnaireParc.getInstance().getGestionnaireEvenements();
                if (ge != null && ge.getEvenementActuel() != null) {
                    long now = System.currentTimeMillis();
                    if (now - derniereSatisfactionEventMs > 60_000) {
                        var ev = ge.getEvenementActuel();
                        if (ev instanceof main.java.com.parcattractions.models.evenements.HappyHour) satisfaction += 1;
                        else if (ev instanceof main.java.com.parcattractions.models.evenements.Parade
                                || ev instanceof main.java.com.parcattractions.models.evenements.SpectacleNocturne) satisfaction += 2;
                        derniereSatisfactionEventMs = now;
                    }
                }
                
                // Heure > 18h et souvenirs non achetés -> boutique
                var h = GestionnaireParc.getInstance().getHorloge();
                if (h != null && h.getHeureActuelle().getHour() >= 18 && !souvenirsAchetes && argent >= 10) {
                    acheterSouvenir();
                    continue;
                }
                
                // Gérer besoins physiologiques
                if (faim > 70) {
                    allerRestaurant();
                    continue;
                }
                
                if (besoinToilettes) {
                    allerToilettes();
                    continue;
                }
                
                // Choisir et rejoindre une attraction
                try {
                    Attraction choix = choisirAttraction();
                    
                    if (choix != null) {
                        rejoindrefile(choix);
                    } else {
                        // Plus d'attractions intéressantes
                        Logger.logInfo("Visiteur " + id + " : Plus d'attractions disponibles");
                        if (Math.random() < 0.5 && argent >= 20) {
                            acheterSouvenir();
                        }
                        break;
                    }
                } catch (AttractionException e) {
                    Logger.logDebug("Visiteur " + id + " : " + e.getMessage());
                    gererEchecAttraction(e);
                }
                
                // Incrémenter faim et fatigue
                faim += 5;
                patience -= 2;
                
                // Besoin toilettes aléatoire
                if (Math.random() < 0.1) {
                    besoinToilettes = true;
                }
                
                // Petite pause entre activités
                Thread.sleep(2000);
            }
            
        } catch (InterruptedException e) {
            Logger.logInfo("Visiteur " + id + " : Interrompu");
            Thread.currentThread().interrupt();
        } catch (BudgetInsuffisantException e) {
            Logger.logError("Visiteur " + id + " ne peut pas entrer: " + e.getMessage());
            this.actif = false;
            GestionnaireParc.getInstance().retirerVisiteur(this);
            return;
        } catch (Exception e) {
            Logger.logException("Visiteur " + id + " : Erreur", e);
        } finally {
            quitterParc();
        }
    }
    
    /**
     * Méthode abstraite : chaque profil choisit différemment
     */
    protected abstract Attraction choisirAttraction();
    
    /**
     * Achète un billet d'entrée
     */
    private void acheterBillet() throws BudgetInsuffisantException {
        TypeBillet type = (argent >= 80 && Math.random() < 0.3) 
            ? TypeBillet.FAST_PASS 
            : TypeBillet.STANDARD;
        
        double prix = Tarification.calculerPrix(age, type);
        
        if (argent < prix) {
            throw new BudgetInsuffisantException(id, argent, prix);
        }
        
        this.billet = new Billet(type, prix, age);
        argent -= prix;
        GestionnaireParc.getInstance().getStatistiques().ajouterRevenus(prix);
        
        Logger.logInfo(String.format("Visiteur %d : Billet acheté [%s] - %.2f€", 
            id, type.getLibelle(), prix));
    }
    
    /**
     * Rejoint la file d'une attraction
     * @throws PatienceEpuiseeException 
     */
    protected void rejoindrefile(Attraction attraction) 
            throws AttractionException, InterruptedException, PatienceEpuiseeException {
        
        Logger.logInfo(String.format("Visiteur %d : Rejoint %s (File: %d, Attente: %s)", 
            id, attraction.getNom(), attraction.getTailleFileTotal(), 
            attraction.getTempsAttenteFormate()));
        
        // Vérifier restrictions
        if (!attraction.accepteVisiteur(age, taille)) {
            if (age < attraction.getAgeMin()) {
                throw new RestrictionException(attraction.getNom(), "age", 
                    age, attraction.getAgeMin());
            } else {
                throw new RestrictionException(attraction.getNom(), "taille", 
                    taille, attraction.getTailleMin());
            }
        }
        
        // Ajouter à la file
        boolean fastPass = billet.donnePriorite();
        attraction.ajouterVisiteur(id, fastPass);
        
        etat = EtatVisiteur.EN_FILE;
        
        // Simuler attente (temps estimé / 10 pour accélérer simulation)
        int tempsAttente = attraction.getTempsAttenteEstime() / 10;
        
        // Vérifier patience pendant l'attente
        if (tempsAttente > patience) {
            Logger.logWarning("Visiteur " + id + " : Patience insuffisante, quitte la file");
            satisfaction -= 10;
            throw new PatienceEpuiseeException(id, "Temps d'attente trop long");
        }
        
        // Attendre (simplifié)
        Thread.sleep(tempsAttente * 1000L);
        
        // Tour effectué
        etat = EtatVisiteur.EN_ATTRACTION;
        Thread.sleep(attraction.getDureeTour() * 100L); // Durée tour / 10
        
        etat = EtatVisiteur.EN_DEPLACEMENT;
        
        // Mise à jour après le tour
        attractionsVisitees.add(attraction);
        satisfaction += 15;
        patience -= 5;
        
        var stats = GestionnaireParc.getInstance().getStatistiques();
        stats.mettreAJourTempsAttente((double) tempsAttente);
        stats.mettreAJourSatisfaction((double) satisfaction);
        
        Logger.logInfo(String.format("Visiteur %d : Tour sur %s terminé (Satisfaction: %d%%)", 
            id, attraction.getNom(), satisfaction));
    }
    
    /**
     * Va au restaurant
     */
    protected void allerRestaurant() throws InterruptedException {
        Logger.logInfo("Visiteur " + id + " : Va au restaurant");
        
        etat = EtatVisiteur.EN_PAUSE;
        
        double prix = 15.0 + (Math.random() * 15.0); // 15-30€
        
        if (argent >= prix) {
            argent -= prix;
            faim = 0;
            satisfaction += 5;
            GestionnaireParc.getInstance().getStatistiques().ajouterRevenus(prix);
            Logger.logInfo(String.format("Visiteur %d : Repas terminé (%.2f€)", id, prix));
        } else {
            // Snack rapide
            double snack = Math.min(argent, 5.0);
            argent -= snack;
            faim = 30;
            if (snack > 0) {
                GestionnaireParc.getInstance().getStatistiques().ajouterRevenus(snack);
            }
            Logger.logInfo("Visiteur " + id + " : Snack rapide");
        }
        
        Thread.sleep(3000);
        etat = EtatVisiteur.EN_DEPLACEMENT;
    }
    
    /**
     * Va aux toilettes
     */
    protected void allerToilettes() throws InterruptedException {
        Logger.logDebug("Visiteur " + id + " : Pause toilettes");
        etat = EtatVisiteur.EN_PAUSE;
        Thread.sleep(2000);
        besoinToilettes = false;
        etat = EtatVisiteur.EN_DEPLACEMENT;
    }
    
    /**
     * Achète un souvenir
     */
    protected void acheterSouvenir() throws InterruptedException {
        if (argent < 10) {
            return;
        }
        
        Logger.logInfo("Visiteur " + id + " : Achète des souvenirs");
        
        double prix = 10.0 + (Math.random() * 30.0); // 10-40€
        
        if (argent >= prix) {
            argent -= prix;
            satisfaction += 8;
            souvenirsAchetes = true;
            GestionnaireParc.getInstance().getStatistiques().ajouterRevenus(prix);
            Logger.logInfo(String.format("Visiteur %d : Souvenirs achetés (%.2f€)", id, prix));
        }
        
        Thread.sleep(2000);
    }
    
    /**
     * Gère l'échec de rejoindre une attraction
     */
    protected void gererEchecAttraction(AttractionException e) {
        if (e instanceof CapaciteDepasseeException) {
            satisfaction -= 5;
            patience -= 10;
        } else if (e instanceof AttractionFermeeException) {
            satisfaction -= 3;
        } else if (e instanceof RestrictionException) {
            // Blacklister l'attraction (ne plus jamais essayer)
            Logger.logDebug("Visiteur " + id + " : Restriction pour " + 
                e.getClass().getSimpleName());
        }
    }
    
    /**
     * Quitte le parc
     */
    protected void quitterParc() {
        etat = EtatVisiteur.QUITTE;
        actif = false;
        GestionnaireParc.getInstance().getStatistiques().enregistrerAvis(satisfaction > 70);
        GestionnaireParc.getInstance().retirerVisiteur(this);
        
        String niveauSatisfaction;
        if (satisfaction >= 80) {
            niveauSatisfaction = "Tres satisfait [Excellent]";
        } else if (satisfaction >= 60) {
            niveauSatisfaction = "Satisfait [Bon]";
        } else if (satisfaction >= 40) {
            niveauSatisfaction = "Neutre [Moyen]";
        } else {
            niveauSatisfaction = "Insatisfait [Mauvais]";
        }
        
        Logger.logInfo(String.format(
            "Visiteur %d quitte le parc - Attractions: %d - Satisfaction: %d%% (%s) - Argent restant: %.2f€",
            id, attractionsVisitees.size(), satisfaction, niveauSatisfaction, argent));
        
        Logger.logThreadStop(getName());
    }
    
    /**
     * Arrête le visiteur
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
    
    public String getNomVisiteur() {
        return nom;
    }
    
    public int getAge() {
        return age;
    }
    
    public int getTaille() {
        return taille;
    }
    
    public ProfilVisiteur getProfil() {
        return profil;
    }
    
    public EtatVisiteur getEtat() {
        return etat;
    }
    
    public int getPatience() {
        return patience;
    }
    
    public int getSatisfaction() {
        return satisfaction;
    }
    
    public double getArgent() {
        return argent;
    }
    
    public List<Attraction> getAttractionsVisitees() {
        return new ArrayList<>(attractionsVisitees);
    }
    
    public boolean hasFastPass() {
        return billet != null && billet.donnePriorite();
    }
    
    @Override
    public String toString() {
        return String.format("Visiteur #%d [%s] - État: %s - Satisfaction: %d%% - Attractions: %d", 
            id, profil.getLibelle(), etat.getDescription(), 
            satisfaction, attractionsVisitees.size());
    }
}