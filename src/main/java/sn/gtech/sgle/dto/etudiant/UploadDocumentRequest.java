package sn.gtech.sgle.dto.etudiant;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.TypeDocumentEnum;

import java.time.LocalDate;

/**
 * DTO pour téléverser un document
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadDocumentRequest {
    
    @NotNull(message = "Le type de document est obligatoire")
    private TypeDocumentEnum type;
    
    private String nom;
    
    // Date d'expiration (pour les documents qui en ont une)
    private LocalDate dateExpiration;
    
    // Commentaire optionnel
    private String commentaire;
}
