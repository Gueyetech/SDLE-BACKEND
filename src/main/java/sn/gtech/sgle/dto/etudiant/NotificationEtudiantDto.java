package sn.gtech.sgle.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Notification;
import sn.gtech.sgle.entity.enums.CanalEnum;
import sn.gtech.sgle.entity.enums.TypeNotificationEnum;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * DTO pour une notification (vue étudiant)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEtudiantDto {
    
    private UUID id;
    
    // Contenu
    private TypeNotificationEnum type;
    private String typeLibelle;
    private String titre;
    private String message;
    
    // État
    private Boolean lue;
    private LocalDateTime dateEnvoi;
    private LocalDateTime dateLecture;
    
    // Canal
    private CanalEnum canal;
    private String canalLibelle;
    
    // Métadonnées
    private String tempsDepuisEnvoi; // ex: "il y a 2 heures"
    private Boolean nouvelle; // envoyée il y a moins de 24h
    
    /**
     * Convertit une entité Notification en DTO
     */
    public static NotificationEtudiantDto fromEntity(Notification notification) {
        if (notification == null) return null;
        
        String tempsDepuis = "";
        boolean nouvelle = false;
        if (notification.getDateEnvoi() != null) {
            long minutes = ChronoUnit.MINUTES.between(notification.getDateEnvoi(), LocalDateTime.now());
            long heures = ChronoUnit.HOURS.between(notification.getDateEnvoi(), LocalDateTime.now());
            long jours = ChronoUnit.DAYS.between(notification.getDateEnvoi().toLocalDate(), LocalDateTime.now().toLocalDate());
            
            if (minutes < 60) {
                tempsDepuis = "il y a " + minutes + " minute" + (minutes > 1 ? "s" : "");
            } else if (heures < 24) {
                tempsDepuis = "il y a " + heures + " heure" + (heures > 1 ? "s" : "");
            } else if (jours < 7) {
                tempsDepuis = "il y a " + jours + " jour" + (jours > 1 ? "s" : "");
            } else {
                tempsDepuis = notification.getDateEnvoi().toLocalDate().toString();
            }
            
            nouvelle = heures < 24;
        }
        
        String typeLib = notification.getType() != null ? switch (notification.getType()) {
            case ATTRIBUTION_LOGEMENT -> "Attribution";
            case DEMANDE_APPROUVEE -> "Demande approuvée";
            case DEMANDE_REJETEE -> "Demande rejetée";
            case RAPPEL_PAIEMENT -> "Rappel paiement";
            case INCIDENT_RESOLU -> "Incident résolu";
            case REPONSE_INCIDENT -> "Réponse incident";
            case FIN_CONTRAT_PROCHE -> "Fin de contrat";
            case MAINTENANCE_PLANIFIEE, MAINTENANCE_PROGRAMMEE -> "Maintenance";
            case MESSAGE_GESTIONNAIRE -> "Message";
            case INFORMATION_GENERALE -> "Information";
            case DOCUMENT_EXPIRE -> "Document expiré";
            case BIENVENUE -> "Bienvenue";
            case ALERTE_SYSTEME -> "Alerte système";
        } : null;
        
        return NotificationEtudiantDto.builder()
                .id(notification.getId())
                .type(notification.getType())
                .typeLibelle(typeLib)
                .titre(notification.getTitre())
                .message(notification.getMessage())
                .lue(notification.getLue())
                .dateEnvoi(notification.getDateEnvoi())
                .dateLecture(notification.getDateLecture())
                .canal(notification.getCanal())
                .canalLibelle(notification.getCanal() != null ? notification.getCanal().name() : null)
                .tempsDepuisEnvoi(tempsDepuis)
                .nouvelle(nouvelle)
                .build();
    }
}
