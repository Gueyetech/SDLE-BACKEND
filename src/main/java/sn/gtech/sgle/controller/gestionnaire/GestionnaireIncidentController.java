package sn.gtech.sgle.controller.gestionnaire;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.incident.CreerIncidentRequest;
import sn.gtech.sgle.dto.incident.IncidentResponseDto;
import sn.gtech.sgle.entity.Utilisateur;
import sn.gtech.sgle.entity.enums.StatutIncidentEnum;
import sn.gtech.sgle.entity.enums.TypeIncidentEnum;
import sn.gtech.sgle.entity.enums.UrgenceEnum;
import sn.gtech.sgle.repository.UtilisateurRepository;
import sn.gtech.sgle.service.IncidentService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des incidents par le gestionnaire
 */
@RestController
@RequestMapping("/api/gestionnaire/incidents")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
@RequiredArgsConstructor
@Tag(name = "Gestionnaire - Incidents", description = "API de gestion des incidents pour les gestionnaires")
public class GestionnaireIncidentController {
    
    private final IncidentService incidentService;
    private final UtilisateurRepository utilisateurRepository;
    
    // ==================== LECTURE ====================
    
    @GetMapping
    @Operation(summary = "Liste tous les incidents")
    public ResponseEntity<List<IncidentResponseDto>> getTousIncidents() {
        return ResponseEntity.ok(incidentService.getTousIncidents());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupère un incident par son ID")
    public ResponseEntity<IncidentResponseDto> getIncidentParId(@PathVariable UUID id) {
        return ResponseEntity.ok(incidentService.getIncidentParId(id));
    }
    
    @GetMapping("/ticket/{numeroTicket}")
    @Operation(summary = "Récupère un incident par son numéro de ticket")
    public ResponseEntity<IncidentResponseDto> getIncidentParTicket(@PathVariable String numeroTicket) {
        return ResponseEntity.ok(incidentService.getIncidentParTicket(numeroTicket));
    }
    
    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupère les incidents par statut")
    public ResponseEntity<List<IncidentResponseDto>> getIncidentsParStatut(@PathVariable StatutIncidentEnum statut) {
        return ResponseEntity.ok(incidentService.getIncidentsParStatut(statut));
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "Récupère les incidents par type")
    public ResponseEntity<List<IncidentResponseDto>> getIncidentsParType(@PathVariable TypeIncidentEnum type) {
        return ResponseEntity.ok(incidentService.getIncidentsParType(type));
    }
    
    @GetMapping("/urgence/{urgence}")
    @Operation(summary = "Récupère les incidents par niveau d'urgence")
    public ResponseEntity<List<IncidentResponseDto>> getIncidentsParUrgence(@PathVariable UrgenceEnum urgence) {
        return ResponseEntity.ok(incidentService.getIncidentsParUrgence(urgence));
    }
    
    @GetMapping("/urgents")
    @Operation(summary = "Récupère les incidents urgents (CRITIQUE et HAUTE)")
    public ResponseEntity<List<IncidentResponseDto>> getIncidentsUrgents() {
        return ResponseEntity.ok(incidentService.getIncidentsUrgents());
    }
    
    @GetMapping("/etudiant/{etudiantId}")
    @Operation(summary = "Récupère les incidents d'un étudiant")
    public ResponseEntity<List<IncidentResponseDto>> getIncidentsParEtudiant(@PathVariable UUID etudiantId) {
        return ResponseEntity.ok(incidentService.getIncidentsParEtudiant(etudiantId));
    }
    
    @GetMapping("/logement/{logementId}")
    @Operation(summary = "Récupère les incidents d'un logement")
    public ResponseEntity<List<IncidentResponseDto>> getIncidentsParLogement(@PathVariable UUID logementId) {
        return ResponseEntity.ok(incidentService.getIncidentsParLogement(logementId));
    }
    
    @GetMapping("/mes-incidents")
    @Operation(summary = "Récupère les incidents assignés au gestionnaire connecté")
    public ResponseEntity<List<IncidentResponseDto>> getMesIncidents(Authentication authentication) {
        Utilisateur gestionnaire = getUtilisateurConnecte(authentication);
        return ResponseEntity.ok(incidentService.getIncidentsParGestionnaire(gestionnaire.getId()));
    }
    
    // ==================== CREATION ====================
    
    @PostMapping
    @Operation(summary = "Crée un nouvel incident")
    public ResponseEntity<IncidentResponseDto> creerIncident(@Valid @RequestBody CreerIncidentRequest request) {
        return ResponseEntity.ok(incidentService.creerIncident(request));
    }
    
    // ==================== ASSIGNATION ====================
    
    @PutMapping("/{id}/assigner-gestionnaire")
    @Operation(summary = "Assigne un gestionnaire à un incident (s'auto-assigne)")
    public ResponseEntity<IncidentResponseDto> assignerGestionnaire(
            @PathVariable UUID id,
            Authentication authentication) {
        Utilisateur gestionnaire = getUtilisateurConnecte(authentication);
        return ResponseEntity.ok(incidentService.assignerGestionnaire(id, gestionnaire.getId()));
    }
    
    @PutMapping("/{id}/assigner-technicien/{technicienId}")
    @Operation(summary = "Assigne un technicien à un incident")
    public ResponseEntity<IncidentResponseDto> assignerTechnicien(
            @PathVariable UUID id,
            @PathVariable UUID technicienId) {
        return ResponseEntity.ok(incidentService.assignerTechnicien(id, technicienId));
    }
    
    // ==================== TRAITEMENT ====================
    
    @PutMapping("/{id}/en-cours")
    @Operation(summary = "Met un incident en cours de traitement")
    public ResponseEntity<IncidentResponseDto> mettreEnCours(@PathVariable UUID id) {
        return ResponseEntity.ok(incidentService.mettreEnCours(id));
    }
    
    @PutMapping("/{id}/resoudre")
    @Operation(summary = "Marque un incident comme résolu")
    public ResponseEntity<IncidentResponseDto> resoudreIncident(
            @PathVariable UUID id,
            @RequestParam String commentaireResolution) {
        return ResponseEntity.ok(incidentService.resoudreIncident(id, commentaireResolution));
    }
    
    @PutMapping("/{id}/fermer")
    @Operation(summary = "Ferme un incident")
    public ResponseEntity<IncidentResponseDto> fermerIncident(@PathVariable UUID id) {
        return ResponseEntity.ok(incidentService.fermerIncident(id));
    }
    
    @PutMapping("/{id}/urgence/{urgence}")
    @Operation(summary = "Change le niveau d'urgence d'un incident")
    public ResponseEntity<IncidentResponseDto> changerUrgence(
            @PathVariable UUID id,
            @PathVariable UrgenceEnum urgence) {
        return ResponseEntity.ok(incidentService.changerUrgence(id, urgence));
    }
    
    @PutMapping("/{id}/commentaire")
    @Operation(summary = "Ajoute un commentaire à un incident")
    public ResponseEntity<IncidentResponseDto> ajouterCommentaire(
            @PathVariable UUID id,
            @RequestBody String commentaire) {
        return ResponseEntity.ok(incidentService.ajouterCommentaire(id, commentaire));
    }
    
    // ==================== SUPPRESSION ====================
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime un incident")
    public ResponseEntity<Void> supprimerIncident(@PathVariable UUID id) {
        incidentService.supprimerIncident(id);
        return ResponseEntity.noContent().build();
    }
    
    // ==================== STATISTIQUES ====================
    
    @GetMapping("/statistiques")
    @Operation(summary = "Récupère les statistiques des incidents")
    public ResponseEntity<Map<String, Object>> getStatistiques() {
        return ResponseEntity.ok(incidentService.getStatistiques());
    }
    
    // ==================== UTILITAIRES ====================
    
    private Utilisateur getUtilisateurConnecte(Authentication authentication) {
        String email = authentication.getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
