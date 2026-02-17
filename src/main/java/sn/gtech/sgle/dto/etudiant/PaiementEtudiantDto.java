package sn.gtech.sgle.dto.etudiant;

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
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * DTO pour un paiement (vue étudiant)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaiementEtudiantDto {
    
    private UUID id;
    private String numeroPaiement;
    
    // Montant
    private BigDecimal montant;
    private String montantFormate;
    
    // Dates
    private LocalDateTime datePaiement;
    private LocalDate dateEcheance;
    private String moisConcerne;
    
    // Statut
    private StatutPaiementEnum statut;
    private String statutLibelle;
    private Boolean enRetard;
    private Long joursRetard;
    
    // Mode de paiement
    private ModePaiementEnum modePaiement;
    private String modePaiementLibelle;
    private String referenceBancaire;
    
    // Reçu
    private UUID recuId;
    private String recuUrl;
    private Boolean recuDisponible;
    
    // Logement associé
    private String logementCode;
    private String logementAdresse;
    
    /**
     * Convertit une entité Paiement en DTO
     */
    public static PaiementEtudiantDto fromEntity(Paiement paiement) {
        if (paiement == null) return null;
        
        boolean enRetard = paiement.getStatut() != StatutPaiementEnum.PAYE 
                && paiement.getDateEcheance() != null 
                && paiement.getDateEcheance().isBefore(LocalDate.now());
        
        long joursRetard = 0;
        if (enRetard && paiement.getDateEcheance() != null) {
            joursRetard = ChronoUnit.DAYS.between(paiement.getDateEcheance(), LocalDate.now());
        }
        
        String statutLib = switch (paiement.getStatut()) {
            case EN_ATTENTE -> enRetard ? "En retard" : "En attente";
            case PAYE -> "Payé";
            case EN_RETARD -> "En retard";
            case ANNULE -> "Annulé";
            case REMBOURSE -> "Remboursé";
        };
        
        PaiementEtudiantDtoBuilder builder = PaiementEtudiantDto.builder()
                .id(paiement.getId())
                .numeroPaiement(paiement.getNumeroPaiement())
                .montant(paiement.getMontant())
                .montantFormate(paiement.getMontant() != null ? paiement.getMontant().toPlainString() + " FCFA" : null)
                .datePaiement(paiement.getDatePaiement())
                .dateEcheance(paiement.getDateEcheance())
                .moisConcerne(paiement.getMoisConcerne())
                .statut(paiement.getStatut())
                .statutLibelle(statutLib)
                .enRetard(enRetard)
                .joursRetard(joursRetard)
                .modePaiement(paiement.getModePaiement())
                .modePaiementLibelle(paiement.getModePaiement() != null ? paiement.getModePaiement().name() : null)
                .referenceBancaire(paiement.getReferenceBancaire());
        
        if (paiement.getRecu() != null) {
            builder.recuId(paiement.getRecu().getId())
                   .recuUrl(paiement.getRecu().getUrl())
                   .recuDisponible(true);
        } else {
            builder.recuDisponible(false);
        }
        
        if (paiement.getAttribution() != null && paiement.getAttribution().getLogement() != null) {
            var logement = paiement.getAttribution().getLogement();
            builder.logementCode(logement.getCode());
            if (logement.getAdresse() != null) {
                builder.logementAdresse(logement.getAdresse().getVille());
            }
        }
        
        return builder.build();
    }
}
