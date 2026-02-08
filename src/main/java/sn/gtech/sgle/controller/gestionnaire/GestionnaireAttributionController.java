package sn.gtech.sgle.controller.gestionnaire;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.attribution.AttributionResponseDto;
import sn.gtech.sgle.dto.attribution.CreerAttributionRequest;
import sn.gtech.sgle.dto.attribution.ModifierAttributionRequest;
import sn.gtech.sgle.service.AttributionService;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des attributions par le gestionnaire
 */
@RestController
@RequestMapping("/api/gestionnaire/attributions")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
@RequiredArgsConstructor
@Tag(name = "Gestionnaire - Attributions", description = "API de gestion des attributions pour les gestionnaires")
public class GestionnaireAttributionController {
    
    private final AttributionService attributionService;
    
    // ==================== LECTURE ====================
    

    @GetMapping("/toutes")
    @Operation(summary = "Liste toutes les attributions (sans pagination)")
    public ResponseEntity<List<AttributionResponseDto>> getToutesAttributionsSansPagination() {
        return ResponseEntity.ok(attributionService.getToutesLesAttributions());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupère une attribution par son ID")
    public ResponseEntity<AttributionResponseDto> getAttributionParId(@PathVariable UUID id) {
        return ResponseEntity.ok(attributionService.getAttributionParId(id));
    }
    
    @GetMapping("/contrat/{numeroContrat}")
    @Operation(summary = "Récupère une attribution par son numéro de contrat")
    public ResponseEntity<AttributionResponseDto> getAttributionParNumeroContrat(@PathVariable String numeroContrat) {
        return ResponseEntity.ok(attributionService.getAttributionParNumeroContrat(numeroContrat));
    }
    
    @GetMapping("/expirant/{jours}")
    @Operation(summary = "Récupère les attributions expirant dans X jours")
    public ResponseEntity<List<AttributionResponseDto>> getAttributionsExpirantSous(@PathVariable int jours) {
        return ResponseEntity.ok(attributionService.getAttributionsExpirantSous(jours));
    }
    
    // ==================== CREATION ====================
    
    @PostMapping
    @Operation(summary = "Crée une nouvelle attribution")
    public ResponseEntity<AttributionResponseDto> creerAttribution(@Valid @RequestBody CreerAttributionRequest request) {
        return ResponseEntity.ok(attributionService.creerAttribution(request));
    }
    
    // ==================== MODIFICATION ====================
    
    @PutMapping("/{id}")
    @Operation(summary = "Modifie une attribution")
    public ResponseEntity<AttributionResponseDto> modifierAttribution(
            @PathVariable UUID id,
            @Valid @RequestBody ModifierAttributionRequest request) {
        return ResponseEntity.ok(attributionService.modifierAttribution(id, request));
    }
    
    @PutMapping("/{id}/prolonger")
    @Operation(summary = "Prolonge une attribution")
    public ResponseEntity<AttributionResponseDto> prolongerAttribution(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nouvelleDateFin) {
        return ResponseEntity.ok(attributionService.prolongerAttribution(id, nouvelleDateFin));
    }
    
    // ==================== CHECK-IN / CHECK-OUT ====================
    
    @PutMapping("/{id}/check-in")
    @Operation(summary = "Enregistre le check-in d'un étudiant")
    public ResponseEntity<AttributionResponseDto> enregistrerCheckIn(@PathVariable UUID id) {
        return ResponseEntity.ok(attributionService.enregistrerCheckIn(id));
    }
    
    @PutMapping("/{id}/check-out")
    @Operation(summary = "Enregistre le check-out d'un étudiant")
    public ResponseEntity<AttributionResponseDto> enregistrerCheckOut(@PathVariable UUID id) {
        return ResponseEntity.ok(attributionService.enregistrerCheckOut(id));
    }
    
    // ==================== RESILIATION / TERMINAISON ====================
    
    @PutMapping("/{id}/revoquer")
    @Operation(summary = "Révoque une attribution")
    public ResponseEntity<AttributionResponseDto> revoquerAttribution(
            @PathVariable UUID id,
            @RequestParam String motif) {
        return ResponseEntity.ok(attributionService.revoquerAttribution(id, motif));
    }
    
    @PutMapping("/{id}/terminer")
    @Operation(summary = "Termine une attribution")
    public ResponseEntity<AttributionResponseDto> terminerAttribution(@PathVariable UUID id) {
        return ResponseEntity.ok(attributionService.terminerAttribution(id));
    }
    
    // ==================== STATISTIQUES ====================
    
    @GetMapping("/statistiques")
    @Operation(summary = "Récupère les statistiques des attributions")
    public ResponseEntity<AttributionService.AttributionStatistiques> getStatistiques() {
        return ResponseEntity.ok(attributionService.getStatistiques());
    }
}
