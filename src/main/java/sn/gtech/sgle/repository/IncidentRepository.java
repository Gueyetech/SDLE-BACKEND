package sn.gtech.sgle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Incident;
import sn.gtech.sgle.entity.enums.StatutIncidentEnum;
import sn.gtech.sgle.entity.enums.TypeIncidentEnum;
import sn.gtech.sgle.entity.enums.UrgenceEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, UUID> {
    
    Optional<Incident> findByNumeroTicket(String numeroTicket);
    
    Boolean existsByNumeroTicket(String numeroTicket);
    
    List<Incident> findByEtudiantId(UUID etudiantId);
    
    List<Incident> findByLogementId(UUID logementId);
    
    List<Incident> findByStatut(StatutIncidentEnum statut);
    
    List<Incident> findByType(TypeIncidentEnum type);
    
    List<Incident> findByUrgence(UrgenceEnum urgence);
    
    List<Incident> findByGestionnaireId(UUID gestionnaireId);
    
    List<Incident> findByStatutAndUrgence(StatutIncidentEnum statut, UrgenceEnum urgence);
    
    Long countByStatut(StatutIncidentEnum statut);
    
    long countByUrgence(UrgenceEnum urgence);
}
