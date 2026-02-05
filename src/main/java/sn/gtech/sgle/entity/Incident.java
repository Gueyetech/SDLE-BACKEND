package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.StatutIncidentEnum;
import sn.gtech.sgle.entity.enums.TypeIncidentEnum;
import sn.gtech.sgle.entity.enums.UrgenceEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "incidents")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Incident {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String numeroTicket;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logement_id", nullable = false)
    private Logement logement;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeIncidentEnum type;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UrgenceEnum urgence = UrgenceEnum.MOYENNE;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutIncidentEnum statut = StatutIncidentEnum.OUVERT;
    
    @Builder.Default
    private LocalDateTime dateSignalement = LocalDateTime.now();
    
    private LocalDateTime dateResolution;
    
    @ElementCollection
    @CollectionTable(name = "incident_photos", joinColumns = @JoinColumn(name = "incident_id"))
    @Column(name = "photo_url")
    @Builder.Default
    private List<String> photos = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gestionnaire_id")
    private GestionnaireLogements gestionnaire;
    
    private String technicien;
    
    @Column(columnDefinition = "TEXT")
    private String commentaireResolution;
    
    @PrePersist
    public void prePersist() {
        if (this.numeroTicket == null) {
            this.numeroTicket = genererTicket();
        }
        this.dateSignalement = LocalDateTime.now();
    }
    
    public String genererTicket() {
        return "INC-" + System.currentTimeMillis();
    }
    
    public void assignerTechnicien(String nomTechnicien) {
        this.technicien = nomTechnicien;
        this.statut = StatutIncidentEnum.EN_COURS;
    }
    
    public void marquerResolu(String commentaire) {
        this.statut = StatutIncidentEnum.RESOLU;
        this.commentaireResolution = commentaire;
        this.dateResolution = LocalDateTime.now();
    }
    
    public void envoyerNotification() {
        // Logique d'envoi de notification
    }
    
    public void ajouterCommentaire(String commentaire) {
        if (this.commentaireResolution == null) {
            this.commentaireResolution = commentaire;
        } else {
            this.commentaireResolution += "\n" + commentaire;
        }
    }
}
