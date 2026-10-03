package br.com.motiva.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public class IntervencaoDto {

    private IntervencaoDto() {
    }

    public record Request(
            @NotNull(message = "trechoId é obrigatório")
            @Positive(message = "trechoId deve ser maior que zero") Long trechoId,
            @NotNull(message = "equipeId é obrigatório")
            @Positive(message = "equipeId deve ser maior que zero") Long equipeId) {
    }

    public record Response(
            Long id,
            Long trechoId,
            Long equipeId,
            String nome,
            Integer quilometroInicial,
            Integer quilometroFinal,
            String tipoClima,
            String nomeEquipe,
            String tipoServico,
            Double nivelVegetacaoAntesCm,
            LocalDateTime dataGeracao) {
    }

    public record GeracaoResultado(
            List<Response> intervencoes,
            List<String> mensagens,
            List<String> avisos) {
    }
}
