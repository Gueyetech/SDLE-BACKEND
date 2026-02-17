package sn.gtech.sgle.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.NiveauEtudesEnum;
import sn.gtech.sgle.entity.enums.StatutEtudiantEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO pour le profil complet d'un étudiant
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfilEtudiantDto {
    
    private UUID id;
    private String email;
    
    // Informations personnelles
    private String nom;
    private String prenom;
    private String nomComplet;
    private LocalDate dateNaissance;
    private String telephone;
    private String adresseOriginale;
    private String lieuNaissance;
    private String nationalite;
    private String sexe;
    
    // Informations universitaires
    private String matricule;
    private String universite;
    private String faculte;
    private String filiere;
    private NiveauEtudesEnum niveauEtudes;
    private String anneeAcademique;
    private StatutEtudiantEnum statutEtudiant;
    
    // Contact d'urgence
    private ContactUrgenceDto contactUrgence;
    
    // Compte
    private Boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereConnexion;
    private Boolean profilComplet;
    
    // Documents
    private List<DocumentEtudiantDto> documents;
    private Integer nombreDocuments;
    private Integer documentsValides;
    private Integer documentsPending;
    
    // Logement actuel (si attribué)
    private Boolean aLogementActuel;
    private String logementCode;
    private String logementAdresse;
    
    /**
     * DTO pour contact d'urgence
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContactUrgenceDto {
        private String nom;
        private String prenom;
        private String nomComplet;
        private String relation;
        private String telephone;
        private String email;
        private String adresse;
    }
    
    /**
     * DTO pour document étudiant
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DocumentEtudiantDto {
        private UUID id;
        private String nom;
        private String type;
        private String url;
        private LocalDateTime dateUpload;
        private LocalDate dateExpiration;
        private Boolean valide;
        private Boolean expire;
    }
}
