package sn.gtech.sgle.dto.paiement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO pour les statistiques de paiement
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesPaiementDto {
    
    // Totaux
    private Long totalPaiements;
    private Long paiementsPayes;
    private Long paiementsEnAttente;
    private Long paiementsEnRetard;
    private Long paiementsAnnules;
    private Long paiementsRembourses;
    
    // Montants
    private BigDecimal montantTotalAttendu;
    private BigDecimal montantTotalPercu;
    private BigDecimal montantEnAttente;
    private BigDecimal montantEnRetard;
    private BigDecimal montantRembourse;
    
    // Taux
    private Double tauxRecouvrement; // Pourcentage
    private Double tauxRetard; // Pourcentage
    
    // Par période
    private BigDecimal revenusMoisCourant;
    private BigDecimal revenusAnneeCourante;
    private Long paiementsRetardMoisCourant;
    
    // Moyennes
    private BigDecimal montantMoyenPaiement;
    private Double delaiMoyenPaiement; // En jours
}
