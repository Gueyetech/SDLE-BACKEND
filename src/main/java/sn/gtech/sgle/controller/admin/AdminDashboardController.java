package sn.gtech.sgle.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.common.ApiResponse;
import sn.gtech.sgle.dto.dashboard.AlerteDto;
import sn.gtech.sgle.dto.dashboard.MetriquePerformanceDto;
import sn.gtech.sgle.dto.dashboard.StatistiquesGlobalesDto;
import sn.gtech.sgle.service.DashboardService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Dashboard", description = "Tableau de bord et statistiques globales")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques globales", description = "Récupère les statistiques globales du système (logements, étudiants, demandes, paiements)")
    public ResponseEntity<ApiResponse<StatistiquesGlobalesDto>> getStatistiquesGlobales() {
        StatistiquesGlobalesDto stats = dashboardService.getStatistiquesGlobales();
        return ResponseEntity.ok(ApiResponse.success(stats, "Statistiques récupérées avec succès"));
    }

    @GetMapping("/metriques")
    @Operation(summary = "Métriques de performance", description = "Récupère les métriques de performance du système")
    public ResponseEntity<ApiResponse<MetriquePerformanceDto>> getMetriquesPerformance() {
        MetriquePerformanceDto metriques = dashboardService.getMetriquesPerformance();
        return ResponseEntity.ok(ApiResponse.success(metriques, "Métriques récupérées"));
    }

    @GetMapping("/alertes")
    @Operation(summary = "Alertes système", description = "Récupère les alertes et notifications importantes")
    public ResponseEntity<ApiResponse<List<AlerteDto>>> getAlertes() {
        List<AlerteDto> alertes = dashboardService.getAlertes();
        return ResponseEntity.ok(ApiResponse.success(alertes, alertes.size() + " alerte(s) active(s)"));
    }

    @GetMapping("/resume")
    @Operation(summary = "Résumé complet", description = "Récupère un résumé complet du tableau de bord (statistiques + métriques + alertes)")
    public ResponseEntity<ApiResponse<DashboardResume>> getResume() {
        DashboardResume resume = DashboardResume.builder()
                .statistiques(dashboardService.getStatistiquesGlobales())
                .metriques(dashboardService.getMetriquesPerformance())
                .alertes(dashboardService.getAlertes())
                .build();
        return ResponseEntity.ok(ApiResponse.success(resume, "Résumé du dashboard récupéré"));
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DashboardResume {
        private StatistiquesGlobalesDto statistiques;
        private MetriquePerformanceDto metriques;
        private List<AlerteDto> alertes;
    }
}
