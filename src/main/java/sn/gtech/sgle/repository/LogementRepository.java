package sn.gtech.sgle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Logement;
import sn.gtech.sgle.entity.enums.StatutLogementEnum;
import sn.gtech.sgle.entity.enums.TypeLogementEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LogementRepository extends JpaRepository<Logement, UUID> {
    
    Optional<Logement> findByCode(String code);
    
    Boolean existsByCode(String code);
    
    List<Logement> findByStatut(StatutLogementEnum statut);
    
    Page<Logement> findByStatut(StatutLogementEnum statut, Pageable pageable);
    
    List<Logement> findByType(TypeLogementEnum type);
    
    Page<Logement> findByType(TypeLogementEnum type, Pageable pageable);
    
    List<Logement> findByTypeAndStatut(TypeLogementEnum type, StatutLogementEnum statut);
    
    Page<Logement> findByTypeAndStatut(TypeLogementEnum type, StatutLogementEnum statut, Pageable pageable);
    
    List<Logement> findByGestionnaireId(UUID gestionnaireId);
    
    List<Logement> findByPrixMensuelLessThanEqual(BigDecimal prixMax);
    
    List<Logement> findByPrixMensuelBetween(BigDecimal prixMin, BigDecimal prixMax);
    
    List<Logement> findByMeuble(Boolean meuble);
    
    List<Logement> findByCapaciteGreaterThanEqual(Integer capacite);
    
    List<Logement> findByAdresseVille(String ville);
    
    List<Logement> findByAdresseQuartier(String quartier);
    
    // Méthodes de comptage
    long countByStatut(StatutLogementEnum statut);
    
    long countByType(TypeLogementEnum type);
    
    // Recherche avec pagination
    Page<Logement> findByCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String code, String description, Pageable pageable);
}
