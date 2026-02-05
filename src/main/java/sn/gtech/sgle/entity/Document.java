package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.TypeDocumentEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String nom;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocumentEnum type;
    
    @Column(nullable = false)
    private String url;
    
    private Long tailleFichier;
    
    @Builder.Default
    private LocalDateTime dateUpload = LocalDateTime.now();
    
    private LocalDate dateExpiration;
    
    @Builder.Default
    private Boolean valide = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietaire_id")
    private Utilisateur proprietaire;
    
    // Relation optionnelle avec Attribution (pour les contrats)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribution_id")
    private Attribution attribution;
    
    // Relation optionnelle avec Paiement (pour les reçus)
    @OneToOne(mappedBy = "recu")
    private Paiement paiement;
    
    public byte[] telecharger() {
        // Logique de téléchargement du fichier
        return new byte[0];
    }
    
    public void valider() {
        this.valide = true;
    }
    
    public void supprimer() {
        // Logique de suppression du fichier
    }
    
    public Boolean verifierExpiration() {
        if (this.dateExpiration == null) {
            return false;
        }
        return LocalDate.now().isAfter(this.dateExpiration);
    }
}
