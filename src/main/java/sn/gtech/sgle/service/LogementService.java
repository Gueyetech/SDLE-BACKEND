package sn.gtech.sgle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.gtech.sgle.dto.logement.CreerLogementRequest;
import sn.gtech.sgle.dto.logement.LogementResponseDto;
import sn.gtech.sgle.dto.logement.MaintenanceDto;
import sn.gtech.sgle.dto.logement.ModifierLogementRequest;
import sn.gtech.sgle.entity.Adresse;
import sn.gtech.sgle.entity.Logement;
import sn.gtech.sgle.entity.Maintenance;
import sn.gtech.sgle.entity.enums.StatutLogementEnum;
import sn.gtech.sgle.entity.enums.StatutMaintenanceEnum;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;
import sn.gtech.sgle.exception.AuthException;
import sn.gtech.sgle.repository.LogementRepository;
import sn.gtech.sgle.repository.MaintenanceRepository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des logements
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LogementService {
    
    private final LogementRepository logementRepository;
    private final MaintenanceRepository maintenanceRepository;
    
    /**
     * Crée un nouveau logement
     */
    public LogementResponseDto creerLogement(CreerLogementRequest request) {
        Adresse adresse = Adresse.builder()
                .rue(request.getAdresseRue())
                .numero(request.getAdresseNumero())
                .codePostal(request.getAdresseCodePostal())
                .ville(request.getAdresseVille())
                .quartier(request.getAdresseQuartier())
                .region(request.getAdresseRegion())
                .pays(request.getAdressePays() != null ? request.getAdressePays() : "Sénégal")
                .latitude(request.getAdresseLatitude())
                .longitude(request.getAdresseLongitude())
                .complementAdresse(request.getAdresseComplement())
                .build();
        
        Logement logement = Logement.builder()
                .code(genererCodeLogement(request.getType()))
                .type(request.getType())
                .adresse(adresse)
                .capacite(request.getCapacite())
                .superficie(request.getSuperficie())
                .prixMensuel(request.getPrixMensuel())
                .description(request.getDescription())
                .meuble(request.getMeuble() != null ? request.getMeuble() : false)
                .statut(StatutLogementEnum.DISPONIBLE)
                .dateCreation(LocalDateTime.now())
                .build();
        
        if (request.getPhotos() != null) {
            logement.setPhotos(request.getPhotos());
        }
        
        logement = logementRepository.save(logement);
        return LogementResponseDto.fromEntity(logement);
    }
    
    /**
     * Génère un code unique pour un logement
     */
    private String genererCodeLogement(TypeLogementEnum type) {
        String prefixe = switch (type) {
            case STUDIO -> "ST";
            case T1 -> "T1";
            case T2 -> "T2";
            case T3 -> "T3";
            case CHAMBRE_UNIVERSITAIRE -> "CU";
            case APPARTEMENT_PARTAGE -> "AP";
            case RESIDENCE_ETUDIANTE -> "RE";
            default -> "LG";
        };
        
        long count = logementRepository.count() + 1;
        return prefixe + "-" + Year.now().getValue() + "-" + String.format("%05d", count);
    }
    
    /**
     * Récupère tous les logements
     */
    @Transactional(readOnly = true)
    public List<LogementResponseDto> getTousLesLogements() {
        return logementRepository.findAll().stream()
                .map(LogementResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère tous les logements avec pagination
     */
    @Transactional(readOnly = true)
    public Page<LogementResponseDto> getTousLesLogements(Pageable pageable) {
        return logementRepository.findAll(pageable).map(LogementResponseDto::fromEntity);
    }
    
    /**
     * Récupère tous les logements avec pagination et filtres
     */
    @Transactional(readOnly = true)
    public Page<LogementResponseDto> getTousLesLogements(int page, int taille,
            TypeLogementEnum type, StatutLogementEnum statut, String ville, String recherche) {
        Pageable pageable = PageRequest.of(page, taille, Sort.by("dateCreation").descending());
        
        Page<Logement> logements;
        
        if (recherche != null && !recherche.isEmpty()) {
            logements = logementRepository.findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    recherche, recherche, pageable);
        } else if (type != null && statut != null) {
            logements = logementRepository.findByTypeAndStatut(type, statut, pageable);
        } else if (type != null) {
            logements = logementRepository.findByType(type, pageable);
        } else if (statut != null) {
            logements = logementRepository.findByStatut(statut, pageable);
        } else {
            logements = logementRepository.findAll(pageable);
        }
        
        return logements.map(LogementResponseDto::fromEntity);
    }
    
    /**
     * Récupère un logement par son ID
     */
    @Transactional(readOnly = true)
    public LogementResponseDto getLogementParId(UUID id) {
        Logement logement = logementRepository.findById(id)
                .orElseThrow(() -> new AuthException("Logement non trouvé"));
        return LogementResponseDto.fromEntity(logement);
    }
    
    /**
     * Récupère un logement par son code
     */
    @Transactional(readOnly = true)
    public LogementResponseDto getLogementParCode(String code) {
        Logement logement = logementRepository.findByCode(code)
                .orElseThrow(() -> new AuthException("Logement non trouvé"));
        return LogementResponseDto.fromEntity(logement);
    }
    
    /**
     * Modifie un logement existant
     */
    public LogementResponseDto modifierLogement(UUID id, ModifierLogementRequest request) {
        Logement logement = logementRepository.findById(id)
                .orElseThrow(() -> new AuthException("Logement non trouvé"));
        
        if (request.getType() != null) {
            logement.setType(request.getType());
        }
        if (request.getCapacite() != null) {
            logement.setCapacite(request.getCapacite());
        }
        if (request.getSuperficie() != null) {
            logement.setSuperficie(request.getSuperficie());
        }
        if (request.getPrixMensuel() != null) {
            logement.setPrixMensuel(request.getPrixMensuel());
        }
        if (request.getDescription() != null) {
            logement.setDescription(request.getDescription());
        }
        if (request.getStatut() != null) {
            logement.setStatut(request.getStatut());
        }
        if (request.getMeuble() != null) {
            logement.setMeuble(request.getMeuble());
        }
        
        // Mise à jour de l'adresse
        if (logement.getAdresse() == null) {
            logement.setAdresse(new Adresse());
        }
        Adresse adresse = logement.getAdresse();
        if (request.getAdresseRue() != null) adresse.setRue(request.getAdresseRue());
        if (request.getAdresseNumero() != null) adresse.setNumero(request.getAdresseNumero());
        if (request.getAdresseCodePostal() != null) adresse.setCodePostal(request.getAdresseCodePostal());
        if (request.getAdresseVille() != null) adresse.setVille(request.getAdresseVille());
        if (request.getAdresseQuartier() != null) adresse.setQuartier(request.getAdresseQuartier());
        if (request.getAdresseRegion() != null) adresse.setRegion(request.getAdresseRegion());
        if (request.getAdressePays() != null) adresse.setPays(request.getAdressePays());
        if (request.getAdresseLatitude() != null) adresse.setLatitude(request.getAdresseLatitude());
        if (request.getAdresseLongitude() != null) adresse.setLongitude(request.getAdresseLongitude());
        if (request.getAdresseComplement() != null) adresse.setComplementAdresse(request.getAdresseComplement());
        
        if (request.getPhotos() != null) {
            logement.setPhotos(request.getPhotos());
        }
        
        logement.setDateModification(LocalDateTime.now());
        logement = logementRepository.save(logement);
        return LogementResponseDto.fromEntity(logement);
    }
    
    /**
     * Supprime un logement
     */
    public void supprimerLogement(UUID id) {
        Logement logement = logementRepository.findById(id)
                .orElseThrow(() -> new AuthException("Logement non trouvé"));
        
        // Vérifier si le logement est occupé
        if (logement.getStatut() == StatutLogementEnum.OCCUPE) {
            throw new AuthException("Impossible de supprimer un logement occupé");
        }
        
        logementRepository.delete(logement);
    }
    
    /**
     * Archive un logement
     */
    public LogementResponseDto archiverLogement(UUID id) {
        Logement logement = logementRepository.findById(id)
                .orElseThrow(() -> new AuthException("Logement non trouvé"));
        
        if (logement.getStatut() == StatutLogementEnum.OCCUPE) {
            throw new AuthException("Impossible d'archiver un logement occupé");
        }
        
        logement.setStatut(StatutLogementEnum.ARCHIVE);
        logement.setDateModification(LocalDateTime.now());
        logement = logementRepository.save(logement);
        return LogementResponseDto.fromEntity(logement);
    }
    
    /**
     * Planifie une maintenance
     */
    public MaintenanceDto planifierMaintenance(MaintenanceDto request) {
        Logement logement = logementRepository.findById(request.getLogementId())
                .orElseThrow(() -> new AuthException("Logement non trouvé"));
        
        Maintenance maintenance = Maintenance.builder()
                .logement(logement)
                .type(request.getType())
                .description(request.getDescription())
                .statut(StatutMaintenanceEnum.PLANIFIEE)
                .datePlanifiee(request.getDatePrevue())
                .cout(request.getCout())
                .technicien(request.getTechnicien())
                .build();
        
        maintenance = maintenanceRepository.save(maintenance);
        return MaintenanceDto.fromEntity(maintenance);
    }
    
    /**
     * Récupère les maintenances d'un logement
     */
    @Transactional(readOnly = true)
    public Page<MaintenanceDto> getMaintenancesLogement(UUID logementId, int page, int taille) {
        Pageable pageable = PageRequest.of(page, taille, Sort.by("dateCreation").descending());
        Page<Maintenance> maintenances = maintenanceRepository.findByLogementId(logementId, pageable);
        return maintenances.map(MaintenanceDto::fromEntity);
    }
    
    /**
     * Récupère les statistiques des logements
     */
    @Transactional(readOnly = true)
    public LogementStatistiques getStatistiques() {
        long total = logementRepository.count();
        long disponibles = logementRepository.countByStatut(StatutLogementEnum.DISPONIBLE);
        long occupes = logementRepository.countByStatut(StatutLogementEnum.OCCUPE);
        long enMaintenance = logementRepository.countByStatut(StatutLogementEnum.EN_MAINTENANCE);
        long archives = logementRepository.countByStatut(StatutLogementEnum.ARCHIVE);
        
        double tauxOccupation = total > 0 ? (double) occupes / total * 100 : 0;
        
        return new LogementStatistiques(
                total,
                disponibles,
                occupes,
                enMaintenance,
                archives,
                Math.round(tauxOccupation * 100.0) / 100.0
        );
    }
    
    /**
     * Planifie une maintenance pour un logement (surcharge avec ID et request)
     */
    public LogementResponseDto planifierMaintenance(UUID logementId, sn.gtech.sgle.dto.logement.PlanifierMaintenanceRequest request) {
        Logement logement = logementRepository.findById(logementId)
                .orElseThrow(() -> new AuthException("Logement non trouvé"));
        
        Maintenance maintenance = Maintenance.builder()
                .logement(logement)
                .type(request.getType())
                .description(request.getDescription())
                .statut(StatutMaintenanceEnum.PLANIFIEE)
                .datePlanifiee(request.getDateDebutPlanifiee().toLocalDate())
                .cout(request.getCoutEstime() != null ? 
                        java.math.BigDecimal.valueOf(request.getCoutEstime()) : null)
                .technicien(request.getPrestataire())
                .build();
        
        maintenanceRepository.save(maintenance);
        
        // Mettre le logement en maintenance si urgente
        if (request.getPriorite() == sn.gtech.sgle.dto.logement.PlanifierMaintenanceRequest.PrioriteMaintenance.URGENTE) {
            logement.setStatut(StatutLogementEnum.EN_MAINTENANCE);
            logementRepository.save(logement);
        }
        
        return LogementResponseDto.fromEntity(logement);
    }
    
    /**
     * Record pour les statistiques des logements
     */
    public record LogementStatistiques(
            long total,
            long disponibles,
            long occupes,
            long enMaintenance,
            long archives,
            double tauxOccupation
    ) {}
}
