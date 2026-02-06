package sn.gtech.sgle.dto.rapport;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.gtech.sgle.entity.enums.TypeRapportEnum;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenererRapportRequest {

    @NotNull(message = "Le type de rapport est obligatoire")
    private TypeRapportEnum type;

    private String titre;

    private LocalDate periodeDebut;

    private LocalDate periodeFin;

    private Map<String, Object> filtres;

    private FormatExport format;

    private Boolean inclureGraphiques;

    private Boolean inclureDetails;

    public enum FormatExport {
        PDF,
        EXCEL,
        CSV,
        JSON
    }
}
