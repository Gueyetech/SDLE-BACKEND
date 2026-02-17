package sn.gtech.sgle.dto.etudiant;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO pour soumettre une demande de logement par l'étudiant
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SoumettreDemandeRequest {
    
    // Préférence de type de logement
    private TypeLogementEnum typeLogementSouhaite;
    
    // Budget maximum mensuel
    @NotNull(message = "Le budget maximum est obligatoire")
    private BigDecimal budgetMaximum;
    
    // Date de début souhaitée
    @NotNull(message = "La date de début souhaitée est obligatoire")
    private LocalDate dateDebutSouhaitee;
    
    // Durée souhaitée en mois
    private Integer dureeSouhaitee;
    
    // Préférences de localisation
    private String villePreferee;
    private String quartierPrefere;
    
    // Autres préférences
    private Boolean prefereMeuble;
    private Integer capaciteMinimale;
    private List<String> equipementsSouhaites;
    
    // Besoins spécifiques / commentaires
    private String besoinsSpecifiques;
    
    // Documents joints (IDs des documents déjà uploadés)
    private List<UUID> documentsJoints;
}
