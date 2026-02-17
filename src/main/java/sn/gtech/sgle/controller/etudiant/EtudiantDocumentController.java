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
import sn.gtech.sgle.dto.etudiant.DocumentEtudiantSimpleDto;
import sn.gtech.sgle.dto.etudiant.UploadDocumentRequest;
import sn.gtech.sgle.entity.enums.TypeDocumentEnum;
import sn.gtech.sgle.service.EtudiantService;

import java.util.List;
import java.util.UUID;

/**
 * Contrôleur pour la gestion des documents de l'étudiant
 * 
 * Fonctionnalités:
 * - Télécharger mon contrat
 * - Accéder aux documents administratifs
 * - Téléverser des documents
 * - Télécharger les attestations
 */
@RestController
@RequestMapping("/api/etudiant/documents")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
@Tag(name = "Étudiant - Documents", description = "Gestion des documents et attestations")
public class EtudiantDocumentController {
    
    private final EtudiantService etudiantService;
    
    /**
     * Récupère tous les documents de l'étudiant
     */
    @GetMapping
    @Operation(summary = "Mes documents", description = "Liste de tous mes documents")
    public ResponseEntity<ApiResponse<List<DocumentEtudiantSimpleDto>>> getMesDocuments(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<DocumentEtudiantSimpleDto> documents = etudiantService.getMesDocuments(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(documents, 
            documents.size() + " document(s) trouvé(s)"));
    }
    
    /**
     * Récupère les documents par type
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "Documents par type", description = "Liste des documents d'un type spécifique")
    public ResponseEntity<ApiResponse<List<DocumentEtudiantSimpleDto>>> getDocumentsParType(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Type de document") @PathVariable TypeDocumentEnum type) {
        List<DocumentEtudiantSimpleDto> documents = etudiantService.getMesDocumentsParType(
            userDetails.getUsername(), type);
        return ResponseEntity.ok(ApiResponse.success(documents, 
            documents.size() + " document(s) de type " + type.name()));
    }
    
    /**
     * Récupère le contrat de location actuel
     */
    @GetMapping("/contrat")
    @Operation(summary = "Mon contrat", description = "Récupère le contrat de location actuel")
    public ResponseEntity<ApiResponse<DocumentEtudiantSimpleDto>> getMonContrat(
            @AuthenticationPrincipal UserDetails userDetails) {
        DocumentEtudiantSimpleDto contrat = etudiantService.getMonContrat(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(contrat, "Contrat récupéré"));
    }
    
    /**
     * Récupère les reçus de paiement
     */
    @GetMapping("/recus")
    @Operation(summary = "Reçus de paiement", description = "Liste des reçus de paiement")
    public ResponseEntity<ApiResponse<List<DocumentEtudiantSimpleDto>>> getRecus(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<DocumentEtudiantSimpleDto> recus = etudiantService.getMesDocumentsParType(
            userDetails.getUsername(), TypeDocumentEnum.RECU_PAIEMENT);
        return ResponseEntity.ok(ApiResponse.success(recus, 
            recus.size() + " reçu(s) trouvé(s)"));
    }
    
