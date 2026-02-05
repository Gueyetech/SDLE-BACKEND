package sn.gtech.sgle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.GestionnaireLogements;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GestionnaireRepository extends JpaRepository<GestionnaireLogements, UUID> {
    
    Optional<GestionnaireLogements> findByEmail(String email);
    
    Boolean existsByEmail(String email);
    
    List<GestionnaireLogements> findByDepartement(String departement);
    
    List<GestionnaireLogements> findByActif(Boolean actif);
}
