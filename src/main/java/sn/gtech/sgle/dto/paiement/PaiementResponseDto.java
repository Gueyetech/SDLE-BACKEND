package sn.gtech.sgle.dto.paiement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Paiement;
import sn.gtech.sgle.entity.enums.ModePaiementEnum;
import sn.gtech.sgle.entity.enums.StatutPaiementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour un paiement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaiementResponseDto {
    
    private UUID id;
    private String numeroPaiement;
    
    // Attribution
    private UUID attributionId;
    private String numeroContrat;
    
    // Étudiant
    private UUID etudiantId;
    private String etudiantNom;
    private String etudiantMatricule;
    
    // Logement
    private UUID logementId;
    private String logementCode;
    
    // Montant et dates
    private BigDecimal montant;
    private LocalDateTime datePaiement;
    private LocalDate dateEcheance;
    private String moisConcerne;
    
    // Mode et statut
    private ModePaiementEnum modePaiement;
    private StatutPaiementEnum statut;
    private String referenceBancaire;
    
    // Reçu
    private UUID recuId;
    private String recuUrl;
    
    // Métadonnées calculées
    private Boolean enRetard;
    private Long joursRetard;
    private BigDecimal penalites;
    
    // Résumés imbriqués
    private AttributionResumeDto attribution;
    private DocumentResumeDto recu;
    
    /**
     * Convertit une entité Paiement en DTO
     */
    public static PaiementResponseDto fromEntity(Paiement paiement) {
        if (paiement == null) return null;
        
        PaiementResponseDtoBuilder builder = PaiementResponseDto.builder()
                .id(paiement.getId())
                .numeroPaiement(paiement.getNumeroPaiement())
                .montant(paiement.getMontant())
                .datePaiement(paiement.getDatePaiement())
                .dateEcheance(paiement.getDateEcheance())
                .moisConcerne(paiement.getMoisConcerne())
                .modePaiement(paiement.getModePaiement())
                .statut(paiement.getStatut())
                .referenceBancaire(paiement.getReferenceBancaire());
        
        // Attribution
        if (paiement.getAttribution() != null) {
            builder.attributionId(paiement.getAttribution().getId())
                    .numeroContrat(paiement.getAttribution().getNumeroContrat());
            
            // Étudiant (maintenant Utilisateur)
            if (paiement.getAttribution().getEtudiant() != null) {
                builder.etudiantId(paiement.getAttribution().getEtudiant().getId())
                        .etudiantNom(paiement.getAttribution().getEtudiant().getEmail())
                        .etudiantMatricule(paiement.getAttribution().getEtudiant().getId().toString());
            }
            
            // Logement
            if (paiement.getAttribution().getLogement() != null) {
                builder.logementId(paiement.getAttribution().getLogement().getId())
                        .logementCode(paiement.getAttribution().getLogement().getCode());
            }
        }
        
        // Reçu
        if (paiement.getRecu() != null) {
            builder.recuId(paiement.getRecu().getId())
                    .recuUrl(paiement.getRecu().getUrl());
        }
        
        // Calcul du retard
        if (paiement.getStatut() != StatutPaiementEnum.PAYE && paiement.getDateEcheance() != null) {
            LocalDate aujourdHui = LocalDate.now();
            if (aujourdHui.isAfter(paiement.getDateEcheance())) {
                builder.enRetard(true)
                        .joursRetard(java.time.temporal.ChronoUnit.DAYS.between(paiement.getDateEcheance(), aujourdHui));
            } else {
                builder.enRetard(false)
                        .joursRetard(0L);
            }
        } else {
            builder.enRetard(false)
                    .joursRetard(0L);
        }
        
        return builder.build();
    }
    
    /**
     * DTO interne pour le résumé d'une attribution
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttributionResumeDto {
        private UUID id;
        private String numeroContrat;
        private String etudiantNom;
        private String etudiantMatricule;
        private String logementCode;
        private BigDecimal montantLoyer;
    }
    
    /**
     * DTO interne pour le résumé d'un document
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentResumeDto {
        private UUID id;
        private String nom;
        private String url;
    }
}
