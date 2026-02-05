package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import sn.gtech.sgle.entity.enums.NiveauEtudesEnum;
import sn.gtech.sgle.entity.enums.StatutEtudiantEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "etudiants")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Etudiant extends Utilisateur {
    
    @Column(unique = true)
    private String matricule;
    
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String telephone;
    private String adresseOriginale;
    private String universite;
    
    @Enumerated(EnumType.STRING)
    private NiveauEtudesEnum niveauEtudes;
    
    private String anneeAcademique;
    private String photoIdentite;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "nomContact", column = @Column(name = "urgence_nom")),
        @AttributeOverride(name = "prenomContact", column = @Column(name = "urgence_prenom")),
        @AttributeOverride(name = "relation", column = @Column(name = "urgence_relation")),
        @AttributeOverride(name = "telephoneContact", column = @Column(name = "urgence_telephone")),
        @AttributeOverride(name = "emailContact", column = @Column(name = "urgence_email")),
        @AttributeOverride(name = "adresseContact", column = @Column(name = "urgence_adresse"))
    })
    private ContactUrgence contactUrgence;
    
    @Builder.Default
    private LocalDateTime dateInscription = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutEtudiantEnum statut = StatutEtudiantEnum.EN_ATTENTE_VALIDATION;
    
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DemandeLogement> demandes = new ArrayList<>();
    
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Attribution> attributions = new ArrayList<>();
    
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Incident> incidents = new ArrayList<>();
    
    public Boolean completerProfil() {
        return this.nom != null && this.prenom != null && this.matricule != null;
    }
    
    public List<Logement> rechercherLogement() {
        return new ArrayList<>();
    }
    
    public DemandeLogement soumettreDemandeLogement() {
        return new DemandeLogement();
    }
    
    public Logement consulterMonLogement() {
        return this.attributions.stream()
            .filter(a -> a.getStatut() == sn.gtech.sgle.entity.enums.StatutAttributionEnum.ACTIVE)
            .findFirst()
            .map(Attribution::getLogement)
            .orElse(null);
    }
    
    public Incident signalerProbleme() {
        return new Incident();
    }
    
    public byte[] telechargerDocument(Document document) {
        return new byte[0];
    }
    
    public String verifierStatutDemande() {
        return this.demandes.stream()
            .findFirst()
            .map(d -> d.getStatut().name())
            .orElse("Aucune demande");
    }
}
