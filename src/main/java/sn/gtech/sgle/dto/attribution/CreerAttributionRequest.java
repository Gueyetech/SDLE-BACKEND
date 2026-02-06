package sn.gtech.sgle.dto.attribution;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO pour créer une nouvelle attribution
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreerAttributionRequest {
    
    @NotNull(message = "L'ID de l'étudiant est obligatoire")
    private UUID etudiantId;
    
    @NotNull(message = "L'ID du logement est obligatoire")
    private UUID logementId;
    
    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début doit être dans le présent ou le futur")
    private LocalDate dateDebut;
    
    @NotNull(message = "La date de fin est obligatoire")
    @Future(message = "La date de fin doit être dans le futur")
    private LocalDate dateFin;
    
    @NotNull(message = "Le montant du loyer est obligatoire")
    @Positive(message = "Le montant du loyer doit être positif")
    private BigDecimal montantLoyer;
    
    @PositiveOrZero(message = "Le montant de la caution doit être positif ou nul")
    private BigDecimal montantCaution;
    
    private UUID demandeId; // Si lié à une demande existante
    
    private String commentaires;
}
