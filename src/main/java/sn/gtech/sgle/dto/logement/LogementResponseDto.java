package sn.gtech.sgle.dto.logement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Logement;
import sn.gtech.sgle.entity.enums.StatutLogementEnum;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de réponse pour un logement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogementResponseDto {
    
    private UUID id;
    private String code;
    private TypeLogementEnum type;
    private Integer capacite;
    private Float superficie;
    private BigDecimal prixMensuel;
    private String description;
    private StatutLogementEnum statut;
    private Boolean meuble;
    
    // Adresse
    private String adresseRue;
    private String adresseNumero;
    private String adresseCodePostal;
    private String adresseVille;
    private String adresseQuartier;
    private String adresseRegion;
    private String adressePays;
    private Float adresseLatitude;
    private Float adresseLongitude;
    private String adresseComplement;
    private String adresseComplete;
    
    // Équipements
    private List<String> equipements;
    
    // Photos
    private List<String> photos;
    
    // Gestionnaire
    private UUID gestionnaireId;
    private String gestionnaireNom;
    
    // Statistiques
    private Integer nombreAttributions;
    private Integer nombreIncidents;
    private Integer nombreMaintenances;
    
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    
    /**
     * Convertit une entité Logement en DTO
     */
    public static LogementResponseDto fromEntity(Logement logement) {
        if (logement == null) return null;
        
        LogementResponseDtoBuilder builder = LogementResponseDto.builder()
                .id(logement.getId())
                .code(logement.getCode())
                .type(logement.getType())
                .capacite(logement.getCapacite())
                .superficie(logement.getSuperficie())
                .prixMensuel(logement.getPrixMensuel())
                .description(logement.getDescription())
                .statut(logement.getStatut())
                .meuble(logement.getMeuble())
                .photos(logement.getPhotos())
                .dateCreation(logement.getDateCreation())
                .dateModification(logement.getDateModification());
        
        // Adresse
        if (logement.getAdresse() != null) {
            builder.adresseRue(logement.getAdresse().getRue())
                    .adresseNumero(logement.getAdresse().getNumero())
                    .adresseCodePostal(logement.getAdresse().getCodePostal())
                    .adresseVille(logement.getAdresse().getVille())
                    .adresseQuartier(logement.getAdresse().getQuartier())
                    .adresseRegion(logement.getAdresse().getRegion())
                    .adressePays(logement.getAdresse().getPays())
                    .adresseLatitude(logement.getAdresse().getLatitude())
                    .adresseLongitude(logement.getAdresse().getLongitude())
                    .adresseComplement(logement.getAdresse().getComplementAdresse())
                    .adresseComplete(logement.getAdresse().formaterAdresse());
        }
        
        // Gestionnaire
        if (logement.getGestionnaire() != null) {
            builder.gestionnaireId(logement.getGestionnaire().getId())
                    .gestionnaireNom(logement.getGestionnaire().getEmail());
        }
        
        // Équipements
        if (logement.getEquipements() != null) {
            builder.equipements(logement.getEquipements().stream()
                    .map(eq -> eq.getNom())
                    .toList());
        }
        
        // Statistiques
        builder.nombreAttributions(logement.getAttributions() != null ? logement.getAttributions().size() : 0)
                .nombreIncidents(logement.getIncidents() != null ? logement.getIncidents().size() : 0)
                .nombreMaintenances(logement.getMaintenances() != null ? logement.getMaintenances().size() : 0);
        
        return builder.build();
    }
}
