package sn.gtech.sgle.controller.etudiant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.common.ApiResponse;
import sn.gtech.sgle.dto.etudiant.PaiementEtudiantDto;
import sn.gtech.sgle.dto.etudiant.RecapitulatifPaiementsDto;
import sn.gtech.sgle.service.EtudiantService;

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des paiements de l'étudiant
 * 
 * Fonctionnalités:
 * - Consulter les loyers à payer
 * - Voir l'historique des paiements
 * - Télécharger les reçus/factures
 */
@RestController
@RequestMapping("/api/etudiant/paiements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
@Tag(name = "Étudiant - Paiements", description = "Gestion des paiements et factures")
public class EtudiantPaiementController {
    
    private final EtudiantService etudiantService;
    
    /**
     * Récupère le récapitulatif des paiements
     */
    @GetMapping("/recapitulatif")
    @Operation(summary = "Récapitulatif", description = "Vue d'ensemble des paiements")
    public ResponseEntity<ApiResponse<RecapitulatifPaiementsDto>> getRecapitulatif(
            @AuthenticationPrincipal UserDetails userDetails) {
        RecapitulatifPaiementsDto recap = etudiantService.getRecapitulatifPaiements(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(recap, "Récapitulatif récupéré"));
    }
    
    /**
     * Récupère l'historique complet des paiements
     */
    @GetMapping
    @Operation(summary = "Historique des paiements", description = "Liste complète des paiements")
    public ResponseEntity<ApiResponse<List<PaiementEtudiantDto>>> getHistoriquePaiements(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PaiementEtudiantDto> paiements = etudiantService.getHistoriquePaiements(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(paiements, 
            paiements.size() + " paiement(s) trouvé(s)"));
    }
    
    /**
     * Récupère les paiements en attente (à régler)
     */
    @GetMapping("/a-payer")
    @Operation(summary = "Paiements à régler", description = "Liste des paiements en attente")
    public ResponseEntity<ApiResponse<List<PaiementEtudiantDto>>> getPaiementsAPayer(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PaiementEtudiantDto> paiements = etudiantService.getPaiementsEnAttente(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(paiements, 
            paiements.size() + " paiement(s) à régler"));
    }
    
    /**
     * Récupère les paiements en retard
     */
    @GetMapping("/en-retard")
    @Operation(summary = "Paiements en retard", description = "Liste des paiements en retard")
    public ResponseEntity<ApiResponse<List<PaiementEtudiantDto>>> getPaiementsEnRetard(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PaiementEtudiantDto> paiements = etudiantService.getPaiementsEnAttente(userDetails.getUsername())
            .stream()
            .filter(p -> p.getEnRetard())
            .toList();
        return ResponseEntity.ok(ApiResponse.success(paiements, 
            paiements.size() + " paiement(s) en retard"));
    }
    
    /**
     * Récupère les paiements effectués
     */
    @GetMapping("/effectues")
    @Operation(summary = "Paiements effectués", description = "Liste des paiements déjà réglés")
    public ResponseEntity<ApiResponse<List<PaiementEtudiantDto>>> getPaiementsEffectues(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PaiementEtudiantDto> paiements = etudiantService.getHistoriquePaiements(userDetails.getUsername())
            .stream()
            .filter(p -> p.getStatut().name().equals("PAYE"))
            .toList();
        return ResponseEntity.ok(ApiResponse.success(paiements, 
            paiements.size() + " paiement(s) effectué(s)"));
    }
    
    /**
     * Récupère le détail d'un paiement
     */
    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un paiement", description = "Informations détaillées d'un paiement")
    public ResponseEntity<ApiResponse<PaiementEtudiantDto>> getDetailPaiement(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID du paiement") @PathVariable UUID id) {
        List<PaiementEtudiantDto> paiements = etudiantService.getHistoriquePaiements(userDetails.getUsername());
        PaiementEtudiantDto paiement = paiements.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
        return ResponseEntity.ok(ApiResponse.success(paiement, "Paiement récupéré"));
    }
    
    /**
     * Télécharge le reçu d'un paiement
     */
    @GetMapping("/{id}/recu")
    @Operation(summary = "Télécharger reçu", description = "Télécharge le reçu d'un paiement effectué")
    public ResponseEntity<ApiResponse<Object>> telechargerRecu(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID du paiement") @PathVariable UUID id) {
        List<PaiementEtudiantDto> paiements = etudiantService.getHistoriquePaiements(userDetails.getUsername());
        PaiementEtudiantDto paiement = paiements.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
        
        if (!paiement.getRecuDisponible()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Aucun reçu disponible pour ce paiement"));
        }
        
        var recu = new Object() {
            public final UUID recuId = paiement.getRecuId();
            public final String url = paiement.getRecuUrl();
            public final String numeroPaiement = paiement.getNumeroPaiement();
        };
        
        return ResponseEntity.ok(ApiResponse.success(recu, "Reçu disponible au téléchargement"));
    }
    
    /**
     * Récupère le prochain paiement à effectuer
     */
    @GetMapping("/prochain")
    @Operation(summary = "Prochain paiement", description = "Détails du prochain paiement à effectuer")
    public ResponseEntity<ApiResponse<Object>> getProchainPaiement(
            @AuthenticationPrincipal UserDetails userDetails) {
        RecapitulatifPaiementsDto recap = etudiantService.getRecapitulatifPaiements(userDetails.getUsername());
        
        if (recap.getProchainEcheance() == null) {
            return ResponseEntity.ok(ApiResponse.success(null, "Aucun paiement en attente"));
        }
        
        var prochain = new Object() {
            public final String dateEcheance = recap.getProchainEcheance().toString();
            public final String montant = recap.getMontantProchainPaiement() != null 
                ? recap.getMontantProchainPaiement().toPlainString() + " FCFA" : null;
            public final Long joursRestants = recap.getJoursAvantProchainPaiement();
            public final Boolean urgent = recap.getProchainPaiementUrgent();
        };
        
        return ResponseEntity.ok(ApiResponse.success(prochain, "Prochain paiement récupéré"));
    }
}
