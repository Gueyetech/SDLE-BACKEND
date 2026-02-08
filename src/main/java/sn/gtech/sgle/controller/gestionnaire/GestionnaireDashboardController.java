package sn.gtech.sgle.controller.gestionnaire;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.attribution.AttributionResponseDto;
import sn.gtech.sgle.dto.dashboard.AlerteDto;
import sn.gtech.sgle.dto.dashboard.MetriquePerformanceDto;
import sn.gtech.sgle.dto.dashboard.StatistiquesGlobalesDto;
import sn.gtech.sgle.dto.demande.DemandeResponseDto;
import sn.gtech.sgle.dto.incident.IncidentResponseDto;
import sn.gtech.sgle.entity.Utilisateur;
import sn.gtech.sgle.repository.UtilisateurRepository;
import sn.gtech.sgle.service.AttributionService;
import sn.gtech.sgle.service.DashboardService;
import sn.gtech.sgle.service.DemandeLogementService;
import sn.gtech.sgle.service.IncidentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour le tableau de bord du gestionnaire
 */
@RestController
@RequestMapping("/api/gestionnaire/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
@RequiredArgsConstructor
@Tag(name = "Gestionnaire - Dashboard", description = "API du tableau de bord pour les gestionnaires")
public class GestionnaireDashboardController {
    
    private final DashboardService dashboardService;
    private final DemandeLogementService demandeService;
    private final IncidentService incidentService;
    private final AttributionService attributionService;
    private final UtilisateurRepository utilisateurRepository;
    
    // ==================== STATISTIQUES GLOBALES ====================
    
    @GetMapping("/statistiques")
    @Operation(summary = "Récupère les statistiques globales")
    public ResponseEntity<StatistiquesGlobalesDto> getStatistiquesGlobales() {
        return ResponseEntity.ok(dashboardService.getStatistiquesGlobales());
    }
    
    @GetMapping("/metriques")
    @Operation(summary = "Récupère les métriques de performance")
    public ResponseEntity<MetriquePerformanceDto> getMetriquesPerformance() {
        return ResponseEntity.ok(dashboardService.getMetriquesPerformance());
    }
    
    // ==================== ALERTES ====================
    
    @GetMapping("/alertes")
    @Operation(summary = "Récupère toutes les alertes actives")
    public ResponseEntity<List<AlerteDto>> getAlertes() {
        return ResponseEntity.ok(dashboardService.getAlertes());
    }
    
    // ==================== ATTRIBUTIONS EXPIRANT BIENTOT ====================
    
    @GetMapping("/attributions-expirant")
    @Operation(summary = "Récupère les attributions expirant dans les 30 prochains jours")
    public ResponseEntity<List<AttributionResponseDto>> getAttributionsExpirantBientot() {
        return ResponseEntity.ok(attributionService.getAttributionsExpirantSous(30));
    }
    
    @GetMapping("/attributions-expirant/{jours}")
    @Operation(summary = "Récupère les attributions expirant dans X jours")
    public ResponseEntity<List<AttributionResponseDto>> getAttributionsExpirantSous(@PathVariable int jours) {
        return ResponseEntity.ok(attributionService.getAttributionsExpirantSous(jours));
    }
    
    // ==================== INCIDENTS URGENTS ====================
    
    @GetMapping("/incidents-urgents")
    @Operation(summary = "Récupère les incidents urgents (CRITIQUE et HAUTE)")
    public ResponseEntity<List<IncidentResponseDto>> getIncidentsUrgents() {
        return ResponseEntity.ok(incidentService.getIncidentsUrgents());
    }
    
    // ==================== DEMANDES EN ATTENTE ====================
    
    @GetMapping("/demandes-en-attente")
    @Operation(summary = "Récupère les demandes en attente")
    public ResponseEntity<List<DemandeResponseDto>> getDemandesEnAttente() {
        return ResponseEntity.ok(demandeService.getDemandesEnAttente());
    }
    
    // ==================== RESUME POUR LE GESTIONNAIRE ====================
    
    @GetMapping("/resume")
    @Operation(summary = "Récupère un résumé du dashboard pour le gestionnaire")
    public ResponseEntity<Map<String, Object>> getResume(Authentication authentication) {
        Map<String, Object> resume = new HashMap<>();
        
        // Statistiques demandes
        Map<String, Object> statsDemandes = demandeService.getStatistiques();
        resume.put("demandes", statsDemandes);
        
        // Statistiques incidents
        Map<String, Object> statsIncidents = incidentService.getStatistiques();
        resume.put("incidents", statsIncidents);
        
        // Statistiques attributions
        AttributionService.AttributionStatistiques statsAttributions = attributionService.getStatistiques();
        resume.put("attributions", statsAttributions);
        
        // Alertes
        List<AlerteDto> alertes = dashboardService.getAlertes();
        resume.put("alertes", alertes);
        resume.put("nombreAlertes", alertes.size());
        
        // Incidents urgents
        List<IncidentResponseDto> incidentsUrgents = incidentService.getIncidentsUrgents();
        resume.put("nombreIncidentsUrgents", incidentsUrgents.size());
        
        // Attributions expirant bientôt
        List<AttributionResponseDto> attributionsExpirant = attributionService.getAttributionsExpirantSous(30);
        resume.put("nombreAttributionsExpirant", attributionsExpirant.size());
        
        return ResponseEntity.ok(resume);
    }
    
    // ==================== MON PORTEFEUILLE ====================
    
    @GetMapping("/mon-portefeuille")
    @Operation(summary = "Récupère le portefeuille du gestionnaire connecté")
    public ResponseEntity<Map<String, Object>> getMonPortefeuille(Authentication authentication) {
        Utilisateur gestionnaire = getUtilisateurConnecte(authentication);
        Map<String, Object> portefeuille = new HashMap<>();
        
        // Mes demandes en cours de traitement
        List<DemandeResponseDto> mesDemandes = demandeService.getDemandesParGestionnaire(gestionnaire.getId());
        portefeuille.put("demandes", mesDemandes);
        portefeuille.put("nombreDemandes", mesDemandes.size());
        
        // Mes incidents assignés
        List<IncidentResponseDto> mesIncidents = incidentService.getIncidentsParGestionnaire(gestionnaire.getId());
        portefeuille.put("incidents", mesIncidents);
        portefeuille.put("nombreIncidents", mesIncidents.size());
        
        return ResponseEntity.ok(portefeuille);
    }
    
    // ==================== UTILITAIRES ====================
    
    private Utilisateur getUtilisateurConnecte(Authentication authentication) {
        String email = authentication.getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
