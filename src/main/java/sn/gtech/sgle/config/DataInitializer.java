package sn.gtech.sgle.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sn.gtech.sgle.entity.Administrateur;
import sn.gtech.sgle.entity.Etudiant;
import sn.gtech.sgle.entity.GestionnaireLogements;
import sn.gtech.sgle.entity.enums.NiveauEtudesEnum;
import sn.gtech.sgle.entity.enums.RoleEnum;
import sn.gtech.sgle.entity.enums.StatutEtudiantEnum;
import sn.gtech.sgle.repository.AdministrateurRepository;
import sn.gtech.sgle.repository.EtudiantRepository;
import sn.gtech.sgle.repository.GestionnaireRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AdministrateurRepository administrateurRepository;
    private final GestionnaireRepository gestionnaireRepository;
    private final EtudiantRepository etudiantRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initialisation des données de base...");
        
        // Créer un administrateur par défaut
        creerAdminParDefaut();
        
        // Créer un gestionnaire de test
        creerGestionnaireTest();
        
        // Créer un étudiant de test
        creerEtudiantTest();
        
        log.info("Initialisation des données terminée.");
    }

    private void creerAdminParDefaut() {
        String adminEmail = "admin@sgle.sn";
        
        if (!administrateurRepository.existsByEmail(adminEmail)) {
            Administrateur admin = Administrateur.builder()
                    .email(adminEmail)
                    .motDePasse(passwordEncoder.encode("Admin@123"))
                    .role(RoleEnum.ADMIN)
                    .actif(true)
                    .dateCreation(LocalDateTime.now())
                    .build();
            
            administrateurRepository.save(admin);
            log.info("Administrateur par défaut créé: {}", adminEmail);
        } else {
            log.info("Administrateur par défaut existe déjà: {}", adminEmail);
        }
    }

    private void creerGestionnaireTest() {
        String gestionnaireEmail = "gestionnaire@sgle.sn";
        
        if (!gestionnaireRepository.existsByEmail(gestionnaireEmail)) {
            GestionnaireLogements gestionnaire = GestionnaireLogements.builder()
                    .email(gestionnaireEmail)
                    .motDePasse(passwordEncoder.encode("Gestionnaire@123"))
                    .role(RoleEnum.GESTIONNAIRE)
                    .actif(true)
                    .dateCreation(LocalDateTime.now())
                    .telephone("+221 77 123 45 67")
                    .departement("Résidences Universitaires Dakar")
                    .build();
            
            gestionnaireRepository.save(gestionnaire);
            log.info("Gestionnaire de test créé: {}", gestionnaireEmail);
        } else {
            log.info("Gestionnaire de test existe déjà: {}", gestionnaireEmail);
        }
    }

    private void creerEtudiantTest() {
        String etudiantEmail = "etudiant@ucad.edu.sn";
        
        if (!etudiantRepository.existsByEmail(etudiantEmail)) {
            Etudiant etudiant = Etudiant.builder()
                    .email(etudiantEmail)
                    .motDePasse(passwordEncoder.encode("Etudiant@123"))
                    .role(RoleEnum.ETUDIANT)
                    .actif(true)
                    .dateCreation(LocalDateTime.now())
                    .matricule("ETU-2026-001")
                    .nom("Diallo")
                    .prenom("Mamadou")
                    .dateNaissance(LocalDate.of(2000, 5, 15))
                    .telephone("+221 76 987 65 43")
                    .adresseOriginale("Thiès, Sénégal")
                    .universite("Université Cheikh Anta Diop")
                    .niveauEtudes(NiveauEtudesEnum.MASTER_1)
                    .anneeAcademique("2025-2026")
                    .statut(StatutEtudiantEnum.ACTIF)
                    .dateInscription(LocalDateTime.now())
                    .build();
            
            etudiantRepository.save(etudiant);
            log.info("Étudiant de test créé: {}", etudiantEmail);
        } else {
            log.info("Étudiant de test existe déjà: {}", etudiantEmail);
        }
    }
}
