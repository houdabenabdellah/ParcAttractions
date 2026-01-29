package main.java.com.parcattractions.models.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import main.java.com.parcattractions.enums.TypeBillet;
import main.java.com.parcattractions.utils.Tarification;

public class Billet {
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private static int compteurId = 0;
    
    private final int id;
    private final TypeBillet type;
    private final double prix;
    private final LocalDateTime dateAchat;
    private final int ageAcheteur;
    private boolean utilise;
    
    /**
     * Constructeur complet
     * @param type Type de billet
     * @param prix Prix payé
     * @param ageAcheteur Âge de l'acheteur
     */
    public Billet(TypeBillet type, double prix, int ageAcheteur) {
        this.id = ++compteurId;
        this.type = type;
        this.prix = prix;
        this.dateAchat = LocalDateTime.now();
        this.ageAcheteur = ageAcheteur;
        this.utilise = false;
    }
    
    /**
     * Constructeur simplifié (calcul automatique du prix)
     * @param type Type de billet
     * @param ageAcheteur Âge de l'acheteur
     */
    public Billet(TypeBillet type, int ageAcheteur) {
        this(type, Tarification.calculerPrix(ageAcheteur, type), ageAcheteur);
    }
    
    /**
     * Constructeur standard (billet standard)
     * @param ageAcheteur Âge de l'acheteur
     */
    public Billet(int ageAcheteur) {
        this(TypeBillet.STANDARD, ageAcheteur);
    }
    
    /**
     * @return ID unique du billet
     */
    public int getId() {
        return id;
    }
    
    /**
     * @return Type de billet
     */
    public TypeBillet getType() {
        return type;
    }
    
    /**
     * @return Prix payé
     */
    public double getPrix() {
        return prix;
    }
    
    /**
     * @return Date et heure d'achat
     */
    public LocalDateTime getDateAchat() {
        return dateAchat;
    }
    
    /**
     * @return Âge de l'acheteur
     */
    public int getAgeAcheteur() {
        return ageAcheteur;
    }
    
    /**
     * @return Vrai si le billet a été utilisé
     */
    public boolean estUtilise() {
        return utilise;
    }
    
    /**
     * Marque le billet comme utilisé
     */
    public void utiliser() {
        this.utilise = true;
    }
    
    /**
     * Vérifie si le billet est valide (non utilisé et date valide)
     * @return Vrai si valide
     */
    public boolean estValide() {
        // Le billet est valide s'il n'est pas utilisé
        // et si la date d'achat est le jour même
        if (utilise) {
            return false;
        }
        
        LocalDateTime maintenant = LocalDateTime.now();
        return dateAchat.toLocalDate().equals(maintenant.toLocalDate());
    }
    
    /**
     * Vérifie si le billet donne accès prioritaire
     * @return Vrai si Fast-Pass ou Pass journée
     */
    public boolean donnePriorite() {
        return type.donnePriorite();
    }
    
    /**
     * @return Catégorie tarifaire de l'acheteur
     */
    public String getCategorieTarif() {
        return Tarification.getCategorieTarif(ageAcheteur);
    }
    
    /**
     * @return Date d'achat formatée
     */
    public String getDateAchatFormatee() {
        return dateAchat.format(FORMATTER);
    }
    
    /**
     * @return Prix formaté
     */
    public String getPrixFormate() {
        return Tarification.formaterPrix(prix);
    }
    
    /**
     * Génère un code-barres fictif pour le billet
     * @return Code-barres (format: TYPE-ID-TIMESTAMP)
     */
    public String genererCodeBarre() {
        return String.format("%s-%05d-%d", 
            type.name(), 
            id, 
            dateAchat.toLocalDate().toEpochDay()
        );
    }
    
    /**
     * Représentation textuelle du billet
     */
    @Override
    public String toString() {
        return String.format("Billet #%d [%s] - %s - %s - %s", 
            id, 
            type.getLibelle(), 
            getCategorieTarif(),
            getPrixFormate(),
            estValide() ? "Valide" : "Non valide"
        );
    }
    
    /**
     * Génère un reçu détaillé
     * @return Reçu formaté
     */
    public String genererRecu() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════╗\n");
        sb.append("║     PARC D'ATTRACTIONS             ║\n");
        sb.append("║          BILLET D'ENTRÉE           ║\n");
        sb.append("╠════════════════════════════════════╣\n");
        sb.append(String.format("║ N° Billet : %-22d ║\n", id));
        sb.append(String.format("║ Type      : %-22s ║\n", type.getLibelle()));
        sb.append(String.format("║ Catégorie : %-22s ║\n", getCategorieTarif()));
        sb.append(String.format("║ Prix      : %-22s ║\n", getPrixFormate()));
        sb.append(String.format("║ Date      : %-22s ║\n", getDateAchatFormatee()));
        sb.append(String.format("║ Code-barre: %-22s ║\n", genererCodeBarre()));
        sb.append("╠════════════════════════════════════╣\n");
        if (donnePriorite()) {
            sb.append("║  ⭐ ACCÈS PRIORITAIRE INCLUS ⭐   ║\n");
        }
        sb.append("║   Merci de votre visite !          ║\n");
        sb.append("╚════════════════════════════════════╝");
        return sb.toString();
    }
}
