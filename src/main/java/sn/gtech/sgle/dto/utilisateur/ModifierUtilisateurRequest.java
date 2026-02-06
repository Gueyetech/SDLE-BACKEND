package sn.gtech.sgle.dto.utilisateur;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.RoleEnum;

/**
 * DTO pour modifier un utilisateur existant
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifierUtilisateurRequest {
    
    @Email(message = "L'email doit être valide")
    private String email;
    
    private RoleEnum role;
    
    private Boolean actif;
    
    // Informations supplémentaires
    private String nom;
    private String prenom;
    private String telephone;
}
