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
import sn.gtech.sgle.dto.paiement.EnregistrerPaiementRequest;
import sn.gtech.sgle.dto.paiement.PaiementResponseDto;
import sn.gtech.sgle.service.PaiementService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/paiements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Paiements", description = "Gestion financière et des paiements")
public class AdminPaiementController {

    private final PaiementService paiementService;

    @PostMapping
    @Operation(summary = "Enregistrer un paiement", description = "Enregistre un nouveau paiement pour une attribution")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Paiement enregistré avec succès"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Attribution non trouvée")
    })
    public ResponseEntity<ApiResponse<PaiementResponseDto>> enregistrerPaiement(
            @Valid @RequestBody EnregistrerPaiementRequest request) {
        PaiementResponseDto paiement = paiementService.enregistrerPaiement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(paiement, "Paiement enregistré avec succès"));
    }

    @GetMapping
    @Operation(summary = "Lister les paiements", description = "Liste tous les paiements")
    public ResponseEntity<ApiResponse<List<PaiementResponseDto>>> listerPaiements() {
        List<PaiementResponseDto> paiements = paiementService.getTousLesPaiements();
        return ResponseEntity.ok(ApiResponse.success(paiements, paiements.size() + " paiement(s) trouvé(s)"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un paiement", description = "Récupère les détails d'un paiement")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paiement trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Paiement non trouvé")
    })
    public ResponseEntity<ApiResponse<PaiementResponseDto>> getPaiement(
            @Parameter(description = "ID du paiement") @PathVariable UUID id) {
        PaiementResponseDto paiement = paiementService.getPaiement(id);
        return ResponseEntity.ok(ApiResponse.success(paiement, "Paiement récupéré"));
    }

    @GetMapping("/numero/{numeroPaiement}")
    @Operation(summary = "Obtenir un paiement par numéro", description = "Récupère un paiement par son numéro")
    public ResponseEntity<ApiResponse<PaiementResponseDto>> getPaiementByNumero(
            @Parameter(description = "Numéro du paiement") @PathVariable String numeroPaiement) {
        PaiementResponseDto paiement = paiementService.getPaiementByNumero(numeroPaiement);
        return ResponseEntity.ok(ApiResponse.success(paiement, "Paiement récupéré"));
    }

    // ============ Actions sur les paiements ============

    @PatchMapping("/{id}/payer")
    @Operation(summary = "Marquer comme payé", description = "Marque un paiement comme réglé")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paiement marqué comme payé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Le paiement a déjà été réglé")
    })
    public ResponseEntity<ApiResponse<PaiementResponseDto>> marquerCommePaye(
            @Parameter(description = "ID du paiement") @PathVariable UUID id,
            @Parameter(description = "Référence bancaire") @RequestParam(required = false) String referenceBancaire) {
        PaiementResponseDto paiement = paiementService.marquerCommePaye(id, referenceBancaire);
        return ResponseEntity.ok(ApiResponse.success(paiement, "Paiement marqué comme payé"));
    }

    @PatchMapping("/{id}/annuler")
    @Operation(summary = "Annuler un paiement", description = "Annule un paiement en attente")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paiement annulé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Impossible d'annuler un paiement déjà réglé")
    })
    public ResponseEntity<ApiResponse<PaiementResponseDto>> annulerPaiement(
            @Parameter(description = "ID du paiement") @PathVariable UUID id,
            @Parameter(description = "Motif de l'annulation") @RequestParam(required = false) String motif) {
        PaiementResponseDto paiement = paiementService.annulerPaiement(id, motif);
        return ResponseEntity.ok(ApiResponse.success(paiement, "Paiement annulé"));
    }

    @PatchMapping("/{id}/rembourser")
    @Operation(summary = "Rembourser un paiement", description = "Rembourse un paiement déjà réglé")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Paiement remboursé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Seuls les paiements réglés peuvent être remboursés")
    })
    public ResponseEntity<ApiResponse<PaiementResponseDto>> rembourserPaiement(
            @Parameter(description = "ID du paiement") @PathVariable UUID id,
            @Parameter(description = "Motif du remboursement") @RequestParam(required = false) String motif) {
        PaiementResponseDto paiement = paiementService.rembourserPaiement(id, motif);
        return ResponseEntity.ok(ApiResponse.success(paiement, "Paiement remboursé"));
    }

    // ============ Gestion par attribution ============

    @GetMapping("/attribution/{attributionId}")
    @Operation(summary = "Paiements d'une attribution", description = "Liste tous les paiements d'une attribution")
    public ResponseEntity<ApiResponse<List<PaiementResponseDto>>> getPaiementsParAttribution(
            @Parameter(description = "ID de l'attribution") @PathVariable UUID attributionId) {
        List<PaiementResponseDto> paiements = paiementService.getPaiementsParAttribution(attributionId);
        return ResponseEntity.ok(ApiResponse.success(paiements, paiements.size() + " paiement(s) trouvé(s)"));
    }

    @PostMapping("/attribution/{attributionId}/generer")
    @Operation(summary = "Générer des paiements mensuels", description = "Génère automatiquement les paiements mensuels pour une attribution")
    public ResponseEntity<ApiResponse<List<PaiementResponseDto>>> genererPaiementsMensuels(
            @Parameter(description = "ID de l'attribution") @PathVariable UUID attributionId,
            @Parameter(description = "Nombre de mois") @RequestParam(defaultValue = "12") int nombreMois) {
        List<PaiementResponseDto> paiements = paiementService.genererPaiementsMensuels(attributionId, nombreMois);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(paiements, paiements.size() + " paiement(s) généré(s)"));
    }

    @GetMapping("/attribution/{attributionId}/solde")
    @Operation(summary = "Solde d'une attribution", description = "Calcule le solde et les pénalités d'une attribution")
    public ResponseEntity<ApiResponse<PaiementService.SoldeAttribution>> getSoldeAttribution(
            @Parameter(description = "ID de l'attribution") @PathVariable UUID attributionId) {
        PaiementService.SoldeAttribution solde = paiementService.calculerSoldeAttribution(attributionId);
        return ResponseEntity.ok(ApiResponse.success(solde, "Solde calculé"));
    }

    // ============ Retards et rappels ============

    @GetMapping("/en-retard")
    @Operation(summary = "Paiements en retard", description = "Liste tous les paiements en retard")
    public ResponseEntity<ApiResponse<List<PaiementResponseDto>>> getPaiementsEnRetard() {
        List<PaiementResponseDto> paiements = paiementService.getPaiementsEnRetard();
        return ResponseEntity.ok(ApiResponse.success(paiements, paiements.size() + " paiement(s) en retard"));
    }

    @PostMapping("/rappels")
    @Operation(summary = "Envoyer des rappels", description = "Envoie des rappels pour tous les paiements en retard")
    public ResponseEntity<ApiResponse<Integer>> envoyerRappels() {
        int nombreRappels = paiementService.envoyerRappelsPaiements();
        return ResponseEntity.ok(ApiResponse.success(nombreRappels, nombreRappels + " rappel(s) envoyé(s)"));
    }

    // ============ Statistiques ============

    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques paiements", description = "Récupère les statistiques globales des paiements")
    public ResponseEntity<ApiResponse<PaiementService.PaiementStatistiques>> getStatistiques() {
        PaiementService.PaiementStatistiques stats = paiementService.getStatistiques();
        return ResponseEntity.ok(ApiResponse.success(stats, "Statistiques récupérées"));
    }
}
