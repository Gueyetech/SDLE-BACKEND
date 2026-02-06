package sn.gtech.sgle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Attribution;
import sn.gtech.sgle.entity.enums.StatutAttributionEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttributionRepository extends JpaRepository<Attribution, UUID> {
    
    Optional<Attribution> findByNumeroContrat(String numeroContrat);
    
    List<Attribution> findByEtudiantId(UUID etudiantId);
    
    Page<Attribution> findByEtudiantId(UUID etudiantId, Pageable pageable);
    
    List<Attribution> findByLogementId(UUID logementId);
    
    Page<Attribution> findByLogementId(UUID logementId, Pageable pageable);
    
    List<Attribution> findByStatut(StatutAttributionEnum statut);
    
    Page<Attribution> findByStatut(StatutAttributionEnum statut, Pageable pageable);
    
    List<Attribution> findByGestionnaireId(UUID gestionnaireId);
    
    List<Attribution> findByEtudiantIdAndStatut(UUID etudiantId, StatutAttributionEnum statut);
    
    List<Attribution> findByLogementIdAndStatut(UUID logementId, StatutAttributionEnum statut);
    
    List<Attribution> findByDateFinBefore(LocalDate date);
    
    List<Attribution> findByDateFinBetween(LocalDate dateDebut, LocalDate dateFin);
    
    List<Attribution> findByStatutAndDateFinBetween(StatutAttributionEnum statut, LocalDate dateDebut, LocalDate dateFin);
    
    Optional<Attribution> findByEtudiantIdAndStatutAndLogementId(UUID etudiantId, StatutAttributionEnum statut, UUID logementId);
    
    // Méthodes de comptage
    long countByStatut(StatutAttributionEnum statut);
    
    long countByStatutAndDateFinBetween(StatutAttributionEnum statut, LocalDate dateDebut, LocalDate dateFin);
    
    boolean existsByEtudiantIdAndStatut(UUID etudiantId, StatutAttributionEnum statut);
}
