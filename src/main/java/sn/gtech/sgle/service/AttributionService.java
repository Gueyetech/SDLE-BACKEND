package sn.gtech.sgle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.gtech.sgle.dto.attribution.AttributionResponseDto;
import sn.gtech.sgle.dto.attribution.CreerAttributionRequest;
import sn.gtech.sgle.dto.attribution.ModifierAttributionRequest;
import sn.gtech.sgle.entity.Attribution;
import sn.gtech.sgle.entity.Utilisateur;
import sn.gtech.sgle.entity.Logement;
import sn.gtech.sgle.entity.enums.StatutAttributionEnum;
import sn.gtech.sgle.entity.enums.StatutLogementEnum;
import sn.gtech.sgle.exception.AuthException;
import sn.gtech.sgle.repository.AttributionRepository;
import sn.gtech.sgle.repository.UtilisateurRepository;
import sn.gtech.sgle.repository.LogementRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des attributions
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AttributionService {
    
    private final AttributionRepository attributionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final LogementRepository logementRepository;
    
    /**
     * Crée une nouvelle attribution
     */
    public AttributionResponseDto creerAttribution(CreerAttributionRequest request) {
        Utilisateur etudiant = utilisateurRepository.findById(request.getEtudiantId())
                .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));
        
        Logement logement = logementRepository.findById(request.getLogementId())
                .orElseThrow(() -> new AuthException("Logement non trouvé"));
        
        // Vérifier si le logement est disponible
        if (logement.getStatut() != StatutLogementEnum.DISPONIBLE) {
            throw new AuthException("Le logement n'est pas disponible");
        }
        
        // Vérifier si l'étudiant n'a pas déjà une attribution active
        boolean aAttributionActive = attributionRepository
                .existsByEtudiantIdAndStatut(request.getEtudiantId(), StatutAttributionEnum.ACTIVE);
        if (aAttributionActive) {
            throw new AuthException("L'étudiant a déjà une attribution active");
        }
        
        // Vérifier les dates
        if (request.getDateFin().isBefore(request.getDateDebut())) {
            throw new AuthException("La date de fin doit être postérieure à la date de début");
        }
        
        Attribution attribution = Attribution.builder()
                .numeroContrat(genererNumeroContrat())
                .etudiant(etudiant)
                .logement(logement)
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .montantLoyer(request.getMontantLoyer())
                .montantCaution(request.getMontantCaution())
                .statut(StatutAttributionEnum.ACTIVE)
                .commentaires(request.getCommentaires())
                .dateAttribution(LocalDateTime.now())
                .build();
        
        // Mettre à jour le statut du logement
        logement.setStatut(StatutLogementEnum.OCCUPE);
        logementRepository.save(logement);
        
        attribution = attributionRepository.save(attribution);
        return AttributionResponseDto.fromEntity(attribution);
    }
    
    /**
     * Génère un numéro de contrat unique
     */
    private String genererNumeroContrat() {
        int annee = Year.now().getValue();
        long count = attributionRepository.count() + 1;
        return "CONT-" + annee + "-" + String.format("%05d", count);
    }
    
    /**
     * Récupère toutes les attributions
     */
    @Transactional(readOnly = true)
    public List<AttributionResponseDto> getToutesLesAttributions() {
        return attributionRepository.findAll().stream()
                .map(AttributionResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère toutes les attributions avec pagination
     */
    @Transactional(readOnly = true)
    public Page<AttributionResponseDto> getToutesLesAttributions(Pageable pageable) {
        return attributionRepository.findAll(pageable).map(AttributionResponseDto::fromEntity);
    }
    
    /**
     * Récupère toutes les attributions avec pagination et filtres
     */
    @Transactional(readOnly = true)
    public Page<AttributionResponseDto> getToutesLesAttributions(int page, int taille,
            StatutAttributionEnum statut, UUID etudiantId, UUID logementId) {
        Pageable pageable = PageRequest.of(page, taille, Sort.by("dateAttribution").descending());
        
        Page<Attribution> attributions;
        
        if (etudiantId != null) {
            attributions = attributionRepository.findByEtudiantId(etudiantId, pageable);
        } else if (logementId != null) {
            attributions = attributionRepository.findByLogementId(logementId, pageable);
        } else if (statut != null) {
            attributions = attributionRepository.findByStatut(statut, pageable);
        } else {
            attributions = attributionRepository.findAll(pageable);
        }
        
        return attributions.map(AttributionResponseDto::fromEntity);
    }
    
    /**
     * Récupère une attribution par son ID
     */
    @Transactional(readOnly = true)
    public AttributionResponseDto getAttributionParId(UUID id) {
        Attribution attribution = attributionRepository.findById(id)
                .orElseThrow(() -> new AuthException("Attribution non trouvée"));
        return AttributionResponseDto.fromEntity(attribution);
    }
    
    /**
     * Récupère une attribution par son numéro de contrat
     */
    @Transactional(readOnly = true)
    public AttributionResponseDto getAttributionParNumeroContrat(String numeroContrat) {
        Attribution attribution = attributionRepository.findByNumeroContrat(numeroContrat)
                .orElseThrow(() -> new AuthException("Attribution non trouvée"));
        return AttributionResponseDto.fromEntity(attribution);
    }
    
    /**
     * Modifie une attribution existante
     */
    public AttributionResponseDto modifierAttribution(UUID id, ModifierAttributionRequest request) {
        Attribution attribution = attributionRepository.findById(id)
                .orElseThrow(() -> new AuthException("Attribution non trouvée"));
        
        if (request.getDateDebut() != null) {
            attribution.setDateDebut(request.getDateDebut());
        }
        if (request.getDateFin() != null) {
            if (request.getDateFin().isBefore(attribution.getDateDebut())) {
                throw new AuthException("La date de fin doit être postérieure à la date de début");
            }
            attribution.setDateFin(request.getDateFin());
        }
        if (request.getMontantLoyer() != null) {
            attribution.setMontantLoyer(request.getMontantLoyer());
        }
        if (request.getMontantCaution() != null) {
            attribution.setMontantCaution(request.getMontantCaution());
        }
        if (request.getCommentaires() != null) {
            attribution.setCommentaires(request.getCommentaires());
        }
        if (request.getStatut() != null) {
            attribution.setStatut(request.getStatut());
        }
        
        attribution = attributionRepository.save(attribution);
        return AttributionResponseDto.fromEntity(attribution);
    }
    
    /**
     * Révoque une attribution
     */
    public AttributionResponseDto revoquerAttribution(UUID id, String motif) {
        Attribution attribution = attributionRepository.findById(id)
                .orElseThrow(() -> new AuthException("Attribution non trouvée"));
        
        attribution.setStatut(StatutAttributionEnum.RESILIEE);
        attribution.setCommentaires(attribution.getCommentaires() + "\n[RÉVOCATION] " + motif);
        
        // Libérer le logement
        Logement logement = attribution.getLogement();
        logement.setStatut(StatutLogementEnum.DISPONIBLE);
        logementRepository.save(logement);
        
        attribution = attributionRepository.save(attribution);
        return AttributionResponseDto.fromEntity(attribution);
    }
    
    /**
     * Termine une attribution
     */
    public AttributionResponseDto terminerAttribution(UUID id) {
        Attribution attribution = attributionRepository.findById(id)
                .orElseThrow(() -> new AuthException("Attribution non trouvée"));
        
        attribution.setStatut(StatutAttributionEnum.EXPIREE);
        
        // Libérer le logement
        Logement logement = attribution.getLogement();
        logement.setStatut(StatutLogementEnum.DISPONIBLE);
        logementRepository.save(logement);
        
        attribution = attributionRepository.save(attribution);
        return AttributionResponseDto.fromEntity(attribution);
    }
    
    /**
     * Enregistre le check-in d'un étudiant
     */
    public AttributionResponseDto enregistrerCheckIn(UUID id) {
        Attribution attribution = attributionRepository.findById(id)
                .orElseThrow(() -> new AuthException("Attribution non trouvée"));
        
        if (attribution.getDateCheckIn() != null) {
            throw new AuthException("Le check-in a déjà été effectué");
        }
        
        attribution.setDateCheckIn(LocalDateTime.now());
        attribution = attributionRepository.save(attribution);
        return AttributionResponseDto.fromEntity(attribution);
    }
    
    /**
     * Enregistre le check-out d'un étudiant
     */
    public AttributionResponseDto enregistrerCheckOut(UUID id) {
        Attribution attribution = attributionRepository.findById(id)
                .orElseThrow(() -> new AuthException("Attribution non trouvée"));
        
        if (attribution.getDateCheckIn() == null) {
            throw new AuthException("Le check-in n'a pas été effectué");
        }
        if (attribution.getDateCheckOut() != null) {
            throw new AuthException("Le check-out a déjà été effectué");
        }
        
        attribution.setDateCheckOut(LocalDateTime.now());
        attribution = attributionRepository.save(attribution);
        return AttributionResponseDto.fromEntity(attribution);
    }
    
    /**
     * Prolonge une attribution
     */
    public AttributionResponseDto prolongerAttribution(UUID id, LocalDate nouvelleDateFin) {
        Attribution attribution = attributionRepository.findById(id)
                .orElseThrow(() -> new AuthException("Attribution non trouvée"));
        
        if (nouvelleDateFin.isBefore(attribution.getDateFin())) {
            throw new AuthException("La nouvelle date de fin doit être postérieure à la date de fin actuelle");
        }
        
        attribution.setDateFin(nouvelleDateFin);
        attribution.setCommentaires(attribution.getCommentaires() + 
                "\n[PROLONGATION] Nouvelle date de fin: " + nouvelleDateFin);
        
        attribution = attributionRepository.save(attribution);
        return AttributionResponseDto.fromEntity(attribution);
    }
    
    /**
     * Récupère les attributions expirant dans un nombre de jours donné
     */
    @Transactional(readOnly = true)
    public List<AttributionResponseDto> getAttributionsExpirantSous(int jours) {
        LocalDate aujourdHui = LocalDate.now();
        LocalDate dateLimite = aujourdHui.plusDays(jours);
        
        List<Attribution> attributions = attributionRepository
                .findByStatutAndDateFinBetween(StatutAttributionEnum.ACTIVE, aujourdHui, dateLimite);
        
        return attributions.stream()
                .map(AttributionResponseDto::fromEntity)
                .toList();
    }
    
    /**
     * Récupère les statistiques des attributions
     */
    @Transactional(readOnly = true)
    public AttributionStatistiques getStatistiques() {
        long total = attributionRepository.count();
        long actives = attributionRepository.countByStatut(StatutAttributionEnum.ACTIVE);
        long expirees = attributionRepository.countByStatut(StatutAttributionEnum.EXPIREE);
        long resiliees = attributionRepository.countByStatut(StatutAttributionEnum.RESILIEE);
        
        LocalDate aujourdHui = LocalDate.now();
        long expirantBientot = attributionRepository
                .countByStatutAndDateFinBetween(StatutAttributionEnum.ACTIVE, aujourdHui, aujourdHui.plusDays(30));
        
        return new AttributionStatistiques(
                total,
                actives,
                expirees,
                resiliees,
                expirantBientot
        );
    }
    
    /**
     * Record pour les statistiques des attributions
     */
    public record AttributionStatistiques(
            long total,
            long actives,
            long expirees,
            long resiliees,
            long expirantBientot
    ) {}
}
