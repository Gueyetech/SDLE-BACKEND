package sn.gtech.sgle.dto.logement;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Maintenance;
import sn.gtech.sgle.entity.enums.StatutMaintenanceEnum;
import sn.gtech.sgle.entity.enums.TypeMaintenanceEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO pour la maintenance d'un logement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceDto {
    
    private UUID id;
    
    @NotNull(message = "L'ID du logement est obligatoire")
    private UUID logementId;
    
    private String logementCode;
    
    @NotNull(message = "Le type de maintenance est obligatoire")
    private TypeMaintenanceEnum type;
    
    @NotBlank(message = "La description est obligatoire")
    private String description;
    
    private StatutMaintenanceEnum statut;
    
    @NotNull(message = "La date prévue est obligatoire")
    @FutureOrPresent(message = "La date prévue doit être dans le présent ou le futur")
    private LocalDate datePrevue;
    
    private LocalDate dateRealisation;
    
    private Integer dureeEstimee;
    
    private String technicien;
    
    @Positive(message = "Le coût doit être positif")
    private BigDecimal cout;
    
    /**
     * Convertit une entité Maintenance en DTO
     */
    public static MaintenanceDto fromEntity(Maintenance maintenance) {
        if (maintenance == null) return null;
        
        MaintenanceDtoBuilder builder = MaintenanceDto.builder()
                .id(maintenance.getId())
                .type(maintenance.getType())
                .description(maintenance.getDescription())
                .statut(maintenance.getStatut())
                .datePrevue(maintenance.getDatePlanifiee())
                .dateRealisation(maintenance.getDateRealisation())
                .dureeEstimee(maintenance.getDureeEstimee())
                .technicien(maintenance.getTechnicien())
                .cout(maintenance.getCout());
        
        if (maintenance.getLogement() != null) {
            builder.logementId(maintenance.getLogement().getId())
                    .logementCode(maintenance.getLogement().getCode());
        }
        
        return builder.build();
    }
}
