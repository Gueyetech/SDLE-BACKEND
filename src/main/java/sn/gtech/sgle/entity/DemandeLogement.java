package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.PrioriteEnum;
import sn.gtech.sgle.entity.enums.StatutDemandeEnum;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "demandes_logement")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DemandeLogement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String numeroReference;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;
    
    @Enumerated(EnumType.STRING)
    private TypeLogementEnum typeLogementSouhaite;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal budgetMaximum;
    
    private LocalDate dateDebutSouhaitee;
    
    private Integer dureeSouhaitee; // en mois
    
    @Column(columnDefinition = "TEXT")
    private String preferences;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutDemandeEnum statut = StatutDemandeEnum.EN_ATTENTE;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PrioriteEnum priorite = PrioriteEnum.NORMALE;
    
    @Builder.Default
    private LocalDateTime dateDemande = LocalDateTime.now();
    
    private LocalDateTime dateTraitement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestionnaire_id")
    private GestionnaireLogements gestionnaire;
    
    private String motifRejet;
    
    @Column(columnDefinition = "TEXT")
    private String commentaires;
    
    @OneToOne(mappedBy = "demande", cascade = CascadeType.ALL)
    private Attribution attribution;
    
    @PrePersist
    public void prePersist() {
        if (this.numeroReference == null) {
            this.numeroReference = genererReference();
        }
        this.dateDemande = LocalDateTime.now();
    }
    
    public String genererReference() {
        return "DEM-" + System.currentTimeMillis();
    }
    
    public Integer calculerPriorite() {
        // Logique de calcul de priorité basée sur différents critères
        int score = 0;
        
        // Distance de l'université
        // Situation sociale
        // Niveau d'études
        if (etudiant != null && etudiant.getNiveauEtudes() != null) {
            switch (etudiant.getNiveauEtudes()) {
                case DOCTORAT -> score += 5;
                case MASTER_2, MASTER_1 -> score += 4;
                case LICENCE_3 -> score += 3;
                default -> score += 1;
            }
        }
        
        return score;
    }
    
    public void approuver() {
        this.statut = StatutDemandeEnum.APPROUVEE;
        this.dateTraitement = LocalDateTime.now();
    }
    
    public void rejeter(String motif) {
        this.statut = StatutDemandeEnum.REJETEE;
        this.motifRejet = motif;
        this.dateTraitement = LocalDateTime.now();
    }
    
    public void mettreEnAttente() {
        this.statut = StatutDemandeEnum.EN_ATTENTE;
    }
    
    public void notifierEtudiant() {
        // Logique de notification de l'étudiant
    }
}
