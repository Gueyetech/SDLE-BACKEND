package sn.gtech.sgle.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.common.ApiResponse;
import sn.gtech.sgle.dto.utilisateur.CreerUtilisateurRequest;
import sn.gtech.sgle.dto.utilisateur.ModifierUtilisateurRequest;
import sn.gtech.sgle.dto.utilisateur.ResetMotDePasseRequest;
import sn.gtech.sgle.dto.utilisateur.UtilisateurResponseDto;
import sn.gtech.sgle.entity.enums.RoleEnum;
import sn.gtech.sgle.service.UtilisateurService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/utilisateurs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Utilisateurs", description = "Gestion des comptes utilisateurs")
public class AdminUtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping
    @Operation(summary = "Créer un utilisateur", description = "Crée un nouveau compte utilisateur (Admin, Gestionnaire, Étudiant)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<ApiResponse<UtilisateurResponseDto>> creerUtilisateur(
            @Valid @RequestBody CreerUtilisateurRequest request) {
        UtilisateurResponseDto utilisateur = utilisateurService.creerUtilisateur(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(utilisateur, "Utilisateur créé avec succès"));
    }

    @GetMapping
    @Operation(summary = "Lister les utilisateurs", description = "Liste tous les utilisateurs")
    public ResponseEntity<ApiResponse<List<UtilisateurResponseDto>>> listerUtilisateurs() {
        List<UtilisateurResponseDto> utilisateurs = utilisateurService.getTousLesUtilisateurs();
        return ResponseEntity.ok(ApiResponse.success(utilisateurs, utilisateurs.size() + " utilisateur(s) trouvé(s)"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un utilisateur", description = "Récupère les détails d'un utilisateur par son ID")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<ApiResponse<UtilisateurResponseDto>> getUtilisateur(
            @Parameter(description = "ID de l'utilisateur") @PathVariable UUID id) {
        UtilisateurResponseDto utilisateur = utilisateurService.getUtilisateurParId(id);
        return ResponseEntity.ok(ApiResponse.success(utilisateur, "Utilisateur récupéré"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un utilisateur", description = "Met à jour les informations d'un utilisateur")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Utilisateur modifié avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ApiResponse<UtilisateurResponseDto>> modifierUtilisateur(
            @Parameter(description = "ID de l'utilisateur") @PathVariable UUID id,
            @Valid @RequestBody ModifierUtilisateurRequest request) {
        UtilisateurResponseDto utilisateur = utilisateurService.modifierUtilisateur(id, request);
        return ResponseEntity.ok(ApiResponse.success(utilisateur, "Utilisateur modifié avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur", description = "Supprime un utilisateur (soft delete ou hard delete)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Utilisateur supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<ApiResponse<Void>> supprimerUtilisateur(
            @Parameter(description = "ID de l'utilisateur") @PathVariable UUID id) {
        utilisateurService.supprimerUtilisateur(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Utilisateur supprimé avec succès"));
    }

    // ============ Gestion des rôles ============

    @PatchMapping("/{id}/role")
    @Operation(summary = "Changer le rôle", description = "Modifie le rôle d'un utilisateur")
    public ResponseEntity<ApiResponse<UtilisateurResponseDto>> changerRole(
            @Parameter(description = "ID de l'utilisateur") @PathVariable UUID id,
            @Parameter(description = "Nouveau rôle") @RequestParam RoleEnum role) {
        UtilisateurResponseDto utilisateur = utilisateurService.changerRole(id, role);
        return ResponseEntity.ok(ApiResponse.success(utilisateur, "Rôle modifié avec succès"));
    }

    // ============ Activation / Désactivation ============

    @PatchMapping("/{id}/activer")
    @Operation(summary = "Activer un compte", description = "Active le compte d'un utilisateur")
    public ResponseEntity<ApiResponse<UtilisateurResponseDto>> activerCompte(
            @Parameter(description = "ID de l'utilisateur") @PathVariable UUID id) {
        UtilisateurResponseDto utilisateur = utilisateurService.activerUtilisateur(id);
        return ResponseEntity.ok(ApiResponse.success(utilisateur, "Compte activé"));
    }

    @PatchMapping("/{id}/desactiver")
    @Operation(summary = "Désactiver un compte", description = "Désactive le compte d'un utilisateur")
    public ResponseEntity<ApiResponse<UtilisateurResponseDto>> desactiverCompte(
            @Parameter(description = "ID de l'utilisateur") @PathVariable UUID id) {
        UtilisateurResponseDto utilisateur = utilisateurService.desactiverUtilisateur(id);
        return ResponseEntity.ok(ApiResponse.success(utilisateur, "Compte désactivé"));
    }

    // ============ Gestion des mots de passe ============

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "Réinitialiser le mot de passe", description = "Réinitialise le mot de passe d'un utilisateur")
    public ResponseEntity<ApiResponse<String>> resetMotDePasse(
            @Parameter(description = "ID de l'utilisateur") @PathVariable UUID id,
            @Valid @RequestBody(required = false) ResetMotDePasseRequest request) {
        String nouveauMotDePasse = utilisateurService.resetMotDePasse(id, request);
        return ResponseEntity.ok(ApiResponse.success(
                request != null && request.getNouveauMotDePasse() != null ? "Mot de passe mis à jour" : nouveauMotDePasse,
                "Mot de passe réinitialisé avec succès"));
    }

    // ============ Statistiques ============

    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques utilisateurs", description = "Récupère les statistiques globales des utilisateurs")
    public ResponseEntity<ApiResponse<UtilisateurService.UtilisateurStatistiques>> getStatistiques() {
        UtilisateurService.UtilisateurStatistiques stats = utilisateurService.getStatistiques();
        return ResponseEntity.ok(ApiResponse.success(stats, "Statistiques récupérées"));
    }
}
