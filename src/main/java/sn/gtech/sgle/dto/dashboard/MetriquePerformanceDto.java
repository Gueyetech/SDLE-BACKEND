package sn.gtech.sgle.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les métriques de performance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetriquePerformanceDto {
    
    private String nomMetrique;
    private String categorie;
    private Double valeurActuelle;
    private Double valeurCible;
    private Double valeurPrecedente;
    private String unite;
    private Double pourcentageObjectif; // Pourcentage d'atteinte de l'objectif
    private String tendance; // HAUSSE, BAISSE, STABLE
    private Double variationPourcentage;
    private LocalDateTime dateCalcul;
    private String description;
    private String statut; // BON, MOYEN, MAUVAIS, CRITIQUE
    
    /**
     * Calcule le statut basé sur le pourcentage d'objectif
     */
    public static String calculerStatut(Double pourcentageObjectif) {
        if (pourcentageObjectif == null) return "INCONNU";
        if (pourcentageObjectif >= 90) return "BON";
        if (pourcentageObjectif >= 70) return "MOYEN";
        if (pourcentageObjectif >= 50) return "MAUVAIS";
        return "CRITIQUE";
    }
    
    /**
     * Calcule la tendance basée sur les valeurs actuelle et précédente
     */
    public static String calculerTendance(Double valeurActuelle, Double valeurPrecedente) {
        if (valeurActuelle == null || valeurPrecedente == null) return "STABLE";
        if (valeurActuelle > valeurPrecedente) return "HAUSSE";
        if (valeurActuelle < valeurPrecedente) return "BAISSE";
        return "STABLE";
    }
}
