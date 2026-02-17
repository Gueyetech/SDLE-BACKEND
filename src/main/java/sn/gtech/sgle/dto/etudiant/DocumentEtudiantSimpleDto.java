package sn.gtech.sgle.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.Document;
import sn.gtech.sgle.entity.enums.TypeDocumentEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * DTO pour un document (vue étudiant)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentEtudiantSimpleDto {
    
    private UUID id;
    private String nom;
    
    // Type
    private TypeDocumentEnum type;
    private String typeLibelle;
    
    // URL et téléchargement
    private String url;
    private Long tailleFichier;
    private String tailleFormatee;
    
    // Dates
    private LocalDateTime dateUpload;
    private LocalDate dateExpiration;
    
    // État
    private Boolean valide;
    private Boolean expire;
    private Long joursAvantExpiration;
    private Boolean expireBientot; // < 30 jours
    
    /**
     * Convertit une entité Document en DTO
     */
    public static DocumentEtudiantSimpleDto fromEntity(Document document) {
        if (document == null) return null;
        
        boolean expire = document.verifierExpiration();
        Long joursAvant = null;
        boolean expireBientot = false;
        
        if (document.getDateExpiration() != null && !expire) {
            joursAvant = ChronoUnit.DAYS.between(LocalDate.now(), document.getDateExpiration());
            expireBientot = joursAvant <= 30;
        }
        
        String tailleFormatee = null;
        if (document.getTailleFichier() != null) {
            long taille = document.getTailleFichier();
            if (taille < 1024) {
                tailleFormatee = taille + " octets";
            } else if (taille < 1024 * 1024) {
                tailleFormatee = String.format("%.1f Ko", taille / 1024.0);
            } else {
                tailleFormatee = String.format("%.1f Mo", taille / (1024.0 * 1024));
            }
        }
        
        String typeLib = document.getType() != null ? switch (document.getType()) {
            case CARTE_ETUDIANT -> "Carte étudiante";
            case PIECE_IDENTITE -> "Pièce d'identité";
            case CERTIFICAT_SCOLARITE -> "Certificat de scolarité";
            case ATTESTATION_BOURSE -> "Attestation de bourse";
            case CONTRAT_LOCATION -> "Contrat de location";
            case RECU_PAIEMENT -> "Reçu de paiement";
            case PHOTO_IDENTITE -> "Photo d'identité";
            case PHOTO_LOGEMENT -> "Photo logement";
            case JUSTIFICATIF_DOMICILE -> "Justificatif de domicile";
            case AUTRE -> "Autre document";
        } : null;
        
        return DocumentEtudiantSimpleDto.builder()
                .id(document.getId())
                .nom(document.getNom())
                .type(document.getType())
                .typeLibelle(typeLib)
                .url(document.getUrl())
                .tailleFichier(document.getTailleFichier())
                .tailleFormatee(tailleFormatee)
                .dateUpload(document.getDateUpload())
                .dateExpiration(document.getDateExpiration())
                .valide(document.getValide())
                .expire(expire)
                .joursAvantExpiration(joursAvant)
                .expireBientot(expireBientot)
                .build();
    }
}
