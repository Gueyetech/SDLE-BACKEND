package sn.gtech.sgle.dto.rapport;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.FormatRapportEnum;
import sn.gtech.sgle.entity.enums.TypeRapportEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO pour générer un rapport
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationRapportRequest {
    
    @NotNull(message = "Le type de rapport est obligatoire")
    private TypeRapportEnum type;
    
    @NotNull(message = "Le format est obligatoire")
    private FormatRapportEnum format;
    
    private String titre;
    
    private String description;
    
    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;
    
    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;
    
    // Filtres optionnels
    private List<String> filtresStatuts;
    private List<String> filtresCategories;
    
    // Paramètres supplémentaires
    private Map<String, Object> parametres;
    
    private Boolean inclureGraphiques;
    private Boolean inclureDetails;
}
