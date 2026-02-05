package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.CategorieEquipementEnum;

import java.util.UUID;

@Entity
@Table(name = "equipements")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Equipement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String nom;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private CategorieEquipementEnum categorie;
    
    @Builder.Default
    private Boolean fonctionnel = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logement_id")
    private Logement logement;
    
    public Boolean verifierEtat() {
        return this.fonctionnel;
    }
}
