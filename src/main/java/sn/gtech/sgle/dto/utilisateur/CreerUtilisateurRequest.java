package sn.gtech.sgle.dto.utilisateur;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.RoleEnum;

/**
 * DTO pour créer un nouvel utilisateur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreerUtilisateurRequest {
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$", 
             message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial")
    private String motDePasse;
    
    @NotNull(message = "Le rôle est obligatoire")
    private RoleEnum role;
    
    @Builder.Default
    private Boolean actif = true;
    
    // Informations supplémentaires selon le type d'utilisateur
    private String nom;
    private String prenom;
    private String telephone;
}
