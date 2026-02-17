package sn.gtech.sgle.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Logement;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO simplifié pour le catalogue des logements (vue étudiant)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogementCatalogueDto {
    
    private UUID id;
    private String code;
    private TypeLogementEnum type;
    private String typeLibelle;
    
    // Prix
    private BigDecimal prixMensuel;
    private String prixFormate;
    
    // Capacité et superficie
    private Integer capacite;
    private Float superficie;
    
    // Localisation
    private String adresseComplete;
    private String ville;
    private String quartier;
    private Float latitude;
    private Float longitude;
    
    // Description courte
    private String description;
    private String descriptionCourte;
    
    // Photos
    private String photoprincipale;
    private List<String> photos;
    private Integer nombrePhotos;
    
    // Équipements
    private List<String> equipements;
    private Integer nombreEquipements;
    
    // Caractéristiques
    private Boolean meuble;
    private Boolean disponible;
    
    /**
     * Convertit une entité Logement en DTO catalogue
     */
    public static LogementCatalogueDto fromEntity(Logement logement) {
        if (logement == null) return null;
        
        String adresseComplete = "";
        if (logement.getAdresse() != null) {
            StringBuilder sb = new StringBuilder();
            if (logement.getAdresse().getNumero() != null) sb.append(logement.getAdresse().getNumero()).append(" ");
            if (logement.getAdresse().getRue() != null) sb.append(logement.getAdresse().getRue()).append(", ");
            if (logement.getAdresse().getQuartier() != null) sb.append(logement.getAdresse().getQuartier()).append(", ");
            if (logement.getAdresse().getVille() != null) sb.append(logement.getAdresse().getVille());
            adresseComplete = sb.toString().replaceAll(", $", "");
        }
        
        List<String> equipementsList = logement.getEquipements() != null 
            ? logement.getEquipements().stream().map(e -> e.getNom()).collect(Collectors.toList())
            : List.of();
        
        String descCourte = logement.getDescription();
        if (descCourte != null && descCourte.length() > 150) {
            descCourte = descCourte.substring(0, 147) + "...";
        }
        
        return LogementCatalogueDto.builder()
                .id(logement.getId())
                .code(logement.getCode())
                .type(logement.getType())
                .typeLibelle(logement.getType() != null ? logement.getType().name() : null)
                .prixMensuel(logement.getPrixMensuel())
                .prixFormate(logement.getPrixMensuel() != null ? logement.getPrixMensuel().toPlainString() + " FCFA" : null)
                .capacite(logement.getCapacite())
                .superficie(logement.getSuperficie())
                .adresseComplete(adresseComplete)
                .ville(logement.getAdresse() != null ? logement.getAdresse().getVille() : null)
                .quartier(logement.getAdresse() != null ? logement.getAdresse().getQuartier() : null)
                .latitude(logement.getAdresse() != null ? logement.getAdresse().getLatitude() : null)
                .longitude(logement.getAdresse() != null ? logement.getAdresse().getLongitude() : null)
                .description(logement.getDescription())
                .descriptionCourte(descCourte)
                .photoprincipale(logement.getPhotos() != null && !logement.getPhotos().isEmpty() ? logement.getPhotos().get(0) : null)
                .photos(logement.getPhotos())
                .nombrePhotos(logement.getPhotos() != null ? logement.getPhotos().size() : 0)
                .equipements(equipementsList)
                .nombreEquipements(equipementsList.size())
                .meuble(logement.getMeuble())
                .disponible(logement.verifierDisponibilite())
                .build();
    }
}
