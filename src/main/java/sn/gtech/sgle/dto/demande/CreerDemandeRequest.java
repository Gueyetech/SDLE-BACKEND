package sn.gtech.sgle.dto.demande;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.PrioriteEnum;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreerDemandeRequest {
    
    @NotNull(message = "L'ID de l'étudiant est obligatoire")
    private UUID etudiantId;
    
    private TypeLogementEnum typeLogementSouhaite;
    
    private BigDecimal budgetMaximum;
    
    private LocalDate dateDebutSouhaitee;
    
    private Integer dureeSouhaitee;
    
    private String preferences;
    
    private PrioriteEnum priorite;
    
    private String commentaires;
}
