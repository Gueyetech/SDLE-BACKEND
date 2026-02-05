package sn.gtech.sgle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Paiement;
import sn.gtech.sgle.entity.enums.StatutPaiementEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, UUID> {
    
    Optional<Paiement> findByNumeroPaiement(String numeroPaiement);
    
    List<Paiement> findByAttributionId(UUID attributionId);
    
    List<Paiement> findByStatut(StatutPaiementEnum statut);
    
    List<Paiement> findByDateEcheanceBefore(LocalDate date);
    
    List<Paiement> findByStatutAndDateEcheanceBefore(StatutPaiementEnum statut, LocalDate date);
    
    List<Paiement> findByMoisConcerne(String moisConcerne);
}
