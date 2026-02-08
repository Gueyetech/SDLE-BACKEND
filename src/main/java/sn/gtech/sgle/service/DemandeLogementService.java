package sn.gtech.sgle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.gtech.sgle.dto.demande.CreerDemandeRequest;
import sn.gtech.sgle.dto.demande.DemandeResponseDto;
import sn.gtech.sgle.entity.DemandeLogement;
import sn.gtech.sgle.entity.Utilisateur;
import sn.gtech.sgle.entity.enums.PrioriteEnum;
import sn.gtech.sgle.entity.enums.StatutDemandeEnum;
import sn.gtech.sgle.exception.AuthException;
import sn.gtech.sgle.repository.DemandeLogementRepository;
import sn.gtech.sgle.repository.UtilisateurRepository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des demandes de logement
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DemandeLogementService {
    
    private final DemandeLogementRepository demandeRepository;
    private final UtilisateurRepository utilisateurRepository;
    
    /**
     * Crée une nouvelle demande de logement
     */
    public DemandeResponseDto creerDemande(CreerDemandeRequest request) {
        Utilisateur etudiant = utilisateurRepository.findById(request.getEtudiantId())
                .orElseThrow(() -> new AuthException("Étudiant non trouvé"));
        
        DemandeLogement demande = DemandeLogement.builder()
                .numeroReference(genererReference())
                .etudiant(etudiant)
                .typeLogementSouhaite(request.getTypeLogementSouhaite())
                .budgetMaximum(request.getBudgetMaximum())
                .dateDebutSouhaitee(request.getDateDebutSouhaitee())
                .dureeSouhaitee(request.getDureeSouhaitee())
                .preferences(request.getPreferences())
                .statut(StatutDemandeEnum.EN_ATTENTE)
                .priorite(request.getPriorite() != null ? request.getPriorite() : PrioriteEnum.NORMALE)
                .commentaires(request.getCommentaires())
                .dateDemande(LocalDateTime.now())
                .build();
        
        demande = demandeRepository.save(demande);
        return DemandeResponseDto.fromEntity(demande);
    }
    
    /**
     * Génère une référence unique pour une demande
     */
    private String genererReference() {
        int annee = Year.now().getValue();
        long count = demandeRepository.count() + 1;
        return "DEM-" + annee + "-" + String.format("%05d", count);
    }
    
    /**
     * Récupère toutes les demandes
     */
    @Transactional(readOnly = true)
    public List<DemandeResponseDto> getToutesDemandes() {
        return demandeRepository.findAll().stream()
                .map(DemandeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère une demande par son ID
     */
    @Transactional(readOnly = true)
    public DemandeResponseDto getDemandeParId(UUID id) {
        DemandeLogement demande = demandeRepository.findById(id)
                .orElseThrow(() -> new AuthException("Demande non trouvée"));
        return DemandeResponseDto.fromEntity(demande);
    }
    
    /**
     * Récupère une demande par sa référence
     */
    @Transactional(readOnly = true)
    public DemandeResponseDto getDemandeParReference(String reference) {
        DemandeLogement demande = demandeRepository.findByNumeroReference(reference)
                .orElseThrow(() -> new AuthException("Demande non trouvée"));
        return DemandeResponseDto.fromEntity(demande);
    }
    
    /**
     * Récupère les demandes par statut
     */
    @Transactional(readOnly = true)
    public List<DemandeResponseDto> getDemandesParStatut(StatutDemandeEnum statut) {
        return demandeRepository.findByStatut(statut).stream()
                .map(DemandeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les demandes en attente triées par date
     */
    @Transactional(readOnly = true)
    public List<DemandeResponseDto> getDemandesEnAttente() {
        return demandeRepository.findByStatutOrderByDateDemandeAsc(StatutDemandeEnum.EN_ATTENTE).stream()
                .map(DemandeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les demandes d'un étudiant
     */
    @Transactional(readOnly = true)
    public List<DemandeResponseDto> getDemandesParEtudiant(UUID etudiantId) {
        return demandeRepository.findByEtudiantId(etudiantId).stream()
                .map(DemandeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les demandes gérées par un gestionnaire
     */
    @Transactional(readOnly = true)
    public List<DemandeResponseDto> getDemandesParGestionnaire(UUID gestionnaireId) {
        return demandeRepository.findByGestionnaireId(gestionnaireId).stream()
                .map(DemandeResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Approuve une demande
     */
    public DemandeResponseDto approuverDemande(UUID id, UUID gestionnaireId) {
        DemandeLogement demande = demandeRepository.findById(id)
                .orElseThrow(() -> new AuthException("Demande non trouvée"));
        
        if (demande.getStatut() != StatutDemandeEnum.EN_ATTENTE && 
            demande.getStatut() != StatutDemandeEnum.EN_COURS_TRAITEMENT) {
            throw new AuthException("La demande ne peut pas être approuvée dans son état actuel");
        }
        
        Utilisateur gestionnaire = utilisateurRepository.findById(gestionnaireId)
                .orElseThrow(() -> new AuthException("Gestionnaire non trouvé"));
        
        demande.setStatut(StatutDemandeEnum.APPROUVEE);
        demande.setGestionnaire(gestionnaire);
        demande.setDateTraitement(LocalDateTime.now());
        
        demande = demandeRepository.save(demande);
        return DemandeResponseDto.fromEntity(demande);
    }
    
    /**
     * Rejette une demande
     */
    public DemandeResponseDto rejeterDemande(UUID id, UUID gestionnaireId, String motif) {
        DemandeLogement demande = demandeRepository.findById(id)
                .orElseThrow(() -> new AuthException("Demande non trouvée"));
        
        if (demande.getStatut() != StatutDemandeEnum.EN_ATTENTE && 
            demande.getStatut() != StatutDemandeEnum.EN_COURS_TRAITEMENT) {
            throw new AuthException("La demande ne peut pas être rejetée dans son état actuel");
        }
        
        Utilisateur gestionnaire = utilisateurRepository.findById(gestionnaireId)
                .orElseThrow(() -> new AuthException("Gestionnaire non trouvé"));
        
        demande.setStatut(StatutDemandeEnum.REJETEE);
        demande.setGestionnaire(gestionnaire);
        demande.setMotifRejet(motif);
        demande.setDateTraitement(LocalDateTime.now());
        
        demande = demandeRepository.save(demande);
        return DemandeResponseDto.fromEntity(demande);
    }
    
    /**
     * Met une demande en cours de traitement
     */
    public DemandeResponseDto mettreEnTraitement(UUID id, UUID gestionnaireId) {
        DemandeLogement demande = demandeRepository.findById(id)
                .orElseThrow(() -> new AuthException("Demande non trouvée"));
        
        if (demande.getStatut() != StatutDemandeEnum.EN_ATTENTE) {
            throw new AuthException("Seules les demandes en attente peuvent être mises en traitement");
        }
        
        Utilisateur gestionnaire = utilisateurRepository.findById(gestionnaireId)
                .orElseThrow(() -> new AuthException("Gestionnaire non trouvé"));
        
        demande.setStatut(StatutDemandeEnum.EN_COURS_TRAITEMENT);
        demande.setGestionnaire(gestionnaire);
        
        demande = demandeRepository.save(demande);
        return DemandeResponseDto.fromEntity(demande);
    }
    
    /**
     * Met une demande en attente
     */
    public DemandeResponseDto mettreEnAttente(UUID id) {
        DemandeLogement demande = demandeRepository.findById(id)
                .orElseThrow(() -> new AuthException("Demande non trouvée"));
        
        demande.setStatut(StatutDemandeEnum.EN_ATTENTE);
        
        demande = demandeRepository.save(demande);
        return DemandeResponseDto.fromEntity(demande);
    }
    
    /**
     * Change la priorité d'une demande
     */
    public DemandeResponseDto changerPriorite(UUID id, PrioriteEnum priorite) {
        DemandeLogement demande = demandeRepository.findById(id)
                .orElseThrow(() -> new AuthException("Demande non trouvée"));
        
        demande.setPriorite(priorite);
        
        demande = demandeRepository.save(demande);
        return DemandeResponseDto.fromEntity(demande);
    }
    
    /**
     * Ajoute un commentaire à une demande
     */
    public DemandeResponseDto ajouterCommentaire(UUID id, String commentaire) {
        DemandeLogement demande = demandeRepository.findById(id)
                .orElseThrow(() -> new AuthException("Demande non trouvée"));
        
        String commentaires = demande.getCommentaires();
        if (commentaires == null || commentaires.isEmpty()) {
            demande.setCommentaires(commentaire);
        } else {
            demande.setCommentaires(commentaires + "\n---\n" + commentaire);
        }
        
        demande = demandeRepository.save(demande);
        return DemandeResponseDto.fromEntity(demande);
    }
    
    /**
     * Supprime une demande
     */
    public void supprimerDemande(UUID id) {
        DemandeLogement demande = demandeRepository.findById(id)
                .orElseThrow(() -> new AuthException("Demande non trouvée"));
        
        if (demande.getStatut() == StatutDemandeEnum.APPROUVEE && demande.getAttribution() != null) {
            throw new AuthException("Impossible de supprimer une demande avec une attribution active");
        }
        
        demandeRepository.delete(demande);
    }
    
    /**
     * Récupère les statistiques des demandes
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistiques() {
        Map<String, Object> stats = new HashMap<>();
        
        long total = demandeRepository.count();
        long enAttente = demandeRepository.countByStatut(StatutDemandeEnum.EN_ATTENTE);
        long enTraitement = demandeRepository.countByStatut(StatutDemandeEnum.EN_COURS_TRAITEMENT);
        long approuvees = demandeRepository.countByStatut(StatutDemandeEnum.APPROUVEE);
        long rejetees = demandeRepository.countByStatut(StatutDemandeEnum.REJETEE);
        long annulees = demandeRepository.countByStatut(StatutDemandeEnum.ANNULEE);
        
        stats.put("total", total);
        stats.put("enAttente", enAttente);
        stats.put("enTraitement", enTraitement);
        stats.put("approuvees", approuvees);
        stats.put("rejetees", rejetees);
        stats.put("annulees", annulees);
        
        double tauxApprobation = total > 0 ? (approuvees * 100.0) / total : 0;
        stats.put("tauxApprobation", Math.round(tauxApprobation * 100.0) / 100.0);
        
        return stats;
    }
}
