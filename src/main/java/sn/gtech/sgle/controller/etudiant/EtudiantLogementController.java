package sn.gtech.sgle.controller.etudiant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.common.ApiResponse;
import sn.gtech.sgle.dto.etudiant.MonLogementDto;
import sn.gtech.sgle.service.EtudiantService;

import java.util.List;

/**
 * Contrôleur pour la gestion du logement actuel de l'étudiant
 * 
 * Fonctionnalités:
 * - Consulter mon logement actuel
 * - Voir les détails (code, adresse, équipements)
 * - Accéder aux informations du contrat
 * - Historique des logements
 */
@RestController
@RequestMapping("/api/etudiant/mon-logement")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
@Tag(name = "Étudiant - Mon Logement", description = "Consultation du logement attribué")
public class EtudiantLogementController {
    
    private final EtudiantService etudiantService;
    
    /**
     * Récupère les détails du logement actuel de l'étudiant
     */
    @GetMapping
    @Operation(summary = "Mon logement actuel", description = "Détails complets du logement attribué")
    public ResponseEntity<ApiResponse<MonLogementDto>> getMonLogement(
            @AuthenticationPrincipal UserDetails userDetails) {
        MonLogementDto logement = etudiantService.getMonLogement(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(logement, "Logement récupéré avec succès"));
    }
    
    /**
     * Vérifie si l'étudiant a un logement attribué
     */
    @GetMapping("/existe")
    @Operation(summary = "Vérifier attribution", description = "Vérifie si l'étudiant a un logement attribué")
    public ResponseEntity<ApiResponse<Boolean>> aUnLogement(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            etudiantService.getMonLogement(userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success(true, "Vous avez un logement attribué"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success(false, "Vous n'avez pas de logement attribué"));
        }
    }
    
    /**
     * Récupère l'historique des logements de l'étudiant
     */
    @GetMapping("/historique")
    @Operation(summary = "Historique des logements", description = "Liste de tous les logements attribués")
    public ResponseEntity<ApiResponse<List<MonLogementDto>>> getHistoriqueLogements(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MonLogementDto> historique = etudiantService.getHistoriqueLogements(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(historique, 
            historique.size() + " attribution(s) trouvée(s)"));
    }
    
    /**
     * Récupère les informations de contact du gestionnaire
     */
    @GetMapping("/gestionnaire")
    @Operation(summary = "Contact gestionnaire", description = "Informations de contact du gestionnaire du logement")
    public ResponseEntity<ApiResponse<Object>> getContactGestionnaire(
            @AuthenticationPrincipal UserDetails userDetails) {
        MonLogementDto logement = etudiantService.getMonLogement(userDetails.getUsername());
        
        var contact = new Object() {
            public final String email = logement.getGestionnaireEmail();
            public final String telephone = logement.getGestionnaireTelephone();
            public final String nom = logement.getGestionnaireNom();
        };
        
        return ResponseEntity.ok(ApiResponse.success(contact, "Contact gestionnaire récupéré"));
    }
    
    /**
     * Récupère un résumé du logement (pour affichage rapide)
     */
    @GetMapping("/resume")
    @Operation(summary = "Résumé", description = "Résumé rapide du logement actuel")
    public ResponseEntity<ApiResponse<Object>> getResume(
            @AuthenticationPrincipal UserDetails userDetails) {
        MonLogementDto logement = etudiantService.getMonLogement(userDetails.getUsername());
        
        var resume = new Object() {
            public final String code = logement.getLogementCode();
            public final String adresse = logement.getAdresseComplete();
            public final String loyer = logement.getLoyerFormate();
            public final Long joursRestants = logement.getJoursRestants();
            public final Boolean expireBientot = logement.getExpireBientot();
            public final Integer paiementsEnRetard = logement.getPaiementsEnRetard();
            public final String prochainPaiement = logement.getProchainEcheance() != null 
                ? logement.getProchainEcheance().toString() : null;
        };
        
        return ResponseEntity.ok(ApiResponse.success(resume, "Résumé récupéré"));
    }
}
