package sn.gtech.sgle.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sn.gtech.sgle.entity.Utilisateur;
import sn.gtech.sgle.entity.enums.RoleEnum;
import sn.gtech.sgle.repository.UtilisateurRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initialisation des données de base...");
        
        // Créer un administrateur par défaut
        creerUtilisateur("admin@sgle.sn", "Admin@123", RoleEnum.ADMIN);
        
        // Créer un gestionnaire de test
        creerUtilisateur("gestionnaire@sgle.sn", "Gestionnaire@123", RoleEnum.GESTIONNAIRE);
        
        // Créer un étudiant de test
        creerUtilisateur("etudiant@ucad.edu.sn", "Etudiant@123", RoleEnum.ETUDIANT);
        
        log.info("Initialisation des données terminée.");
    }

    private void creerUtilisateur(String email, String motDePasse, RoleEnum role) {
        if (!utilisateurRepository.existsByEmail(email)) {
            Utilisateur utilisateur = Utilisateur.builder()
                    .email(email)
                    .motDePasse(passwordEncoder.encode(motDePasse))
                    .role(role)
                    .actif(true)
                    .dateCreation(LocalDateTime.now())
                    .build();
            
            utilisateurRepository.save(utilisateur);
            log.info("Utilisateur créé: {} ({})", email, role);
        } else {
            log.info("Utilisateur existe déjà: {}", email);
        }
    }
}
