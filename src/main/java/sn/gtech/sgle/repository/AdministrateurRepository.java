package sn.gtech.sgle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Administrateur;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, UUID> {
    
    Optional<Administrateur> findByEmail(String email);
    
    Boolean existsByEmail(String email);
    
    List<Administrateur> findByActif(Boolean actif);
}
