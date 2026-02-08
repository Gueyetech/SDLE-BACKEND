package sn.gtech.sgle.controller.gestionnaire;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.demande.CreerDemandeRequest;
import sn.gtech.sgle.dto.demande.DemandeResponseDto;
import sn.gtech.sgle.entity.Utilisateur;
import sn.gtech.sgle.entity.enums.PrioriteEnum;
import sn.gtech.sgle.entity.enums.StatutDemandeEnum;
import sn.gtech.sgle.repository.UtilisateurRepository;
import sn.gtech.sgle.service.DemandeLogementService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des demandes de logement par le gestionnaire
 */
@RestController
@RequestMapping("/api/gestionnaire/demandes")
@PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
@RequiredArgsConstructor
@Tag(name = "Gestionnaire - Demandes", description = "API de gestion des demandes de logement pour les gestionnaires")
public class GestionnaireDemandeController {
    
    private final DemandeLogementService demandeService;
    private final UtilisateurRepository utilisateurRepository;
    
    // ==================== LECTURE ====================
    
    @GetMapping
    @Operation(summary = "Liste toutes les demandes")
    public ResponseEntity<List<DemandeResponseDto>> getToutesDemandes() {
        return ResponseEntity.ok(demandeService.getToutesDemandes());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupère une demande par son ID")
    public ResponseEntity<DemandeResponseDto> getDemandeParId(@PathVariable UUID id) {
        return ResponseEntity.ok(demandeService.getDemandeParId(id));
    }
    
    @GetMapping("/reference/{reference}")
    @Operation(summary = "Récupère une demande par sa référence")
    public ResponseEntity<DemandeResponseDto> getDemandeParReference(@PathVariable String reference) {
        return ResponseEntity.ok(demandeService.getDemandeParReference(reference));
    }
    
    @GetMapping("/en-attente")
    @Operation(summary = "Récupère les demandes en attente (triées par date)")
    public ResponseEntity<List<DemandeResponseDto>> getDemandesEnAttente() {
        return ResponseEntity.ok(demandeService.getDemandesEnAttente());
    }
    
    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupère les demandes par statut")
    public ResponseEntity<List<DemandeResponseDto>> getDemandesParStatut(@PathVariable StatutDemandeEnum statut) {
        return ResponseEntity.ok(demandeService.getDemandesParStatut(statut));
    }
    
    @GetMapping("/etudiant/{etudiantId}")
    @Operation(summary = "Récupère les demandes d'un étudiant")
    public ResponseEntity<List<DemandeResponseDto>> getDemandesParEtudiant(@PathVariable UUID etudiantId) {
        return ResponseEntity.ok(demandeService.getDemandesParEtudiant(etudiantId));
    }
    
    @GetMapping("/mes-demandes")
    @Operation(summary = "Récupère les demandes gérées par le gestionnaire connecté")
    public ResponseEntity<List<DemandeResponseDto>> getMesDemandesGerees(Authentication authentication) {
        Utilisateur gestionnaire = getUtilisateurConnecte(authentication);
        return ResponseEntity.ok(demandeService.getDemandesParGestionnaire(gestionnaire.getId()));
    }
    
    // ==================== CREATION ====================
    
    @PostMapping
    @Operation(summary = "Crée une nouvelle demande (pour un étudiant)")
    public ResponseEntity<DemandeResponseDto> creerDemande(@Valid @RequestBody CreerDemandeRequest request) {
        return ResponseEntity.ok(demandeService.creerDemande(request));
    }
    
    // ==================== TRAITEMENT ====================
    
    @PutMapping("/{id}/approuver")
    @Operation(summary = "Approuve une demande")
    public ResponseEntity<DemandeResponseDto> approuverDemande(@PathVariable UUID id, Authentication authentication) {
        Utilisateur gestionnaire = getUtilisateurConnecte(authentication);
        return ResponseEntity.ok(demandeService.approuverDemande(id, gestionnaire.getId()));
    }
    
    @PutMapping("/{id}/rejeter")
    @Operation(summary = "Rejette une demande")
    public ResponseEntity<DemandeResponseDto> rejeterDemande(
            @PathVariable UUID id,
            @RequestParam String motif,
            Authentication authentication) {
        Utilisateur gestionnaire = getUtilisateurConnecte(authentication);
        return ResponseEntity.ok(demandeService.rejeterDemande(id, gestionnaire.getId(), motif));
    }
    
    @PutMapping("/{id}/en-traitement")
    @Operation(summary = "Met une demande en cours de traitement")
    public ResponseEntity<DemandeResponseDto> mettreEnTraitement(@PathVariable UUID id, Authentication authentication) {
        Utilisateur gestionnaire = getUtilisateurConnecte(authentication);
        return ResponseEntity.ok(demandeService.mettreEnTraitement(id, gestionnaire.getId()));
    }
    
    @PutMapping("/{id}/en-attente")
    @Operation(summary = "Met une demande en attente")
    public ResponseEntity<DemandeResponseDto> mettreEnAttente(@PathVariable UUID id) {
        return ResponseEntity.ok(demandeService.mettreEnAttente(id));
    }
    
    @PutMapping("/{id}/priorite/{priorite}")
    @Operation(summary = "Change la priorité d'une demande")
    public ResponseEntity<DemandeResponseDto> changerPriorite(
            @PathVariable UUID id,
            @PathVariable PrioriteEnum priorite) {
        return ResponseEntity.ok(demandeService.changerPriorite(id, priorite));
    }
    
    @PutMapping("/{id}/commentaire")
    @Operation(summary = "Ajoute un commentaire à une demande")
    public ResponseEntity<DemandeResponseDto> ajouterCommentaire(
            @PathVariable UUID id,
            @RequestBody String commentaire) {
        return ResponseEntity.ok(demandeService.ajouterCommentaire(id, commentaire));
    }
    
    // ==================== SUPPRESSION ====================
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprime une demande")
    public ResponseEntity<Void> supprimerDemande(@PathVariable UUID id) {
        demandeService.supprimerDemande(id);
        return ResponseEntity.noContent().build();
    }
    
    // ==================== STATISTIQUES ====================
    
    @GetMapping("/statistiques")
    @Operation(summary = "Récupère les statistiques des demandes")
    public ResponseEntity<Map<String, Object>> getStatistiques() {
        return ResponseEntity.ok(demandeService.getStatistiques());
    }
    
    // ==================== UTILITAIRES ====================
    
    private Utilisateur getUtilisateurConnecte(Authentication authentication) {
        String email = authentication.getName();
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
