package sn.gtech.sgle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.gtech.sgle.dto.utilisateur.CreerUtilisateurRequest;
import sn.gtech.sgle.dto.utilisateur.ModifierUtilisateurRequest;
import sn.gtech.sgle.dto.utilisateur.ResetMotDePasseRequest;
import sn.gtech.sgle.dto.utilisateur.UtilisateurResponseDto;
import sn.gtech.sgle.entity.Utilisateur;
import sn.gtech.sgle.entity.enums.RoleEnum;
import sn.gtech.sgle.exception.AuthException;
import sn.gtech.sgle.repository.UtilisateurRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service pour la gestion des utilisateurs
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Crée un nouvel utilisateur
     */
    public UtilisateurResponseDto creerUtilisateur(CreerUtilisateurRequest request) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Un utilisateur avec cet email existe déjà");
        }
        
        Utilisateur utilisateur = Utilisateur.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(request.getRole())
                .actif(request.getActif() != null ? request.getActif() : true)
                .dateCreation(LocalDateTime.now())
                .build();
        
        utilisateur = utilisateurRepository.save(utilisateur);
        return UtilisateurResponseDto.fromEntity(utilisateur);
    }
    
    /**
     * Récupère tous les utilisateurs
     */
    @Transactional(readOnly = true)
    public List<UtilisateurResponseDto> getTousLesUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        
        return utilisateurs.stream()
                .map(UtilisateurResponseDto::fromEntity)
                .toList();
    }
    
    /**
     * Récupère un utilisateur par son ID
     */
    @Transactional(readOnly = true)
    public UtilisateurResponseDto getUtilisateurParId(UUID id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));
        return UtilisateurResponseDto.fromEntity(utilisateur);
    }
    
    /**
     * Récupère un utilisateur par son email
     */
    @Transactional(readOnly = true)
    public UtilisateurResponseDto getUtilisateurParEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));
        return UtilisateurResponseDto.fromEntity(utilisateur);
    }
    
    /**
     * Modifie un utilisateur existant
     */
    public UtilisateurResponseDto modifierUtilisateur(UUID id, ModifierUtilisateurRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));
        
        if (request.getEmail() != null && !request.getEmail().equals(utilisateur.getEmail())) {
            if (utilisateurRepository.existsByEmail(request.getEmail())) {
                throw new AuthException("Un utilisateur avec cet email existe déjà");
            }
            utilisateur.setEmail(request.getEmail());
        }
        
        if (request.getRole() != null) {
            utilisateur.setRole(request.getRole());
        }
        
        if (request.getActif() != null) {
            utilisateur.setActif(request.getActif());
        }
        
        utilisateur = utilisateurRepository.save(utilisateur);
        return UtilisateurResponseDto.fromEntity(utilisateur);
    }
    
    /**
     * Supprime un utilisateur
     */
    public void supprimerUtilisateur(UUID id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new AuthException("Utilisateur non trouvé");
        }
        utilisateurRepository.deleteById(id);
    }
    
    /**
     * Active un utilisateur
     */
    public UtilisateurResponseDto activerUtilisateur(UUID id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));
        utilisateur.setActif(true);
        utilisateur = utilisateurRepository.save(utilisateur);
        return UtilisateurResponseDto.fromEntity(utilisateur);
    }
    
    /**
     * Désactive un utilisateur
     */
    public UtilisateurResponseDto desactiverUtilisateur(UUID id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));
        utilisateur.setActif(false);
        utilisateur = utilisateurRepository.save(utilisateur);
        return UtilisateurResponseDto.fromEntity(utilisateur);
    }
    
    /**
     * Change le rôle d'un utilisateur
     */
    public UtilisateurResponseDto changerRole(UUID id, RoleEnum nouveauRole) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));
        utilisateur.setRole(nouveauRole);
        utilisateur = utilisateurRepository.save(utilisateur);
        return UtilisateurResponseDto.fromEntity(utilisateur);
    }
    
    /**
     * Réinitialise le mot de passe d'un utilisateur
     */
    public void reinitialiserMotDePasse(UUID id, String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));
        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(utilisateur);
    }
    
    /**
     * Réinitialise le mot de passe avec options (génération aléatoire, etc.)
     */
    public String resetMotDePasse(UUID id, ResetMotDePasseRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));
        
        String nouveauMotDePasse;
        
        if (request != null && request.getNouveauMotDePasse() != null) {
            nouveauMotDePasse = request.getNouveauMotDePasse();
        } else if (request != null && Boolean.TRUE.equals(request.getGenererMotDePasseAleatoire())) {
            nouveauMotDePasse = genererMotDePasseAleatoire();
        } else {
            nouveauMotDePasse = genererMotDePasseAleatoire();
        }
        
        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(utilisateur);
        
        return nouveauMotDePasse;
    }
    
    /**
     * Génère un mot de passe aléatoire sécurisé
     */
    private String genererMotDePasseAleatoire() {
        String majuscules = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String minuscules = "abcdefghijklmnopqrstuvwxyz";
        String chiffres = "0123456789";
        String speciaux = "@$!%*?&";
        String tousCaracteres = majuscules + minuscules + chiffres + speciaux;
        
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        // Assurer au moins un caractère de chaque type
        password.append(majuscules.charAt(random.nextInt(majuscules.length())));
        password.append(minuscules.charAt(random.nextInt(minuscules.length())));
        password.append(chiffres.charAt(random.nextInt(chiffres.length())));
        password.append(speciaux.charAt(random.nextInt(speciaux.length())));
        
        // Compléter avec des caractères aléatoires
        for (int i = 4; i < 12; i++) {
            password.append(tousCaracteres.charAt(random.nextInt(tousCaracteres.length())));
        }
        
        // Mélanger le mot de passe
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        return new String(passwordArray);
    }
    
    /**
     * Récupère les statistiques des utilisateurs
     */
    @Transactional(readOnly = true)
    public UtilisateurStatistiques getStatistiques() {
        long total = utilisateurRepository.count();
        long actifs = utilisateurRepository.countByActif(true);
        long inactifs = utilisateurRepository.countByActif(false);
        long admins = utilisateurRepository.countByRole(RoleEnum.ADMIN);
        long gestionnaires = utilisateurRepository.countByRole(RoleEnum.GESTIONNAIRE);
        long etudiants = utilisateurRepository.countByRole(RoleEnum.ETUDIANT);
        
        return new UtilisateurStatistiques(
                total,
                actifs,
                inactifs,
                admins,
                gestionnaires,
                etudiants
        );
    }
    
    /**
     * Record pour les statistiques des utilisateurs
     */
    public record UtilisateurStatistiques(
            long total,
            long actifs,
            long inactifs,
            long admins,
            long gestionnaires,
            long etudiants
    ) {}
}
