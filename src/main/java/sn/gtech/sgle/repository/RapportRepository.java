package sn.gtech.sgle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Rapport;
import sn.gtech.sgle.entity.enums.TypeRapportEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RapportRepository extends JpaRepository<Rapport, UUID> {
    
    List<Rapport> findByType(TypeRapportEnum type);
    
    List<Rapport> findByGenereParId(UUID administrateurId);
    
    List<Rapport> findByPeriodeDebutBetween(LocalDate dateDebut, LocalDate dateFin);
    
    List<Rapport> findByTypeAndGenereParId(TypeRapportEnum type, UUID administrateurId);
}
