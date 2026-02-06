package sn.gtech.sgle.dto.logement;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.TypeMaintenanceEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanifierMaintenanceRequest {

    @NotNull(message = "L'ID du logement est obligatoire")
    private UUID logementId;

    @NotNull(message = "Le type de maintenance est obligatoire")
    private TypeMaintenanceEnum type;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 1000, message = "La description doit contenir entre 10 et 1000 caractères")
    private String description;

    @NotNull(message = "La date de début planifiée est obligatoire")
    @Future(message = "La date de début doit être dans le futur")
    private LocalDateTime dateDebutPlanifiee;

    private LocalDateTime dateFinPlanifiee;

    private String prestataire;

    private Double coutEstime;

    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    private String notes;

    // Priorité de la maintenance
    private PrioriteMaintenance priorite;

    public enum PrioriteMaintenance {
        BASSE,
        NORMALE,
        HAUTE,
        URGENTE
    }
}
