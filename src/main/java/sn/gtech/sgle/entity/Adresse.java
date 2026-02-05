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
public class Adresse {
    
    private String rue;
    private String numero;
    private String codePostal;
    private String ville;
    private String quartier;
    private String region;
    private String pays;
    private Float latitude;
    private Float longitude;
    private String complementAdresse;
    
    public String formaterAdresse() {
        StringBuilder sb = new StringBuilder();
        if (numero != null) sb.append(numero).append(" ");
        if (rue != null) sb.append(rue).append(", ");
        if (quartier != null) sb.append(quartier).append(", ");
        if (codePostal != null) sb.append(codePostal).append(" ");
        if (ville != null) sb.append(ville).append(", ");
        if (region != null) sb.append(region).append(", ");
        if (pays != null) sb.append(pays);
        return sb.toString().trim();
    }
    
    public double calculerDistance(Adresse autre) {
        if (this.latitude == null || this.longitude == null || 
            autre.getLatitude() == null || autre.getLongitude() == null) {
            return -1;
        }
        
        final int R = 6371; // Rayon de la Terre en km
        double latDistance = Math.toRadians(autre.getLatitude() - this.latitude);
        double lonDistance = Math.toRadians(autre.getLongitude() - this.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(autre.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
