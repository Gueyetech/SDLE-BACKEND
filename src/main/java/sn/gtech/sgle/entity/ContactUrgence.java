package sn.gtech.sgle.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactUrgence {
    
    private String nomContact;
    private String prenomContact;
    private String relation;
    private String telephoneContact;
    private String emailContact;
    private String adresseContact;
    
    public void contacter() {
        // Logique pour contacter la personne d'urgence
        // Peut être implémentée avec un service de notification
    }
}
