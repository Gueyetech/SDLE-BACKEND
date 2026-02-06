package sn.gtech.sgle.dto.logement;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO pour créer un nouveau logement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreerLogementRequest {
    
    @NotNull(message = "Le type de logement est obligatoire")
    private TypeLogementEnum type;
    
    @NotNull(message = "La capacité est obligatoire")
    @Min(value = 1, message = "La capacité doit être d'au moins 1")
    private Integer capacite;
    
    @Positive(message = "La superficie doit être positive")
    private Float superficie;
    
    @NotNull(message = "Le prix mensuel est obligatoire")
    @Positive(message = "Le prix mensuel doit être positif")
    private BigDecimal prixMensuel;
    
    private String description;
    
    @Builder.Default
    private Boolean meuble = false;
    
    // Adresse
    private String adresseRue;
    private String adresseNumero;
    private String adresseCodePostal;
    @NotBlank(message = "La ville est obligatoire")
    private String adresseVille;
    private String adresseQuartier;
    private String adresseRegion;
    @Builder.Default
    private String adressePays = "Sénégal";
    private Float adresseLatitude;
    private Float adresseLongitude;
    private String adresseComplement;
    
    // Équipements (liste d'IDs ou noms)
    private List<String> equipements;
    
    // Photos (URLs)
    private List<String> photos;
}
