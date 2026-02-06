package sn.gtech.sgle.dto.attribution;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.StatutAttributionEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO pour modifier une attribution existante
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifierAttributionRequest {
    
    private UUID logementId;
    
    private LocalDate dateDebut;
    
    @Future(message = "La date de fin doit être dans le futur")
    private LocalDate dateFin;
    
    @Positive(message = "Le montant du loyer doit être positif")
    private BigDecimal montantLoyer;
    
    @PositiveOrZero(message = "Le montant de la caution doit être positif ou nul")
    private BigDecimal montantCaution;
    
    private StatutAttributionEnum statut;
    
    private String commentaires;
}
