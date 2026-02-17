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
import sn.gtech.sgle.dto.etudiant.NotificationEtudiantDto;
import sn.gtech.sgle.service.EtudiantService;

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des notifications de l'étudiant
 * 
 * Fonctionnalités:
 * - Consulter mes notifications
 * - Marquer comme lue
 * - Filtrer par type
 */
@RestController
@RequestMapping("/api/etudiant/notifications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
@Tag(name = "Étudiant - Notifications", description = "Gestion des notifications et alertes")
public class EtudiantNotificationController {
    
    private final EtudiantService etudiantService;
    
    /**
     * Récupère toutes les notifications
     */
    @GetMapping
    @Operation(summary = "Mes notifications", description = "Liste de toutes mes notifications")
    public ResponseEntity<ApiResponse<List<NotificationEtudiantDto>>> getMesNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<NotificationEtudiantDto> notifications = etudiantService.getMesNotifications(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(notifications, 
            notifications.size() + " notification(s)"));
    }
    
    /**
     * Récupère les notifications non lues
     */
    @GetMapping("/non-lues")
    @Operation(summary = "Notifications non lues", description = "Liste des notifications non lues")
    public ResponseEntity<ApiResponse<List<NotificationEtudiantDto>>> getNotificationsNonLues(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<NotificationEtudiantDto> notifications = etudiantService.getMesNotificationsNonLues(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(notifications, 
            notifications.size() + " notification(s) non lue(s)"));
    }
    
    /**
     * Compte les notifications non lues
     */
    @GetMapping("/count")
    @Operation(summary = "Compteur", description = "Nombre de notifications non lues")
    public ResponseEntity<ApiResponse<Long>> compterNotificationsNonLues(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long count = etudiantService.compterNotificationsNonLues(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(count, count + " notification(s) non lue(s)"));
    }
    
    /**
     * Récupère les dernières notifications (les plus récentes)
     */
    @GetMapping("/recentes")
    @Operation(summary = "Notifications récentes", description = "Les 10 dernières notifications")
    public ResponseEntity<ApiResponse<List<NotificationEtudiantDto>>> getNotificationsRecentes(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<NotificationEtudiantDto> notifications = etudiantService.getMesNotifications(userDetails.getUsername())
            .stream()
            .limit(10)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notifications récentes"));
    }
    
    /**
     * Marque une notification comme lue
     */
    @PostMapping("/{id}/lire")
    @Operation(summary = "Marquer comme lue", description = "Marque une notification comme lue")
    public ResponseEntity<ApiResponse<Void>> marquerCommeLue(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID de la notification") @PathVariable UUID id) {
        etudiantService.marquerNotificationLue(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification marquée comme lue"));
    }
    
    /**
     * Marque toutes les notifications comme lues
     */
    @PostMapping("/lire-toutes")
    @Operation(summary = "Tout marquer comme lu", description = "Marque toutes les notifications comme lues")
    public ResponseEntity<ApiResponse<Void>> marquerToutesCommeLues(
            @AuthenticationPrincipal UserDetails userDetails) {
        etudiantService.marquerToutesNotificationsLues(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "Toutes les notifications ont été marquées comme lues"));
    }
    
    /**
     * Filtre les notifications par type
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "Par type", description = "Filtre les notifications par type")
    public ResponseEntity<ApiResponse<List<NotificationEtudiantDto>>> getNotificationsParType(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Type de notification") @PathVariable String type) {
        List<NotificationEtudiantDto> notifications = etudiantService.getMesNotifications(userDetails.getUsername())
            .stream()
            .filter(n -> n.getType() != null && n.getType().name().equals(type))
            .toList();
        return ResponseEntity.ok(ApiResponse.success(notifications, 
            notifications.size() + " notification(s) de type " + type));
    }
    
    /**
     * Récupère les notifications nouvelles (dernières 24h)
     */
    @GetMapping("/nouvelles")
    @Operation(summary = "Notifications nouvelles", description = "Notifications des dernières 24 heures")
    public ResponseEntity<ApiResponse<List<NotificationEtudiantDto>>> getNotificationsNouvelles(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<NotificationEtudiantDto> notifications = etudiantService.getMesNotifications(userDetails.getUsername())
            .stream()
            .filter(n -> n.getNouvelle())
            .toList();
        return ResponseEntity.ok(ApiResponse.success(notifications, 
            notifications.size() + " nouvelle(s) notification(s)"));
    }
}
