package sn.gtech.sgle.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.common.ApiResponse;
import sn.gtech.sgle.dto.rapport.GenererRapportRequest;
import sn.gtech.sgle.dto.rapport.RapportResponseDto;
import sn.gtech.sgle.entity.enums.TypeRapportEnum;
import sn.gtech.sgle.service.RapportService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/rapports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Rapports", description = "Génération de rapports et exportation de données")
public class AdminRapportController {

    private final RapportService rapportService;

    @PostMapping
    @Operation(summary = "Générer un rapport", description = "Génère un nouveau rapport selon le type et la période spécifiés")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Rapport généré avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ApiResponse<RapportResponseDto>> genererRapport(
            @Valid @RequestBody GenererRapportRequest request) {
        RapportResponseDto rapport = rapportService.genererRapport(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rapport, "Rapport généré avec succès"));
    }

    @GetMapping
    @Operation(summary = "Lister les rapports", description = "Liste tous les rapports générés")
    public ResponseEntity<ApiResponse<List<RapportResponseDto>>> listerRapports() {
        List<RapportResponseDto> rapports = rapportService.listerRapports();
        return ResponseEntity.ok(ApiResponse.success(rapports, rapports.size() + " rapport(s) trouvé(s)"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un rapport", description = "Récupère les détails d'un rapport")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rapport trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Rapport non trouvé")
    })
    public ResponseEntity<ApiResponse<RapportResponseDto>> getRapport(
            @Parameter(description = "ID du rapport") @PathVariable UUID id) {
        RapportResponseDto rapport = rapportService.getRapport(id);
        return ResponseEntity.ok(ApiResponse.success(rapport, "Rapport récupéré"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un rapport", description = "Supprime un rapport généré")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rapport supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Rapport non trouvé")
    })
    public ResponseEntity<ApiResponse<Void>> supprimerRapport(
            @Parameter(description = "ID du rapport") @PathVariable UUID id) {
        rapportService.supprimerRapport(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Rapport supprimé avec succès"));
    }

    // ============ Export de données ============

    @GetMapping("/export")
    @Operation(summary = "Exporter des données", description = "Exporte les données au format demandé (CSV, JSON, PDF, Excel)")
    public ResponseEntity<byte[]> exporterDonnees(
            @Parameter(description = "Type de rapport") @RequestParam TypeRapportEnum type,
            @Parameter(description = "Format d'export") @RequestParam GenererRapportRequest.FormatExport format,
            @Parameter(description = "Date de début de la période") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodeDebut,
            @Parameter(description = "Date de fin de la période") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodeFin) {

        RapportService.ExportResult result = rapportService.exporterDonnees(type, format, periodeDebut, periodeFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(result.getContentType()));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(result.getNomFichier())
                .build());
        headers.setContentLength(result.getTaille());

        return new ResponseEntity<>(result.getContenu(), headers, HttpStatus.OK);
    }

    // ============ Rapports rapides ============

    @GetMapping("/occupation")
    @Operation(summary = "Rapport d'occupation rapide", description = "Génère rapidement un rapport d'occupation")
    public ResponseEntity<ApiResponse<RapportResponseDto>> rapportOccupationRapide(
            @Parameter(description = "Date de début") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodeDebut,
            @Parameter(description = "Date de fin") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodeFin) {

        GenererRapportRequest request = GenererRapportRequest.builder()
                .type(TypeRapportEnum.OCCUPATION)
                .format(GenererRapportRequest.FormatExport.PDF)
                .periodeDebut(periodeDebut)
                .periodeFin(periodeFin)
                .build();

        RapportResponseDto rapport = rapportService.genererRapport(request);
        return ResponseEntity.ok(ApiResponse.success(rapport, "Rapport d'occupation généré"));
    }

    @GetMapping("/financier")
    @Operation(summary = "Rapport financier rapide", description = "Génère rapidement un rapport financier")
    public ResponseEntity<ApiResponse<RapportResponseDto>> rapportFinancierRapide(
            @Parameter(description = "Date de début") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodeDebut,
            @Parameter(description = "Date de fin") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodeFin) {

        GenererRapportRequest request = GenererRapportRequest.builder()
                .type(TypeRapportEnum.FINANCIER)
                .format(GenererRapportRequest.FormatExport.PDF)
                .periodeDebut(periodeDebut)
                .periodeFin(periodeFin)
                .build();

        RapportResponseDto rapport = rapportService.genererRapport(request);
        return ResponseEntity.ok(ApiResponse.success(rapport, "Rapport financier généré"));
    }

    @GetMapping("/demandes")
    @Operation(summary = "Rapport des demandes rapide", description = "Génère rapidement un rapport des demandes")
    public ResponseEntity<ApiResponse<RapportResponseDto>> rapportDemandesRapide(
            @Parameter(description = "Date de début") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodeDebut,
            @Parameter(description = "Date de fin") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodeFin) {

        GenererRapportRequest request = GenererRapportRequest.builder()
                .type(TypeRapportEnum.DEMANDES)
                .format(GenererRapportRequest.FormatExport.PDF)
                .periodeDebut(periodeDebut)
                .periodeFin(periodeFin)
                .build();

        RapportResponseDto rapport = rapportService.genererRapport(request);
        return ResponseEntity.ok(ApiResponse.success(rapport, "Rapport des demandes généré"));
    }

    @GetMapping("/performance")
    @Operation(summary = "Rapport de performance rapide", description = "Génère rapidement un rapport de performance")
    public ResponseEntity<ApiResponse<RapportResponseDto>> rapportPerformanceRapide(
            @Parameter(description = "Date de début") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodeDebut,
            @Parameter(description = "Date de fin") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodeFin) {

        GenererRapportRequest request = GenererRapportRequest.builder()
                .type(TypeRapportEnum.PERFORMANCE)
                .format(GenererRapportRequest.FormatExport.PDF)
                .periodeDebut(periodeDebut)
                .periodeFin(periodeFin)
                .build();

        RapportResponseDto rapport = rapportService.genererRapport(request);
        return ResponseEntity.ok(ApiResponse.success(rapport, "Rapport de performance généré"));
    }
}
