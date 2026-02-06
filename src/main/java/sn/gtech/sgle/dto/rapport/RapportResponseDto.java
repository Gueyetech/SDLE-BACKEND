package sn.gtech.sgle.dto.rapport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Rapport;
import sn.gtech.sgle.entity.enums.FormatEnum;
import sn.gtech.sgle.entity.enums.TypeRapportEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour un rapport
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RapportResponseDto {
    
    private UUID id;
    private TypeRapportEnum type;
    private String titre;
    private LocalDateTime dateGeneration;
    private LocalDate periodeDebut;
    private LocalDate periodeFin;
    private FormatEnum format;
    private String donnees;
    
    // Générateur
    private UUID generateurId;
    private String generateurNom;
    
    // URL de téléchargement
    private String urlTelechargement;
    
    // Métadonnées
    private Long tailleFichier; // En octets
    private String contentType;
    
    /**
     * Convertit une entité Rapport en DTO
     */
    public static RapportResponseDto fromEntity(Rapport rapport) {
        if (rapport == null) return null;
        
        RapportResponseDtoBuilder builder = RapportResponseDto.builder()
                .id(rapport.getId())
                .type(rapport.getType())
                .titre(rapport.getTitre())
                .dateGeneration(rapport.getDateGeneration())
                .periodeDebut(rapport.getPeriodeDebut())
                .periodeFin(rapport.getPeriodeFin())
                .format(rapport.getFormat())
                .donnees(rapport.getDonnees());
        
        if (rapport.getGenerePar() != null) {
            builder.generateurId(rapport.getGenerePar().getId())
                    .generateurNom(rapport.getGenerePar().getEmail());
        }
        
        // Déterminer le content type basé sur le format
        if (rapport.getFormat() != null) {
            builder.contentType(getContentType(rapport.getFormat()));
        }
        
        return builder.build();
    }
    
    private static String getContentType(FormatEnum format) {
        return switch (format) {
            case PDF -> "application/pdf";
            case EXCEL -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case CSV -> "text/csv";
            default -> "application/octet-stream";
        };
    }
}
