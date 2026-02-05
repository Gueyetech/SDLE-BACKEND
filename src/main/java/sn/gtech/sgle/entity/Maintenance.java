package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.StatutMaintenanceEnum;
import sn.gtech.sgle.entity.enums.TypeMaintenanceEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "maintenances")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Maintenance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logement_id", nullable = false)
    private Logement logement;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMaintenanceEnum type;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private LocalDate datePlanifiee;
    
    private LocalDate dateRealisation;
    
    private Integer dureeEstimee; // en heures
    
    private String technicien;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal cout;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutMaintenanceEnum statut = StatutMaintenanceEnum.PLANIFIEE;
    
    public void planifier() {
        this.statut = StatutMaintenanceEnum.PLANIFIEE;
    }
    
    public void reporter(LocalDate nouvelleDate) {
        this.datePlanifiee = nouvelleDate;
        this.statut = StatutMaintenanceEnum.REPORTEE;
    }
    
    public void marquerTerminee() {
        this.statut = StatutMaintenanceEnum.TERMINEE;
        this.dateRealisation = LocalDate.now();
    }
    
    public void demarrer() {
        this.statut = StatutMaintenanceEnum.EN_COURS;
    }
    
    public void annuler() {
        this.statut = StatutMaintenanceEnum.ANNULEE;
    }
}
