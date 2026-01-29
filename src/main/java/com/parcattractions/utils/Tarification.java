package main.java.com.parcattractions.utils;

import java.time.LocalTime;
import main.java.com.parcattractions.enums.TypeBillet;

/**
 * Classe utilitaire pour gérer la tarification des billets
 * Toutes les méthodes sont statiques
 */
public class Tarification {
    
    // Prix de base (adulte, billet standard)
    private static final double PRIX_BASE_ADULTE = 50.0;
    private static final double PRIX_BASE_ENFANT = 30.0;  // < 12 ans
    private static final double PRIX_BASE_SENIOR = 40.0;  // >= 65 ans
    
    // Seuils d'âge
    private static final int AGE_ENFANT_MAX = 12;
    private static final int AGE_SENIOR_MIN = 65;
    
    // Happy Hour (14h-16h)
    private static final LocalTime HAPPY_HOUR_DEBUT = LocalTime.of(14, 0);
    private static final LocalTime HAPPY_HOUR_FIN = LocalTime.of(16, 0);
    private static final double REDUCTION_HAPPY_HOUR = 0.20; // 20%
    
    /**
     * Constructeur privé pour empêcher l'instanciation
     */
    private Tarification() {
        throw new AssertionError("Classe utilitaire, ne pas instancier");
    }
    
    /**
     * Calcule le prix d'un billet selon l'âge et le type
     * 
     * @param age Âge du visiteur
     * @param typeBillet Type de billet
     * @return Prix du billet en euros
     */
    public static double calculerPrix(int age, TypeBillet typeBillet) {
        // Déterminer le prix de base selon l'âge
        double prixBase;
        if (age < AGE_ENFANT_MAX) {
            prixBase = PRIX_BASE_ENFANT;
        } else if (age >= AGE_SENIOR_MIN) {
            prixBase = PRIX_BASE_SENIOR;
        } else {
            prixBase = PRIX_BASE_ADULTE;
        }
        
        // Appliquer le multiplicateur du type de billet
        return prixBase * typeBillet.getMultiplicateurPrix();
    }
    
    /**
     * Calcule le prix avec réduction Happy Hour si applicable
     * 
     * @param age Âge du visiteur
     * @param typeBillet Type de billet
     * @param heure Heure d'achat
     * @return Prix du billet (avec réduction si applicable)
     */
    public static double calculerPrixAvecHeure(int age, TypeBillet typeBillet, LocalTime heure) {
        double prix = calculerPrix(age, typeBillet);
        
        // Appliquer réduction Happy Hour si applicable
        if (estHappyHour(heure)) {
            prix = appliquerReduction(prix, REDUCTION_HAPPY_HOUR);
        }
        
        return prix;
    }
    
    /**
     * Applique une réduction à un prix
     * 
     * @param prix Prix original
     * @param tauxReduction Taux de réduction (0.20 = 20%)
     * @return Prix après réduction
     */
    public static double appliquerReduction(double prix, double tauxReduction) {
        return prix * (1.0 - tauxReduction);
    }
    
    /**
     * Vérifie si l'heure donnée est pendant le Happy Hour
     * 
     * @param heure Heure à vérifier
     * @return Vrai si pendant Happy Hour
     */
    public static boolean estHappyHour(LocalTime heure) {
        return !heure.isBefore(HAPPY_HOUR_DEBUT) && heure.isBefore(HAPPY_HOUR_FIN);
    }
    
    /**
     * Retourne le prix de base adulte
     * @return Prix adulte
     */
    public static double getPrixBaseAdulte() {
        return PRIX_BASE_ADULTE;
    }
    
    /**
     * Retourne le prix de base enfant
     * @return Prix enfant
     */
    public static double getPrixBaseEnfant() {
        return PRIX_BASE_ENFANT;
    }
    
    /**
     * Retourne le prix de base senior
     * @return Prix senior
     */
    public static double getPrixBaseSenior() {
        return PRIX_BASE_SENIOR;
    }
    
    /**
     * Détermine la catégorie tarifaire selon l'âge
     * 
     * @param age Âge du visiteur
     * @return "Enfant", "Adulte" ou "Senior"
     */
    public static String getCategorieTarif(int age) {
        if (age < AGE_ENFANT_MAX) {
            return "Enfant";
        } else if (age >= AGE_SENIOR_MIN) {
            return "Senior";
        } else {
            return "Adulte";
        }
    }
    
    /**
     * Calcule le montant économisé pendant Happy Hour
     * 
     * @param prixNormal Prix normal
     * @return Montant de la réduction
     */
    public static double calculerEconomieHappyHour(double prixNormal) {
        return prixNormal * REDUCTION_HAPPY_HOUR;
    }
    
    /**
     * Formate un prix en euros
     * 
     * @param prix Prix à formater
     * @return Prix formaté (ex: "50,00 €")
     */
    public static String formaterPrix(double prix) {
        return String.format("%.2f €", prix);
    }
}
