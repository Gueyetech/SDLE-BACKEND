package sn.gtech.sgle.controller.etudiant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.common.ApiResponse;
import sn.gtech.sgle.dto.etudiant.SoumettreDemandeRequest;
import sn.gtech.sgle.dto.etudiant.SuiviDemandeDto;
import sn.gtech.sgle.service.EtudiantService;

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des demandes de logement par l'étudiant
 * 
 * Fonctionnalités:
 * - Soumettre une demande de logement
 * - Suivre le statut de mes demandes
 * - Annuler une demande
 */
@RestController
@RequestMapping("/api/etudiant/demandes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
@Tag(name = "Étudiant - Demandes", description = "Gestion des demandes de logement")
public class EtudiantDemandeController {
    
    private final EtudiantService etudiantService;
    
    /**
     * Soumet une nouvelle demande de logement
     */
    @PostMapping
    @Operation(summary = "Soumettre une demande", description = "Soumet une nouvelle demande de logement")
    public ResponseEntity<ApiResponse<SuiviDemandeDto>> soumettreDemandc(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SoumettreDemandeRequest request) {
        SuiviDemandeDto demande = etudiantService.soumettreDemandeLogement(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(demande, 
            "Demande soumise avec succès. Référence: " + demande.getNumeroReference()));
    }
    
    /**
     * Récupère toutes les demandes de l'étudiant
     */
    @GetMapping
    @Operation(summary = "Mes demandes", description = "Liste de toutes mes demandes de logement")
    public ResponseEntity<ApiResponse<List<SuiviDemandeDto>>> getMesDemandes(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<SuiviDemandeDto> demandes = etudiantService.getMesDemandes(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(demandes, 
            demandes.size() + " demande(s) trouvée(s)"));
    }
    
    /**
     * Récupère le détail d'une demande spécifique
     */
    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une demande", description = "Récupère le détail et le suivi d'une demande")
    public ResponseEntity<ApiResponse<SuiviDemandeDto>> getMaDemande(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de la demande") @PathVariable UUID id) {
        SuiviDemandeDto demande = etudiantService.getMaDemande(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success(demande, "Demande récupérée"));
    }
    
    /**
     * Récupère le suivi d'une demande par numéro de référence
     */
    @GetMapping("/reference/{reference}")
    @Operation(summary = "Suivi par référence", description = "Récupère le suivi d'une demande par son numéro de référence")
    public ResponseEntity<ApiResponse<SuiviDemandeDto>> getSuiviParReference(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Numéro de référence") @PathVariable String reference) {
        // On récupère toutes les demandes et on filtre par référence
        List<SuiviDemandeDto> demandes = etudiantService.getMesDemandes(userDetails.getUsername());
        SuiviDemandeDto demande = demandes.stream()
            .filter(d -> reference.equals(d.getNumeroReference()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        return ResponseEntity.ok(ApiResponse.success(demande, "Demande récupérée"));
    }
    
    /**
     * Annule une demande en cours
     */
    @PostMapping("/{id}/annuler")
    @Operation(summary = "Annuler une demande", description = "Annule une demande en attente ou en traitement")
    public ResponseEntity<ApiResponse<SuiviDemandeDto>> annulerDemande(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de la demande") @PathVariable UUID id) {
        SuiviDemandeDto demande = etudiantService.annulerDemande(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success(demande, "Demande annulée avec succès"));
    }
}
