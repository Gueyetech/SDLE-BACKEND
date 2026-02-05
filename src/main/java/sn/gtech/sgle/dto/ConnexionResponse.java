package sn.gtech.sgle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.RoleEnum;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnexionResponse {
    
    private String accessToken;
    private String refreshToken;
    
    @Builder.Default
    private String tokenType = "Bearer";
    
    private Long expiresIn;
    
    // Informations utilisateur
    private UUID utilisateurId;
    private String email;
    private RoleEnum role;
    private String typeUtilisateur;
    
    // Informations supplémentaires selon le type
    private String nom;
    private String prenom;
    private String matricule;
    private String departement;
    
    private String message;
}
