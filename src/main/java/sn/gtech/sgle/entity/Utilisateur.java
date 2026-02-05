package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import sn.gtech.sgle.entity.enums.RoleEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Utilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String motDePasse;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEnum role;
    
    @Builder.Default
    private Boolean actif = true;
    
    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    private LocalDateTime derniereConnexion;
    
    @OneToMany(mappedBy = "proprietaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();
    
    @OneToMany(mappedBy = "destinataire", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();
    
    public Boolean seConnecter() {
        this.derniereConnexion = LocalDateTime.now();
        return this.actif;
    }
    
    public void seDeconnecter() {
        // Logique de déconnexion
    }
    
    public Boolean changerMotDePasse(String nouveauMotDePasse) {
        this.motDePasse = nouveauMotDePasse;
        return true;
    }
    
    public Boolean verifierPermission(String permission) {
        // Logique de vérification des permissions basée sur le rôle
        return true;
    }
}
