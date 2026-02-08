package sn.gtech.sgle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.gtech.sgle.dto.incident.CreerIncidentRequest;
import sn.gtech.sgle.dto.incident.IncidentResponseDto;
import sn.gtech.sgle.entity.Incident;
import sn.gtech.sgle.entity.Logement;
import sn.gtech.sgle.entity.Utilisateur;
import sn.gtech.sgle.entity.enums.StatutIncidentEnum;
import sn.gtech.sgle.entity.enums.TypeIncidentEnum;
import sn.gtech.sgle.entity.enums.UrgenceEnum;
import sn.gtech.sgle.exception.AuthException;
import sn.gtech.sgle.repository.IncidentRepository;
import sn.gtech.sgle.repository.LogementRepository;
import sn.gtech.sgle.repository.UtilisateurRepository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des incidents
 */
@Service
@RequiredArgsConstructor
@Transactional
public class IncidentService {
    
    private final IncidentRepository incidentRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final LogementRepository logementRepository;
    
    /**
     * Crée un nouvel incident
     */
    public IncidentResponseDto creerIncident(CreerIncidentRequest request) {
        Utilisateur etudiant = utilisateurRepository.findById(request.getEtudiantId())
                .orElseThrow(() -> new AuthException("Étudiant non trouvé"));
        
        Logement logement = logementRepository.findById(request.getLogementId())
                .orElseThrow(() -> new AuthException("Logement non trouvé"));
        
        Incident incident = Incident.builder()
                .numeroTicket(genererNumeroTicket())
                .etudiant(etudiant)
                .logement(logement)
                .type(request.getType())
                .description(request.getDescription())
                .urgence(request.getUrgence() != null ? request.getUrgence() : UrgenceEnum.FAIBLE)
                .statut(StatutIncidentEnum.OUVERT)
                .dateSignalement(LocalDateTime.now())
                .photos(request.getPhotos())
                .build();
        
        incident = incidentRepository.save(incident);
        return IncidentResponseDto.fromEntity(incident);
    }
    
    /**
     * Génère un numéro de ticket unique
     */
    private String genererNumeroTicket() {
        int annee = Year.now().getValue();
        long count = incidentRepository.count() + 1;
        return "INC-" + annee + "-" + String.format("%05d", count);
    }
    
