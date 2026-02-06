package sn.gtech.sgle.dto.logement;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.StatutLogementEnum;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO pour modifier un logement existant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifierLogementRequest {
    
    private TypeLogementEnum type;
    
    @Min(value = 1, message = "La capacité doit être d'au moins 1")
    private Integer capacite;
    
    @Positive(message = "La superficie doit être positive")
    private Float superficie;
    
    @Positive(message = "Le prix mensuel doit être positif")
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
    
    // Équipements
    private List<String> equipements;
    
    // Photos
    private List<String> photos;
}
