package sn.gtech.sgle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.gtech.sgle.entity.Utilisateur;
import sn.gtech.sgle.entity.enums.RoleEnum;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, UUID>, JpaSpecificationExecutor<Utilisateur> {
    
    Optional<Utilisateur> findByEmail(String email);
    
    Boolean existsByEmail(String email);

    Page<Utilisateur> findByRole(RoleEnum role, Pageable pageable);

    Page<Utilisateur> findByActif(Boolean actif, Pageable pageable);
    
    Page<Utilisateur> findByRoleAndActif(RoleEnum role, Boolean actif, Pageable pageable);
    
    Page<Utilisateur> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    long countByRole(RoleEnum role);

    long countByActif(Boolean actif);
    
    long countByDateCreationAfter(LocalDateTime date);

    @Query("SELECT u FROM Utilisateur u WHERE " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :recherche, '%'))")
    Page<Utilisateur> searchByEmail(@Param("recherche") String recherche, Pageable pageable);
}
