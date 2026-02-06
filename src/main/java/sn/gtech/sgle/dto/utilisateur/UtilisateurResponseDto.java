package sn.gtech.sgle.dto.utilisateur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Utilisateur;
import sn.gtech.sgle.entity.enums.RoleEnum;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour un utilisateur
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurResponseDto {
    
    private UUID id;
    private String email;
    private RoleEnum role;
    private Boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereConnexion;
    
    // Informations supplémentaires (si disponibles)
    private String nom;
    private String prenom;
    private String nomComplet;
    private String telephone;
    
    // Type spécifique
    private String typeUtilisateur; // ETUDIANT, ADMINISTRATEUR, GESTIONNAIRE
    
    // Statistiques
    private Integer nombreDocuments;
    private Integer nombreNotifications;
    private Integer nombreNotificationsNonLues;
    
    /**
     * Convertit une entité Utilisateur en DTO
     */
    public static UtilisateurResponseDto fromEntity(Utilisateur utilisateur) {
        if (utilisateur == null) return null;
        
        UtilisateurResponseDtoBuilder builder = UtilisateurResponseDto.builder()
                .id(utilisateur.getId())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .actif(utilisateur.getActif())
                .dateCreation(utilisateur.getDateCreation())
                .derniereConnexion(utilisateur.getDerniereConnexion())
                .typeUtilisateur(utilisateur.getRole() != null ? utilisateur.getRole().name() : "UTILISATEUR")
                .nomComplet(utilisateur.getEmail());
        
        // Statistiques
        builder.nombreDocuments(utilisateur.getDocuments() != null ? utilisateur.getDocuments().size() : 0);
        
        if (utilisateur.getNotifications() != null) {
            builder.nombreNotifications(utilisateur.getNotifications().size())
                    .nombreNotificationsNonLues((int) utilisateur.getNotifications().stream()
                            .filter(n -> !n.getLue())
                            .count());
        } else {
            builder.nombreNotifications(0)
                    .nombreNotificationsNonLues(0);
        }
        
        return builder.build();
    }
}
