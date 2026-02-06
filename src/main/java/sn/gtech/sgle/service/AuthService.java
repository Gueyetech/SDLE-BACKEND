package sn.gtech.sgle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.gtech.sgle.dto.*;
import sn.gtech.sgle.entity.*;
import sn.gtech.sgle.entity.enums.RoleEnum;
import sn.gtech.sgle.exception.AuthException;
import sn.gtech.sgle.repository.*;
import sn.gtech.sgle.security.JwtTokenProvider;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Inscription d'un nouvel utilisateur
     */
    @Transactional
    public ConnexionResponse inscrire(InscriptionRequest request) {
        // Vérification email unique
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Cet email est déjà utilisé");
        }

        // Vérification mot de passe
        if (!request.getMotDePasse().equals(request.getConfirmationMotDePasse())) {
            throw new AuthException("Les mots de passe ne correspondent pas");
        }

        if (request.getRole() == RoleEnum.ADMIN) {
            throw new AuthException("L'inscription en tant qu'administrateur n'est pas autorisée");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(request.getRole() != null ? request.getRole() : RoleEnum.ETUDIANT)
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .build();

        utilisateur = utilisateurRepository.save(utilisateur);

        String accessToken = jwtTokenProvider.generateAccessToken(utilisateur.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(utilisateur.getEmail());

        return buildConnexionResponse(utilisateur, accessToken, refreshToken, "Inscription réussie");
    }

    /**
     * Connexion d'un utilisateur
     */
    public ConnexionResponse connecter(ConnexionRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));

        if (!utilisateur.getActif()) {
            throw new AuthException("Compte désactivé. Veuillez contacter l'administrateur.");
        }

        // Mise à jour dernière connexion
        utilisateur.setDerniereConnexion(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        return buildConnexionResponse(utilisateur, accessToken, refreshToken, "Connexion réussie");
    }

    /**
     * Rafraîchissement du token
     */
    public ConnexionResponse rafraichirToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthException("Token de rafraîchissement invalide");
        }

        String email = jwtTokenProvider.getUsernameFromToken(refreshToken);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(email);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

        return buildConnexionResponse(utilisateur, newAccessToken, newRefreshToken, "Token rafraîchi");
    }

    /**
     * Validation du token et récupération des informations utilisateur
     */
    public UtilisateurDto validerToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new AuthException("Token invalide");
        }

        String email = jwtTokenProvider.getUsernameFromToken(token);
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));

        return toUtilisateurDto(utilisateur);
    }

    /**
     * Récupération du profil utilisateur
     */
    public UtilisateurDto getProfil(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));
        return toUtilisateurDto(utilisateur);
    }

    /**
     * Changement de mot de passe
     */
    @Transactional
    public void changerMotDePasse(String email, String ancienMotDePasse, String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(ancienMotDePasse, utilisateur.getMotDePasse())) {
            throw new AuthException("Ancien mot de passe incorrect");
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(utilisateur);
    }

    /**
     * Construction de la réponse de connexion
     */
    private ConnexionResponse buildConnexionResponse(Utilisateur utilisateur, String accessToken, 
                                                      String refreshToken, String message) {
        return ConnexionResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getJwtExpiration())
                .utilisateurId(utilisateur.getId())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .message(message)
                .typeUtilisateur(utilisateur.getRole().name())
                .build();
    }

    /**
     * Conversion en DTO
     */
    private UtilisateurDto toUtilisateurDto(Utilisateur utilisateur) {
        return UtilisateurDto.builder()
                .id(utilisateur.getId())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .actif(utilisateur.getActif())
                .dateCreation(utilisateur.getDateCreation())
                .derniereConnexion(utilisateur.getDerniereConnexion())
                .build();
    }
}
