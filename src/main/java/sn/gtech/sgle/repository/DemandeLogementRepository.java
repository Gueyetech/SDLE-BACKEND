package sn.gtech.sgle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.DemandeLogement;
import sn.gtech.sgle.entity.enums.StatutDemandeEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DemandeLogementRepository extends JpaRepository<DemandeLogement, UUID> {
    
    Optional<DemandeLogement> findByNumeroReference(String numeroReference);
    
    List<DemandeLogement> findByEtudiantId(UUID etudiantId);
    
    List<DemandeLogement> findByStatut(StatutDemandeEnum statut);
    
    List<DemandeLogement> findByGestionnaireId(UUID gestionnaireId);
    
    List<DemandeLogement> findByEtudiantIdAndStatut(UUID etudiantId, StatutDemandeEnum statut);
    
    List<DemandeLogement> findByStatutOrderByDateDemandeAsc(StatutDemandeEnum statut);
    
    Long countByStatut(StatutDemandeEnum statut);
}
