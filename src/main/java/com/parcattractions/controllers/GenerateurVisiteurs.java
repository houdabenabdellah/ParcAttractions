package main.java.com.parcattractions.controllers;

import java.util.Random;
import main.java.com.parcattractions.models.visiteurs.Visiteur;
import main.java.com.parcattractions.models.visiteurs.VisiteurCouple;
import main.java.com.parcattractions.models.visiteurs.VisiteurEnfant;
import main.java.com.parcattractions.models.visiteurs.VisiteurExtreme;
import main.java.com.parcattractions.models.visiteurs.VisiteurFamille;
import main.java.com.parcattractions.models.visiteurs.VisiteurGroupe;
import main.java.com.parcattractions.utils.Logger;

/**
 * Générateur de visiteurs pour le parc
 * Crée des visiteurs avec différents profils selon des probabilités
 */
public class GenerateurVisiteurs extends Thread {
    
    private static final Random random = new Random();
    
    // Probabilités de chaque profil (doivent sommer à 1.0)
    private static final double PROBA_FAMILLE = 0.30;
    private static final double PROBA_EXTREME = 0.20;
    private static final double PROBA_ENFANT = 0.15;
    private static final double PROBA_COUPLE = 0.20;
    private static final double PROBA_GROUPE = 0.15;
    
    // Taux d'arrivée (visiteurs par minute simulée)
    private volatile double tauxArrivee;
    private volatile boolean actif;
    
    private final GestionnaireParc gestionnaireParc;
    
    /**
     * Constructeur
     */
    public GenerateurVisiteurs(GestionnaireParc gestionnaireParc) {
        super("GenerateurVisiteurs");
        this.gestionnaireParc = gestionnaireParc;
        this.tauxArrivee = 2.0; // Par défaut: 2 visiteurs/minute
        this.actif = false;
    }
    
    @Override
    public void run() {
        Logger.logThreadStart(getName());
        actif = true;
        
        try {
            while (actif && gestionnaireParc.estOuvert()) {
                // Générer visiteurs selon taux d'arrivée
                // Taux = visiteurs par minute simulée
                // 1 minute simulée = 6 secondes réelles (10 min sim = 1 sec réelle)
                // Donc 1 min sim = 0.1 sec réelle
                
                double proba = tauxArrivee / 10.0; // Probabilité par seconde réelle
                
                if (random.nextDouble() < proba) {
                    genererVisiteur();
                }
                
                // Attendre 1 seconde réelle
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Logger.logInfo("GenerateurVisiteurs interrompu");
            Thread.currentThread().interrupt();
        } finally {
            actif = false;
            Logger.logThreadStop(getName());
        }
    }
    
    /**
     * Génère un visiteur selon les probabilités
     */
    private void genererVisiteur() {
        double proba = random.nextDouble();
        Visiteur visiteur = null;
        
        // Générer selon profil
        if (proba < PROBA_FAMILLE) {
            visiteur = creerVisiteurFamille();
        } else if (proba < PROBA_FAMILLE + PROBA_EXTREME) {
            visiteur = creerVisiteurExtreme();
        } else if (proba < PROBA_FAMILLE + PROBA_EXTREME + PROBA_ENFANT) {
            visiteur = creerVisiteurEnfant();
        } else if (proba < PROBA_FAMILLE + PROBA_EXTREME + PROBA_ENFANT + PROBA_COUPLE) {
            visiteur = creerVisiteurCouple();
        } else {
            visiteur = creerVisiteurGroupe();
        }
        
        if (visiteur != null) {
            gestionnaireParc.ajouterVisiteur(visiteur);
        }
    }
    
    /**
     * Crée un visiteur famille
     */
    private VisiteurFamille creerVisiteurFamille() {
        int age = 30 + random.nextInt(20); // 30-50 ans
        int taille = 160 + random.nextInt(20); // 160-180 cm
        int nombreEnfants = 1 + random.nextInt(3); // 1-3 enfants
        
        return new VisiteurFamille(age, taille, nombreEnfants);
    }
    
    /**
     * Crée un visiteur extrême
     */
    private VisiteurExtreme creerVisiteurExtreme() {
        int age = 18 + random.nextInt(25); // 18-43 ans
        int taille = 170 + random.nextInt(15); // 170-185 cm
        
        return new VisiteurExtreme(age, taille);
    }
    
    /**
     * Crée un visiteur enfant
     */
    private VisiteurEnfant creerVisiteurEnfant() {
        int age = 5 + random.nextInt(8); // 5-12 ans
        int taille = 100 + random.nextInt(50); // 100-150 cm
        
        // Créer un accompagnateur (parent)
        int ageParent = 25 + random.nextInt(20); // 25-45 ans
        int tailleParent = 160 + random.nextInt(20); // 160-180 cm
        VisiteurFamille parent = new VisiteurFamille(ageParent, tailleParent, 1);
        
        return new VisiteurEnfant(age, taille, parent);
    }
    
    /**
     * Crée un visiteur couple
     */
    private VisiteurCouple creerVisiteurCouple() {
        int age1 = 20 + random.nextInt(30); // 20-50 ans
        int taille1 = 160 + random.nextInt(25); // 160-185 cm
        
        int age2 = age1 - 5 + random.nextInt(10); // ±5 ans du premier
        int taille2 = 155 + random.nextInt(25); // 155-180 cm
        
        VisiteurCouple visiteur1 = new VisiteurCouple(age1, taille1, null);
        VisiteurCouple visiteur2 = new VisiteurCouple(age2, taille2, visiteur1);
        visiteur1 = new VisiteurCouple(age1, taille1, visiteur2);
        
        return visiteur1;
    }
    
    /**
     * Crée un visiteur groupe
     */
    private VisiteurGroupe creerVisiteurGroupe() {
        int age = 18 + random.nextInt(20); // 18-38 ans
        int taille = 165 + random.nextInt(20); // 165-185 cm
        
        VisiteurGroupe groupe = new VisiteurGroupe(age, taille);
        
        // Ajouter 3-5 membres au groupe
        int nombreMembres = 3 + random.nextInt(3); // 3-5 membres
        
        for (int i = 0; i < nombreMembres; i++) {
            int ageMembre = age - 3 + random.nextInt(6); // ±3 ans
            int tailleMembre = taille - 10 + random.nextInt(20); // ±10 cm
            VisiteurGroupe membre = new VisiteurGroupe(ageMembre, tailleMembre);
            groupe.ajouterMembre(membre);
        }
        
        return groupe;
    }
    
    /**
     * Définit le taux d'arrivée
     * @param tauxArrivee Visiteurs par minute simulée
     */
    public void setTauxArrivee(double tauxArrivee) {
        this.tauxArrivee = tauxArrivee;
        Logger.logInfo("Taux d'arrivée modifié: " + tauxArrivee + " visiteurs/min");
    }
    
    /**
     * Retourne le taux d'arrivée
     */
    public double getTauxArrivee() {
        return tauxArrivee;
    }
    
    /**
     * Arrête le générateur
     */
    public void arreter() {
        actif = false;
        interrupt();
    }
}
