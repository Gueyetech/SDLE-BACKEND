package sn.gtech.sgle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Equipement;
import sn.gtech.sgle.entity.enums.CategorieEquipementEnum;

import java.util.List;
import java.util.UUID;

@Repository
public interface EquipementRepository extends JpaRepository<Equipement, UUID> {
    
    List<Equipement> findByLogementId(UUID logementId);
    
    List<Equipement> findByCategorie(CategorieEquipementEnum categorie);
    
    List<Equipement> findByFonctionnel(Boolean fonctionnel);
    
    List<Equipement> findByLogementIdAndFonctionnel(UUID logementId, Boolean fonctionnel);
}
