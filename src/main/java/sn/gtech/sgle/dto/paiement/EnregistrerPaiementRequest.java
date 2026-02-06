package sn.gtech.sgle.dto.paiement;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.ModePaiementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO pour enregistrer un nouveau paiement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnregistrerPaiementRequest {
    
    @NotNull(message = "L'ID de l'attribution est obligatoire")
    private UUID attributionId;
    
    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;
    
    @NotNull(message = "La date d'échéance est obligatoire")
    private LocalDate dateEcheance;
    
    @NotBlank(message = "Le mois concerné est obligatoire")
    private String moisConcerne; // Format: "2024-01"
    
    private ModePaiementEnum modePaiement;
    
    private String referenceBancaire;
}
