package sn.gtech.sgle.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Logement;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO détaillé pour un logement (vue étudiant)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogementDetailsDto {
    
    private UUID id;
    private String code;
    private TypeLogementEnum type;
    private String typeLibelle;
    
    // Prix
    private BigDecimal prixMensuel;
    private String prixFormate;
    
    // Capacité et superficie
    private Integer capacite;
    private Float superficie;
    private String superficieFormatee;
    
    // Description complète
    private String description;
    
    // Adresse complète
    private AdresseDto adresse;
    
    // Photos
    private List<String> photos;
    private Integer nombrePhotos;
    
    // Équipements détaillés
    private List<EquipementDto> equipements;
    
    // Caractéristiques
    private Boolean meuble;
    private Boolean disponible;
    
    // Contact gestionnaire (simplifié)
    private GestionnaireContactDto gestionnaire;
    
    /**
     * DTO pour l'adresse
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdresseDto {
        private String rue;
        private String numero;
        private String codePostal;
        private String ville;
        private String quartier;
        private String region;
        private String pays;
        private Float latitude;
        private Float longitude;
        private String complement;
        private String adresseComplete;
    }
    
    /**
     * DTO pour les équipements
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EquipementDto {
        private String nom;
        private String categorie;
        private String description;
    }
    
    /**
     * DTO pour le contact gestionnaire
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GestionnaireContactDto {
        private String nom;
        private String email;
        private String telephone;
    }
    
    /**
     * Convertit une entité Logement en DTO détaillé
     */
    public static LogementDetailsDto fromEntity(Logement logement) {
        if (logement == null) return null;
        
        AdresseDto adresseDto = null;
        String adresseComplete = "";
        if (logement.getAdresse() != null) {
            var adr = logement.getAdresse();
            StringBuilder sb = new StringBuilder();
            if (adr.getNumero() != null) sb.append(adr.getNumero()).append(" ");
            if (adr.getRue() != null) sb.append(adr.getRue()).append(", ");
            if (adr.getQuartier() != null) sb.append(adr.getQuartier()).append(", ");
            if (adr.getVille() != null) sb.append(adr.getVille()).append(", ");
            if (adr.getRegion() != null) sb.append(adr.getRegion()).append(", ");
            if (adr.getPays() != null) sb.append(adr.getPays());
            adresseComplete = sb.toString().replaceAll(", $", "");
            
            adresseDto = AdresseDto.builder()
                    .rue(adr.getRue())
                    .numero(adr.getNumero())
                    .codePostal(adr.getCodePostal())
                    .ville(adr.getVille())
                    .quartier(adr.getQuartier())
                    .region(adr.getRegion())
                    .pays(adr.getPays())
                    .latitude(adr.getLatitude())
                    .longitude(adr.getLongitude())
                    .complement(adr.getComplementAdresse())
                    .adresseComplete(adresseComplete)
                    .build();
        }
        
        List<EquipementDto> equipementsList = logement.getEquipements() != null 
            ? logement.getEquipements().stream()
                .map(e -> EquipementDto.builder()
                    .nom(e.getNom())
                    .categorie(e.getCategorie() != null ? e.getCategorie().name() : null)
                    .description(e.getDescription())
                    .build())
                .collect(Collectors.toList())
            : List.of();
        
        GestionnaireContactDto gestionnaireDto = null;
        if (logement.getGestionnaire() != null) {
            gestionnaireDto = GestionnaireContactDto.builder()
                    .email(logement.getGestionnaire().getEmail())
                    .build();
        }
        
        return LogementDetailsDto.builder()
                .id(logement.getId())
                .code(logement.getCode())
                .type(logement.getType())
                .typeLibelle(logement.getType() != null ? logement.getType().name() : null)
                .prixMensuel(logement.getPrixMensuel())
                .prixFormate(logement.getPrixMensuel() != null ? logement.getPrixMensuel().toPlainString() + " FCFA/mois" : null)
                .capacite(logement.getCapacite())
                .superficie(logement.getSuperficie())
                .superficieFormatee(logement.getSuperficie() != null ? logement.getSuperficie() + " m²" : null)
                .description(logement.getDescription())
                .adresse(adresseDto)
                .photos(logement.getPhotos())
                .nombrePhotos(logement.getPhotos() != null ? logement.getPhotos().size() : 0)
                .equipements(equipementsList)
                .meuble(logement.getMeuble())
                .disponible(logement.verifierDisponibilite())
                .gestionnaire(gestionnaireDto)
                .build();
    }
}
