package sn.gtech.sgle.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.*;
import sn.gtech.sgle.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "API de gestion de l'authentification des utilisateurs")
public class AuthController {

    private final AuthService authService;

    /**
     * Inscription d'un nouvel utilisateur (étudiant par défaut)
     */
    @PostMapping("/inscription")
    public ResponseEntity<ConnexionResponse> inscription(@Valid @RequestBody InscriptionRequest request) {
        ConnexionResponse response = authService.inscrire(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Connexion d'un utilisateur
     */
    @PostMapping("/connexion")
    public ResponseEntity<ConnexionResponse> connexion(@Valid @RequestBody ConnexionRequest request) {
        ConnexionResponse response = authService.connecter(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Rafraîchissement du token
     */
    @PostMapping("/rafraichir")
    public ResponseEntity<ConnexionResponse> rafraichirToken(
            @Parameter(description = "Refresh token JWT") @RequestParam String refreshToken) {
        ConnexionResponse response = authService.rafraichirToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Validation du token et récupération des informations utilisateur
     */
    @GetMapping("/valider")
    public ResponseEntity<UtilisateurDto> validerToken(
            @Parameter(description = "Token JWT (Bearer)") @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UtilisateurDto utilisateur = authService.validerToken(token);
        return ResponseEntity.ok(utilisateur);
    }

    /**
     * Récupération du profil de l'utilisateur connecté
     */
    @GetMapping("/profil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UtilisateurDto> getProfil() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UtilisateurDto profil = authService.getProfil(email);
        return ResponseEntity.ok(profil);
    }

    /**
     * Changement de mot de passe
     */
    @PostMapping("/changer-mot-de-passe")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changerMotDePasse(
            @RequestParam String ancienMotDePasse,
            @RequestParam String nouveauMotDePasse) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        authService.changerMotDePasse(email, ancienMotDePasse, nouveauMotDePasse);
        return ResponseEntity.ok("Mot de passe modifié avec succès");
    }

    /**
     * Déconnexion (côté client, invalider le token)
     */
    @PostMapping("/deconnexion")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deconnexion() {
        // La déconnexion est gérée côté client en supprimant le token
        // On peut ajouter une blacklist de tokens si nécessaire
        return ResponseEntity.ok("Déconnexion réussie");
    }

    /**
     * Vérification de la santé du service
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service d'authentification opérationnel");
    }
}
