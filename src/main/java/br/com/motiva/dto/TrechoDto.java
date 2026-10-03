package br.com.motiva.dto;

import br.com.motiva.model.NivelPrioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TrechoDto {

    private TrechoDto() {
    }

    public record Request(
            @NotBlank(message = "nomeTrecho é obrigatório") String nomeTrecho,
            @NotNull(message = "quilometroInicial é obrigatório") Integer quilometroInicial,
            @NotNull(message = "quilometroFinal é obrigatório") Integer quilometroFinal,
            @NotNull(message = "nivelVegetacaoEmCm é obrigatório") Double nivelVegetacaoEmCm,
            @NotBlank(message = "tipoClima é obrigatório (umido ou seco)") String tipoClima,
            Boolean comSensor) {
    }

    public record Response(
            Long id,
            String nomeTrecho,
            Integer quilometroInicial,
            Integer quilometroFinal,
            Double nivelVegetacaoEmCm,
            String tipoClima,
            Boolean comSensor,
            NivelPrioridade prioridade) {
    }
}
