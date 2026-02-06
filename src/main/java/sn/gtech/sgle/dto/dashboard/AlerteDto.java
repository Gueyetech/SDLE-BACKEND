package sn.gtech.sgle.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les alertes du système
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlerteDto {
    
    private Long id;
    private String type; // PAIEMENT_RETARD, ATTRIBUTION_EXPIRE, INCIDENT_CRITIQUE, MAINTENANCE_URGENTE, etc.
    private String titre;
    private String message;
    private String priorite; // BASSE, MOYENNE, HAUTE, CRITIQUE
    private String categorie; // FINANCE, LOGEMENT, ETUDIANT, MAINTENANCE, SECURITE
    private boolean lue;
    private boolean traitee;
    private LocalDateTime dateCreation;
    private LocalDateTime dateTraitement;
    private Long entiteId; // ID de l'entité concernée
    private String entiteType; // Type de l'entité (ETUDIANT, LOGEMENT, PAIEMENT, etc.)
    private String actionRecommandee;
    private String traitePar; // Nom de l'utilisateur qui a traité l'alerte
    
    /**
     * Types d'alertes prédéfinis
     */
    public enum TypeAlerte {
        PAIEMENT_RETARD("Paiement en retard"),
        ATTRIBUTION_EXPIRE_BIENTOT("Attribution expire bientôt"),
        ATTRIBUTION_EXPIREE("Attribution expirée"),
        INCIDENT_CRITIQUE("Incident critique"),
        INCIDENT_NON_TRAITE("Incident non traité"),
        MAINTENANCE_URGENTE("Maintenance urgente requise"),
        MAINTENANCE_PLANIFIEE("Maintenance planifiée"),
        LOGEMENT_INDISPONIBLE("Logement indisponible"),
        OCCUPATION_ELEVEE("Taux d'occupation élevé"),
        OCCUPATION_BASSE("Taux d'occupation bas"),
        DOCUMENT_EXPIRE("Document expiré"),
        DEMANDE_EN_ATTENTE("Demande en attente longue durée"),
        COMPTE_INACTIF("Compte inactif"),
        TENTATIVES_CONNEXION("Tentatives de connexion suspectes"),
        SYSTEME("Alerte système");
        
        private final String description;
        
        TypeAlerte(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Niveaux de priorité
     */
    public enum Priorite {
        BASSE(1),
        MOYENNE(2),
        HAUTE(3),
        CRITIQUE(4);
        
        private final int niveau;
        
        Priorite(int niveau) {
            this.niveau = niveau;
        }
        
        public int getNiveau() {
            return niveau;
        }
    }
}
