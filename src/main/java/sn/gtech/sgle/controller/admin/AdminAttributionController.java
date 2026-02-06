package sn.gtech.sgle.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.attribution.AttributionResponseDto;
import sn.gtech.sgle.dto.attribution.CreerAttributionRequest;
import sn.gtech.sgle.dto.attribution.ModifierAttributionRequest;
import sn.gtech.sgle.dto.common.ApiResponse;
import sn.gtech.sgle.service.AttributionService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/attributions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Attributions", description = "Gestion administrative des attributions de logements")
public class AdminAttributionController {

    private final AttributionService attributionService;

    @PostMapping
    @Operation(summary = "Créer une attribution", description = "Attribue manuellement un logement à un étudiant")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Attribution créée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides ou étudiant/logement non éligible"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Étudiant ou logement non trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflit - étudiant déjà logé ou logement non disponible")
    })
    public ResponseEntity<ApiResponse<AttributionResponseDto>> creerAttribution(
            @Valid @RequestBody CreerAttributionRequest request) {
        AttributionResponseDto attribution = attributionService.creerAttribution(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(attribution, "Attribution créée avec succès"));
    }

    @GetMapping
    @Operation(summary = "Lister les attributions", description = "Liste toutes les attributions")
    public ResponseEntity<ApiResponse<List<AttributionResponseDto>>> listerAttributions() {
        List<AttributionResponseDto> attributions = attributionService.getToutesLesAttributions();
        return ResponseEntity.ok(ApiResponse.success(attributions, attributions.size() + " attribution(s) trouvée(s)"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une attribution", description = "Récupère les détails complets d'une attribution")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Attribution trouvée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Attribution non trouvée")
    })
    public ResponseEntity<ApiResponse<AttributionResponseDto>> getAttribution(
            @Parameter(description = "ID de l'attribution") @PathVariable UUID id) {
        AttributionResponseDto attribution = attributionService.getAttributionParId(id);
        return ResponseEntity.ok(ApiResponse.success(attribution, "Attribution récupérée"));
    }

    @GetMapping("/contrat/{numeroContrat}")
    @Operation(summary = "Obtenir une attribution par numéro de contrat", description = "Récupère une attribution par son numéro de contrat")
    public ResponseEntity<ApiResponse<AttributionResponseDto>> getAttributionByContrat(
            @Parameter(description = "Numéro de contrat") @PathVariable String numeroContrat) {
        AttributionResponseDto attribution = attributionService.getAttributionParNumeroContrat(numeroContrat);
        return ResponseEntity.ok(ApiResponse.success(attribution, "Attribution récupérée"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une attribution", description = "Met à jour les informations d'une attribution")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Attribution modifiée avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Attribution non trouvée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ApiResponse<AttributionResponseDto>> modifierAttribution(
            @Parameter(description = "ID de l'attribution") @PathVariable UUID id,
            @Valid @RequestBody ModifierAttributionRequest request) {
        AttributionResponseDto attribution = attributionService.modifierAttribution(id, request);
        return ResponseEntity.ok(ApiResponse.success(attribution, "Attribution modifiée avec succès"));
    }

    // ============ Actions sur les attributions ============

    @PatchMapping("/{id}/revoquer")
    @Operation(summary = "Révoquer une attribution", description = "Révoque une attribution active (expulsion, non-paiement, etc.)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Attribution révoquée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "L'attribution n'est pas active"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Attribution non trouvée")
    })
    public ResponseEntity<ApiResponse<AttributionResponseDto>> revoquerAttribution(
            @Parameter(description = "ID de l'attribution") @PathVariable UUID id,
            @Parameter(description = "Motif de la révocation") @RequestParam(required = false) String motif) {
        AttributionResponseDto attribution = attributionService.revoquerAttribution(id, motif);
        return ResponseEntity.ok(ApiResponse.success(attribution, "Attribution révoquée"));
    }

    @PatchMapping("/{id}/terminer")
    @Operation(summary = "Terminer une attribution", description = "Termine normalement une attribution (fin de contrat)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Attribution terminée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "L'attribution n'est pas active")
    })
    public ResponseEntity<ApiResponse<AttributionResponseDto>> terminerAttribution(
            @Parameter(description = "ID de l'attribution") @PathVariable UUID id) {
        AttributionResponseDto attribution = attributionService.terminerAttribution(id);
        return ResponseEntity.ok(ApiResponse.success(attribution, "Attribution terminée"));
    }

    @PatchMapping("/{id}/prolonger")
    @Operation(summary = "Prolonger une attribution", description = "Prolonge la durée d'une attribution active")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Attribution prolongée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "La nouvelle date doit être postérieure à la date actuelle")
    })
    public ResponseEntity<ApiResponse<AttributionResponseDto>> prolongerAttribution(
            @Parameter(description = "ID de l'attribution") @PathVariable UUID id,
            @Parameter(description = "Nouvelle date de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nouvelleDateFin) {
        AttributionResponseDto attribution = attributionService.prolongerAttribution(id, nouvelleDateFin);
        return ResponseEntity.ok(ApiResponse.success(attribution, "Attribution prolongée jusqu'au " + nouvelleDateFin));
    }

    // ============ Check-in / Check-out ============

    @PatchMapping("/{id}/checkin")
    @Operation(summary = "Enregistrer le check-in", description = "Enregistre l'entrée de l'étudiant dans le logement")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Check-in enregistré"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Check-in déjà effectué")
    })
    public ResponseEntity<ApiResponse<AttributionResponseDto>> checkIn(
            @Parameter(description = "ID de l'attribution") @PathVariable UUID id) {
        AttributionResponseDto attribution = attributionService.enregistrerCheckIn(id);
        return ResponseEntity.ok(ApiResponse.success(attribution, "Check-in enregistré"));
    }

    @PatchMapping("/{id}/checkout")
    @Operation(summary = "Enregistrer le check-out", description = "Enregistre la sortie de l'étudiant du logement")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Check-out enregistré"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Check-in non effectué")
    })
    public ResponseEntity<ApiResponse<AttributionResponseDto>> checkOut(
            @Parameter(description = "ID de l'attribution") @PathVariable UUID id) {
        AttributionResponseDto attribution = attributionService.enregistrerCheckOut(id);
        return ResponseEntity.ok(ApiResponse.success(attribution, "Check-out enregistré"));
    }

    // ============ Alertes et surveillance ============

    @GetMapping("/expirant")
    @Operation(summary = "Attributions expirant bientôt", description = "Liste les attributions qui arrivent à expiration dans les N prochains jours")
    public ResponseEntity<ApiResponse<List<AttributionResponseDto>>> getAttributionsExpirant(
            @Parameter(description = "Nombre de jours") @RequestParam(defaultValue = "30") int jours) {
        List<AttributionResponseDto> attributions = attributionService.getAttributionsExpirantSous(jours);
        return ResponseEntity.ok(ApiResponse.success(attributions, 
                attributions.size() + " attribution(s) expirant dans les " + jours + " prochains jours"));
    }

    // ============ Statistiques ============

    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques attributions", description = "Récupère les statistiques globales des attributions")
    public ResponseEntity<ApiResponse<AttributionService.AttributionStatistiques>> getStatistiques() {
        AttributionService.AttributionStatistiques stats = attributionService.getStatistiques();
        return ResponseEntity.ok(ApiResponse.success(stats, "Statistiques récupérées"));
    }
}
