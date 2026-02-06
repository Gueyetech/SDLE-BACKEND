package sn.gtech.sgle.dto.rapport;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.FormatRapportEnum;

import java.util.List;
import java.util.Map;

/**
 * DTO pour exporter des données
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {
    
    @NotBlank(message = "Le type d'entité est obligatoire")
    private String entiteType; // ETUDIANT, LOGEMENT, PAIEMENT, ATTRIBUTION, etc.
    
    @NotNull(message = "Le format est obligatoire")
    private FormatRapportEnum format;
    
    // Colonnes à inclure (si vide, toutes les colonnes)
    private List<String> colonnes;
    
    // Filtres
    private Map<String, Object> filtres;
    
    // Options
    private Boolean inclureEntetes;
    private String encodage; // UTF-8 par défaut
    private String separateur; // Pour CSV
    
    // Pagination pour export volumineux
    private Integer page;
    private Integer taille;
}
