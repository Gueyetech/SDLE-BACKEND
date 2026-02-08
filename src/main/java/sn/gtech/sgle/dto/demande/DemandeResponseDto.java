package sn.gtech.sgle.dto.demande;

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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DemandeResponseDto {
    
    private UUID id;
    private String numeroReference;
    private UUID etudiantId;
    private String etudiantEmail;
    private TypeLogementEnum typeLogementSouhaite;
    private BigDecimal budgetMaximum;
    private LocalDate dateDebutSouhaitee;
    private Integer dureeSouhaitee;
    private String preferences;
    private StatutDemandeEnum statut;
    private PrioriteEnum priorite;
    private LocalDateTime dateDemande;
    private LocalDateTime dateTraitement;
    private UUID gestionnaireId;
    private String gestionnaireEmail;
    private String motifRejet;
    private String commentaires;
    private UUID attributionId;
    
    public static DemandeResponseDto fromEntity(DemandeLogement demande) {
        DemandeResponseDtoBuilder builder = DemandeResponseDto.builder()
                .id(demande.getId())
                .numeroReference(demande.getNumeroReference())
                .typeLogementSouhaite(demande.getTypeLogementSouhaite())
                .budgetMaximum(demande.getBudgetMaximum())
                .dateDebutSouhaitee(demande.getDateDebutSouhaitee())
                .dureeSouhaitee(demande.getDureeSouhaitee())
                .preferences(demande.getPreferences())
                .statut(demande.getStatut())
                .priorite(demande.getPriorite())
                .dateDemande(demande.getDateDemande())
                .dateTraitement(demande.getDateTraitement())
                .motifRejet(demande.getMotifRejet())
                .commentaires(demande.getCommentaires());
        
        if (demande.getEtudiant() != null) {
            builder.etudiantId(demande.getEtudiant().getId());
            builder.etudiantEmail(demande.getEtudiant().getEmail());
        }
        
        if (demande.getGestionnaire() != null) {
            builder.gestionnaireId(demande.getGestionnaire().getId());
            builder.gestionnaireEmail(demande.getGestionnaire().getEmail());
        }
        
        if (demande.getAttribution() != null) {
            builder.attributionId(demande.getAttribution().getId());
        }
        
        return builder.build();
    }
}
