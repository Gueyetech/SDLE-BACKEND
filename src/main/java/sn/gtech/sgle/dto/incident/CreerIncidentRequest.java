package sn.gtech.sgle.dto.incident;

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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreerIncidentRequest {
    
    @NotNull(message = "L'ID de l'étudiant est obligatoire")
    private UUID etudiantId;
    
    @NotNull(message = "L'ID du logement est obligatoire")
    private UUID logementId;
    
    @NotNull(message = "Le type d'incident est obligatoire")
    private TypeIncidentEnum type;
    
    @NotBlank(message = "La description est obligatoire")
    private String description;
    
    private UrgenceEnum urgence;
    
    private List<String> photos;
}
