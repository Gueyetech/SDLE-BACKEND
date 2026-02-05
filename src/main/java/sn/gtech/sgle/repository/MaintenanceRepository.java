package sn.gtech.sgle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Maintenance;
import sn.gtech.sgle.entity.enums.StatutMaintenanceEnum;
import sn.gtech.sgle.entity.enums.TypeMaintenanceEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, UUID> {
    
    List<Maintenance> findByLogementId(UUID logementId);
    
    List<Maintenance> findByStatut(StatutMaintenanceEnum statut);
    
    List<Maintenance> findByType(TypeMaintenanceEnum type);
    
    List<Maintenance> findByDatePlanifieeBetween(LocalDate dateDebut, LocalDate dateFin);
    
    List<Maintenance> findByTechnicien(String technicien);
    
    List<Maintenance> findByLogementIdAndStatut(UUID logementId, StatutMaintenanceEnum statut);
}
