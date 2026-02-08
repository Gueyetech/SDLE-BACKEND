package sn.gtech.sgle.dto.demande;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.PrioriteEnum;
import sn.gtech.sgle.entity.enums.StatutDemandeEnum;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TraiterDemandeRequest {
    
    private StatutDemandeEnum statut;
    private String motifRejet;
    private String commentaires;
    private PrioriteEnum priorite;
}
