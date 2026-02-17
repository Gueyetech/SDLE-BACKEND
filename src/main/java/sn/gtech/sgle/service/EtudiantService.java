package sn.gtech.sgle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.gtech.sgle.dto.etudiant.*;
import sn.gtech.sgle.entity.*;
import sn.gtech.sgle.entity.enums.*;
import sn.gtech.sgle.exception.AuthException;
import sn.gtech.sgle.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour les fonctionnalités étudiant
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EtudiantService {
    
    private final UtilisateurRepository utilisateurRepository;
    private final LogementRepository logementRepository;
    private final DemandeLogementRepository demandeLogementRepository;
    private final AttributionRepository attributionRepository;
    private final IncidentRepository incidentRepository;
    private final PaiementRepository paiementRepository;
    private final NotificationRepository notificationRepository;
    private final DocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;
    
    // ========== PROFIL ==========
    
    /**
     * Récupère le profil complet de l'étudiant connecté
     */
    @Transactional(readOnly = true)
    public ProfilEtudiantDto getMonProfil(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        verifierRoleEtudiant(etudiant);
        return buildProfilEtudiantDto(etudiant);
    }
    
    /**
     * Complète ou met à jour le profil de l'étudiant
     * Note: L'entité Utilisateur actuelle est simplifiée - les champs additionnels
     * nécessiteraient une extension de l'entité ou une entité Etudiant dédiée
     */
    public ProfilEtudiantDto completerProfil(String email, CompleterProfilRequest request) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        verifierRoleEtudiant(etudiant);
        
        // Dans l'implémentation actuelle, l'entité Utilisateur est minimale
        // Une version complète nécessiterait d'ajouter ces champs à l'entité
        // ou de créer une entité EtudiantProfil séparée
        
        etudiant = utilisateurRepository.save(etudiant);
        return buildProfilEtudiantDto(etudiant);
    }
    
    /**
     * Change le mot de passe de l'étudiant
     */
    public void changerMotDePasse(String email, String ancienMotDePasse, String nouveauMotDePasse) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        
        if (!passwordEncoder.matches(ancienMotDePasse, etudiant.getMotDePasse())) {
            throw new AuthException("L'ancien mot de passe est incorrect");
        }
        
        if (nouveauMotDePasse.length() < 6) {
            throw new AuthException("Le nouveau mot de passe doit contenir au moins 6 caractères");
        }
        
        etudiant.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(etudiant);
    }
    
    // ========== RECHERCHE DE LOGEMENTS ==========
    
    /**
     * Récupère le catalogue des logements disponibles
     */
    @Transactional(readOnly = true)
    public Page<LogementCatalogueDto> getCatalogueLogements(RechercheLogementRequest request) {
        Sort sort = Sort.by(Sort.Direction.ASC, "prixMensuel");
        if (request.getTriPar() != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(request.getOrdreTri()) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
            String sortField = switch (request.getTriPar().toLowerCase()) {
                case "prix" -> "prixMensuel";
                case "date" -> "dateCreation";
                case "capacite" -> "capacite";
                case "superficie" -> "superficie";
                default -> "prixMensuel";
            };
            sort = Sort.by(direction, sortField);
        }
        
        Pageable pageable = PageRequest.of(
            request.getPage() != null ? request.getPage() : 0,
            request.getTaille() != null ? request.getTaille() : 10,
            sort
        );
        
        // Recherche basique - logements disponibles uniquement
        Page<Logement> logements = logementRepository.findByStatut(
            StatutLogementEnum.DISPONIBLE, pageable
        );
        
        return logements.map(LogementCatalogueDto::fromEntity);
    }
    
    /**
     * Recherche avancée de logements
     */
    @Transactional(readOnly = true)
    public List<LogementCatalogueDto> rechercherLogements(RechercheLogementRequest request) {
        List<Logement> logements = logementRepository.findByStatut(StatutLogementEnum.DISPONIBLE);
        
        return logements.stream()
            .filter(l -> request.getTypeLogement() == null || l.getType() == request.getTypeLogement())
            .filter(l -> request.getPrixMax() == null || 
                (l.getPrixMensuel() != null && l.getPrixMensuel().compareTo(request.getPrixMax()) <= 0))
            .filter(l -> request.getPrixMin() == null || 
                (l.getPrixMensuel() != null && l.getPrixMensuel().compareTo(request.getPrixMin()) >= 0))
            .filter(l -> request.getVille() == null || 
                (l.getAdresse() != null && request.getVille().equalsIgnoreCase(l.getAdresse().getVille())))
            .filter(l -> request.getQuartier() == null || 
                (l.getAdresse() != null && request.getQuartier().equalsIgnoreCase(l.getAdresse().getQuartier())))
            .filter(l -> request.getCapaciteMin() == null || 
                (l.getCapacite() != null && l.getCapacite() >= request.getCapaciteMin()))
            .filter(l -> request.getMeuble() == null || l.getMeuble().equals(request.getMeuble()))
            .filter(l -> request.getSuperficieMin() == null || 
                (l.getSuperficie() != null && l.getSuperficie() >= request.getSuperficieMin()))
            .map(LogementCatalogueDto::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère les détails complets d'un logement
     */
    @Transactional(readOnly = true)
    public LogementDetailsDto getDetailsLogement(UUID logementId) {
        Logement logement = logementRepository.findById(logementId)
            .orElseThrow(() -> new AuthException("Logement non trouvé"));
        return LogementDetailsDto.fromEntity(logement);
    }
    
    // ========== DEMANDES DE LOGEMENT ==========
    
    /**
     * Soumet une nouvelle demande de logement
     */
    public SuiviDemandeDto soumettreDemandeLogement(String email, SoumettreDemandeRequest request) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        verifierRoleEtudiant(etudiant);
        
        // Vérifier si l'étudiant n'a pas déjà une demande en cours
        List<DemandeLogement> demandesEnCours = demandeLogementRepository
            .findByEtudiantIdAndStatut(etudiant.getId(), StatutDemandeEnum.EN_ATTENTE);
        demandesEnCours.addAll(demandeLogementRepository
            .findByEtudiantIdAndStatut(etudiant.getId(), StatutDemandeEnum.EN_TRAITEMENT));
        
        if (!demandesEnCours.isEmpty()) {
            throw new AuthException("Vous avez déjà une demande en cours de traitement");
        }
        
        // Vérifier si l'étudiant n'a pas déjà un logement actif
        boolean hasActiveAttribution = attributionRepository
            .existsByEtudiantIdAndStatut(etudiant.getId(), StatutAttributionEnum.ACTIVE);
        if (hasActiveAttribution) {
            throw new AuthException("Vous avez déjà un logement attribué");
        }
        
        String preferences = buildPreferencesString(request);
        
        DemandeLogement demande = DemandeLogement.builder()
            .etudiant(etudiant)
            .typeLogementSouhaite(request.getTypeLogementSouhaite())
            .budgetMaximum(request.getBudgetMaximum())
            .dateDebutSouhaitee(request.getDateDebutSouhaitee())
            .dureeSouhaitee(request.getDureeSouhaitee())
            .preferences(preferences)
            .commentaires(request.getBesoinsSpecifiques())
            .statut(StatutDemandeEnum.EN_ATTENTE)
            .priorite(PrioriteEnum.NORMALE)
            .build();
        
        demande = demandeLogementRepository.save(demande);
        
        // Créer une notification
        creerNotification(etudiant, TypeNotificationEnum.INFORMATION_GENERALE,
            "Demande de logement enregistrée",
            "Votre demande de logement n°" + demande.getNumeroReference() + " a été enregistrée. " +
            "Nous vous informerons de son évolution.");
        
        return SuiviDemandeDto.fromEntity(demande);
    }
    
    /**
     * Récupère toutes les demandes de l'étudiant
     */
    @Transactional(readOnly = true)
    public List<SuiviDemandeDto> getMesDemandes(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        verifierRoleEtudiant(etudiant);
        
        List<DemandeLogement> demandes = demandeLogementRepository.findByEtudiantId(etudiant.getId());
        return demandes.stream()
            .map(SuiviDemandeDto::fromEntity)
            .sorted((d1, d2) -> d2.getDateDemande().compareTo(d1.getDateDemande()))
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère le détail d'une demande
     */
    @Transactional(readOnly = true)
    public SuiviDemandeDto getMaDemande(String email, UUID demandeId) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        DemandeLogement demande = demandeLogementRepository.findById(demandeId)
            .orElseThrow(() -> new AuthException("Demande non trouvée"));
        
        if (!demande.getEtudiant().getId().equals(etudiant.getId())) {
            throw new AuthException("Vous n'êtes pas autorisé à consulter cette demande");
        }
        
        return SuiviDemandeDto.fromEntity(demande);
    }
    
    /**
     * Annule une demande en cours
     */
    public SuiviDemandeDto annulerDemande(String email, UUID demandeId) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        DemandeLogement demande = demandeLogementRepository.findById(demandeId)
            .orElseThrow(() -> new AuthException("Demande non trouvée"));
        
        if (!demande.getEtudiant().getId().equals(etudiant.getId())) {
            throw new AuthException("Vous n'êtes pas autorisé à annuler cette demande");
        }
        
        if (demande.getStatut() != StatutDemandeEnum.EN_ATTENTE && 
            demande.getStatut() != StatutDemandeEnum.EN_TRAITEMENT) {
            throw new AuthException("Cette demande ne peut plus être annulée");
        }
        
        demande.setStatut(StatutDemandeEnum.ANNULEE);
        demande.setDateTraitement(LocalDateTime.now());
        demande = demandeLogementRepository.save(demande);
        
        return SuiviDemandeDto.fromEntity(demande);
    }
    
    // ========== MON LOGEMENT ==========
    
    /**
     * Récupère le logement actuel de l'étudiant
     */
    @Transactional(readOnly = true)
    public MonLogementDto getMonLogement(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        verifierRoleEtudiant(etudiant);
        
        List<Attribution> attributions = attributionRepository
            .findByEtudiantIdAndStatut(etudiant.getId(), StatutAttributionEnum.ACTIVE);
        
        if (attributions.isEmpty()) {
            throw new AuthException("Vous n'avez pas de logement attribué actuellement");
        }
        
        return MonLogementDto.fromEntity(attributions.get(0));
    }
    
    /**
     * Récupère l'historique des logements de l'étudiant
     */
    @Transactional(readOnly = true)
    public List<MonLogementDto> getHistoriqueLogements(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        verifierRoleEtudiant(etudiant);
        
        List<Attribution> attributions = attributionRepository.findByEtudiantId(etudiant.getId());
        return attributions.stream()
            .map(MonLogementDto::fromEntity)
            .sorted((a1, a2) -> a2.getDateAttribution().compareTo(a1.getDateAttribution()))
            .collect(Collectors.toList());
    }
    
    // ========== INCIDENTS / SIGNALEMENTS ==========
    
    /**
     * Signale un incident/problème
     */
    public SuiviIncidentDto signalerIncident(String email, SignalerIncidentRequest request) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        verifierRoleEtudiant(etudiant);
        
        // Déterminer le logement concerné
        Logement logement;
        if (request.getLogementId() != null) {
            logement = logementRepository.findById(request.getLogementId())
                .orElseThrow(() -> new AuthException("Logement non trouvé"));
        } else {
            // Utiliser le logement actuellement attribué à l'étudiant
            List<Attribution> attributions = attributionRepository
                .findByEtudiantIdAndStatut(etudiant.getId(), StatutAttributionEnum.ACTIVE);
            if (attributions.isEmpty()) {
                throw new AuthException("Vous devez spécifier un logement ou avoir un logement attribué");
            }
            logement = attributions.get(0).getLogement();
        }
        
        Incident incident = Incident.builder()
            .etudiant(etudiant)
            .logement(logement)
            .type(request.getType())
            .description(request.getDescription())
            .urgence(request.getUrgence() != null ? request.getUrgence() : UrgenceEnum.MOYENNE)
            .statut(StatutIncidentEnum.OUVERT)
            .photos(request.getPhotos() != null ? new ArrayList<>(request.getPhotos()) : new ArrayList<>())
            .build();
        
        incident = incidentRepository.save(incident);
        
        // Notifier le gestionnaire du logement si présent
        if (logement.getGestionnaire() != null) {
            creerNotification(logement.getGestionnaire(), TypeNotificationEnum.INFORMATION_GENERALE,
                "Nouveau signalement",
                "Un incident a été signalé pour le logement " + logement.getCode() + 
                ": " + incident.getType().name());
        }
        
        return SuiviIncidentDto.fromEntity(incident);
    }
    
    /**
     * Récupère tous les incidents signalés par l'étudiant
     */
    @Transactional(readOnly = true)
    public List<SuiviIncidentDto> getMesIncidents(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        verifierRoleEtudiant(etudiant);
        
        List<Incident> incidents = incidentRepository.findByEtudiantId(etudiant.getId());
        return incidents.stream()
            .map(SuiviIncidentDto::fromEntity)
            .sorted((i1, i2) -> i2.getDateSignalement().compareTo(i1.getDateSignalement()))
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère le détail d'un incident
     */
    @Transactional(readOnly = true)
    public SuiviIncidentDto getMonIncident(String email, UUID incidentId) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        Incident incident = incidentRepository.findById(incidentId)
            .orElseThrow(() -> new AuthException("Incident non trouvé"));
        
        if (!incident.getEtudiant().getId().equals(etudiant.getId())) {
            throw new AuthException("Vous n'êtes pas autorisé à consulter cet incident");
        }
        
        return SuiviIncidentDto.fromEntity(incident);
    }
    
    /**
     * Ajoute des photos à un incident ouvert
     */
    public SuiviIncidentDto ajouterPhotosIncident(String email, UUID incidentId, List<String> photos) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        Incident incident = incidentRepository.findById(incidentId)
            .orElseThrow(() -> new AuthException("Incident non trouvé"));
        
        if (!incident.getEtudiant().getId().equals(etudiant.getId())) {
            throw new AuthException("Vous n'êtes pas autorisé à modifier cet incident");
        }
        
        if (incident.getStatut() != StatutIncidentEnum.OUVERT) {
            throw new AuthException("Vous ne pouvez plus modifier cet incident");
        }
        
        incident.getPhotos().addAll(photos);
        incident = incidentRepository.save(incident);
        
        return SuiviIncidentDto.fromEntity(incident);
    }
    
    // ========== PAIEMENTS ==========
    
    /**
     * Récupère le récapitulatif des paiements
     */
    @Transactional(readOnly = true)
    public RecapitulatifPaiementsDto getRecapitulatifPaiements(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        verifierRoleEtudiant(etudiant);
        
        List<Attribution> attributions = attributionRepository.findByEtudiantId(etudiant.getId());
        
        List<Paiement> tousPaiements = new ArrayList<>();
        for (Attribution attr : attributions) {
            tousPaiements.addAll(paiementRepository.findByAttributionId(attr.getId()));
        }
        
        BigDecimal totalLoyer = BigDecimal.ZERO;
        BigDecimal totalPaye = BigDecimal.ZERO;
        BigDecimal totalAPayer = BigDecimal.ZERO;
        BigDecimal totalEnRetard = BigDecimal.ZERO;
        int nbTotal = tousPaiements.size();
        int nbPaye = 0, nbEnAttente = 0, nbEnRetard = 0;
        LocalDate prochainEcheance = null;
        BigDecimal montantProchain = null;
        
        List<PaiementEtudiantDto> derniers = new ArrayList<>();
        List<PaiementEtudiantDto> aPayer = new ArrayList<>();
        List<PaiementEtudiantDto> enRetard = new ArrayList<>();
        
        for (Paiement p : tousPaiements) {
            totalLoyer = totalLoyer.add(p.getMontant());
            
            if (p.getStatut() == StatutPaiementEnum.PAYE) {
                totalPaye = totalPaye.add(p.getMontant());
                nbPaye++;
            } else if (p.getStatut() == StatutPaiementEnum.EN_ATTENTE) {
                totalAPayer = totalAPayer.add(p.getMontant());
                nbEnAttente++;
                
                boolean retard = p.getDateEcheance() != null && p.getDateEcheance().isBefore(LocalDate.now());
                if (retard) {
                    totalEnRetard = totalEnRetard.add(p.getMontant());
                    nbEnRetard++;
                    enRetard.add(PaiementEtudiantDto.fromEntity(p));
                } else {
                    aPayer.add(PaiementEtudiantDto.fromEntity(p));
                }
                
                if (p.getDateEcheance() != null && (prochainEcheance == null || p.getDateEcheance().isBefore(prochainEcheance))) {
                    prochainEcheance = p.getDateEcheance();
                    montantProchain = p.getMontant();
                }
            }
        }
        
        // Derniers paiements effectués
        derniers = tousPaiements.stream()
            .filter(p -> p.getStatut() == StatutPaiementEnum.PAYE)
            .sorted((p1, p2) -> p2.getDatePaiement().compareTo(p1.getDatePaiement()))
            .limit(5)
            .map(PaiementEtudiantDto::fromEntity)
            .collect(Collectors.toList());
        
        Long joursAvant = prochainEcheance != null 
            ? ChronoUnit.DAYS.between(LocalDate.now(), prochainEcheance) : null;
        
        Double taux = nbTotal > 0 ? (double) nbPaye / nbTotal * 100 : 0.0;
        
        return RecapitulatifPaiementsDto.builder()
            .totalLoyer(totalLoyer)
            .totalPaye(totalPaye)
            .totalAPayer(totalAPayer)
            .totalEnRetard(totalEnRetard)
            .totalPayeFormate(totalPaye.toPlainString() + " FCFA")
            .totalAPayerFormate(totalAPayer.toPlainString() + " FCFA")
            .nombrePaiementsTotal(nbTotal)
            .nombrePaiementsEffectues(nbPaye)
            .nombrePaiementsEnAttente(nbEnAttente)
            .nombrePaiementsEnRetard(nbEnRetard)
            .prochainEcheance(prochainEcheance)
            .montantProchainPaiement(montantProchain)
            .joursAvantProchainPaiement(joursAvant)
            .prochainPaiementUrgent(joursAvant != null && joursAvant <= 7)
            .tauxPaiement(taux)
            .dernierspaiements(derniers)
            .paiementsAPayer(aPayer)
            .paiementsEnRetard(enRetard)
            .build();
    }
    
    /**
     * Récupère l'historique complet des paiements
     */
    @Transactional(readOnly = true)
    public List<PaiementEtudiantDto> getHistoriquePaiements(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        verifierRoleEtudiant(etudiant);
        
        List<Attribution> attributions = attributionRepository.findByEtudiantId(etudiant.getId());
        List<Paiement> tousPaiements = new ArrayList<>();
        for (Attribution attr : attributions) {
            tousPaiements.addAll(paiementRepository.findByAttributionId(attr.getId()));
        }
        
        return tousPaiements.stream()
            .map(PaiementEtudiantDto::fromEntity)
            .sorted((p1, p2) -> {
                if (p1.getDateEcheance() == null) return 1;
                if (p2.getDateEcheance() == null) return -1;
                return p2.getDateEcheance().compareTo(p1.getDateEcheance());
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère les paiements en attente
     */
    @Transactional(readOnly = true)
    public List<PaiementEtudiantDto> getPaiementsEnAttente(String email) {
        return getHistoriquePaiements(email).stream()
            .filter(p -> p.getStatut() == StatutPaiementEnum.EN_ATTENTE)
            .collect(Collectors.toList());
    }
    
    // ========== NOTIFICATIONS ==========
    
    /**
     * Récupère toutes les notifications de l'étudiant
     */
    @Transactional(readOnly = true)
    public List<NotificationEtudiantDto> getMesNotifications(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        
        List<Notification> notifications = notificationRepository
            .findByDestinataireIdOrderByDateEnvoiDesc(etudiant.getId());
        
        return notifications.stream()
            .map(NotificationEtudiantDto::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère les notifications non lues
     */
    @Transactional(readOnly = true)
    public List<NotificationEtudiantDto> getMesNotificationsNonLues(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        
        List<Notification> notifications = notificationRepository
            .findByDestinataireIdAndLue(etudiant.getId(), false);
        
        return notifications.stream()
            .map(NotificationEtudiantDto::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Compte les notifications non lues
     */
    @Transactional(readOnly = true)
    public Long compterNotificationsNonLues(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        return notificationRepository.countByDestinataireIdAndLue(etudiant.getId(), false);
    }
    
    /**
     * Marque une notification comme lue
     */
    public void marquerNotificationLue(String email, UUID notificationId) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new AuthException("Notification non trouvée"));
        
        if (!notification.getDestinataire().getId().equals(etudiant.getId())) {
            throw new AuthException("Vous n'êtes pas autorisé à modifier cette notification");
        }
        
        notification.marquerCommeLue();
        notificationRepository.save(notification);
    }
    
    /**
     * Marque toutes les notifications comme lues
     */
    public void marquerToutesNotificationsLues(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        List<Notification> notifications = notificationRepository
            .findByDestinataireIdAndLue(etudiant.getId(), false);
        
        for (Notification n : notifications) {
            n.marquerCommeLue();
        }
        notificationRepository.saveAll(notifications);
    }
    
    // ========== DOCUMENTS ==========
    
    /**
     * Récupère tous les documents de l'étudiant
     */
    @Transactional(readOnly = true)
    public List<DocumentEtudiantSimpleDto> getMesDocuments(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        
        List<Document> documents = documentRepository.findByProprietaireId(etudiant.getId());
        return documents.stream()
            .map(DocumentEtudiantSimpleDto::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Récupère les documents par type
     */
    @Transactional(readOnly = true)
    public List<DocumentEtudiantSimpleDto> getMesDocumentsParType(String email, TypeDocumentEnum type) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        
        List<Document> documents = documentRepository.findByProprietaireIdAndType(etudiant.getId(), type);
        return documents.stream()
            .map(DocumentEtudiantSimpleDto::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Enregistre un nouveau document (les métadonnées - l'upload réel se fait séparément)
     */
    public DocumentEtudiantSimpleDto enregistrerDocument(String email, UploadDocumentRequest request, String url) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        verifierRoleEtudiant(etudiant);
        
        Document document = Document.builder()
            .nom(request.getNom() != null ? request.getNom() : request.getType().name() + "_" + System.currentTimeMillis())
            .type(request.getType())
            .url(url)
            .dateExpiration(request.getDateExpiration())
            .proprietaire(etudiant)
            .valide(false) // En attente de validation
            .build();
        
        document = documentRepository.save(document);
        return DocumentEtudiantSimpleDto.fromEntity(document);
    }
    
    /**
     * Supprime un document
     */
    public void supprimerDocument(String email, UUID documentId) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new AuthException("Document non trouvé"));
        
        if (!document.getProprietaire().getId().equals(etudiant.getId())) {
            throw new AuthException("Vous n'êtes pas autorisé à supprimer ce document");
        }
        
        documentRepository.delete(document);
    }
    
    /**
     * Récupère le contrat de location actuel
     */
    @Transactional(readOnly = true)
    public DocumentEtudiantSimpleDto getMonContrat(String email) {
        Utilisateur etudiant = getUtilisateurByEmail(email);
        
        List<Attribution> attributions = attributionRepository
            .findByEtudiantIdAndStatut(etudiant.getId(), StatutAttributionEnum.ACTIVE);
        
        if (attributions.isEmpty()) {
            throw new AuthException("Vous n'avez pas de logement attribué");
        }
        
        Document contrat = attributions.get(0).getContrat();
        if (contrat == null) {
            throw new AuthException("Aucun contrat disponible");
        }
        
        return DocumentEtudiantSimpleDto.fromEntity(contrat);
    }
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    private Utilisateur getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new AuthException("Utilisateur non trouvé"));
    }
    
    private void verifierRoleEtudiant(Utilisateur utilisateur) {
        if (utilisateur.getRole() != RoleEnum.ETUDIANT) {
            throw new AuthException("Cette fonctionnalité est réservée aux étudiants");
        }
    }
    
    private void creerNotification(Utilisateur destinataire, TypeNotificationEnum type, 
                                   String titre, String message) {
        Notification notification = Notification.builder()
            .destinataire(destinataire)
            .type(type)
            .titre(titre)
            .message(message)
            .canal(CanalEnum.APPLICATION)
            .build();
        notificationRepository.save(notification);
    }
    
    private String buildPreferencesString(SoumettreDemandeRequest request) {
        StringBuilder sb = new StringBuilder();
        if (request.getVillePreferee() != null) {
            sb.append("Ville: ").append(request.getVillePreferee()).append("; ");
        }
        if (request.getQuartierPrefere() != null) {
            sb.append("Quartier: ").append(request.getQuartierPrefere()).append("; ");
        }
        if (request.getPrefereMeuble() != null) {
            sb.append("Meublé: ").append(request.getPrefereMeuble() ? "Oui" : "Non").append("; ");
        }
        if (request.getCapaciteMinimale() != null) {
            sb.append("Capacité min: ").append(request.getCapaciteMinimale()).append("; ");
        }
        if (request.getEquipementsSouhaites() != null && !request.getEquipementsSouhaites().isEmpty()) {
            sb.append("Équipements: ").append(String.join(", ", request.getEquipementsSouhaites())).append("; ");
        }
        return sb.toString();
    }
    
    private ProfilEtudiantDto buildProfilEtudiantDto(Utilisateur etudiant) {
        List<Document> documents = documentRepository.findByProprietaireId(etudiant.getId());
        int docsValides = (int) documents.stream().filter(d -> d.getValide()).count();
        int docsPending = documents.size() - docsValides;
        
        // Vérifier si l'étudiant a un logement
        List<Attribution> attributions = attributionRepository
            .findByEtudiantIdAndStatut(etudiant.getId(), StatutAttributionEnum.ACTIVE);
        boolean aLogement = !attributions.isEmpty();
        String logementCode = null;
        String logementAdresse = null;
        if (aLogement && attributions.get(0).getLogement() != null) {
            var logement = attributions.get(0).getLogement();
            logementCode = logement.getCode();
            if (logement.getAdresse() != null) {
                logementAdresse = logement.getAdresse().getVille();
            }
        }
        
        return ProfilEtudiantDto.builder()
            .id(etudiant.getId())
            .email(etudiant.getEmail())
            .actif(etudiant.getActif())
            .dateCreation(etudiant.getDateCreation())
            .derniereConnexion(etudiant.getDerniereConnexion())
            .nombreDocuments(documents.size())
            .documentsValides(docsValides)
            .documentsPending(docsPending)
            .aLogementActuel(aLogement)
            .logementCode(logementCode)
            .logementAdresse(logementAdresse)
            .build();
    }
}
