package sn.gtech.sgle.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Incident;
import sn.gtech.sgle.entity.enums.StatutIncidentEnum;
import sn.gtech.sgle.entity.enums.TypeIncidentEnum;
import sn.gtech.sgle.entity.enums.UrgenceEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * DTO pour le suivi d'un incident signalé par l'étudiant
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuiviIncidentDto {
    
    private UUID id;
    private String numeroTicket;
    
    // Type et description
    private TypeIncidentEnum type;
    private String typeLibelle;
    private String description;
    
    // Urgence
    private UrgenceEnum urgence;
    private String urgenceLibelle;
    
    // Statut
    private StatutIncidentEnum statut;
    private String statutLibelle;
    private String statutDescription;
    
    // Dates
    private LocalDateTime dateSignalement;
    private LocalDateTime dateResolution;
    private Long joursDepuisSignalement;
    
    // Photos
    private List<String> photos;
    
    // Logement concerné
    private String logementCode;
    private String logementAdresse;
    
    // Résolution
    private String commentaireResolution;
    private String technicienAssigne;
    private Boolean enCoursDeResolution;
    
    // Progression
    private Integer etapeActuelle;
    private Integer totalEtapes;
    private String prochainEtape;
    
    /**
     * Convertit une entité Incident en DTO de suivi
     */
    public static SuiviIncidentDto fromEntity(Incident incident) {
        if (incident == null) return null;
        
        long jours = incident.getDateSignalement() != null 
            ? ChronoUnit.DAYS.between(incident.getDateSignalement().toLocalDate(), LocalDate.now())
            : 0;
        
        String statutDesc = switch (incident.getStatut()) {
            case OUVERT -> "Votre signalement a été enregistré et sera traité prochainement";
            case EN_COURS -> "Un technicien est en train de résoudre le problème";
            case RESOLU -> "Le problème a été résolu";
            case FERME -> "Le ticket est fermé";
            case ANNULE -> "Le signalement a été annulé";
        };
        
        int etape = switch (incident.getStatut()) {
            case OUVERT -> 1;
            case EN_COURS -> 2;
            case RESOLU -> 3;
            case FERME -> 4;
            case ANNULE -> 0;
        };
        
        String prochaine = switch (incident.getStatut()) {
            case OUVERT -> "Attribution à un technicien";
            case EN_COURS -> "Résolution du problème";
            case RESOLU -> "Confirmation de fermeture";
            case FERME -> "Ticket clôturé";
            case ANNULE -> "Signalement annulé";
        };
        
        SuiviIncidentDtoBuilder builder = SuiviIncidentDto.builder()
                .id(incident.getId())
                .numeroTicket(incident.getNumeroTicket())
                .type(incident.getType())
                .typeLibelle(incident.getType() != null ? incident.getType().name() : null)
                .description(incident.getDescription())
                .urgence(incident.getUrgence())
                .urgenceLibelle(incident.getUrgence() != null ? incident.getUrgence().name() : null)
                .statut(incident.getStatut())
                .statutLibelle(incident.getStatut().name())
                .statutDescription(statutDesc)
                .dateSignalement(incident.getDateSignalement())
                .dateResolution(incident.getDateResolution())
                .joursDepuisSignalement(jours)
                .photos(incident.getPhotos())
                .commentaireResolution(incident.getCommentaireResolution())
                .technicienAssigne(incident.getTechnicien())
                .enCoursDeResolution(incident.getStatut() == StatutIncidentEnum.EN_COURS)
                .etapeActuelle(etape)
                .totalEtapes(4)
                .prochainEtape(prochaine);
        
        if (incident.getLogement() != null) {
            builder.logementCode(incident.getLogement().getCode());
            if (incident.getLogement().getAdresse() != null) {
                builder.logementAdresse(incident.getLogement().getAdresse().getVille());
            }
        }
        
        return builder.build();
    }
}
