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
import sn.gtech.sgle.dto.etudiant.SignalerIncidentRequest;
import sn.gtech.sgle.dto.etudiant.SuiviIncidentDto;
import sn.gtech.sgle.service.EtudiantService;

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur pour les signalements et le support de l'étudiant
 * 
 * Fonctionnalités:
 * - Signaler un problème dans le logement
 * - Suivre l'état de résolution
 * - Ajouter des informations à un signalement
 */
@RestController
@RequestMapping("/api/etudiant/incidents")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
@Tag(name = "Étudiant - Signalements", description = "Gestion des signalements et support")
public class EtudiantIncidentController {
    
    private final EtudiantService etudiantService;
    
    /**
     * Signale un nouveau problème/incident
     */
    @PostMapping
    @Operation(summary = "Signaler un problème", description = "Créer un nouveau signalement de problème")
    public ResponseEntity<ApiResponse<SuiviIncidentDto>> signalerIncident(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SignalerIncidentRequest request) {
        SuiviIncidentDto incident = etudiantService.signalerIncident(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(incident, 
            "Signalement enregistré. Ticket: " + incident.getNumeroTicket()));
    }
    
    /**
     * Récupère tous les incidents signalés par l'étudiant
     */
    @GetMapping
    @Operation(summary = "Mes signalements", description = "Liste de tous mes signalements")
    public ResponseEntity<ApiResponse<List<SuiviIncidentDto>>> getMesIncidents(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<SuiviIncidentDto> incidents = etudiantService.getMesIncidents(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(incidents, 
            incidents.size() + " signalement(s) trouvé(s)"));
    }
    
    /**
     * Récupère les incidents en cours (non résolus)
     */
    @GetMapping("/en-cours")
    @Operation(summary = "Signalements en cours", description = "Liste des signalements non résolus")
    public ResponseEntity<ApiResponse<List<SuiviIncidentDto>>> getIncidentsEnCours(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<SuiviIncidentDto> incidents = etudiantService.getMesIncidents(userDetails.getUsername());
        List<SuiviIncidentDto> enCours = incidents.stream()
            .filter(i -> i.getStatut().name().equals("OUVERT") || i.getStatut().name().equals("EN_COURS"))
            .toList();
        return ResponseEntity.ok(ApiResponse.success(enCours, 
            enCours.size() + " signalement(s) en cours"));
    }
    
    /**
     * Récupère le détail d'un incident
     */
    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un signalement", description = "Récupère le détail et le suivi d'un signalement")
    public ResponseEntity<ApiResponse<SuiviIncidentDto>> getMonIncident(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de l'incident") @PathVariable UUID id) {
        SuiviIncidentDto incident = etudiantService.getMonIncident(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success(incident, "Signalement récupéré"));
    }
    
    /**
     * Récupère le suivi d'un incident par numéro de ticket
     */
    @GetMapping("/ticket/{numeroTicket}")
    @Operation(summary = "Suivi par ticket", description = "Récupère le suivi d'un signalement par son numéro de ticket")
    public ResponseEntity<ApiResponse<SuiviIncidentDto>> getSuiviParTicket(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Numéro de ticket") @PathVariable String numeroTicket) {
        List<SuiviIncidentDto> incidents = etudiantService.getMesIncidents(userDetails.getUsername());
        SuiviIncidentDto incident = incidents.stream()
            .filter(i -> numeroTicket.equals(i.getNumeroTicket()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Signalement non trouvé"));
        return ResponseEntity.ok(ApiResponse.success(incident, "Signalement récupéré"));
    }
    
    /**
     * Ajoute des photos à un incident ouvert
     */
    @PostMapping("/{id}/photos")
    @Operation(summary = "Ajouter des photos", description = "Ajoute des photos supplémentaires à un signalement")
    public ResponseEntity<ApiResponse<SuiviIncidentDto>> ajouterPhotos(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de l'incident") @PathVariable UUID id,
            @RequestBody List<String> photos) {
        SuiviIncidentDto incident = etudiantService.ajouterPhotosIncident(
            userDetails.getUsername(), id, photos);
        return ResponseEntity.ok(ApiResponse.success(incident, "Photos ajoutées avec succès"));
    }
}
