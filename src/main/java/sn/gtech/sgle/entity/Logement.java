package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.StatutLogementEnum;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "logements")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Logement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String code;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeLogementEnum type;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "rue", column = @Column(name = "adresse_rue")),
        @AttributeOverride(name = "numero", column = @Column(name = "adresse_numero")),
        @AttributeOverride(name = "codePostal", column = @Column(name = "adresse_code_postal")),
        @AttributeOverride(name = "ville", column = @Column(name = "adresse_ville")),
        @AttributeOverride(name = "quartier", column = @Column(name = "adresse_quartier")),
        @AttributeOverride(name = "region", column = @Column(name = "adresse_region")),
        @AttributeOverride(name = "pays", column = @Column(name = "adresse_pays")),
        @AttributeOverride(name = "latitude", column = @Column(name = "adresse_latitude")),
        @AttributeOverride(name = "longitude", column = @Column(name = "adresse_longitude")),
        @AttributeOverride(name = "complementAdresse", column = @Column(name = "adresse_complement"))
    })
    private Adresse adresse;
    
    @Column(nullable = false)
    private Integer capacite;
    
    private Float superficie;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixMensuel;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutLogementEnum statut = StatutLogementEnum.DISPONIBLE;
    
    @Builder.Default
    private Boolean meuble = false;
    
    @OneToMany(mappedBy = "logement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Equipement> equipements = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "logement_photos", joinColumns = @JoinColumn(name = "logement_id"))
    @Column(name = "photo_url")
    @Builder.Default
    private List<String> photos = new ArrayList<>();
    
    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    private LocalDateTime dateModification;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestionnaire_id")
    private Utilisateur gestionnaire;
    
    @OneToMany(mappedBy = "logement", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Attribution> attributions = new ArrayList<>();
    
    @OneToMany(mappedBy = "logement", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Incident> incidents = new ArrayList<>();
    
    @OneToMany(mappedBy = "logement", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Maintenance> maintenances = new ArrayList<>();
    
    @PrePersist
    public void prePersist() {
        if (this.code == null) {
            this.code = genererCode();
        }
        this.dateCreation = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }
    
    public String genererCode() {
        String prefix = this.type != null ? this.type.name().substring(0, 3) : "LOG";
        return prefix + "-" + System.currentTimeMillis();
    }
    
    public Boolean verifierDisponibilite() {
        return this.statut == StatutLogementEnum.DISPONIBLE;
    }
    
    public void changerStatut(StatutLogementEnum nouveauStatut) {
        this.statut = nouveauStatut;
        this.dateModification = LocalDateTime.now();
    }
    
    public void ajouterPhoto(String photoUrl) {
        this.photos.add(photoUrl);
    }
    
    public Float calculerTauxOccupation() {
        if (this.attributions == null || this.attributions.isEmpty()) {
            return 0f;
        }
        long attributionsActives = this.attributions.stream()
            .filter(a -> a.getStatut() == sn.gtech.sgle.entity.enums.StatutAttributionEnum.ACTIVE)
            .count();
        return (float) attributionsActives / this.capacite * 100;
    }
    
    public Maintenance planifierMaintenance() {
        return new Maintenance();
    }
}
