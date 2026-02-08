package sn.gtech.sgle.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import sn.gtech.sgle.entity.enums.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/enums")
@RequiredArgsConstructor
@Tag(name = "Énumérations", description = "Récupération des valeurs des énumérations utilisées dans l'application")
public class TypeEnumController {

    @GetMapping("/canal")
    @Operation(summary = "Liste des canaux de communication")
    public List<CanalEnum> canal() {
        return Arrays.asList(CanalEnum.values());
    }

    @GetMapping("/categorie-equipement")
    @Operation(summary = "Liste des catégories d'équipement")
    public List<CategorieEquipementEnum> categorieEquipement() {
        return Arrays.asList(CategorieEquipementEnum.values());
    }

    @GetMapping("/format")
    @Operation(summary = "Liste des formats")
    public List<FormatEnum> format() {
        return Arrays.asList(FormatEnum.values());
    }

    @GetMapping("/format-rapport")
    @Operation(summary = "Liste des formats de rapport")
    public List<FormatRapportEnum> formatRapport() {
        return Arrays.asList(FormatRapportEnum.values());
    }

    @GetMapping("/mode-paiement")
    @Operation(summary = "Liste des modes de paiement")
    public List<ModePaiementEnum> modePaiement() {
        return Arrays.asList(ModePaiementEnum.values());
    }

    @GetMapping("/niveau-etudes")
    @Operation(summary = "Liste des niveaux d'études")
    public List<NiveauEtudesEnum> niveauEtudes() {
        return Arrays.asList(NiveauEtudesEnum.values());
    }

    @GetMapping("/priorite")
    @Operation(summary = "Liste des priorités")
    public List<PrioriteEnum> priorite() {
        return Arrays.asList(PrioriteEnum.values());
    }

    @GetMapping("/role")
    @Operation(summary = "Liste des rôles utilisateur")
    public List<RoleEnum> role() {
        return Arrays.asList(RoleEnum.values());
    }

    @GetMapping("/statut-attribution")
    @Operation(summary = "Liste des statuts d'attribution")
    public List<StatutAttributionEnum> statutAttribution() {
        return Arrays.asList(StatutAttributionEnum.values());
    }

    @GetMapping("/statut-demande")
    @Operation(summary = "Liste des statuts de demande")
    public List<StatutDemandeEnum> statutDemande() {
        return Arrays.asList(StatutDemandeEnum.values());
    }

    @GetMapping("/statut-etudiant")
    @Operation(summary = "Liste des statuts d'étudiant")
    public List<StatutEtudiantEnum> statutEtudiant() {
        return Arrays.asList(StatutEtudiantEnum.values());
    }

    @GetMapping("/statut-incident")
    @Operation(summary = "Liste des statuts d'incident")
    public List<StatutIncidentEnum> statutIncident() {
        return Arrays.asList(StatutIncidentEnum.values());
    }

    @GetMapping("/statut-logement")
    @Operation(summary = "Liste des statuts de logement")
    public List<StatutLogementEnum> statutLogement() {
        return Arrays.asList(StatutLogementEnum.values());
    }

    @GetMapping("/statut-maintenance")
    @Operation(summary = "Liste des statuts de maintenance")
    public List<StatutMaintenanceEnum> statutMaintenance() {
        return Arrays.asList(StatutMaintenanceEnum.values());
    }

    @GetMapping("/statut-paiement")
    @Operation(summary = "Liste des statuts de paiement")
    public List<StatutPaiementEnum> statutPaiement() {
        return Arrays.asList(StatutPaiementEnum.values());
    }

    @GetMapping("/type-document")
    @Operation(summary = "Liste des types de document")
    public List<TypeDocumentEnum> typeDocument() {
        return Arrays.asList(TypeDocumentEnum.values());
    }

    @GetMapping("/type-incident")
    @Operation(summary = "Liste des types d'incident")
    public List<TypeIncidentEnum> typeIncident() {
        return Arrays.asList(TypeIncidentEnum.values());
    }

    @GetMapping("/type-logement")
    @Operation(summary = "Liste des types de logement")
    public List<TypeLogementEnum> typeLogement() {
        return Arrays.asList(TypeLogementEnum.values());
    }

    @GetMapping("/type-maintenance")
    @Operation(summary = "Liste des types de maintenance")
    public List<TypeMaintenanceEnum> typeMaintenance() {
        return Arrays.asList(TypeMaintenanceEnum.values());
    }

    @GetMapping("/type-notification")
    @Operation(summary = "Liste des types de notification")
    public List<TypeNotificationEnum> typeNotification() {
        return Arrays.asList(TypeNotificationEnum.values());
    }

    @GetMapping("/type-rapport")
    @Operation(summary = "Liste des types de rapport")
    public List<TypeRapportEnum> typeRapport() {
        return Arrays.asList(TypeRapportEnum.values());
    }

    @GetMapping("/urgence")
    @Operation(summary = "Liste des niveaux d'urgence")
    public List<UrgenceEnum> urgence() {
        return Arrays.asList(UrgenceEnum.values());
    }
}
