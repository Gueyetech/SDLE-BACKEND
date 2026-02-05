package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.FormatEnum;
import sn.gtech.sgle.entity.enums.TypeRapportEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rapports")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rapport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeRapportEnum type;
    
    @Column(nullable = false)
    private String titre;
    
    @Builder.Default
    private LocalDateTime dateGeneration = LocalDateTime.now();
    
    private LocalDate periodeDebut;
    
    private LocalDate periodeFin;
    
    @Column(columnDefinition = "TEXT")
    private String donnees; // JSON stocké en texte
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FormatEnum format = FormatEnum.PDF;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genere_par_id")
    private Administrateur generePar;
    
    @PrePersist
    public void prePersist() {
        this.dateGeneration = LocalDateTime.now();
    }
    
    public byte[] generer() {
        // Logique de génération du rapport selon le format
        switch (this.format) {
            case PDF:
                return genererPDF();
            case EXCEL:
                return genererExcel();
            case CSV:
                return genererCSV();
            default:
                return new byte[0];
        }
    }
    
    private byte[] genererPDF() {
        // Logique de génération PDF
        return new byte[0];
    }
    
    private byte[] genererExcel() {
        // Logique de génération Excel
        return new byte[0];
    }
    
    private byte[] genererCSV() {
        // Logique de génération CSV
        return new byte[0];
    }
    
    public byte[] exporter() {
        return generer();
    }
    
    public void envoyer(String email) {
        // Logique d'envoi du rapport par email
    }
}
