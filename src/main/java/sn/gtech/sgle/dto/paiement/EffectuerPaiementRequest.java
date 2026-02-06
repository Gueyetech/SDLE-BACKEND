package sn.gtech.sgle.dto.paiement;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.ModePaiementEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO pour effectuer un paiement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EffectuerPaiementRequest {
    
    @NotNull(message = "L'ID du paiement est obligatoire")
    private UUID paiementId;
    
    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiementEnum modePaiement;
    
    private String referenceBancaire;
    
    @NotNull(message = "Le montant payé est obligatoire")
    @Positive(message = "Le montant payé doit être positif")
    private BigDecimal montantPaye;
    
    private LocalDateTime datePaiement; // Si non fourni, date courante
}