    /**
     * Récupère tous les incidents
     */
    @Transactional(readOnly = true)
    public List<IncidentResponseDto> getTousIncidents() {
        return incidentRepository.findAll().stream()
                .map(IncidentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère un incident par son ID
     */
    @Transactional(readOnly = true)
    public IncidentResponseDto getIncidentParId(UUID id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new AuthException("Incident non trouvé"));
        return IncidentResponseDto.fromEntity(incident);
    }
    
    /**
     * Récupère un incident par son numéro de ticket
     */
    @Transactional(readOnly = true)
    public IncidentResponseDto getIncidentParTicket(String numeroTicket) {
        Incident incident = incidentRepository.findByNumeroTicket(numeroTicket)
                .orElseThrow(() -> new AuthException("Incident non trouvé"));
        return IncidentResponseDto.fromEntity(incident);
    }
    
    /**
     * Récupère les incidents par statut
     */
    @Transactional(readOnly = true)
    public List<IncidentResponseDto> getIncidentsParStatut(StatutIncidentEnum statut) {
        return incidentRepository.findByStatut(statut).stream()
                .map(IncidentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les incidents par type
     */
    @Transactional(readOnly = true)
    public List<IncidentResponseDto> getIncidentsParType(TypeIncidentEnum type) {
        return incidentRepository.findByType(type).stream()
                .map(IncidentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les incidents par urgence
     */
    @Transactional(readOnly = true)
    public List<IncidentResponseDto> getIncidentsParUrgence(UrgenceEnum urgence) {
        return incidentRepository.findByUrgence(urgence).stream()
                .map(IncidentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les incidents d'un étudiant
     */
    @Transactional(readOnly = true)
    public List<IncidentResponseDto> getIncidentsParEtudiant(UUID etudiantId) {
        return incidentRepository.findByEtudiantId(etudiantId).stream()
                .map(IncidentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les incidents d'un logement
     */
    @Transactional(readOnly = true)
    public List<IncidentResponseDto> getIncidentsParLogement(UUID logementId) {
        return incidentRepository.findByLogementId(logementId).stream()
                .map(IncidentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupère les incidents gérés par un gestionnaire
     */
    @Transactional(readOnly = true)
    public List<IncidentResponseDto> getIncidentsParGestionnaire(UUID gestionnaireId) {
        return incidentRepository.findByGestionnaireId(gestionnaireId).stream()
                .map(IncidentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Assigne un gestionnaire à un incident
     */
    public IncidentResponseDto assignerGestionnaire(UUID incidentId, UUID gestionnaireId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new AuthException("Incident non trouvé"));
        
        Utilisateur gestionnaire = utilisateurRepository.findById(gestionnaireId)
                .orElseThrow(() -> new AuthException("Gestionnaire non trouvé"));
        
        incident.setGestionnaire(gestionnaire);
        if (incident.getStatut() == StatutIncidentEnum.OUVERT) {
            incident.setStatut(StatutIncidentEnum.EN_COURS);
        }
        
        incident = incidentRepository.save(incident);
        return IncidentResponseDto.fromEntity(incident);
    }
    
    /**
     * Assigne un technicien à un incident
     */
    public IncidentResponseDto assignerTechnicien(UUID incidentId, UUID technicienId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new AuthException("Incident non trouvé"));
        
        Utilisateur technicien = utilisateurRepository.findById(technicienId)
                .orElseThrow(() -> new AuthException("Technicien non trouvé"));
        
        // Le champ technicien est un String (nom du technicien)
        incident.setTechnicien(technicien.getEmail());
        if (incident.getStatut() == StatutIncidentEnum.OUVERT) {
            incident.setStatut(StatutIncidentEnum.EN_COURS);
        }
        
        incident = incidentRepository.save(incident);
        return IncidentResponseDto.fromEntity(incident);
    }
    
    /**
     * Marque un incident comme en cours
     */
    public IncidentResponseDto mettreEnCours(UUID incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new AuthException("Incident non trouvé"));
        
        if (incident.getStatut() != StatutIncidentEnum.OUVERT) {
            throw new AuthException("L'incident doit être ouvert pour être mis en cours");
        }
        
        incident.setStatut(StatutIncidentEnum.EN_COURS);
        
        incident = incidentRepository.save(incident);
        return IncidentResponseDto.fromEntity(incident);
    }
    
    /**
     * Marque un incident comme résolu
     */
    public IncidentResponseDto resoudreIncident(UUID incidentId, String commentaireResolution) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new AuthException("Incident non trouvé"));
        
        if (incident.getStatut() == StatutIncidentEnum.RESOLU || 
            incident.getStatut() == StatutIncidentEnum.FERME) {
            throw new AuthException("L'incident est déjà résolu ou fermé");
        }
        
        incident.setStatut(StatutIncidentEnum.RESOLU);
        incident.setCommentaireResolution(commentaireResolution);
        incident.setDateResolution(LocalDateTime.now());
        
        incident = incidentRepository.save(incident);
        return IncidentResponseDto.fromEntity(incident);
    }
    
    /**
     * Ferme un incident
     */
    public IncidentResponseDto fermerIncident(UUID incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new AuthException("Incident non trouvé"));
        
        incident.setStatut(StatutIncidentEnum.FERME);
        if (incident.getDateResolution() == null) {
            incident.setDateResolution(LocalDateTime.now());
        }
        
        incident = incidentRepository.save(incident);
        return IncidentResponseDto.fromEntity(incident);
    }
    
    /**
     * Change l'urgence d'un incident
     */
    public IncidentResponseDto changerUrgence(UUID incidentId, UrgenceEnum urgence) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new AuthException("Incident non trouvé"));
        
        incident.setUrgence(urgence);
        
        incident = incidentRepository.save(incident);
        return IncidentResponseDto.fromEntity(incident);
    }
    
    /**
     * Ajoute un commentaire de résolution
     */
    public IncidentResponseDto ajouterCommentaire(UUID incidentId, String commentaire) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new AuthException("Incident non trouvé"));
        
        String commentaires = incident.getCommentaireResolution();
        if (commentaires == null || commentaires.isEmpty()) {
            incident.setCommentaireResolution(commentaire);
        } else {
            incident.setCommentaireResolution(commentaires + "\n---\n" + commentaire);
        }
        
        incident = incidentRepository.save(incident);
        return IncidentResponseDto.fromEntity(incident);
    }
    
    /**
     * Supprime un incident
     */
    public void supprimerIncident(UUID incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new AuthException("Incident non trouvé"));
        
        if (incident.getStatut() == StatutIncidentEnum.EN_COURS) {
            throw new AuthException("Impossible de supprimer un incident en cours de traitement");
        }
        
        incidentRepository.delete(incident);
    }
    
    /**
     * Récupère les statistiques des incidents
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistiques() {
        Map<String, Object> stats = new HashMap<>();
        
        long total = incidentRepository.count();
        long ouverts = incidentRepository.countByStatut(StatutIncidentEnum.OUVERT);
        long enCours = incidentRepository.countByStatut(StatutIncidentEnum.EN_COURS);
        long resolus = incidentRepository.countByStatut(StatutIncidentEnum.RESOLU);
        long fermes = incidentRepository.countByStatut(StatutIncidentEnum.FERME);
        
        stats.put("total", total);
        stats.put("ouverts", ouverts);
        stats.put("enCours", enCours);
        stats.put("resolus", resolus);
        stats.put("fermes", fermes);
        
        double tauxResolution = total > 0 ? ((resolus + fermes) * 100.0) / total : 0;
        stats.put("tauxResolution", Math.round(tauxResolution * 100.0) / 100.0);
        
        return stats;
    }
    
    /**
     * Récupère les incidents urgents (CRITIQUE et HAUTE)
     */
    @Transactional(readOnly = true)
    public List<IncidentResponseDto> getIncidentsUrgents() {
        List<Incident> critiques = incidentRepository.findByUrgence(UrgenceEnum.CRITIQUE);
        List<Incident> hauts = incidentRepository.findByUrgence(UrgenceEnum.HAUTE);
        
        critiques.addAll(hauts);
        
        return critiques.stream()
                .filter(i -> i.getStatut() != StatutIncidentEnum.RESOLU && i.getStatut() != StatutIncidentEnum.FERME)
                .map(IncidentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
