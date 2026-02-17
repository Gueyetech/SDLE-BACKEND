package sn.gtech.sgle.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO pour la recherche de logements par l'étudiant
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RechercheLogementRequest {
    
    // Type de logement
    private TypeLogementEnum typeLogement;
    
    // Budget maximum
    private BigDecimal prixMax;
    
    // Budget minimum (optionnel)
    private BigDecimal prixMin;
    
    // Localisation
    private String ville;
    private String quartier;
    private String region;
    
    // Proximité université (en km)
    private Double distanceMaxUniversite;
    private String nomUniversite;
    
    // Capacité minimale
    private Integer capaciteMin;
    
    // Équipements souhaités
    private List<String> equipementsSouhaites;
    
    // Meublé ou non
    private Boolean meuble;
    
    // Superficie minimale
    private Float superficieMin;
    
    // Tri
    private String triPar; // prix, date, capacite, superficie
    private String ordreTri; // asc, desc
    
    // Pagination
    @Builder.Default
    private Integer page = 0;
    
    @Builder.Default
    private Integer taille = 10;
}
