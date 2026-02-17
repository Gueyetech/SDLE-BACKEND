package sn.gtech.sgle.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.DemandeLogement;
import sn.gtech.sgle.entity.enums.PrioriteEnum;
import sn.gtech.sgle.entity.enums.StatutDemandeEnum;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO pour le suivi d'une demande de logement (vue étudiant)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuiviDemandeDto {
    
    private UUID id;
    private String numeroReference;
    
    // Informations de la demande
    private TypeLogementEnum typeLogementSouhaite;
    private String typeLogementLibelle;
    private BigDecimal budgetMaximum;
    private LocalDate dateDebutSouhaitee;
    private Integer dureeSouhaitee;
    private String preferences;
    
    // Statut
    private StatutDemandeEnum statut;
    private String statutLibelle;
    private String statutDescription;
    private PrioriteEnum priorite;
    
    // Dates
    private LocalDateTime dateDemande;
    private LocalDateTime dateTraitement;
    private Long joursDepuisDemande;
    
    // En cas de rejet
    private String motifRejet;
    
    // Logement attribué (si approuvé)
    private UUID logementAttribueId;
    private String logementAttribueCode;
    private String logementAttribueAdresse;
    
    // Progression
    private Integer etapeActuelle;
    private Integer totalEtapes;
    private String prochainEtape;
    
    /**
     * Convertit une entité DemandeLogement en DTO de suivi
     */
    public static SuiviDemandeDto fromEntity(DemandeLogement demande) {
        if (demande == null) return null;
        
        long joursDepuis = demande.getDateDemande() != null 
            ? java.time.temporal.ChronoUnit.DAYS.between(demande.getDateDemande().toLocalDate(), LocalDate.now())
            : 0;
        
        String statutDesc = switch (demande.getStatut()) {
            case EN_ATTENTE -> "Votre demande est en cours d'examen par nos gestionnaires";
            case EN_TRAITEMENT, EN_COURS_TRAITEMENT -> "Votre demande est actuellement traitée par un gestionnaire";
            case APPROUVEE -> "Félicitations ! Votre demande a été approuvée";
            case REJETEE -> "Votre demande n'a pas pu être acceptée";
            case ANNULEE -> "Votre demande a été annulée";
            case EXPIREE -> "Votre demande a expiré";
        };
        
        int etape = switch (demande.getStatut()) {
            case EN_ATTENTE -> 1;
            case EN_TRAITEMENT, EN_COURS_TRAITEMENT -> 2;
            case APPROUVEE, REJETEE, EXPIREE -> 3;
            case ANNULEE -> 0;
        };
        
        String prochaine = switch (demande.getStatut()) {
            case EN_ATTENTE -> "En attente de prise en charge";
            case EN_TRAITEMENT, EN_COURS_TRAITEMENT -> "Décision en cours";
            case APPROUVEE -> "Attribution du logement";
            case REJETEE, EXPIREE -> "Vous pouvez soumettre une nouvelle demande";
            case ANNULEE -> "Demande annulée";
        };
        
        SuiviDemandeDtoBuilder builder = SuiviDemandeDto.builder()
                .id(demande.getId())
                .numeroReference(demande.getNumeroReference())
                .typeLogementSouhaite(demande.getTypeLogementSouhaite())
                .typeLogementLibelle(demande.getTypeLogementSouhaite() != null ? demande.getTypeLogementSouhaite().name() : null)
                .budgetMaximum(demande.getBudgetMaximum())
                .dateDebutSouhaitee(demande.getDateDebutSouhaitee())
                .dureeSouhaitee(demande.getDureeSouhaitee())
                .preferences(demande.getPreferences())
                .statut(demande.getStatut())
                .statutLibelle(demande.getStatut().name())
                .statutDescription(statutDesc)
                .priorite(demande.getPriorite())
                .dateDemande(demande.getDateDemande())
                .dateTraitement(demande.getDateTraitement())
                .joursDepuisDemande(joursDepuis)
                .motifRejet(demande.getMotifRejet())
                .etapeActuelle(etape)
                .totalEtapes(3)
                .prochainEtape(prochaine);
        
        if (demande.getAttribution() != null && demande.getAttribution().getLogement() != null) {
            var logement = demande.getAttribution().getLogement();
            builder.logementAttribueId(logement.getId())
                   .logementAttribueCode(logement.getCode());
            if (logement.getAdresse() != null) {
                builder.logementAttribueAdresse(logement.getAdresse().getVille());
            }
        }
        
        return builder.build();
    }
}
