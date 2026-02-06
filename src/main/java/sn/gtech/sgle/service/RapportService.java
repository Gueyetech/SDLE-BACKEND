package sn.gtech.sgle.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.gtech.sgle.dto.rapport.GenererRapportRequest;
import sn.gtech.sgle.dto.rapport.RapportResponseDto;
import sn.gtech.sgle.entity.*;
import sn.gtech.sgle.entity.enums.*;
import sn.gtech.sgle.repository.*;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RapportService {

    private final RapportRepository rapportRepository;
    private final LogementRepository logementRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AttributionRepository attributionRepository;
    private final PaiementRepository paiementRepository;
    private final DemandeLogementRepository demandeLogementRepository;
    private final IncidentRepository incidentRepository;
    private final MaintenanceRepository maintenanceRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * Générer un rapport selon le type demandé
     */
    public RapportResponseDto genererRapport(GenererRapportRequest request) {
        log.info("Génération d'un rapport de type: {}", request.getType());

        Map<String, Object> donnees = switch (request.getType()) {
            case OCCUPATION -> genererRapportOccupation(request.getPeriodeDebut(), request.getPeriodeFin());
            case FINANCIER -> genererRapportFinancier(request.getPeriodeDebut(), request.getPeriodeFin());
            case DEMANDES -> genererRapportDemandes(request.getPeriodeDebut(), request.getPeriodeFin());
            case INCIDENTS -> genererRapportIncidents(request.getPeriodeDebut(), request.getPeriodeFin());
            case MAINTENANCE -> genererRapportMaintenance(request.getPeriodeDebut(), request.getPeriodeFin());
            case ETUDIANTS -> genererRapportEtudiants(request.getFiltres());
            case PERFORMANCE -> genererRapportPerformance(request.getPeriodeDebut(), request.getPeriodeFin());
        };

        String titre = request.getTitre() != null ? request.getTitre() 
                : "Rapport " + request.getType().name() + " - " + LocalDate.now();

        // Convertir les données en JSON
        String donneesJson;
        try {
            donneesJson = objectMapper.writeValueAsString(donnees);
        } catch (Exception e) {
            log.error("Erreur lors de la sérialisation des données", e);
            donneesJson = "{}";
        }

        Rapport rapport = Rapport.builder()
                .type(request.getType())
                .titre(titre)
                .periodeDebut(request.getPeriodeDebut())
                .periodeFin(request.getPeriodeFin())
                .donnees(donneesJson)
                .format(FormatEnum.valueOf(request.getFormat().name()))
                .dateGeneration(LocalDateTime.now())
                .build();

        Rapport saved = rapportRepository.save(rapport);
        log.info("Rapport généré: {} - ID: {}", saved.getTitre(), saved.getId());

        return mapToResponse(saved);
    }

    /**
     * Obtenir un rapport par ID
     */
    @Transactional(readOnly = true)
    public RapportResponseDto getRapport(UUID id) {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé: " + id));
        return mapToResponse(rapport);
    }

    /**
     * Lister tous les rapports générés
     */
    @Transactional(readOnly = true)
    public List<RapportResponseDto> listerRapports() {
        return rapportRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lister les rapports générés avec filtre par type
     */
    @Transactional(readOnly = true)
    public List<RapportResponseDto> listerRapports(TypeRapportEnum type) {
        List<Rapport> rapports;
        if (type != null) {
            rapports = rapportRepository.findByType(type);
        } else {
            rapports = rapportRepository.findAll();
        }
        return rapports.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Supprimer un rapport
     */
    public void supprimerRapport(UUID id) {
        if (!rapportRepository.existsById(id)) {
            throw new EntityNotFoundException("Rapport non trouvé: " + id);
        }
        rapportRepository.deleteById(id);
        log.info("Rapport supprimé: {}", id);
    }

    /**
     * Exporter les données au format demandé
     */
    @Transactional(readOnly = true)
    public ExportResult exporterDonnees(TypeRapportEnum type, GenererRapportRequest.FormatExport format, 
                                         LocalDate periodeDebut, LocalDate periodeFin) {
        log.info("Export des données {} au format {}", type, format);

        Map<String, Object> donnees = switch (type) {
            case OCCUPATION -> genererRapportOccupation(periodeDebut, periodeFin);
            case FINANCIER -> genererRapportFinancier(periodeDebut, periodeFin);
            case DEMANDES -> genererRapportDemandes(periodeDebut, periodeFin);
            case INCIDENTS -> genererRapportIncidents(periodeDebut, periodeFin);
            case MAINTENANCE -> genererRapportMaintenance(periodeDebut, periodeFin);
            case ETUDIANTS -> genererRapportEtudiants(null);
            case PERFORMANCE -> genererRapportPerformance(periodeDebut, periodeFin);
        };

        String contenu;
        String contentType;
        String extension;

        switch (format) {
            case CSV -> {
                contenu = convertToCSV(donnees);
                contentType = "text/csv";
                extension = "csv";
            }
            case JSON -> {
                try {
                    contenu = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(donnees);
                } catch (Exception e) {
                    contenu = "{}";
                }
                contentType = "application/json";
                extension = "json";
            }
            default -> {
                // PDF et Excel nécessitent des librairies supplémentaires
                // Pour l'instant, on retourne du JSON
                try {
                    contenu = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(donnees);
                } catch (Exception e) {
                    contenu = "{}";
                }
                contentType = "application/json";
                extension = "json";
            }
        }

        String nomFichier = "rapport_" + type.name().toLowerCase() + "_" + LocalDate.now() + "." + extension;

        return ExportResult.builder()
                .nomFichier(nomFichier)
                .contentType(contentType)
                .contenu(contenu.getBytes())
                .taille((long) contenu.getBytes().length)
                .build();
    }

    // ============ Rapports spécifiques ============

    /**
     * Rapport d'occupation des logements
     */
    private Map<String, Object> genererRapportOccupation(LocalDate debut, LocalDate fin) {
        Map<String, Object> rapport = new LinkedHashMap<>();
        
        long totalLogements = logementRepository.count();
        long logementsOccupes = logementRepository.countByStatut(StatutLogementEnum.OCCUPE);
        long logementsDisponibles = logementRepository.countByStatut(StatutLogementEnum.DISPONIBLE);
        long logementsEnMaintenance = logementRepository.countByStatut(StatutLogementEnum.EN_MAINTENANCE);

        rapport.put("dateGeneration", LocalDateTime.now());
        rapport.put("periodeDebut", debut);
        rapport.put("periodeFin", fin);
        
        // Statistiques globales
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalLogements", totalLogements);
        stats.put("logementsOccupes", logementsOccupes);
        stats.put("logementsDisponibles", logementsDisponibles);
        stats.put("logementsEnMaintenance", logementsEnMaintenance);
        stats.put("tauxOccupation", totalLogements > 0 ? 
                (double) logementsOccupes / totalLogements * 100 : 0);
        rapport.put("statistiques", stats);

        // Répartition par type
        Map<String, Long> parType = new LinkedHashMap<>();
        for (TypeLogementEnum type : TypeLogementEnum.values()) {
            parType.put(type.name(), logementRepository.countByType(type));
        }
        rapport.put("repartitionParType", parType);

        // Attributions actives
        long attributionsActives = attributionRepository.countByStatut(StatutAttributionEnum.ACTIVE);
        rapport.put("attributionsActives", attributionsActives);

        return rapport;
    }

    /**
     * Rapport financier
     */
    private Map<String, Object> genererRapportFinancier(LocalDate debut, LocalDate fin) {
        Map<String, Object> rapport = new LinkedHashMap<>();

        rapport.put("dateGeneration", LocalDateTime.now());
        rapport.put("periodeDebut", debut);
        rapport.put("periodeFin", fin);

        // Statistiques de paiements
        Map<String, Object> paiements = new LinkedHashMap<>();
        paiements.put("totalPaiements", paiementRepository.count());
        paiements.put("paiementsPayes", paiementRepository.countByStatut(StatutPaiementEnum.PAYE));
        paiements.put("paiementsEnAttente", paiementRepository.countByStatut(StatutPaiementEnum.EN_ATTENTE));
        paiements.put("paiementsEnRetard", (long) paiementRepository.findPaiementsEnRetard().size());

        // Montants
        LocalDateTime debutDateTime = debut != null ? debut.atStartOfDay() : LocalDate.now().withDayOfYear(1).atStartOfDay();
        BigDecimal totalEncaisse = paiementRepository.sumMontantByStatutAndDatePaiementAfter(
                StatutPaiementEnum.PAYE, debutDateTime);
        paiements.put("totalEncaisse", totalEncaisse != null ? totalEncaisse : BigDecimal.ZERO);

        // Montant en retard
        List<Paiement> enRetard = paiementRepository.findPaiementsEnRetard();
        BigDecimal montantEnRetard = enRetard.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        paiements.put("montantEnRetard", montantEnRetard);

        rapport.put("paiements", paiements);

        // Loyers des attributions actives
        List<Attribution> attributionsActives = attributionRepository.findByStatut(StatutAttributionEnum.ACTIVE);
        BigDecimal loyersMensuelsPrevus = attributionsActives.stream()
                .map(Attribution::getMontantLoyer)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        rapport.put("loyersMensuelsPrevus", loyersMensuelsPrevus);

        return rapport;
    }

    /**
     * Rapport des demandes
     */
    private Map<String, Object> genererRapportDemandes(LocalDate debut, LocalDate fin) {
        Map<String, Object> rapport = new LinkedHashMap<>();

        rapport.put("dateGeneration", LocalDateTime.now());
        rapport.put("periodeDebut", debut);
        rapport.put("periodeFin", fin);

        // Statistiques par statut
        Map<String, Long> parStatut = new LinkedHashMap<>();
        for (StatutDemandeEnum statut : StatutDemandeEnum.values()) {
            parStatut.put(statut.name(), demandeLogementRepository.countByStatut(statut));
        }
        rapport.put("repartitionParStatut", parStatut);

        // Total
        rapport.put("totalDemandes", demandeLogementRepository.count());

        // Demandes en attente (simplifié - pas de filtre par date)
        long enAttente = demandeLogementRepository.countByStatut(StatutDemandeEnum.EN_ATTENTE);
        rapport.put("demandesEnAttente", enAttente);

        return rapport;
    }

    /**
     * Rapport des incidents
     */
    private Map<String, Object> genererRapportIncidents(LocalDate debut, LocalDate fin) {
        Map<String, Object> rapport = new LinkedHashMap<>();

        rapport.put("dateGeneration", LocalDateTime.now());
        rapport.put("periodeDebut", debut);
        rapport.put("periodeFin", fin);

        // Statistiques par statut
        Map<String, Long> parStatut = new LinkedHashMap<>();
        for (StatutIncidentEnum statut : StatutIncidentEnum.values()) {
            parStatut.put(statut.name(), incidentRepository.countByStatut(statut));
        }
        rapport.put("repartitionParStatut", parStatut);

        // Statistiques par urgence (comptage par list)
        Map<String, Long> parUrgence = new LinkedHashMap<>();
        for (UrgenceEnum urgence : UrgenceEnum.values()) {
            long count = incidentRepository.findByStatutAndUrgence(StatutIncidentEnum.OUVERT, urgence).size();
            parUrgence.put(urgence.name(), count);
        }
        rapport.put("incidentsOuvertsParUrgence", parUrgence);

        // Total
        rapport.put("totalIncidents", incidentRepository.count());

        return rapport;
    }

    /**
     * Rapport de maintenance
     */
    private Map<String, Object> genererRapportMaintenance(LocalDate debut, LocalDate fin) {
        Map<String, Object> rapport = new LinkedHashMap<>();

        rapport.put("dateGeneration", LocalDateTime.now());
        rapport.put("periodeDebut", debut);
        rapport.put("periodeFin", fin);

        // Statistiques par statut
        Map<String, Long> parStatut = new LinkedHashMap<>();
        for (StatutMaintenanceEnum statut : StatutMaintenanceEnum.values()) {
            parStatut.put(statut.name(), maintenanceRepository.countByStatut(statut));
        }
        rapport.put("repartitionParStatut", parStatut);

        // Total
        rapport.put("totalMaintenances", maintenanceRepository.count());

        // Logements en maintenance
        rapport.put("logementsEnMaintenance", logementRepository.countByStatut(StatutLogementEnum.EN_MAINTENANCE));

        return rapport;
    }

    /**
     * Rapport des étudiants
     */
    private Map<String, Object> genererRapportEtudiants(Map<String, Object> filtres) {
        Map<String, Object> rapport = new LinkedHashMap<>();

        rapport.put("dateGeneration", LocalDateTime.now());

        // Statistiques par role
        Map<String, Long> parRole = new LinkedHashMap<>();
        for (RoleEnum role : RoleEnum.values()) {
            parRole.put(role.name(), utilisateurRepository.countByRole(role));
        }
        rapport.put("repartitionParRole", parRole);

        // Total utilisateurs
        rapport.put("totalUtilisateurs", utilisateurRepository.count());
        // Utilisateurs avec role ETUDIANT
        rapport.put("totalEtudiants", utilisateurRepository.countByRole(RoleEnum.ETUDIANT));

        return rapport;
    }

    /**
     * Rapport de performance
     */
    private Map<String, Object> genererRapportPerformance(LocalDate debut, LocalDate fin) {
        Map<String, Object> rapport = new LinkedHashMap<>();

        rapport.put("dateGeneration", LocalDateTime.now());
        rapport.put("periodeDebut", debut);
        rapport.put("periodeFin", fin);

        // Taux d'occupation
        long totalLogements = logementRepository.count();
        long logementsOccupes = logementRepository.countByStatut(StatutLogementEnum.OCCUPE);
        double tauxOccupation = totalLogements > 0 ? (double) logementsOccupes / totalLogements * 100 : 0;
        rapport.put("tauxOccupation", Math.round(tauxOccupation * 100.0) / 100.0);

        // Taux de recouvrement (paiements payés / total)
        long totalPaiements = paiementRepository.count();
        long paiementsPayes = paiementRepository.countByStatut(StatutPaiementEnum.PAYE);
        double tauxRecouvrement = totalPaiements > 0 ? (double) paiementsPayes / totalPaiements * 100 : 0;
        rapport.put("tauxRecouvrement", Math.round(tauxRecouvrement * 100.0) / 100.0);

        // Temps moyen de traitement des demandes (simplifié)
        rapport.put("tempsTraitementMoyenJours", 5); // À calculer réellement

        // Incidents résolus ce mois (simplifié - comptage par statut)
        long incidentsResolus = incidentRepository.countByStatut(StatutIncidentEnum.RESOLU);
        rapport.put("incidentsResolus", incidentsResolus);

        return rapport;
    }

    /**
     * Convertir les données en format CSV
     */
    private String convertToCSV(Map<String, Object> donnees) {
        StringBuilder csv = new StringBuilder();
        
        // Parcourir récursivement les données
        for (Map.Entry<String, Object> entry : donnees.entrySet()) {
            if (entry.getValue() instanceof Map) {
                csv.append("\n").append(entry.getKey()).append("\n");
                @SuppressWarnings("unchecked")
                Map<String, Object> subMap = (Map<String, Object>) entry.getValue();
                for (Map.Entry<String, Object> subEntry : subMap.entrySet()) {
                    csv.append(subEntry.getKey()).append(",").append(subEntry.getValue()).append("\n");
                }
            } else {
                csv.append(entry.getKey()).append(",").append(entry.getValue()).append("\n");
            }
        }

        return csv.toString();
    }

    /**
     * Mapper un Rapport vers RapportResponseDto
     */
    private RapportResponseDto mapToResponse(Rapport rapport) {
        return RapportResponseDto.builder()
                .id(rapport.getId())
                .titre(rapport.getTitre())
                .type(rapport.getType())
                .format(rapport.getFormat())
                .periodeDebut(rapport.getPeriodeDebut())
                .periodeFin(rapport.getPeriodeFin())
                .dateGeneration(rapport.getDateGeneration())
                .generateurNom(rapport.getGenerePar() != null ? rapport.getGenerePar().getEmail() : null)
                .urlTelechargement("/api/admin/rapports/" + rapport.getId() + "/download")
                .tailleFichier(rapport.getDonnees() != null ? (long) rapport.getDonnees().length() : 0L)
                .build();
    }

    /**
     * DTO pour le résultat d'export
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ExportResult {
        private String nomFichier;
        private String contentType;
        private byte[] contenu;
        private Long taille;
    }
}
