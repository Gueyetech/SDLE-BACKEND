package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.StatutAttributionEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "attributions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Attribution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String numeroContrat;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logement_id", nullable = false)
    private Logement logement;
    
    @Column(nullable = false)
    private LocalDate dateDebut;
    
    @Column(nullable = false)
    private LocalDate dateFin;
    
    @Builder.Default
    private LocalDateTime dateAttribution = LocalDateTime.now();
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantLoyer;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal montantCaution;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutAttributionEnum statut = StatutAttributionEnum.ACTIVE;
    
    @OneToOne
    @JoinColumn(name = "contrat_id")
    private Document contrat;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestionnaire_id")
    private GestionnaireLogements gestionnaire;
    
    @OneToOne
    @JoinColumn(name = "demande_id")
    private DemandeLogement demande;
    
    private LocalDateTime dateCheckIn;
    private LocalDateTime dateCheckOut;
    
    @Column(columnDefinition = "TEXT")
    private String commentaires;
    
    @OneToMany(mappedBy = "attribution", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Paiement> paiements = new ArrayList<>();
    
    @OneToMany(mappedBy = "attribution", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();
    
    @PrePersist
    public void prePersist() {
        if (this.numeroContrat == null) {
            this.numeroContrat = "CONT-" + System.currentTimeMillis();
        }
        this.dateAttribution = LocalDateTime.now();
    }
    
    public Document genererContrat() {
        Document doc = Document.builder()
            .nom("Contrat_" + this.numeroContrat + ".pdf")
            .type(sn.gtech.sgle.entity.enums.TypeDocumentEnum.CONTRAT_LOCATION)
            .url("/documents/contrats/" + this.numeroContrat + ".pdf")
            .dateUpload(LocalDateTime.now())
            .valide(true)
            .build();
        this.contrat = doc;
        return doc;
    }
    
    public Integer calculerDuree() {
        if (dateDebut == null || dateFin == null) {
            return 0;
        }
        return (int) ChronoUnit.MONTHS.between(dateDebut, dateFin);
    }
    
    public Attribution renouveler() {
        Attribution nouvelleAttribution = Attribution.builder()
            .etudiant(this.etudiant)
            .logement(this.logement)
            .dateDebut(this.dateFin.plusDays(1))
            .dateFin(this.dateFin.plusMonths(12))
            .montantLoyer(this.montantLoyer)
            .montantCaution(this.montantCaution)
            .gestionnaire(this.gestionnaire)
            .statut(StatutAttributionEnum.ACTIVE)
            .build();
        
        this.statut = StatutAttributionEnum.EXPIREE;
        return nouvelleAttribution;
    }
    
    public void resilier() {
        this.statut = StatutAttributionEnum.RESILIEE;
        this.dateFin = LocalDate.now();
    }
    
    public void effectuerCheckIn() {
        this.dateCheckIn = LocalDateTime.now();
    }
    
    public void effectuerCheckOut() {
        this.dateCheckOut = LocalDateTime.now();
    }
    
    public Boolean verifierValidite() {
        if (this.statut != StatutAttributionEnum.ACTIVE) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !today.isBefore(dateDebut) && !today.isAfter(dateFin);
    }
    
    public void envoyerNotification() {
        // Logique d'envoi de notification
    }
}