    /**
     * Récupère les documents personnels (carte étudiant, pièce d'identité, etc.)
     */
    @GetMapping("/personnels")
    @Operation(summary = "Documents personnels", description = "Carte étudiant, pièce d'identité, etc.")
    public ResponseEntity<ApiResponse<List<DocumentEtudiantSimpleDto>>> getDocumentsPersonnels(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<DocumentEtudiantSimpleDto> documents = etudiantService.getMesDocuments(userDetails.getUsername())
            .stream()
            .filter(d -> d.getType() == TypeDocumentEnum.CARTE_ETUDIANT 
                || d.getType() == TypeDocumentEnum.PIECE_IDENTITE
                || d.getType() == TypeDocumentEnum.PHOTO_IDENTITE
                || d.getType() == TypeDocumentEnum.CERTIFICAT_SCOLARITE)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(documents, 
            documents.size() + " document(s) personnel(s)"));
    }
    
    /**
     * Récupère les documents qui expirent bientôt
     */
    @GetMapping("/expirent-bientot")
    @Operation(summary = "Documents expirant bientôt", description = "Documents qui expirent dans les 30 jours")
    public ResponseEntity<ApiResponse<List<DocumentEtudiantSimpleDto>>> getDocumentsExpirantBientot(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<DocumentEtudiantSimpleDto> documents = etudiantService.getMesDocuments(userDetails.getUsername())
            .stream()
            .filter(d -> d.getExpireBientot() != null && d.getExpireBientot())
            .toList();
        return ResponseEntity.ok(ApiResponse.success(documents, 
            documents.size() + " document(s) expire(nt) bientôt"));
    }
    
    /**
     * Récupère les documents expirés
     */
    @GetMapping("/expires")
    @Operation(summary = "Documents expirés", description = "Liste des documents expirés")
    public ResponseEntity<ApiResponse<List<DocumentEtudiantSimpleDto>>> getDocumentsExpires(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<DocumentEtudiantSimpleDto> documents = etudiantService.getMesDocuments(userDetails.getUsername())
            .stream()
            .filter(d -> d.getExpire() != null && d.getExpire())
            .toList();
        return ResponseEntity.ok(ApiResponse.success(documents, 
            documents.size() + " document(s) expiré(s)"));
    }
    
    /**
     * Enregistre un nouveau document (métadonnées)
     * Note: L'upload réel du fichier se fait via un autre endpoint de stockage
     */
    @PostMapping
    @Operation(summary = "Ajouter un document", description = "Enregistre les métadonnées d'un nouveau document")
    public ResponseEntity<ApiResponse<DocumentEtudiantSimpleDto>> ajouterDocument(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UploadDocumentRequest request,
            @Parameter(description = "URL du fichier uploadé") @RequestParam String fileUrl) {
        DocumentEtudiantSimpleDto document = etudiantService.enregistrerDocument(
            userDetails.getUsername(), request, fileUrl);
        return ResponseEntity.ok(ApiResponse.success(document, 
            "Document enregistré avec succès (en attente de validation)"));
    }
    
    /**
     * Supprime un document
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un document", description = "Supprime un document personnel")
    public ResponseEntity<ApiResponse<Void>> supprimerDocument(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID du document") @PathVariable UUID id) {
        etudiantService.supprimerDocument(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success(null, "Document supprimé avec succès"));
    }
    
    /**
     * Récupère les documents requis pour la demande de logement
     */
    @GetMapping("/requis")
    @Operation(summary = "Documents requis", description = "Liste des types de documents requis pour une demande")
    public ResponseEntity<ApiResponse<List<Object>>> getDocumentsRequis(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        List<DocumentEtudiantSimpleDto> mesDocuments = etudiantService.getMesDocuments(userDetails.getUsername());
        
        List<Object> documentsRequis = List.of(
            new Object() {
                public final String type = "CARTE_ETUDIANT";
                public final String libelle = "Carte étudiante";
                public final boolean obligatoire = true;
                public final boolean fourni = mesDocuments.stream()
                    .anyMatch(d -> d.getType() == TypeDocumentEnum.CARTE_ETUDIANT && d.getValide());
            },
            new Object() {
                public final String type = "PIECE_IDENTITE";
                public final String libelle = "Pièce d'identité";
                public final boolean obligatoire = true;
                public final boolean fourni = mesDocuments.stream()
                    .anyMatch(d -> d.getType() == TypeDocumentEnum.PIECE_IDENTITE && d.getValide());
            },
            new Object() {
                public final String type = "CERTIFICAT_SCOLARITE";
                public final String libelle = "Certificat de scolarité";
                public final boolean obligatoire = true;
                public final boolean fourni = mesDocuments.stream()
                    .anyMatch(d -> d.getType() == TypeDocumentEnum.CERTIFICAT_SCOLARITE && d.getValide());
            },
            new Object() {
                public final String type = "PHOTO_IDENTITE";
                public final String libelle = "Photo d'identité";
                public final boolean obligatoire = true;
                public final boolean fourni = mesDocuments.stream()
                    .anyMatch(d -> d.getType() == TypeDocumentEnum.PHOTO_IDENTITE && d.getValide());
            },
            new Object() {
                public final String type = "ATTESTATION_BOURSE";
                public final String libelle = "Attestation de bourse (si applicable)";
                public final boolean obligatoire = false;
                public final boolean fourni = mesDocuments.stream()
                    .anyMatch(d -> d.getType() == TypeDocumentEnum.ATTESTATION_BOURSE && d.getValide());
            }
        );
        
        return ResponseEntity.ok(ApiResponse.success(documentsRequis, "Liste des documents requis"));
    }
}
