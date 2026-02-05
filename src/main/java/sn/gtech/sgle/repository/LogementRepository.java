package sn.gtech.sgle.repository;

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
    
    List<Logement> findByType(TypeLogementEnum type);
    
    List<Logement> findByTypeAndStatut(TypeLogementEnum type, StatutLogementEnum statut);
    
    List<Logement> findByGestionnaireId(UUID gestionnaireId);
    
    List<Logement> findByPrixMensuelLessThanEqual(BigDecimal prixMax);
    
    List<Logement> findByPrixMensuelBetween(BigDecimal prixMin, BigDecimal prixMax);
    
    List<Logement> findByMeuble(Boolean meuble);
    
    List<Logement> findByCapaciteGreaterThanEqual(Integer capacite);
    
    List<Logement> findByAdresseVille(String ville);
    
    List<Logement> findByAdresseQuartier(String quartier);
}
