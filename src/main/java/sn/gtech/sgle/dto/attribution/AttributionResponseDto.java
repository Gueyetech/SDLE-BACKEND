package sn.gtech.sgle.dto.attribution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Attribution;
import sn.gtech.sgle.entity.enums.StatutAttributionEnum;
import sn.gtech.sgle.entity.enums.StatutPaiementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * DTO de réponse pour une attribution
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributionResponseDto {
    
    private UUID id;
    private String numeroContrat;
    
    // Étudiant
    private UUID etudiantId;
    private String etudiantMatricule;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantNomComplet;
    private String etudiantEmail;
    
    // Logement
    private UUID logementId;
    private String logementCode;
    private String logementType;
    private String logementAdresse;
    
    // Dates
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDateTime dateAttribution;
    private LocalDateTime dateCheckIn;
    private LocalDateTime dateCheckOut;
    
    // Montants
    private BigDecimal montantLoyer;
    private BigDecimal montantCaution;
    private BigDecimal totalPaye;
    private BigDecimal soldeRestant;
    
    // Statut
    private StatutAttributionEnum statut;
    private String commentaires;
    
    // Métadonnées calculées
    private Long joursRestants;
    private Long dureeEnMois;
    private Boolean estExpiree;
    private Boolean expireBientot; // Dans les 30 jours
    private Boolean checkInEffectue;
    private Boolean checkOutEffectue;
    
    // Gestionnaire
    private UUID gestionnaireId;
    private String gestionnaireNom;
    
    // Statistiques
    private Integer nombrePaiements;
    private Integer paiementsEnRetard;
    
    /**
     * Convertit une entité Attribution en DTO
     */
    public static AttributionResponseDto fromEntity(Attribution attribution) {
        if (attribution == null) return null;
        
        AttributionResponseDtoBuilder builder = AttributionResponseDto.builder()
                .id(attribution.getId())
                .numeroContrat(attribution.getNumeroContrat())
                .dateDebut(attribution.getDateDebut())
                .dateFin(attribution.getDateFin())
                .dateAttribution(attribution.getDateAttribution())
                .dateCheckIn(attribution.getDateCheckIn())
                .dateCheckOut(attribution.getDateCheckOut())
                .montantLoyer(attribution.getMontantLoyer())
                .montantCaution(attribution.getMontantCaution())
                .statut(attribution.getStatut())
                .commentaires(attribution.getCommentaires())
                .checkInEffectue(attribution.getDateCheckIn() != null)
                .checkOutEffectue(attribution.getDateCheckOut() != null);
        
        // Étudiant (maintenant Utilisateur)
        if (attribution.getEtudiant() != null) {
            builder.etudiantId(attribution.getEtudiant().getId())
                    .etudiantMatricule(attribution.getEtudiant().getId().toString())
                    .etudiantNom(attribution.getEtudiant().getEmail())
                    .etudiantPrenom("")
                    .etudiantNomComplet(attribution.getEtudiant().getEmail())
                    .etudiantEmail(attribution.getEtudiant().getEmail());
        }
        
        // Logement
        if (attribution.getLogement() != null) {
            builder.logementId(attribution.getLogement().getId())
                    .logementCode(attribution.getLogement().getCode())
                    .logementType(attribution.getLogement().getType() != null ? 
                                 attribution.getLogement().getType().name() : null);
            if (attribution.getLogement().getAdresse() != null) {
                builder.logementAdresse(attribution.getLogement().getAdresse().formaterAdresse());
            }
        }
        
        // Gestionnaire
        if (attribution.getGestionnaire() != null) {
            builder.gestionnaireId(attribution.getGestionnaire().getId())
                    .gestionnaireNom(attribution.getGestionnaire().getEmail());
        }
        
        // Calculs temporels
        LocalDate aujourdHui = LocalDate.now();
        if (attribution.getDateFin() != null) {
            long joursRestants = ChronoUnit.DAYS.between(aujourdHui, attribution.getDateFin());
            builder.joursRestants(joursRestants)
                    .estExpiree(joursRestants < 0)
                    .expireBientot(joursRestants >= 0 && joursRestants <= 30);
        }
        
        if (attribution.getDateDebut() != null && attribution.getDateFin() != null) {
            long mois = ChronoUnit.MONTHS.between(attribution.getDateDebut(), attribution.getDateFin());
            builder.dureeEnMois(mois);
        }
        
        // Paiements
        if (attribution.getPaiements() != null) {
            builder.nombrePaiements(attribution.getPaiements().size());
            
            long retards = attribution.getPaiements().stream()
                    .filter(p -> p.getStatut() == StatutPaiementEnum.EN_RETARD)
                    .count();
            builder.paiementsEnRetard((int) retards);
            
            BigDecimal totalPaye = attribution.getPaiements().stream()
                    .filter(p -> p.getStatut() == StatutPaiementEnum.PAYE)
                    .map(p -> p.getMontant())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            builder.totalPaye(totalPaye);
        }
        
        return builder.build();
    }
}
