package sn.gtech.sgle.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.gtech.sgle.dto.paiement.EnregistrerPaiementRequest;
import sn.gtech.sgle.dto.paiement.PaiementResponseDto;
import sn.gtech.sgle.entity.*;
import sn.gtech.sgle.entity.enums.StatutPaiementEnum;
import sn.gtech.sgle.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final AttributionRepository attributionRepository;

    // Taux de pénalité par jour de retard (ex: 0.5%)
    private static final BigDecimal TAUX_PENALITE_JOUR = new BigDecimal("0.005");

    /**
     * Enregistrer un nouveau paiement (prévu ou effectué)
     */
    public PaiementResponseDto enregistrerPaiement(EnregistrerPaiementRequest request) {
        log.info("Enregistrement d'un paiement pour l'attribution: {}", request.getAttributionId());

        Attribution attribution = attributionRepository.findById(request.getAttributionId())
                .orElseThrow(() -> new EntityNotFoundException("Attribution non trouvée: " + request.getAttributionId()));

        Paiement paiement = Paiement.builder()
                .numeroPaiement(genererNumeroPaiement())
                .attribution(attribution)
                .montant(request.getMontant())
                .modePaiement(request.getModePaiement())
                .dateEcheance(request.getDateEcheance())
                .moisConcerne(request.getMoisConcerne())
                .referenceBancaire(request.getReferenceBancaire())
                .statut(StatutPaiementEnum.EN_ATTENTE)
                .build();

        Paiement saved = paiementRepository.save(paiement);
        log.info("Paiement enregistré: {} - Numéro: {}", saved.getId(), saved.getNumeroPaiement());

        return mapToResponse(saved);
    }

    /**
     * Marquer un paiement comme payé
     */
    public PaiementResponseDto marquerCommePaye(UUID id, String referenceBancaire) {
        log.info("Marquage du paiement {} comme payé", id);

        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paiement non trouvé: " + id));

        if (paiement.getStatut() == StatutPaiementEnum.PAYE) {
            throw new IllegalStateException("Ce paiement a déjà été réglé");
        }

        paiement.setStatut(StatutPaiementEnum.PAYE);
        paiement.setDatePaiement(LocalDateTime.now());
        if (referenceBancaire != null) {
            paiement.setReferenceBancaire(referenceBancaire);
        }

        Paiement saved = paiementRepository.save(paiement);
        log.info("Paiement marqué comme payé: {}", id);

        return mapToResponse(saved);
    }

    /**
     * Annuler un paiement
     */
    public PaiementResponseDto annulerPaiement(UUID id, String motif) {
        log.info("Annulation du paiement: {} - Motif: {}", id, motif);

        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paiement non trouvé: " + id));

        if (paiement.getStatut() == StatutPaiementEnum.PAYE) {
            throw new IllegalStateException("Impossible d'annuler un paiement déjà réglé");
        }

        paiement.setStatut(StatutPaiementEnum.ANNULE);
        Paiement saved = paiementRepository.save(paiement);

        log.info("Paiement annulé: {}", id);
        return mapToResponse(saved);
    }

    /**
     * Rembourser un paiement
     */
    public PaiementResponseDto rembourserPaiement(UUID id, String motif) {
        log.info("Remboursement du paiement: {} - Motif: {}", id, motif);

        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paiement non trouvé: " + id));

        if (paiement.getStatut() != StatutPaiementEnum.PAYE) {
            throw new IllegalStateException("Seuls les paiements réglés peuvent être remboursés");
        }

        paiement.setStatut(StatutPaiementEnum.REMBOURSE);
        Paiement saved = paiementRepository.save(paiement);

        log.info("Paiement remboursé: {}", id);
        return mapToResponse(saved);
    }

    /**
     * Obtenir un paiement par ID
     */
    @Transactional(readOnly = true)
    public PaiementResponseDto getPaiement(UUID id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Paiement non trouvé: " + id));
        return mapToResponse(paiement);
    }

    /**
     * Obtenir un paiement par numéro
     */
    @Transactional(readOnly = true)
    public PaiementResponseDto getPaiementByNumero(String numeroPaiement) {
        Paiement paiement = paiementRepository.findByNumeroPaiement(numeroPaiement)
                .orElseThrow(() -> new EntityNotFoundException("Paiement non trouvé: " + numeroPaiement));
        return mapToResponse(paiement);
    }

    /**
     * Récupère tous les paiements
     */
    @Transactional(readOnly = true)
    public List<PaiementResponseDto> getTousLesPaiements() {
        return paiementRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les paiements avec pagination
     */
    @Transactional(readOnly = true)
    public Page<PaiementResponseDto> getTousLesPaiements(Pageable pageable) {
        return paiementRepository.findAll(pageable).map(this::mapToResponse);
    }

    /**
     * Rechercher des paiements avec filtres et pagination
     */
    @Transactional(readOnly = true)
    public Page<PaiementResponseDto> rechercherPaiements(
            StatutPaiementEnum statut,
            UUID attributionId,
            Pageable pageable) {

        Page<Paiement> page;

        if (attributionId != null) {
            page = paiementRepository.findByAttributionId(attributionId, pageable);
        } else if (statut != null) {
            page = paiementRepository.findByStatut(statut, pageable);
        } else {
            page = paiementRepository.findAll(pageable);
        }

        return page.map(this::mapToResponse);
    }

    /**
     * Obtenir les paiements en retard
     */
    @Transactional(readOnly = true)
    public List<PaiementResponseDto> getPaiementsEnRetard() {
        return paiementRepository.findPaiementsEnRetard().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les paiements d'une attribution
     */
    @Transactional(readOnly = true)
    public List<PaiementResponseDto> getPaiementsParAttribution(UUID attributionId) {
        return paiementRepository.findByAttributionId(attributionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Générer les paiements mensuels pour une attribution
     */
    public List<PaiementResponseDto> genererPaiementsMensuels(UUID attributionId, int nombreMois) {
        log.info("Génération de {} paiements mensuels pour l'attribution: {}", nombreMois, attributionId);

        Attribution attribution = attributionRepository.findById(attributionId)
                .orElseThrow(() -> new EntityNotFoundException("Attribution non trouvée: " + attributionId));

        List<Paiement> paiementsGeneres = new java.util.ArrayList<>();
        LocalDate dateEcheance = LocalDate.now().withDayOfMonth(1).plusMonths(1);

        for (int i = 0; i < nombreMois; i++) {
            String moisConcerne = dateEcheance.getMonth().name() + " " + dateEcheance.getYear();

            Paiement paiement = Paiement.builder()
                    .numeroPaiement(genererNumeroPaiement())
                    .attribution(attribution)
                    .montant(attribution.getMontantLoyer())
                    .dateEcheance(dateEcheance)
                    .moisConcerne(moisConcerne)
                    .statut(StatutPaiementEnum.EN_ATTENTE)
                    .build();

            paiementsGeneres.add(paiementRepository.save(paiement));
            dateEcheance = dateEcheance.plusMonths(1);
        }

        log.info("{} paiements générés pour l'attribution: {}", paiementsGeneres.size(), attributionId);

        return paiementsGeneres.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Envoyer des rappels pour les paiements en retard
     */
    public int envoyerRappelsPaiements() {
        log.info("Envoi des rappels pour les paiements en retard");

        List<Paiement> paiementsEnRetard = paiementRepository.findPaiementsEnRetard();
        int compteur = 0;

        for (Paiement paiement : paiementsEnRetard) {
            // Logique d'envoi de rappel (email, notification)
            paiement.envoyerRappel();
            compteur++;
        }

        log.info("{} rappels envoyés", compteur);
        return compteur;
    }

    /**
     * Obtenir les statistiques des paiements
     */
    @Transactional(readOnly = true)
    public PaiementStatistiques getStatistiques() {
        LocalDateTime debutMois = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime debutAnnee = LocalDate.now().withDayOfYear(1).atStartOfDay();

        BigDecimal totalPayeCeMois = paiementRepository.sumMontantByStatutAndDatePaiementAfter(
                StatutPaiementEnum.PAYE, debutMois);
        BigDecimal totalPayeCetteAnnee = paiementRepository.sumMontantByStatutAndDatePaiementAfter(
                StatutPaiementEnum.PAYE, debutAnnee);

        List<Paiement> enRetard = paiementRepository.findPaiementsEnRetard();
        BigDecimal montantEnRetard = enRetard.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return PaiementStatistiques.builder()
                .totalPaiements(paiementRepository.count())
                .paiementsPayes(paiementRepository.countByStatut(StatutPaiementEnum.PAYE))
                .paiementsEnAttente(paiementRepository.countByStatut(StatutPaiementEnum.EN_ATTENTE))
                .paiementsEnRetard((long) enRetard.size())
                .paiementsAnnules(paiementRepository.countByStatut(StatutPaiementEnum.ANNULE))
                .totalPayeCeMois(totalPayeCeMois != null ? totalPayeCeMois : BigDecimal.ZERO)
                .totalPayeCetteAnnee(totalPayeCetteAnnee != null ? totalPayeCetteAnnee : BigDecimal.ZERO)
                .montantEnRetard(montantEnRetard)
                .build();
    }

    /**
     * Calculer le solde d'une attribution
     */
    @Transactional(readOnly = true)
    public SoldeAttribution calculerSoldeAttribution(UUID attributionId) {
        Attribution attribution = attributionRepository.findById(attributionId)
                .orElseThrow(() -> new EntityNotFoundException("Attribution non trouvée: " + attributionId));

        List<Paiement> paiements = paiementRepository.findByAttributionId(attributionId);

        BigDecimal totalDu = paiements.stream()
                .filter(p -> p.getStatut() != StatutPaiementEnum.ANNULE)
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaye = paiements.stream()
                .filter(p -> p.getStatut() == StatutPaiementEnum.PAYE)
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal penalites = paiements.stream()
                .filter(p -> p.getStatut() == StatutPaiementEnum.EN_ATTENTE && p.verifierRetard())
                .map(this::calculerPenalite)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return SoldeAttribution.builder()
                .attributionId(attributionId)
                .numeroContrat(attribution.getNumeroContrat())
                .totalDu(totalDu)
                .totalPaye(totalPaye)
                .solde(totalDu.subtract(totalPaye))
                .penalites(penalites)
                .nombrePaiementsEnRetard((int) paiements.stream()
                        .filter(p -> p.getStatut() == StatutPaiementEnum.EN_ATTENTE && p.verifierRetard())
                        .count())
                .build();
    }

    /**
     * Générer un numéro de paiement unique
     */
    private String genererNumeroPaiement() {
        String annee = String.valueOf(Year.now().getValue());
        String timestamp = String.valueOf(System.currentTimeMillis() % 100000);
        return "PAY-" + annee + "-" + String.format("%05d", Integer.parseInt(timestamp));
    }

    /**
     * Calculer la pénalité pour un paiement en retard
     */
    private BigDecimal calculerPenalite(Paiement paiement) {
        if (!paiement.verifierRetard()) {
            return BigDecimal.ZERO;
        }

        long joursRetard = ChronoUnit.DAYS.between(paiement.getDateEcheance(), LocalDate.now());
        return paiement.getMontant()
                .multiply(TAUX_PENALITE_JOUR)
                .multiply(BigDecimal.valueOf(joursRetard));
    }

    /**
     * Mapper un Paiement vers PaiementResponseDto
     */
    private PaiementResponseDto mapToResponse(Paiement paiement) {
        PaiementResponseDto.PaiementResponseDtoBuilder builder = PaiementResponseDto.builder()
                .id(paiement.getId())
                .numeroPaiement(paiement.getNumeroPaiement())
                .montant(paiement.getMontant())
                .dateEcheance(paiement.getDateEcheance())
                .datePaiement(paiement.getDatePaiement())
                .modePaiement(paiement.getModePaiement())
                .statut(paiement.getStatut())
                .moisConcerne(paiement.getMoisConcerne())
                .referenceBancaire(paiement.getReferenceBancaire());

        // Attribution
        Attribution attribution = paiement.getAttribution();
        if (attribution != null) {
            Utilisateur etudiant = attribution.getEtudiant();
            Logement logement = attribution.getLogement();

            builder.attribution(PaiementResponseDto.AttributionResumeDto.builder()
                    .id(attribution.getId())
                    .numeroContrat(attribution.getNumeroContrat())
                    .etudiantNom(etudiant != null ? etudiant.getEmail() : null)
                    .etudiantMatricule(etudiant != null ? etudiant.getId().toString() : null)
                    .logementCode(logement != null ? logement.getCode() : null)
                    .montantLoyer(attribution.getMontantLoyer())
                    .build());
        }

        // Calcul du retard
        if (paiement.getStatut() == StatutPaiementEnum.EN_ATTENTE && paiement.verifierRetard()) {
            long joursRetard = ChronoUnit.DAYS.between(paiement.getDateEcheance(), LocalDate.now());
            builder.joursRetard(joursRetard);
            builder.penalites(calculerPenalite(paiement));
        } else {
            builder.joursRetard(0L);
            builder.penalites(BigDecimal.ZERO);
        }

        // Reçu
        Document recu = paiement.getRecu();
        if (recu != null) {
            builder.recu(PaiementResponseDto.DocumentResumeDto.builder()
                    .id(recu.getId())
                    .nom(recu.getNom())
                    .url(recu.getUrl())
                    .build());
        }

        return builder.build();
    }

    /**
     * DTO interne pour les statistiques
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PaiementStatistiques {
        private Long totalPaiements;
        private Long paiementsPayes;
        private Long paiementsEnAttente;
        private Long paiementsEnRetard;
        private Long paiementsAnnules;
        private BigDecimal totalPayeCeMois;
        private BigDecimal totalPayeCetteAnnee;
        private BigDecimal montantEnRetard;
    }

    /**
     * DTO interne pour le solde d'une attribution
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SoldeAttribution {
        private UUID attributionId;
        private String numeroContrat;
        private BigDecimal totalDu;
        private BigDecimal totalPaye;
        private BigDecimal solde;
        private BigDecimal penalites;
        private Integer nombrePaiementsEnRetard;
    }
}
