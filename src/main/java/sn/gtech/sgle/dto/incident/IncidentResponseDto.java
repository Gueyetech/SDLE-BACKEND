package sn.gtech.sgle.dto.incident;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Incident;
import sn.gtech.sgle.entity.enums.StatutIncidentEnum;
import sn.gtech.sgle.entity.enums.TypeIncidentEnum;
import sn.gtech.sgle.entity.enums.UrgenceEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncidentResponseDto {
    
    private UUID id;
    private String numeroTicket;
    private UUID etudiantId;
    private String etudiantEmail;
    private UUID logementId;
    private String logementCode;
    private TypeIncidentEnum type;
    private String description;
    private UrgenceEnum urgence;
    private StatutIncidentEnum statut;
    private LocalDateTime dateSignalement;
    private LocalDateTime dateResolution;
    private List<String> photos;
    private UUID gestionnaireId;
    private String gestionnaireEmail;
    private String technicien;
    private String commentaireResolution;
    
    public static IncidentResponseDto fromEntity(Incident incident) {
        IncidentResponseDtoBuilder builder = IncidentResponseDto.builder()
                .id(incident.getId())
                .numeroTicket(incident.getNumeroTicket())
                .type(incident.getType())
                .description(incident.getDescription())
                .urgence(incident.getUrgence())
                .statut(incident.getStatut())
                .dateSignalement(incident.getDateSignalement())
                .dateResolution(incident.getDateResolution())
                .photos(incident.getPhotos())
                .technicien(incident.getTechnicien())
                .commentaireResolution(incident.getCommentaireResolution());
        
        if (incident.getEtudiant() != null) {
            builder.etudiantId(incident.getEtudiant().getId());
            builder.etudiantEmail(incident.getEtudiant().getEmail());
        }
        
        if (incident.getLogement() != null) {
            builder.logementId(incident.getLogement().getId());
            builder.logementCode(incident.getLogement().getCode());
        }
        
        if (incident.getGestionnaire() != null) {
            builder.gestionnaireId(incident.getGestionnaire().getId());
            builder.gestionnaireEmail(incident.getGestionnaire().getEmail());
        }
        
        return builder.build();
    }
}
