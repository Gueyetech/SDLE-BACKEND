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
import sn.gtech.sgle.entity.enums.StatutEtudiantEnum;
import sn.gtech.sgle.exception.AuthException;
import sn.gtech.sgle.repository.*;
import sn.gtech.sgle.security.JwtTokenProvider;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final EtudiantRepository etudiantRepository;
    private final GestionnaireRepository gestionnaireRepository;
    private final AdministrateurRepository administrateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Inscription d'un nouvel utilisateur (principalement étudiant)
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

        Utilisateur utilisateur;

        switch (request.getRole()) {
            case ETUDIANT:
                utilisateur = inscrireEtudiant(request);
                break;
            case GESTIONNAIRE:
                utilisateur = inscrireGestionnaire(request);
                break;
            case ADMIN:
                throw new AuthException("L'inscription en tant qu'administrateur n'est pas autorisée");
            default:
                utilisateur = inscrireEtudiant(request);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(utilisateur.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(utilisateur.getEmail());

        return buildConnexionResponse(utilisateur, accessToken, refreshToken, "Inscription réussie");
    }

    /**
     * Inscription d'un étudiant
     */
    private Etudiant inscrireEtudiant(InscriptionRequest request) {
        // Vérification matricule unique si fourni
        if (request.getMatricule() != null && etudiantRepository.existsByMatricule(request.getMatricule())) {
            throw new AuthException("Ce matricule est déjà utilisé");
        }

        ContactUrgence contactUrgence = null;
        if (request.getNomContactUrgence() != null) {
            contactUrgence = ContactUrgence.builder()
                    .nomContact(request.getNomContactUrgence())
                    .prenomContact(request.getPrenomContactUrgence())
                    .relation(request.getRelationContactUrgence())
                    .telephoneContact(request.getTelephoneContactUrgence())
                    .emailContact(request.getEmailContactUrgence())
                    .build();
        }

        Etudiant etudiant = Etudiant.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(RoleEnum.ETUDIANT)
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .matricule(request.getMatricule())
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .dateNaissance(request.getDateNaissance())
                .telephone(request.getTelephone())
                .adresseOriginale(request.getAdresseOriginale())
                .universite(request.getUniversite())
                .niveauEtudes(request.getNiveauEtudes())
                .anneeAcademique(request.getAnneeAcademique())
                .contactUrgence(contactUrgence)
                .statut(StatutEtudiantEnum.EN_ATTENTE_VALIDATION)
                .dateInscription(LocalDateTime.now())
                .build();

        return etudiantRepository.save(etudiant);
    }

    /**
     * Inscription d'un gestionnaire (réservé aux admins)
     */
    private GestionnaireLogements inscrireGestionnaire(InscriptionRequest request) {
        GestionnaireLogements gestionnaire = GestionnaireLogements.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(RoleEnum.GESTIONNAIRE)
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .telephone(request.getTelephone())
                .departement(request.getDepartement())
                .build();

        return gestionnaireRepository.save(gestionnaire);
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
        ConnexionResponse.ConnexionResponseBuilder builder = ConnexionResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getJwtExpiration())
                .utilisateurId(utilisateur.getId())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .message(message);

        // Ajout des informations spécifiques selon le type
        if (utilisateur instanceof Etudiant etudiant) {
            builder.typeUtilisateur("ETUDIANT")
                    .nom(etudiant.getNom())
                    .prenom(etudiant.getPrenom())
                    .matricule(etudiant.getMatricule());
        } else if (utilisateur instanceof GestionnaireLogements gestionnaire) {
            builder.typeUtilisateur("GESTIONNAIRE")
                    .departement(gestionnaire.getDepartement());
        } else if (utilisateur instanceof Administrateur) {
            builder.typeUtilisateur("ADMINISTRATEUR");
        } else {
            builder.typeUtilisateur("UTILISATEUR");
        }

        return builder.build();
    }

    /**
     * Conversion en DTO
     */
    private UtilisateurDto toUtilisateurDto(Utilisateur utilisateur) {
        UtilisateurDto.UtilisateurDtoBuilder builder = UtilisateurDto.builder()
                .id(utilisateur.getId())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole())
                .actif(utilisateur.getActif())
                .dateCreation(utilisateur.getDateCreation())
                .derniereConnexion(utilisateur.getDerniereConnexion());

        if (utilisateur instanceof Etudiant etudiant) {
            builder.matricule(etudiant.getMatricule())
                    .nom(etudiant.getNom())
                    .prenom(etudiant.getPrenom())
                    .dateNaissance(etudiant.getDateNaissance())
                    .telephone(etudiant.getTelephone())
                    .adresseOriginale(etudiant.getAdresseOriginale())
                    .universite(etudiant.getUniversite())
                    .niveauEtudes(etudiant.getNiveauEtudes())
                    .anneeAcademique(etudiant.getAnneeAcademique())
                    .photoIdentite(etudiant.getPhotoIdentite())
                    .statutEtudiant(etudiant.getStatut());
        } else if (utilisateur instanceof GestionnaireLogements gestionnaire) {
            builder.telephone(gestionnaire.getTelephone())
                    .departement(gestionnaire.getDepartement());
        }

        return builder.build();
    }
}
