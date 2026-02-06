package sn.gtech.sgle.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sn.gtech.sgle.entity.*;
import sn.gtech.sgle.entity.enums.*;
import sn.gtech.sgle.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final LogementRepository logementRepository;
    private final DemandeLogementRepository demandeLogementRepository;
    private final AttributionRepository attributionRepository;
    private final PaiementRepository paiementRepository;
    private final IncidentRepository incidentRepository;
    private final PasswordEncoder passwordEncoder;

    // Stocker les utilisateurs créés pour les réutiliser
    private final List<Utilisateur> etudiants = new ArrayList<>();
    private final List<Utilisateur> gestionnaires = new ArrayList<>();
    private final List<Logement> logements = new ArrayList<>();

    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("Initialisation des données de test...");
        log.info("========================================");

        creerUtilisateurs();
        creerLogements();
        creerDemandesLogement();
        creerAttributions();
        creerPaiements();
        creerIncidents();

        log.info("========================================");
        log.info("Initialisation des données terminée!");
        log.info("========================================");
        afficherResume();
    }

    private void creerUtilisateurs() {
        log.info("--- Création des utilisateurs ---");

        // Administrateurs
        creerUtilisateur("admin@sgle.sn", "Admin@123", RoleEnum.ADMIN);
        creerUtilisateur("superadmin@sgle.sn", "Admin@123", RoleEnum.ADMIN);

        // Gestionnaires
        gestionnaires.add(creerUtilisateur("gestionnaire1@sgle.sn", "Gestionnaire@123", RoleEnum.GESTIONNAIRE));
        gestionnaires.add(creerUtilisateur("gestionnaire2@sgle.sn", "Gestionnaire@123", RoleEnum.GESTIONNAIRE));
        gestionnaires.add(creerUtilisateur("moussa.diop@sgle.sn", "Gestionnaire@123", RoleEnum.GESTIONNAIRE));

        // Étudiants
        etudiants.add(creerUtilisateur("aminata.fall@ucad.edu.sn", "Etudiant@123", RoleEnum.ETUDIANT));
        etudiants.add(creerUtilisateur("ousmane.ndiaye@ucad.edu.sn", "Etudiant@123", RoleEnum.ETUDIANT));
        etudiants.add(creerUtilisateur("fatou.diallo@ucad.edu.sn", "Etudiant@123", RoleEnum.ETUDIANT));
        etudiants.add(creerUtilisateur("ibrahima.sow@ucad.edu.sn", "Etudiant@123", RoleEnum.ETUDIANT));
        etudiants.add(creerUtilisateur("mariama.ba@ucad.edu.sn", "Etudiant@123", RoleEnum.ETUDIANT));
        etudiants.add(creerUtilisateur("modou.dieng@ucad.edu.sn", "Etudiant@123", RoleEnum.ETUDIANT));
        etudiants.add(creerUtilisateur("aissatou.sy@ucad.edu.sn", "Etudiant@123", RoleEnum.ETUDIANT));
        etudiants.add(creerUtilisateur("cheikh.mbaye@ucad.edu.sn", "Etudiant@123", RoleEnum.ETUDIANT));
        etudiants.add(creerUtilisateur("ndeye.gueye@ucad.edu.sn", "Etudiant@123", RoleEnum.ETUDIANT));
        etudiants.add(creerUtilisateur("papa.sarr@ucad.edu.sn", "Etudiant@123", RoleEnum.ETUDIANT));

        // Invités
        creerUtilisateur("invite@sgle.sn", "Invite@123", RoleEnum.INVITE);

        log.info("{} utilisateurs créés", utilisateurRepository.count());
    }

    private void creerLogements() {
        log.info("--- Création des logements ---");

        Utilisateur gestionnaire1 = gestionnaires.isEmpty() ? null : gestionnaires.get(0);
        Utilisateur gestionnaire2 = gestionnaires.size() > 1 ? gestionnaires.get(1) : gestionnaire1;

        // Chambres universitaires
        logements.add(creerLogement("CU-001", TypeLogementEnum.CHAMBRE_UNIVERSITAIRE,
                creerAdresse("Cité Universitaire Claudel", "A101", "10000", "Dakar", "Point E", "Dakar"),
                1, 12f, new BigDecimal("25000"), "Chambre universitaire simple avec bureau intégré",
                StatutLogementEnum.DISPONIBLE, false, gestionnaire1));

        logements.add(creerLogement("CU-002", TypeLogementEnum.CHAMBRE_UNIVERSITAIRE,
                creerAdresse("Cité Universitaire Claudel", "A102", "10000", "Dakar", "Point E", "Dakar"),
                1, 12f, new BigDecimal("25000"), "Chambre universitaire vue sur jardin",
                StatutLogementEnum.OCCUPE, false, gestionnaire1));

        logements.add(creerLogement("CU-003", TypeLogementEnum.CHAMBRE_UNIVERSITAIRE,
                creerAdresse("Cité Universitaire Claudel", "B201", "10000", "Dakar", "Point E", "Dakar"),
                1, 14f, new BigDecimal("30000"), "Chambre universitaire avec salle de bain privée",
                StatutLogementEnum.RESERVE, false, gestionnaire1));

        // Studios
        logements.add(creerLogement("ST-001", TypeLogementEnum.STUDIO,
                creerAdresse("Résidence Les Palmiers", "12", "10200", "Dakar", "Fann", "Dakar"),
                1, 20f, new BigDecimal("75000"), "Studio meublé proche UCAD",
                StatutLogementEnum.DISPONIBLE, true, gestionnaire2));

        logements.add(creerLogement("ST-002", TypeLogementEnum.STUDIO,
                creerAdresse("Résidence Les Palmiers", "15", "10200", "Dakar", "Fann", "Dakar"),
                1, 22f, new BigDecimal("80000"), "Studio moderne avec balcon",
                StatutLogementEnum.OCCUPE, true, gestionnaire2));

        logements.add(creerLogement("ST-003", TypeLogementEnum.STUDIO,
                creerAdresse("Avenue Cheikh Anta Diop", "45B", "10100", "Dakar", "Mermoz", "Dakar"),
                1, 25f, new BigDecimal("90000"), "Grand studio lumineux",
                StatutLogementEnum.EN_MAINTENANCE, true, gestionnaire2));

        // T1
        logements.add(creerLogement("T1-001", TypeLogementEnum.T1,
                creerAdresse("Rue 10", "23", "10300", "Dakar", "Sicap Liberté", "Dakar"),
                2, 35f, new BigDecimal("120000"), "T1 spacieux avec cuisine équipée",
                StatutLogementEnum.DISPONIBLE, true, gestionnaire1));

        logements.add(creerLogement("T1-002", TypeLogementEnum.T1,
                creerAdresse("Boulevard du Centenaire", "78", "10400", "Dakar", "Grand Dakar", "Dakar"),
                2, 32f, new BigDecimal("100000"), "T1 refait à neuf",
                StatutLogementEnum.OCCUPE, true, gestionnaire1));

        // T2
        logements.add(creerLogement("T2-001", TypeLogementEnum.T2,
                creerAdresse("Rue Parchappe", "56", "10100", "Dakar", "Plateau", "Dakar"),
                3, 50f, new BigDecimal("180000"), "T2 centre-ville, idéal colocation",
                StatutLogementEnum.DISPONIBLE, true, gestionnaire2));

        // Résidences étudiantes
        logements.add(creerLogement("RE-001", TypeLogementEnum.RESIDENCE_ETUDIANTE,
                creerAdresse("Campus UCAD", "Bâtiment C", "10000", "Dakar", "UCAD", "Dakar"),
                1, 15f, new BigDecimal("35000"), "Chambre en résidence avec services inclus",
                StatutLogementEnum.DISPONIBLE, true, gestionnaire1));

        logements.add(creerLogement("RE-002", TypeLogementEnum.RESIDENCE_ETUDIANTE,
                creerAdresse("Campus UCAD", "Bâtiment C", "10000", "Dakar", "UCAD", "Dakar"),
                1, 15f, new BigDecimal("35000"), "Chambre en résidence calme",
                StatutLogementEnum.OCCUPE, true, gestionnaire1));

        // Appartements partagés
        logements.add(creerLogement("AP-001", TypeLogementEnum.APPARTEMENT_PARTAGE,
                creerAdresse("Avenue Bourguiba", "112", "10200", "Dakar", "Fann Hock", "Dakar"),
                4, 80f, new BigDecimal("60000"), "Chambre dans appartement partagé 4 personnes",
                StatutLogementEnum.DISPONIBLE, true, gestionnaire2));

        // Logement archivé
        logements.add(creerLogement("ARC-001", TypeLogementEnum.STUDIO,
                creerAdresse("Ancienne Rue", "1", "10000", "Dakar", "Médina", "Dakar"),
                1, 18f, new BigDecimal("50000"), "Ancien studio (archivé)",
                StatutLogementEnum.ARCHIVE, false, gestionnaire1));

        log.info("{} logements créés", logementRepository.count());
    }

    private void creerDemandesLogement() {
        log.info("--- Création des demandes de logement ---");

        if (etudiants.isEmpty())
            return;

        Utilisateur gestionnaire = gestionnaires.isEmpty() ? null : gestionnaires.get(0);

        // Demandes en attente
        creerDemande(etudiants.get(6), TypeLogementEnum.STUDIO, new BigDecimal("80000"),
                LocalDate.now().plusMonths(1), 10, "Proche campus",
                StatutDemandeEnum.EN_ATTENTE, PrioriteEnum.NORMALE, null);

        creerDemande(etudiants.get(7), TypeLogementEnum.CHAMBRE_UNIVERSITAIRE, new BigDecimal("30000"),
                LocalDate.now().plusWeeks(2), 12, "Calme pour études",
                StatutDemandeEnum.EN_ATTENTE, PrioriteEnum.HAUTE, null);

        // Demandes en cours de traitement
        creerDemande(etudiants.get(8), TypeLogementEnum.T1, new BigDecimal("130000"),
                LocalDate.now().plusMonths(2), 12, "Avec parking si possible",
                StatutDemandeEnum.EN_COURS_TRAITEMENT, PrioriteEnum.NORMALE, gestionnaire);

        // Demandes approuvées
        creerDemande(etudiants.get(0), TypeLogementEnum.CHAMBRE_UNIVERSITAIRE, new BigDecimal("30000"),
                LocalDate.now().minusMonths(6), 12, "Premier choix: Cité Claudel",
                StatutDemandeEnum.APPROUVEE, PrioriteEnum.NORMALE, gestionnaire);

        creerDemande(etudiants.get(1), TypeLogementEnum.STUDIO, new BigDecimal("85000"),
                LocalDate.now().minusMonths(4), 10, "Meublé de préférence",
                StatutDemandeEnum.APPROUVEE, PrioriteEnum.HAUTE, gestionnaire);

        // Demandes rejetées
        creerDemande(etudiants.get(9), TypeLogementEnum.T2, new BigDecimal("100000"),
                LocalDate.now().minusMonths(1), 12, "Budget insuffisant pour T2",
                StatutDemandeEnum.REJETEE, PrioriteEnum.NORMALE, gestionnaire);

        // Demande annulée
        creerDemande(etudiants.get(5), TypeLogementEnum.RESIDENCE_ETUDIANTE, new BigDecimal("40000"),
                LocalDate.now().minusWeeks(3), 6, "Annulé - a trouvé ailleurs",
                StatutDemandeEnum.ANNULEE, PrioriteEnum.BASSE, null);

        log.info("{} demandes créées", demandeLogementRepository.count());
    }

    private void creerAttributions() {
        log.info("--- Création des attributions ---");

        if (etudiants.isEmpty() || logements.isEmpty())
            return;

        Utilisateur gestionnaire = gestionnaires.isEmpty() ? null : gestionnaires.get(0);

        // Trouver les logements occupés
        Logement logementOccupe1 = logements.stream()
                .filter(l -> l.getCode().equals("CU-002"))
                .findFirst().orElse(null);
        Logement logementOccupe2 = logements.stream()
                .filter(l -> l.getCode().equals("ST-002"))
                .findFirst().orElse(null);
        Logement logementOccupe3 = logements.stream()
                .filter(l -> l.getCode().equals("T1-002"))
                .findFirst().orElse(null);
        Logement logementOccupe4 = logements.stream()
                .filter(l -> l.getCode().equals("RE-002"))
                .findFirst().orElse(null);

        // Attribution active - étudiant 1
        if (logementOccupe1 != null && etudiants.size() > 0) {
            creerAttribution("CONT-2025-001", etudiants.get(0), logementOccupe1,
                    LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(6),
                    logementOccupe1.getPrixMensuel(), new BigDecimal("50000"),
                    StatutAttributionEnum.ACTIVE, gestionnaire,
                    LocalDateTime.now().minusMonths(6), null);
        }

        // Attribution active - étudiant 2
        if (logementOccupe2 != null && etudiants.size() > 1) {
            creerAttribution("CONT-2025-002", etudiants.get(1), logementOccupe2,
                    LocalDate.now().minusMonths(4), LocalDate.now().plusMonths(6),
                    logementOccupe2.getPrixMensuel(), new BigDecimal("160000"),
                    StatutAttributionEnum.ACTIVE, gestionnaire,
                    LocalDateTime.now().minusMonths(4), null);
        }

        // Attribution active - étudiant 3
        if (logementOccupe3 != null && etudiants.size() > 2) {
            creerAttribution("CONT-2025-003", etudiants.get(2), logementOccupe3,
                    LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(9),
                    logementOccupe3.getPrixMensuel(), new BigDecimal("200000"),
                    StatutAttributionEnum.ACTIVE, gestionnaire,
                    LocalDateTime.now().minusMonths(3), null);
        }

        // Attribution active - étudiant 4
        if (logementOccupe4 != null && etudiants.size() > 3) {
            creerAttribution("CONT-2025-004", etudiants.get(3), logementOccupe4,
                    LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(10),
                    logementOccupe4.getPrixMensuel(), new BigDecimal("70000"),
                    StatutAttributionEnum.ACTIVE, gestionnaire,
                    LocalDateTime.now().minusMonths(2), null);
        }

        // Attribution expirée
        if (etudiants.size() > 4) {
            Logement logementDispo = logements.stream()
                    .filter(l -> l.getStatut() == StatutLogementEnum.DISPONIBLE)
                    .findFirst().orElse(logements.get(0));

            creerAttribution("CONT-2024-010", etudiants.get(4), logementDispo,
                    LocalDate.now().minusYears(1), LocalDate.now().minusMonths(1),
                    logementDispo.getPrixMensuel(), new BigDecimal("50000"),
                    StatutAttributionEnum.EXPIREE, gestionnaire,
                    LocalDateTime.now().minusYears(1), LocalDateTime.now().minusMonths(1));
        }

        log.info("{} attributions créées", attributionRepository.count());
    }

    private void creerPaiements() {
        log.info("--- Création des paiements ---");

        List<Attribution> attributions = attributionRepository.findAll();

        for (Attribution attribution : attributions) {
            if (attribution.getStatut() == StatutAttributionEnum.ACTIVE) {
                creerPaiementsPourAttribution(attribution);
            } else if (attribution.getStatut() == StatutAttributionEnum.EXPIREE) {
                creerPaiementsHistoriques(attribution);
            }
        }

        log.info("{} paiements créés", paiementRepository.count());
    }

    private void creerPaiementsPourAttribution(Attribution attribution) {
        LocalDate debut = attribution.getDateDebut();
        LocalDate maintenant = LocalDate.now();
        int compteur = 1;

        // Créer les paiements pour les mois passés et le mois courant
        LocalDate moisCourant = debut.withDayOfMonth(1);

        while (!moisCourant.isAfter(maintenant) && compteur <= 12) {
            String mois = moisCourant.getMonth().toString() + " " + moisCourant.getYear();
            LocalDate echeance = moisCourant.withDayOfMonth(5);

            StatutPaiementEnum statut;
            LocalDateTime datePaiement = null;
            ModePaiementEnum mode = null;

            if (moisCourant.isBefore(maintenant.withDayOfMonth(1))) {
                // Mois passé - payé ou en retard
                if (compteur % 4 == 0) {
                    statut = StatutPaiementEnum.EN_RETARD;
                } else {
                    statut = StatutPaiementEnum.PAYE;
                    datePaiement = moisCourant.withDayOfMonth(3 + compteur % 5).atTime(10, 30);
                    mode = compteur % 2 == 0 ? ModePaiementEnum.MOBILE_MONEY : ModePaiementEnum.VIREMENT_BANCAIRE;
                }
            } else {
                // Mois courant - en attente
                statut = StatutPaiementEnum.EN_ATTENTE;
            }

            creerPaiement(
                    "PAY-" + attribution.getNumeroContrat().substring(5) + "-" + String.format("%03d", compteur),
                    attribution,
                    attribution.getMontantLoyer(),
                    datePaiement,
                    echeance,
                    mois,
                    mode,
                    statut);

            moisCourant = moisCourant.plusMonths(1);
            compteur++;
        }
    }

    private void creerPaiementsHistoriques(Attribution attribution) {
        // Créer quelques paiements historiques pour les attributions expirées
        for (int i = 1; i <= 6; i++) {
            LocalDate moisPaiement = attribution.getDateDebut().plusMonths(i - 1);
            String mois = moisPaiement.getMonth().toString() + " " + moisPaiement.getYear();

            creerPaiement(
                    "PAY-" + attribution.getNumeroContrat().substring(5) + "-" + String.format("%03d", i),
                    attribution,
                    attribution.getMontantLoyer(),
                    moisPaiement.withDayOfMonth(5).atTime(14, 0),
                    moisPaiement.withDayOfMonth(5),
                    mois,
                    i % 2 == 0 ? ModePaiementEnum.ESPECES : ModePaiementEnum.CARTE_BANCAIRE,
                    StatutPaiementEnum.PAYE);
        }
    }

    private void creerIncidents() {
        log.info("--- Création des incidents ---");

        if (etudiants.isEmpty() || logements.isEmpty())
            return;

        Utilisateur gestionnaire = gestionnaires.isEmpty() ? null : gestionnaires.get(0);

        // Trouver des logements occupés
        Logement logement1 = logements.stream()
                .filter(l -> l.getStatut() == StatutLogementEnum.OCCUPE)
                .findFirst().orElse(logements.get(0));
        Logement logement2 = logements.stream()
                .filter(l -> l.getStatut() == StatutLogementEnum.EN_MAINTENANCE)
                .findFirst().orElse(logements.get(0));

        // Incident ouvert - plomberie
        creerIncident("INC-2026-001", etudiants.get(0), logement1,
                TypeIncidentEnum.PLOMBERIE, "Fuite d'eau au niveau du robinet de la salle de bain",
                UrgenceEnum.HAUTE, StatutIncidentEnum.OUVERT, gestionnaire, null, null);

        // Incident en cours - électricité
        creerIncident("INC-2026-002", etudiants.get(1), logement1,
                TypeIncidentEnum.ELECTRICITE, "Prise électrique qui fait des étincelles",
                UrgenceEnum.CRITIQUE, StatutIncidentEnum.EN_COURS, gestionnaire, "Technicien Diallo", null);

        // Incident en cours - chauffage
        creerIncident("INC-2026-003", etudiants.get(2), logement2,
                TypeIncidentEnum.CHAUFFAGE, "Le climatiseur ne fonctionne plus",
                UrgenceEnum.MOYENNE, StatutIncidentEnum.EN_COURS, gestionnaire, "Technicien Mbaye", null);

        // Incident résolu
        creerIncident("INC-2025-050", etudiants.get(0), logement1,
                TypeIncidentEnum.SERRURERIE, "Porte d'entrée difficile à fermer",
                UrgenceEnum.MOYENNE, StatutIncidentEnum.RESOLU, gestionnaire, "Technicien Seck",
                "Serrure remplacée le 15/01/2026");

        // Incident résolu - équipement
        creerIncident("INC-2025-048", etudiants.get(1), logement1,
                TypeIncidentEnum.EQUIPEMENT_DEFECTUEUX, "Réfrigérateur qui fait du bruit",
                UrgenceEnum.FAIBLE, StatutIncidentEnum.RESOLU, gestionnaire, "Service après-vente",
                "Compresseur réparé");

        // Incident fermé
        creerIncident("INC-2025-030", etudiants.get(3), logement2,
                TypeIncidentEnum.NETTOYAGE, "Demande de désinfection des parties communes",
                UrgenceEnum.FAIBLE, StatutIncidentEnum.FERME, gestionnaire, null,
                "Nettoyage effectué par l'équipe d'entretien");

        log.info("{} incidents créés", incidentRepository.count());
    }

    // ================== Méthodes utilitaires ==================

    private Utilisateur creerUtilisateur(String email, String motDePasse, RoleEnum role) {
        if (!utilisateurRepository.existsByEmail(email)) {
            Utilisateur utilisateur = Utilisateur.builder()
                    .email(email)
                    .motDePasse(passwordEncoder.encode(motDePasse))
                    .role(role)
                    .actif(true)
                    .dateCreation(LocalDateTime.now().minusDays((long) (Math.random() * 365)))
                    .build();

            utilisateur = utilisateurRepository.save(utilisateur);
            log.debug("Utilisateur créé: {} ({})", email, role);
            return utilisateur;
        } else {
            log.debug("Utilisateur existe déjà: {}", email);
            return utilisateurRepository.findByEmail(email).orElse(null);
        }
    }

    private Adresse creerAdresse(String rue, String numero, String codePostal,
            String ville, String quartier, String region) {
        return Adresse.builder()
                .rue(rue)
                .numero(numero)
                .codePostal(codePostal)
                .ville(ville)
                .quartier(quartier)
                .region(region)
                .pays("Sénégal")
                .build();
    }

    private Logement creerLogement(String code, TypeLogementEnum type, Adresse adresse,
            Integer capacite, Float superficie, BigDecimal prixMensuel,
            String description, StatutLogementEnum statut,
            Boolean meuble, Utilisateur gestionnaire) {
        if (!logementRepository.existsByCode(code)) {
            Logement logement = Logement.builder()
                    .code(code)
                    .type(type)
                    .adresse(adresse)
                    .capacite(capacite)
                    .superficie(superficie)
                    .prixMensuel(prixMensuel)
                    .description(description)
                    .statut(statut)
                    .meuble(meuble)
                    .gestionnaire(gestionnaire)
                    .dateCreation(LocalDateTime.now().minusDays((long) (Math.random() * 180)))
                    .build();

            logement = logementRepository.save(logement);
            log.debug("Logement créé: {} - {}", code, type);
            return logement;
        }
        return logementRepository.findByCode(code).orElse(null);
    }

    private void creerDemande(Utilisateur etudiant, TypeLogementEnum type, BigDecimal budget,
            LocalDate dateDebut, Integer duree, String preferences,
            StatutDemandeEnum statut, PrioriteEnum priorite,
            Utilisateur gestionnaire) {
        DemandeLogement demande = DemandeLogement.builder()
                .numeroReference("DEM-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000))
                .etudiant(etudiant)
                .typeLogementSouhaite(type)
                .budgetMaximum(budget)
                .dateDebutSouhaitee(dateDebut)
                .dureeSouhaitee(duree)
                .preferences(preferences)
                .statut(statut)
                .priorite(priorite)
                .gestionnaire(gestionnaire)
                .dateDemande(LocalDateTime.now().minusDays((long) (Math.random() * 60)))
                .dateTraitement(statut != StatutDemandeEnum.EN_ATTENTE
                        ? LocalDateTime.now().minusDays((long) (Math.random() * 30))
                        : null)
                .build();

        demandeLogementRepository.save(demande);
        log.debug("Demande créée: {} - {}", demande.getNumeroReference(), statut);
    }

    private void creerAttribution(String numeroContrat, Utilisateur etudiant, Logement logement,
            LocalDate dateDebut, LocalDate dateFin, BigDecimal montantLoyer,
            BigDecimal montantCaution, StatutAttributionEnum statut,
            Utilisateur gestionnaire, LocalDateTime dateCheckIn,
            LocalDateTime dateCheckOut) {
        if (!attributionRepository.existsByNumeroContrat(numeroContrat)) {
            Attribution attribution = Attribution.builder()
                    .numeroContrat(numeroContrat)
                    .etudiant(etudiant)
                    .logement(logement)
                    .dateDebut(dateDebut)
                    .dateFin(dateFin)
                    .montantLoyer(montantLoyer)
                    .montantCaution(montantCaution)
                    .statut(statut)
                    .gestionnaire(gestionnaire)
                    .dateCheckIn(dateCheckIn)
                    .dateCheckOut(dateCheckOut)
                    .dateAttribution(LocalDateTime.now().minusDays((long) (Math.random() * 180)))
                    .commentaires("Attribution créée pour les tests")
                    .build();

            attributionRepository.save(attribution);
            log.debug("Attribution créée: {} - {} -> {}", numeroContrat, etudiant.getEmail(), logement.getCode());
        }
    }

    private void creerPaiement(String numeroPaiement, Attribution attribution,
            BigDecimal montant, LocalDateTime datePaiement,
            LocalDate dateEcheance, String moisConcerne,
            ModePaiementEnum mode, StatutPaiementEnum statut) {
        if (!paiementRepository.existsByNumeroPaiement(numeroPaiement)) {
            Paiement paiement = Paiement.builder()
                    .numeroPaiement(numeroPaiement)
                    .attribution(attribution)
                    .montant(montant)
                    .datePaiement(datePaiement)
                    .dateEcheance(dateEcheance)
                    .moisConcerne(moisConcerne)
                    .modePaiement(mode)
                    .statut(statut)
                    .build();

            paiementRepository.save(paiement);
            log.debug("Paiement créé: {} - {}", numeroPaiement, statut);
        }
    }

    private void creerIncident(String numeroTicket, Utilisateur etudiant, Logement logement,
            TypeIncidentEnum type, String description, UrgenceEnum urgence,
            StatutIncidentEnum statut, Utilisateur gestionnaire,
            String technicien, String commentaireResolution) {
        if (!incidentRepository.existsByNumeroTicket(numeroTicket)) {
            Incident incident = Incident.builder()
                    .numeroTicket(numeroTicket)
                    .etudiant(etudiant)
                    .logement(logement)
                    .type(type)
                    .description(description)
                    .urgence(urgence)
                    .statut(statut)
                    .gestionnaire(gestionnaire)
                    .technicien(technicien)
                    .commentaireResolution(commentaireResolution)
                    .dateSignalement(LocalDateTime.now().minusDays((long) (Math.random() * 30)))
                    .dateResolution(statut == StatutIncidentEnum.RESOLU || statut == StatutIncidentEnum.FERME
                            ? LocalDateTime.now().minusDays((long) (Math.random() * 10))
                            : null)
                    .build();

            incidentRepository.save(incident);
            log.debug("Incident créé: {} - {}", numeroTicket, type);
        }
    }

    private void afficherResume() {
        log.info("");
        log.info("╔════════════════════════════════════════╗");
        log.info("║       RÉSUMÉ DES DONNÉES DE TEST       ║");
        log.info("╠════════════════════════════════════════╣");
        log.info("║ Utilisateurs: {:>24} ║", utilisateurRepository.count());
        log.info("║ Logements: {:>27} ║", logementRepository.count());
        log.info("║ Demandes: {:>28} ║", demandeLogementRepository.count());
        log.info("║ Attributions: {:>24} ║", attributionRepository.count());
        log.info("║ Paiements: {:>27} ║", paiementRepository.count());
        log.info("║ Incidents: {:>27} ║", incidentRepository.count());
        log.info("╚════════════════════════════════════════╝");
        log.info("");
        log.info("Comptes de test disponibles:");
        log.info("  Admin:        admin@sgle.sn / Admin@123");
        log.info("  Gestionnaire: gestionnaire1@sgle.sn / Gestionnaire@123");
        log.info("  Étudiant:     aminata.fall@ucad.edu.sn / Etudiant@123");
    }
}
