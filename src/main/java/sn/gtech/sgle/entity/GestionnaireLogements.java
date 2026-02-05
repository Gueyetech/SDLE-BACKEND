package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gestionnaires_logements")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GestionnaireLogements extends Utilisateur {
    
    private String telephone;
    private String departement;
    
    @OneToMany(mappedBy = "gestionnaire", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Logement> logements = new ArrayList<>();
    
    @OneToMany(mappedBy = "gestionnaire", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DemandeLogement> demandesTraitees = new ArrayList<>();
    
    @OneToMany(mappedBy = "gestionnaire", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Attribution> attributions = new ArrayList<>();
    
    @OneToMany(mappedBy = "gestionnaire", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Incident> incidentsGeres = new ArrayList<>();
    
    public Logement creerLogement() {
        return new Logement();
    }
    
    public Boolean modifierLogement(Logement logement) {
        return true;
    }
    
    public void traiterDemande(DemandeLogement demande) {
        // Logique de traitement de demande
    }
    
    public Attribution attribuerLogement(Etudiant etudiant, Logement logement) {
        return new Attribution();
    }
    
    public void gererIncident(Incident incident) {
        // Logique de gestion d'incident
    }
    
    public List<Logement> consulterPortefeuille() {
        return this.logements;
    }
}
