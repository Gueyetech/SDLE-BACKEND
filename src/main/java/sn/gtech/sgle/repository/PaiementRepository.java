package sn.gtech.sgle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Paiement;
import sn.gtech.sgle.entity.enums.StatutPaiementEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, UUID> {
    
    Optional<Paiement> findByNumeroPaiement(String numeroPaiement);
    
    List<Paiement> findByAttributionId(UUID attributionId);
    
    Page<Paiement> findByAttributionId(UUID attributionId, Pageable pageable);
    
    List<Paiement> findByStatut(StatutPaiementEnum statut);
    
    Page<Paiement> findByStatut(StatutPaiementEnum statut, Pageable pageable);
    
    List<Paiement> findByDateEcheanceBefore(LocalDate date);
    
    List<Paiement> findByStatutAndDateEcheanceBefore(StatutPaiementEnum statut, LocalDate date);
    
    List<Paiement> findByMoisConcerne(String moisConcerne);
    
    // Méthodes de comptage
    long countByStatut(StatutPaiementEnum statut);
    
    // Méthodes de somme
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p")
    BigDecimal sumMontant();
    
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.statut = :statut AND p.datePaiement > :date")
    BigDecimal sumMontantByStatutAndDatePaiementAfter(@Param("statut") StatutPaiementEnum statut, 
                                                       @Param("date") LocalDateTime date);
    
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.statut IN :statuts")
    BigDecimal sumMontantByStatutIn(@Param("statuts") List<StatutPaiementEnum> statuts);
    
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.attribution.id = :attributionId AND p.statut = :statut")
    BigDecimal sumMontantByAttributionIdAndStatut(@Param("attributionId") UUID attributionId, 
                                                   @Param("statut") StatutPaiementEnum statut);
    
    // Méthode pour trouver les paiements en retard (statut EN_ATTENTE et date d'échéance passée)
    @Query("SELECT p FROM Paiement p WHERE p.statut = 'EN_ATTENTE' AND p.dateEcheance < CURRENT_DATE")
    List<Paiement> findPaiementsEnRetard();
}
