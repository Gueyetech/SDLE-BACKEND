package sn.gtech.sgle.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Attribution;
import sn.gtech.sgle.entity.enums.StatutAttributionEnum;
import sn.gtech.sgle.entity.enums.StatutPaiementEnum;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO pour "Mon Logement" - Vue détaillée du logement attribué à l'étudiant
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonLogementDto {
    
    // Attribution
    private UUID attributionId;
    private String numeroContrat;
    private StatutAttributionEnum statutAttribution;
    
    // Dates attribution
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDateTime dateAttribution;
    private LocalDateTime dateCheckIn;
    private LocalDateTime dateCheckOut;
    
    // Informations logement
    private UUID logementId;
    private String logementCode;
    private TypeLogementEnum logementType;
    private String logementTypeLibelle;
    private Integer capacite;
    private Float superficie;
    private Boolean meuble;
    private String description;
    
    // Adresse
    private String adresseComplete;
    private String ville;
    private String quartier;
    private Float latitude;
    private Float longitude;
    
    // Photos
    private List<String> photos;
    
    // Équipements
    private List<String> equipements;
    
    // Montants
    private BigDecimal montantLoyer;
    private BigDecimal montantCaution;
    private String loyerFormate;
    private String cautionFormatee;
    
    // Paiements
    private BigDecimal totalPaye;
    private BigDecimal soldeRestant;
    private Integer paiementsEffectues;
    private Integer paiementsEnAttente;
    private Integer paiementsEnRetard;
    private LocalDate prochainEcheance;
    private BigDecimal montantProchainPaiement;
    
    // Gestionnaire
    private String gestionnaireNom;
    private String gestionnaireEmail;
    private String gestionnaireTelephone;
    
    // Calculs
    private Long joursRestants;
    private Long moisRestants;
    private Long dureeEnMois;
    private Boolean expireBientot; // Dans les 30 jours
    private Boolean checkInEffectue;
    
    // Contrat
    private UUID contratId;
    private String contratUrl;
    
    /**
     * Convertit une entité Attribution en DTO MonLogement
     */
    public static MonLogementDto fromEntity(Attribution attribution) {
        if (attribution == null) return null;
        
        var logement = attribution.getLogement();
        
        String adresse = "";
        if (logement != null && logement.getAdresse() != null) {
            var adr = logement.getAdresse();
            StringBuilder sb = new StringBuilder();
            if (adr.getNumero() != null) sb.append(adr.getNumero()).append(" ");
            if (adr.getRue() != null) sb.append(adr.getRue()).append(", ");
            if (adr.getQuartier() != null) sb.append(adr.getQuartier()).append(", ");
            if (adr.getVille() != null) sb.append(adr.getVille());
            adresse = sb.toString().replaceAll(", $", "");
        }
        
        // Calculs paiements
        BigDecimal totalPaye = BigDecimal.ZERO;
        BigDecimal soldeRestant = BigDecimal.ZERO;
        int paiementsOk = 0, paiementsWait = 0, paiementsRetard = 0;
        LocalDate prochainEcheance = null;
        BigDecimal montantProchain = null;
        
        if (attribution.getPaiements() != null) {
            for (var p : attribution.getPaiements()) {
                if (p.getStatut() == StatutPaiementEnum.PAYE) {
                    totalPaye = totalPaye.add(p.getMontant());
                    paiementsOk++;
                } else if (p.getStatut() == StatutPaiementEnum.EN_ATTENTE) {
                    soldeRestant = soldeRestant.add(p.getMontant());
                    paiementsWait++;
                    if (p.getDateEcheance() != null && (prochainEcheance == null || p.getDateEcheance().isBefore(prochainEcheance))) {
                        prochainEcheance = p.getDateEcheance();
                        montantProchain = p.getMontant();
                    }
                    if (p.getDateEcheance() != null && p.getDateEcheance().isBefore(LocalDate.now())) {
                        paiementsRetard++;
                    }
                }
            }
        }
        
        long joursRestants = 0;
        long moisRestants = 0;
        boolean expireBientot = false;
        if (attribution.getDateFin() != null) {
            joursRestants = ChronoUnit.DAYS.between(LocalDate.now(), attribution.getDateFin());
            moisRestants = ChronoUnit.MONTHS.between(LocalDate.now(), attribution.getDateFin());
            expireBientot = joursRestants > 0 && joursRestants <= 30;
        }
        
        long duree = 0;
        if (attribution.getDateDebut() != null && attribution.getDateFin() != null) {
            duree = ChronoUnit.MONTHS.between(attribution.getDateDebut(), attribution.getDateFin());
        }
        
        MonLogementDtoBuilder builder = MonLogementDto.builder()
                .attributionId(attribution.getId())
                .numeroContrat(attribution.getNumeroContrat())
                .statutAttribution(attribution.getStatut())
                .dateDebut(attribution.getDateDebut())
                .dateFin(attribution.getDateFin())
                .dateAttribution(attribution.getDateAttribution())
                .dateCheckIn(attribution.getDateCheckIn())
                .dateCheckOut(attribution.getDateCheckOut())
                .montantLoyer(attribution.getMontantLoyer())
                .montantCaution(attribution.getMontantCaution())
                .loyerFormate(attribution.getMontantLoyer() != null ? attribution.getMontantLoyer().toPlainString() + " FCFA/mois" : null)
                .cautionFormatee(attribution.getMontantCaution() != null ? attribution.getMontantCaution().toPlainString() + " FCFA" : null)
                .totalPaye(totalPaye)
                .soldeRestant(soldeRestant)
                .paiementsEffectues(paiementsOk)
                .paiementsEnAttente(paiementsWait)
                .paiementsEnRetard(paiementsRetard)
                .prochainEcheance(prochainEcheance)
                .montantProchainPaiement(montantProchain)
                .joursRestants(joursRestants)
                .moisRestants(moisRestants)
                .dureeEnMois(duree)
                .expireBientot(expireBientot)
                .checkInEffectue(attribution.getDateCheckIn() != null);
        
        if (logement != null) {
            builder.logementId(logement.getId())
                   .logementCode(logement.getCode())
                   .logementType(logement.getType())
                   .logementTypeLibelle(logement.getType() != null ? logement.getType().name() : null)
                   .capacite(logement.getCapacite())
                   .superficie(logement.getSuperficie())
                   .meuble(logement.getMeuble())
                   .description(logement.getDescription())
                   .adresseComplete(adresse)
                   .photos(logement.getPhotos());
            
            if (logement.getAdresse() != null) {
                builder.ville(logement.getAdresse().getVille())
                       .quartier(logement.getAdresse().getQuartier())
                       .latitude(logement.getAdresse().getLatitude())
                       .longitude(logement.getAdresse().getLongitude());
            }
            
            if (logement.getEquipements() != null) {
                builder.equipements(logement.getEquipements().stream()
                        .map(e -> e.getNom())
                        .collect(Collectors.toList()));
            }
        }
        
        if (attribution.getGestionnaire() != null) {
            builder.gestionnaireEmail(attribution.getGestionnaire().getEmail());
        }
        
        if (attribution.getContrat() != null) {
            builder.contratId(attribution.getContrat().getId())
                   .contratUrl(attribution.getContrat().getUrl());
        }
        
        return builder.build();
    }
}
