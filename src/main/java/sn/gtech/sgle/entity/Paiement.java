package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.ModePaiementEnum;
import sn.gtech.sgle.entity.enums.StatutPaiementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "paiements")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Paiement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String numeroPaiement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribution_id", nullable = false)
    private Attribution attribution;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;
    
    private LocalDateTime datePaiement;
    
    @Column(nullable = false)
    private LocalDate dateEcheance;
    
    private String moisConcerne;
    
    @Enumerated(EnumType.STRING)
    private ModePaiementEnum modePaiement;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutPaiementEnum statut = StatutPaiementEnum.EN_ATTENTE;
    
    private String referenceBancaire;
    
    @OneToOne
    @JoinColumn(name = "recu_id")
    private Document recu;
    
    @PrePersist
    public void prePersist() {
        if (this.numeroPaiement == null) {
            this.numeroPaiement = "PAY-" + System.currentTimeMillis();
        }
    }
    
    public Document genererRecu() {
        Document doc = Document.builder()
            .nom("Recu_" + this.numeroPaiement + ".pdf")
            .type(sn.gtech.sgle.entity.enums.TypeDocumentEnum.RECU_PAIEMENT)
            .url("/documents/recus/" + this.numeroPaiement + ".pdf")
            .dateUpload(LocalDateTime.now())
            .valide(true)
            .build();
        this.recu = doc;
        return doc;
    }
    
    public Boolean verifierRetard() {
        if (this.statut == StatutPaiementEnum.PAYE) {
            return false;
        }
        return LocalDate.now().isAfter(this.dateEcheance);
    }
    
    public void envoyerRappel() {
        // Logique d'envoi de rappel de paiement
    }
    
    public void marquerCommeRegle() {
        this.statut = StatutPaiementEnum.PAYE;
        this.datePaiement = LocalDateTime.now();
    }
}
