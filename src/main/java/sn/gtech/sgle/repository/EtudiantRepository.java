package sn.gtech.sgle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Etudiant;
import sn.gtech.sgle.entity.enums.NiveauEtudesEnum;
import sn.gtech.sgle.entity.enums.StatutEtudiantEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, UUID> {
    
    Optional<Etudiant> findByMatricule(String matricule);
    
    Optional<Etudiant> findByEmail(String email);
    
    Boolean existsByMatricule(String matricule);
    
    Boolean existsByEmail(String email);
    
    List<Etudiant> findByStatut(StatutEtudiantEnum statut);
    
    List<Etudiant> findByUniversite(String universite);
    
    List<Etudiant> findByNiveauEtudes(NiveauEtudesEnum niveauEtudes);
    
    List<Etudiant> findByAnneeAcademique(String anneeAcademique);
    
    List<Etudiant> findByActif(Boolean actif);
}
