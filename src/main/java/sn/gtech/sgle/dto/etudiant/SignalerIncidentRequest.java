package sn.gtech.sgle.dto.etudiant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.TypeIncidentEnum;
import sn.gtech.sgle.entity.enums.UrgenceEnum;

import java.util.List;
import java.util.UUID;

/**
 * DTO pour signaler un problème/incident par l'étudiant
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignalerIncidentRequest {
    
    // Type de problème
    @NotNull(message = "Le type de problème est obligatoire")
    private TypeIncidentEnum type;
    
    // Description du problème
    @NotBlank(message = "La description est obligatoire")
    private String description;
    
    // Niveau d'urgence
    @Builder.Default
    private UrgenceEnum urgence = UrgenceEnum.MOYENNE;
    
    // Photos (URLs des photos uploadées)
    private List<String> photos;
    
    // Logement concerné (optionnel, par défaut le logement actuel de l'étudiant)
    private UUID logementId;
    
    // Disponibilités pour intervention
    private String disponibilites;
    
    // Contact alternatif pour cette intervention
    private String telephoneContact;
}
