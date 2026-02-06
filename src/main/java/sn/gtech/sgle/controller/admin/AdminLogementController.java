package sn.gtech.sgle.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.common.ApiResponse;
import sn.gtech.sgle.dto.logement.CreerLogementRequest;
import sn.gtech.sgle.dto.logement.LogementResponseDto;
import sn.gtech.sgle.dto.logement.ModifierLogementRequest;
import sn.gtech.sgle.dto.logement.PlanifierMaintenanceRequest;
import sn.gtech.sgle.service.LogementService;

import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/logements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Logements", description = "Gestion complète des logements")
public class AdminLogementController {

    private final LogementService logementService;

    @PostMapping
    @Operation(summary = "Créer un logement", description = "Crée un nouveau logement avec code unique généré automatiquement (ex: LOG-2026-001)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Logement créé avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ApiResponse<LogementResponseDto>> creerLogement(
            @Valid @RequestBody CreerLogementRequest request) {
        LogementResponseDto logement = logementService.creerLogement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(logement, "Logement créé avec succès"));
    }

    @GetMapping
    @Operation(summary = "Lister les logements", description = "Liste tous les logements")
    public ResponseEntity<ApiResponse<List<LogementResponseDto>>> listerLogements() {
        List<LogementResponseDto> logements = logementService.getTousLesLogements();
        return ResponseEntity.ok(ApiResponse.success(logements, logements.size() + " logement(s) trouvé(s)"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un logement", description = "Récupère les détails complets d'un logement")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logement trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Logement non trouvé")
    })
    public ResponseEntity<ApiResponse<LogementResponseDto>> getLogement(
            @Parameter(description = "ID du logement") @PathVariable UUID id) {
        LogementResponseDto logement = logementService.getLogementParId(id);
        return ResponseEntity.ok(ApiResponse.success(logement, "Logement récupéré"));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Obtenir un logement par code", description = "Récupère un logement par son code unique")
    public ResponseEntity<ApiResponse<LogementResponseDto>> getLogementByCode(
            @Parameter(description = "Code du logement") @PathVariable String code) {
        LogementResponseDto logement = logementService.getLogementParCode(code);
        return ResponseEntity.ok(ApiResponse.success(logement, "Logement récupéré"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un logement", description = "Met à jour les informations d'un logement")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logement modifié avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Logement non trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ApiResponse<LogementResponseDto>> modifierLogement(
            @Parameter(description = "ID du logement") @PathVariable UUID id,
            @Valid @RequestBody ModifierLogementRequest request) {
        LogementResponseDto logement = logementService.modifierLogement(id, request);
        return ResponseEntity.ok(ApiResponse.success(logement, "Logement modifié avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un logement", description = "Supprime un logement (impossible si attributions actives)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logement supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Logement non trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Suppression impossible - attributions actives")
    })
    public ResponseEntity<ApiResponse<Void>> supprimerLogement(
            @Parameter(description = "ID du logement") @PathVariable UUID id) {
        logementService.supprimerLogement(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Logement supprimé avec succès"));
    }

    // ============ Archivage ============

    @PatchMapping("/{id}/archiver")
    @Operation(summary = "Archiver un logement", description = "Archive un logement (désactivé mais conservé)")
    public ResponseEntity<ApiResponse<LogementResponseDto>> archiverLogement(
            @Parameter(description = "ID du logement") @PathVariable UUID id) {
        LogementResponseDto logement = logementService.archiverLogement(id);
        return ResponseEntity.ok(ApiResponse.success(logement, "Logement archivé"));
    }

    // ============ Maintenance ============

    @PostMapping("/{id}/maintenance")
    @Operation(summary = "Planifier une maintenance", description = "Planifie une maintenance pour un logement")
    public ResponseEntity<ApiResponse<LogementResponseDto>> planifierMaintenance(
            @Parameter(description = "ID du logement") @PathVariable UUID id,
            @Valid @RequestBody PlanifierMaintenanceRequest request) {
        LogementResponseDto logement = logementService.planifierMaintenance(id, request);
        return ResponseEntity.ok(ApiResponse.success(logement, "Maintenance planifiée"));
    }

    // ============ Statistiques ============

    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques logements", description = "Récupère les statistiques globales des logements")
    public ResponseEntity<ApiResponse<LogementService.LogementStatistiques>> getStatistiques() {
        LogementService.LogementStatistiques stats = logementService.getStatistiques();
        return ResponseEntity.ok(ApiResponse.success(stats, "Statistiques récupérées"));
    }
}
