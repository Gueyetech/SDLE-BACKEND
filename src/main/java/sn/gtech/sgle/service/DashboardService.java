package sn.gtech.sgle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sn.gtech.sgle.dto.dashboard.AlerteDto;
import sn.gtech.sgle.dto.dashboard.MetriquePerformanceDto;
import sn.gtech.sgle.dto.dashboard.StatistiquesGlobalesDto;
import sn.gtech.sgle.entity.enums.*;
import sn.gtech.sgle.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour le tableau de bord administrateur
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    
    private final LogementRepository logementRepository;
    private final AttributionRepository attributionRepository;
    private final PaiementRepository paiementRepository;
    private final DemandeLogementRepository demandeRepository;
    private final IncidentRepository incidentRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final UtilisateurRepository utilisateurRepository;
    
    /**
     * Récupère les statistiques globales du système
     */
    public StatistiquesGlobalesDto getStatistiquesGlobales() {
        LocalDate aujourdHui = LocalDate.now();
        LocalDate debutMois = aujourdHui.withDayOfMonth(1);
        LocalDate debutAnnee = aujourdHui.withDayOfYear(1);
        
        // Statistiques Logements
        long totalLogements = logementRepository.count();
        long logementsDisponibles = logementRepository.countByStatut(StatutLogementEnum.DISPONIBLE);
        long logementsOccupes = logementRepository.countByStatut(StatutLogementEnum.OCCUPE);
        long logementsEnMaintenance = logementRepository.countByStatut(StatutLogementEnum.EN_MAINTENANCE);
        long logementsArchives = logementRepository.countByStatut(StatutLogementEnum.ARCHIVE);
        double tauxOccupation = totalLogements > 0 ? 
                (double) logementsOccupes / totalLogements * 100 : 0;
        
        // Statistiques Étudiants (Utilisateurs avec role ETUDIANT)
        long totalEtudiants = utilisateurRepository.countByRole(RoleEnum.ETUDIANT);
        long etudiantsActifs = totalEtudiants; // Tous les étudiants sont considérés actifs
        long etudiantsEnAttente = 0L;
        long etudiantsRejetes = 0L;
        long nouveauxEtudiantsMois = 0L;
        
        // Statistiques Demandes
        long totalDemandes = demandeRepository.count();
        long demandesEnAttente = demandeRepository.countByStatut(StatutDemandeEnum.EN_ATTENTE);
        long demandesApprouvees = demandeRepository.countByStatut(StatutDemandeEnum.APPROUVEE);
        long demandesRejetees = demandeRepository.countByStatut(StatutDemandeEnum.REJETEE);
        double tauxApprobation = totalDemandes > 0 ? 
                (double) demandesApprouvees / totalDemandes * 100 : 0;
        
        // Statistiques Attributions
        long totalAttributions = attributionRepository.count();
        long attributionsActives = attributionRepository.countByStatut(StatutAttributionEnum.ACTIVE);
        long attributionsExpirantBientot = attributionRepository
                .countByStatutAndDateFinBetween(
                        StatutAttributionEnum.ACTIVE, 
                        aujourdHui, 
                        aujourdHui.plusDays(30));
        long attributionsTerminees = attributionRepository.countByStatut(StatutAttributionEnum.EXPIREE);
        
        // Statistiques Financières
        BigDecimal totalRevenusAnnee = paiementRepository
                .sumMontantByStatutAndDatePaiementAfter(
                        StatutPaiementEnum.PAYE, 
                        debutAnnee.atStartOfDay());
        BigDecimal totalRevenusMois = paiementRepository
                .sumMontantByStatutAndDatePaiementAfter(
                        StatutPaiementEnum.PAYE, 
                        debutMois.atStartOfDay());
        BigDecimal montantImpayeTotal = paiementRepository
                .sumMontantByStatutIn(List.of(StatutPaiementEnum.EN_ATTENTE, StatutPaiementEnum.EN_RETARD));
        long paiementsEnRetard = paiementRepository.countByStatut(StatutPaiementEnum.EN_RETARD);
        
        BigDecimal totalAttendu = paiementRepository.sumMontant();
        BigDecimal totalPercu = totalRevenusAnnee != null ? totalRevenusAnnee : BigDecimal.ZERO;
        double tauxRecouvrement = totalAttendu != null && totalAttendu.compareTo(BigDecimal.ZERO) > 0 ?
                totalPercu.divide(totalAttendu, 4, RoundingMode.HALF_UP).doubleValue() * 100 : 0;
        
        // Statistiques Incidents
        long totalIncidents = incidentRepository.count();
        long incidentsOuverts = incidentRepository.countByStatut(StatutIncidentEnum.OUVERT);
        long incidentsCritiques = incidentRepository.countByUrgence(UrgenceEnum.CRITIQUE);
        
        // Statistiques Maintenance
        long maintenancesPlanifiees = maintenanceRepository.countByStatut(StatutMaintenanceEnum.PLANIFIEE);
        long maintenancesEnCours = maintenanceRepository.countByStatut(StatutMaintenanceEnum.EN_COURS);
        long maintenancesTerminees = maintenanceRepository.countByStatut(StatutMaintenanceEnum.TERMINEE);
        
        // Statistiques Utilisateurs
        long totalUtilisateurs = utilisateurRepository.count();
        long utilisateursActifs = utilisateurRepository.countByActif(true);
        long nouveauxUtilisateursMois = utilisateurRepository.countByDateCreationAfter(
                debutMois.atStartOfDay());
        
        return StatistiquesGlobalesDto.builder()
                // Logements
                .totalLogements(totalLogements)
                .logementsDisponibles(logementsDisponibles)
                .logementsOccupes(logementsOccupes)
                .logementsEnMaintenance(logementsEnMaintenance)
                .logementsArchives(logementsArchives)
                .tauxOccupation(tauxOccupation)
                // Étudiants
                .totalEtudiants(totalEtudiants)
                .etudiantsActifs(etudiantsActifs)
                .etudiantsEnAttente(etudiantsEnAttente)
                .etudiantsRejetes(etudiantsRejetes)
                .nouveauxEtudiantsMois(nouveauxEtudiantsMois)
                // Demandes
                .totalDemandes(totalDemandes)
                .demandesEnAttente(demandesEnAttente)
                .demandesApprouvees(demandesApprouvees)
                .demandesRejetees(demandesRejetees)
                .tauxApprobation(tauxApprobation)
                // Attributions
                .totalAttributions(totalAttributions)
                .attributionsActives(attributionsActives)
                .attributionsExpirantBientot(attributionsExpirantBientot)
                .attributionsTerminees(attributionsTerminees)
                // Financier
                .totalRevenusAnnee(totalRevenusAnnee != null ? totalRevenusAnnee : BigDecimal.ZERO)
                .totalRevenusMois(totalRevenusMois != null ? totalRevenusMois : BigDecimal.ZERO)
                .montantImpayeTotal(montantImpayeTotal != null ? montantImpayeTotal : BigDecimal.ZERO)
                .paiementsEnRetard(paiementsEnRetard)
                .tauxRecouvrement(tauxRecouvrement)
                // Incidents
                .totalIncidents(totalIncidents)
                .incidentsOuverts(incidentsOuverts)
                .incidentsCritiques(incidentsCritiques)
                // Maintenance
                .maintenancesPlanifiees(maintenancesPlanifiees)
                .maintenancesEnCours(maintenancesEnCours)
                .maintenancesTerminees(maintenancesTerminees)
                // Utilisateurs
                .totalUtilisateurs(totalUtilisateurs)
                .utilisateursActifs(utilisateursActifs)
                .nouveauxUtilisateursMois(nouveauxUtilisateursMois)
                .build();
    }
    
    /**
     * Récupère les métriques de performance
     */
    public MetriquePerformanceDto getMetriquesPerformance() {
        StatistiquesGlobalesDto stats = getStatistiquesGlobales();
        
        return MetriquePerformanceDto.builder()
                .nomMetrique("Performance Globale")
                .categorie("GENERAL")
                .valeurActuelle(stats.getTauxOccupation())
                .valeurCible(85.0) // Objectif de 85% d'occupation
                .unite("%")
                .pourcentageObjectif(stats.getTauxOccupation() / 85.0 * 100)
                .tendance(MetriquePerformanceDto.calculerTendance(stats.getTauxOccupation(), 80.0))
                .dateCalcul(LocalDateTime.now())
                .description("Taux d'occupation global des logements")
                .statut(MetriquePerformanceDto.calculerStatut(stats.getTauxOccupation() / 85.0 * 100))
                .build();
    }
    
    /**
     * Récupère les alertes actives du système
     */
    public List<AlerteDto> getAlertes() {
        List<AlerteDto> alertes = new ArrayList<>();
        LocalDate aujourdHui = LocalDate.now();
        
        // Alertes paiements en retard
        long paiementsRetard = paiementRepository.countByStatut(StatutPaiementEnum.EN_RETARD);
        if (paiementsRetard > 0) {
            alertes.add(AlerteDto.builder()
                    .type(AlerteDto.TypeAlerte.PAIEMENT_RETARD.name())
                    .titre("Paiements en retard")
                    .message(paiementsRetard + " paiement(s) en retard nécessite(nt) votre attention")
                    .priorite(AlerteDto.Priorite.HAUTE.name())
                    .categorie("FINANCE")
                    .lue(false)
                    .traitee(false)
                    .dateCreation(LocalDateTime.now())
                    .actionRecommandee("Envoyer des rappels aux étudiants concernés")
                    .build());
        }
        
        // Alertes attributions expirant bientôt
        long attributionsExpirant = attributionRepository
                .countByStatutAndDateFinBetween(StatutAttributionEnum.ACTIVE, aujourdHui, aujourdHui.plusDays(30));
        if (attributionsExpirant > 0) {
            alertes.add(AlerteDto.builder()
                    .type(AlerteDto.TypeAlerte.ATTRIBUTION_EXPIRE_BIENTOT.name())
                    .titre("Attributions expirant bientôt")
                    .message(attributionsExpirant + " attribution(s) expire(nt) dans les 30 prochains jours")
                    .priorite(AlerteDto.Priorite.MOYENNE.name())
                    .categorie("LOGEMENT")
                    .lue(false)
                    .traitee(false)
                    .dateCreation(LocalDateTime.now())
                    .actionRecommandee("Contacter les étudiants pour renouvellement ou libération")
                    .build());
        }
        
        // Alertes incidents critiques
        long incidentsCritiques = incidentRepository.countByUrgence(UrgenceEnum.CRITIQUE);
        if (incidentsCritiques > 0) {
            alertes.add(AlerteDto.builder()
                    .type(AlerteDto.TypeAlerte.INCIDENT_CRITIQUE.name())
                    .titre("Incidents critiques")
                    .message(incidentsCritiques + " incident(s) critique(s) en attente de traitement")
                    .priorite(AlerteDto.Priorite.CRITIQUE.name())
                    .categorie("MAINTENANCE")
                    .lue(false)
                    .traitee(false)
                    .dateCreation(LocalDateTime.now())
                    .actionRecommandee("Intervenir immédiatement")
                    .build());
        }
        
        // Alertes demandes en attente depuis longtemps
        long demandesEnAttente = demandeRepository.countByStatut(StatutDemandeEnum.EN_ATTENTE);
        if (demandesEnAttente > 10) {
            alertes.add(AlerteDto.builder()
                    .type(AlerteDto.TypeAlerte.DEMANDE_EN_ATTENTE.name())
                    .titre("Demandes en attente")
                    .message(demandesEnAttente + " demande(s) en attente de traitement")
                    .priorite(AlerteDto.Priorite.MOYENNE.name())
                    .categorie("ETUDIANT")
                    .lue(false)
                    .traitee(false)
                    .dateCreation(LocalDateTime.now())
                    .actionRecommandee("Traiter les demandes en attente")
                    .build());
        }
        
        // Alerte taux d'occupation bas
        long totalLogements = logementRepository.count();
        long logementsOccupes = logementRepository.countByStatut(StatutLogementEnum.OCCUPE);
        double tauxOccupation = totalLogements > 0 ? (double) logementsOccupes / totalLogements * 100 : 0;
        if (tauxOccupation < 50) {
            alertes.add(AlerteDto.builder()
                    .type(AlerteDto.TypeAlerte.OCCUPATION_BASSE.name())
                    .titre("Taux d'occupation bas")
                    .message("Le taux d'occupation est de " + String.format("%.1f", tauxOccupation) + "%")
                    .priorite(AlerteDto.Priorite.MOYENNE.name())
                    .categorie("LOGEMENT")
                    .lue(false)
                    .traitee(false)
                    .dateCreation(LocalDateTime.now())
                    .actionRecommandee("Analyser les causes et promouvoir les logements disponibles")
                    .build());
        }
        
        return alertes;
    }
}
