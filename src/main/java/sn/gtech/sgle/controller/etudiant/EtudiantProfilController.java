package sn.gtech.sgle.controller.etudiant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.ChangerMotDePasseRequest;
import sn.gtech.sgle.dto.common.ApiResponse;
import sn.gtech.sgle.dto.etudiant.CompleterProfilRequest;
import sn.gtech.sgle.dto.etudiant.ProfilEtudiantDto;
import sn.gtech.sgle.service.EtudiantService;

/**
 * Contrôleur pour la gestion du compte personnel de l'étudiant
 * 
 * Fonctionnalités:
 * - Consulter mon profil
 * - Compléter/modifier mon profil
 * - Changer mon mot de passe
 */
@RestController
@RequestMapping("/api/etudiant/profil")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
@Tag(name = "Étudiant - Profil", description = "Gestion du compte personnel de l'étudiant")
public class EtudiantProfilController {
    
    private final EtudiantService etudiantService;
    
    /**
     * Récupère le profil complet de l'étudiant connecté
     */
    @GetMapping
    @Operation(summary = "Mon profil", description = "Récupère le profil complet de l'étudiant connecté")
    public ResponseEntity<ApiResponse<ProfilEtudiantDto>> getMonProfil(
            @AuthenticationPrincipal UserDetails userDetails) {
        ProfilEtudiantDto profil = etudiantService.getMonProfil(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(profil, "Profil récupéré avec succès"));
    }
    
    /**
     * Complète ou met à jour le profil de l'étudiant
     */
    @PutMapping
    @Operation(summary = "Compléter mon profil", description = "Complète ou met à jour les informations du profil")
    public ResponseEntity<ApiResponse<ProfilEtudiantDto>> completerProfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CompleterProfilRequest request) {
        ProfilEtudiantDto profil = etudiantService.completerProfil(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(profil, "Profil mis à jour avec succès"));
    }
    
    /**
     * Change le mot de passe de l'étudiant
     */
    @PostMapping("/changer-mot-de-passe")
    @Operation(summary = "Changer mot de passe", description = "Change le mot de passe de l'étudiant")
    public ResponseEntity<ApiResponse<Void>> changerMotDePasse(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangerMotDePasseRequest request) {
        etudiantService.changerMotDePasse(
            userDetails.getUsername(), 
            request.getAncienMotDePasse(), 
            request.getNouveauMotDePasse()
        );
        return ResponseEntity.ok(ApiResponse.success(null, "Mot de passe changé avec succès"));
    }
}
