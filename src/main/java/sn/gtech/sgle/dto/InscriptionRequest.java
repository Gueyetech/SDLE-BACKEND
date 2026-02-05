package sn.gtech.sgle.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.NiveauEtudesEnum;
import sn.gtech.sgle.entity.enums.RoleEnum;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InscriptionRequest {
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;
    
    @NotBlank(message = "La confirmation du mot de passe est obligatoire")
    private String confirmationMotDePasse;
    
    // Type de compte à créer (ETUDIANT par défaut)
    @Builder.Default
    private RoleEnum role = RoleEnum.ETUDIANT;
    
    // ========== Champs spécifiques Étudiant ==========
    private String matricule;
    
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;
    
    private LocalDate dateNaissance;
    
    private String telephone;
    
    private String adresseOriginale;
    
    private String universite;
    
    private NiveauEtudesEnum niveauEtudes;
    
    private String anneeAcademique;
    
    // ========== Champs spécifiques Gestionnaire ==========
    private String departement;
    
    // ========== Contact d'urgence (pour étudiant) ==========
    private String nomContactUrgence;
    private String prenomContactUrgence;
    private String relationContactUrgence;
    private String telephoneContactUrgence;
    private String emailContactUrgence;
}
