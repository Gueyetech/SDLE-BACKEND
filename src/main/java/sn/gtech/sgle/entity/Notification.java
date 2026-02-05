package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.CanalEnum;
import sn.gtech.sgle.entity.enums.TypeNotificationEnum;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeNotificationEnum type;
    
    @Column(nullable = false)
    private String titre;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @Builder.Default
    private LocalDateTime dateEnvoi = LocalDateTime.now();
    
    @Builder.Default
    private Boolean lue = false;
    
    private LocalDateTime dateLecture;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CanalEnum canal = CanalEnum.APPLICATION;
    
    @PrePersist
    public void prePersist() {
        this.dateEnvoi = LocalDateTime.now();
    }
    
    public Boolean envoyer() {
        // Logique d'envoi de notification selon le canal
        switch (this.canal) {
            case EMAIL:
                // Envoyer par email
                break;
            case SMS:
                // Envoyer par SMS
                break;
            case PUSH_NOTIFICATION:
                // Envoyer en push notification
                break;
            case APPLICATION:
            default:
                // Notification dans l'application
                break;
        }
        return true;
    }
    
    public void marquerCommeLue() {
        this.lue = true;
        this.dateLecture = LocalDateTime.now();
    }
}
