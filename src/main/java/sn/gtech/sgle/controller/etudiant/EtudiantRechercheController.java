package sn.gtech.sgle.controller.etudiant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.gtech.sgle.dto.common.ApiResponse;
import sn.gtech.sgle.dto.etudiant.LogementCatalogueDto;
import sn.gtech.sgle.dto.etudiant.LogementDetailsDto;
import sn.gtech.sgle.dto.etudiant.RechercheLogementRequest;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;
import sn.gtech.sgle.service.EtudiantService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Contrôleur pour la recherche de logements par l'étudiant
 * 
 * Fonctionnalités:
 * - Consulter le catalogue des logements disponibles
 * - Rechercher par critères (type, prix, localisation, etc.)
 * - Voir les détails d'un logement
 */
@RestController
@RequestMapping("/api/etudiant/logements")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ETUDIANT')")
@Tag(name = "Étudiant - Recherche Logements", description = "Recherche et consultation du catalogue de logements")
public class EtudiantRechercheController {
    
    private final EtudiantService etudiantService;
    
    /**
     * Récupère le catalogue des logements disponibles avec pagination
     */
    @GetMapping
    @Operation(summary = "Catalogue des logements", description = "Liste paginée des logements disponibles")
    public ResponseEntity<ApiResponse<Page<LogementCatalogueDto>>> getCatalogue(
            @Parameter(description = "Numéro de page (0-indexed)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") Integer taille,
            @Parameter(description = "Trier par: prix, date, capacite, superficie") @RequestParam(required = false) String triPar,
            @Parameter(description = "Ordre: asc ou desc") @RequestParam(required = false) String ordreTri) {
        
        RechercheLogementRequest request = RechercheLogementRequest.builder()
            .page(page)
            .taille(taille)
            .triPar(triPar)
            .ordreTri(ordreTri)
            .build();
        
        Page<LogementCatalogueDto> logements = etudiantService.getCatalogueLogements(request);
        return ResponseEntity.ok(ApiResponse.success(logements, "Catalogue récupéré avec succès"));
    }
    
    /**
     * Recherche avancée de logements
     */
    @PostMapping("/rechercher")
    @Operation(summary = "Recherche avancée", description = "Recherche de logements selon plusieurs critères")
    public ResponseEntity<ApiResponse<List<LogementCatalogueDto>>> rechercherLogements(
            @RequestBody RechercheLogementRequest request) {
        List<LogementCatalogueDto> logements = etudiantService.rechercherLogements(request);
        return ResponseEntity.ok(ApiResponse.success(logements, 
            logements.size() + " logement(s) trouvé(s)"));
    }
    
    /**
     * Recherche rapide par type de logement
     */
    @GetMapping("/type/{type}")
    @Operation(summary = "Par type", description = "Liste des logements disponibles par type")
    public ResponseEntity<ApiResponse<List<LogementCatalogueDto>>> getLogementsParType(
            @Parameter(description = "Type de logement") @PathVariable TypeLogementEnum type) {
        
        RechercheLogementRequest request = RechercheLogementRequest.builder()
            .typeLogement(type)
            .build();
        
        List<LogementCatalogueDto> logements = etudiantService.rechercherLogements(request);
        return ResponseEntity.ok(ApiResponse.success(logements, 
            logements.size() + " logement(s) de type " + type.name()));
    }
    
    /**
     * Recherche par budget maximum
     */
    @GetMapping("/budget/{prixMax}")
    @Operation(summary = "Par budget", description = "Liste des logements dans un budget maximum")
    public ResponseEntity<ApiResponse<List<LogementCatalogueDto>>> getLogementsParBudget(
            @Parameter(description = "Prix maximum mensuel") @PathVariable BigDecimal prixMax) {
        
        RechercheLogementRequest request = RechercheLogementRequest.builder()
            .prixMax(prixMax)
            .build();
        
        List<LogementCatalogueDto> logements = etudiantService.rechercherLogements(request);
        return ResponseEntity.ok(ApiResponse.success(logements, 
            logements.size() + " logement(s) dans votre budget"));
    }
    
    /**
     * Recherche par ville
     */
    @GetMapping("/ville/{ville}")
    @Operation(summary = "Par ville", description = "Liste des logements dans une ville")
    public ResponseEntity<ApiResponse<List<LogementCatalogueDto>>> getLogementsParVille(
            @Parameter(description = "Nom de la ville") @PathVariable String ville) {
        
        RechercheLogementRequest request = RechercheLogementRequest.builder()
            .ville(ville)
            .build();
        
        List<LogementCatalogueDto> logements = etudiantService.rechercherLogements(request);
        return ResponseEntity.ok(ApiResponse.success(logements, 
            logements.size() + " logement(s) à " + ville));
    }
    
    /**
     * Récupère les détails complets d'un logement
     */
    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un logement", description = "Informations complètes d'un logement")
    public ResponseEntity<ApiResponse<LogementDetailsDto>> getDetailsLogement(
            @Parameter(description = "ID du logement") @PathVariable UUID id) {
        LogementDetailsDto logement = etudiantService.getDetailsLogement(id);
        return ResponseEntity.ok(ApiResponse.success(logement, "Détails du logement récupérés"));
    }
}
