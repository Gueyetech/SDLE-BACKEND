package sn.gtech.sgle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "administrateurs")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Administrateur extends Utilisateur {
    
    @OneToMany(mappedBy = "generePar", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Rapport> rapportsGeneres = new ArrayList<>();
    
    public void gererUtilisateurs() {
        // Logique de gestion des utilisateurs
    }
    
    public Rapport genererRapports() {
        // Logique de génération de rapports
        return null;
    }
    
    public void configurerSysteme() {
        // Logique de configuration système
    }
    
    public List<String> consulterLogs() {
        // Logique de consultation des logs
        return new ArrayList<>();
    }
    
    public byte[] exporterDonnees() {
        // Logique d'export des données
        return new byte[0];
    }
}
