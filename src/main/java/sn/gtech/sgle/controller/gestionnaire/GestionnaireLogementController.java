package sn.gtech.sgle.controller.gestionnaire;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.logement.CreerLogementRequest;
import sn.gtech.sgle.dto.logement.LogementResponseDto;
import sn.gtech.sgle.dto.logement.ModifierLogementRequest;
import sn.gtech.sgle.service.LogementService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des logements par le gestionnaire
 */
@RestController
@RequestMapping("/api/gestionnaire/logements")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
@RequiredArgsConstructor
@Tag(name = "Gestionnaire - Logements", description = "API de gestion des logements pour les gestionnaires")
public class GestionnaireLogementController {
    
    private final LogementService logementService;
    
    // ==================== CRUD ====================
    
 
    
    @GetMapping("")
    @Operation(summary = "Liste tous les logements (sans pagination)")
    public ResponseEntity<List<LogementResponseDto>> getTousLogementsSansPagination() {
        return ResponseEntity.ok(logementService.getTousLesLogements());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupère un logement par son ID")
    public ResponseEntity<LogementResponseDto> getLogementParId(@PathVariable UUID id) {
        return ResponseEntity.ok(logementService.getLogementParId(id));
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Récupère un logement par son code")
    public ResponseEntity<LogementResponseDto> getLogementParCode(@PathVariable String code) {
        return ResponseEntity.ok(logementService.getLogementParCode(code));
    }
    
    @PostMapping
    @Operation(summary = "Crée un nouveau logement")
    public ResponseEntity<LogementResponseDto> creerLogement(@Valid @RequestBody CreerLogementRequest request) {
        return ResponseEntity.ok(logementService.creerLogement(request));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Met à jour un logement")
    public ResponseEntity<LogementResponseDto> mettreAJourLogement(
            @PathVariable UUID id, 
            @Valid @RequestBody ModifierLogementRequest request) {
        return ResponseEntity.ok(logementService.modifierLogement(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un logement")
    public ResponseEntity<Void> supprimerLogement(@PathVariable UUID id) {
        logementService.supprimerLogement(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/archiver")
    @Operation(summary = "Archive un logement")
    public ResponseEntity<LogementResponseDto> archiverLogement(@PathVariable UUID id) {
        return ResponseEntity.ok(logementService.archiverLogement(id));
    }
    
    // ==================== STATISTIQUES ====================
    
    @GetMapping("/statistiques")
    @Operation(summary = "Récupère les statistiques des logements")
    public ResponseEntity<LogementService.LogementStatistiques> getStatistiques() {
        return ResponseEntity.ok(logementService.getStatistiques());
    }
}
