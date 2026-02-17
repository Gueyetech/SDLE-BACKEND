package sn.gtech.sgle.dto.etudiant;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.NiveauEtudesEnum;
import sn.gtech.sgle.entity.enums.StatutEtudiantEnum;

import java.time.LocalDate;

/**
 * DTO pour compléter le profil étudiant
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompleterProfilRequest {
    
    // Informations personnelles
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;
    
    private LocalDate dateNaissance;
    
    private String telephone;
    
    private String adresseOriginale;
    
    private String lieuNaissance;
    
    private String nationalite;
    
    private String sexe; // M ou F
    
    // Informations universitaires
    private String matricule;
    
    private String universite;
    
    private String faculte;
    
    private String filiere;
    
    private NiveauEtudesEnum niveauEtudes;
    
    private String anneeAcademique;
    
    private StatutEtudiantEnum statutEtudiant;
    
    // Contact d'urgence
    private String nomContactUrgence;
    
    private String prenomContactUrgence;
    
    private String relationContactUrgence;
    
    private String telephoneContactUrgence;
    
    private String emailContactUrgence;
    
    private String adresseContactUrgence;
}
