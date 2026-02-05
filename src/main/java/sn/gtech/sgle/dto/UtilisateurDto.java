package sn.gtech.sgle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.NiveauEtudesEnum;
import sn.gtech.sgle.entity.enums.RoleEnum;
import sn.gtech.sgle.entity.enums.StatutEtudiantEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurDto {
    
    private UUID id;
    private String email;
    private RoleEnum role;
    private Boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereConnexion;
    
    // Champs Etudiant
    private String matricule;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String telephone;
    private String adresseOriginale;
    private String universite;
    private NiveauEtudesEnum niveauEtudes;
    private String anneeAcademique;
    private String photoIdentite;
    private StatutEtudiantEnum statutEtudiant;
    
    // Champs GestionnaireLogements
    private String departement;
}
