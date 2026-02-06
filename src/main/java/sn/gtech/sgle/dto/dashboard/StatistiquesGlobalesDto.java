package sn.gtech.sgle.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour les statistiques globales du dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesGlobalesDto {
    
    // Statistiques Logements
    private Long totalLogements;
    private Long logementsDisponibles;
    private Long logementsOccupes;
    private Long logementsEnMaintenance;
    private Long logementsArchives;
    private Double tauxOccupation;
    
    // Statistiques Étudiants
    private Long totalEtudiants;
    private Long etudiantsActifs;
    private Long etudiantsEnAttente;
    private Long etudiantsRejetes;
    private Long nouveauxEtudiantsMois;
    
    // Statistiques Demandes
    private Long totalDemandes;
    private Long demandesEnAttente;
    private Long demandesApprouvees;
    private Long demandesRejetees;
    private Double tauxApprobation;
    
    // Statistiques Attributions
    private Long totalAttributions;
    private Long attributionsActives;
    private Long attributionsExpirantBientot; // Dans les 30 prochains jours
    private Long attributionsTerminees;
    
    // Statistiques Financières
    private BigDecimal totalRevenusAnnee;
    private BigDecimal totalRevenusMois;
    private BigDecimal montantImpayeTotal;
    private Long paiementsEnRetard;
    private Double tauxRecouvrement;
    
    // Statistiques Incidents
    private Long totalIncidents;
    private Long incidentsOuverts;
    private Long incidentsCritiques;
    private Double tempsResolutionMoyen; // En heures
    
    // Statistiques Maintenance
    private Long maintenancesPlanifiees;
    private Long maintenancesEnCours;
    private Long maintenancesTerminees;
    
    // Statistiques Utilisateurs
    private Long totalUtilisateurs;
    private Long utilisateursActifs;
    private Long nouveauxUtilisateursMois;
}
