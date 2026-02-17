package sn.gtech.sgle.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour le récapitulatif des paiements (vue étudiant)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecapitulatifPaiementsDto {
    
    // Totaux
    private BigDecimal totalLoyer;
    private BigDecimal totalPaye;
    private BigDecimal totalAPayer;
    private BigDecimal totalEnRetard;
    private String totalPayeFormate;
    private String totalAPayerFormate;
    
    // Compteurs
    private Integer nombrePaiementsTotal;
    private Integer nombrePaiementsEffectues;
    private Integer nombrePaiementsEnAttente;
    private Integer nombrePaiementsEnRetard;
    
    // Prochain paiement
    private LocalDate prochainEcheance;
    private BigDecimal montantProchainPaiement;
    private Long joursAvantProchainPaiement;
    private Boolean prochainPaiementUrgent; // < 7 jours
    
    // Statistiques
    private Double tauxPaiement; // pourcentage
    private Integer moisConsecutifsSansPaiement;
    
    // Historique récent
    private List<PaiementEtudiantDto> dernierspaiements;
    
    // Paiements à régler
    private List<PaiementEtudiantDto> paiementsAPayer;
    
    // Paiements en retard
    private List<PaiementEtudiantDto> paiementsEnRetard;
}
