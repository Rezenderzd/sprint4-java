package br.com.motiva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class EquipeDto {

    private EquipeDto() {
    }

    public record Request(
            @NotBlank(message = "nomeEquipe é obrigatório") String nomeEquipe,
            @NotNull(message = "numeroFuncionarios é obrigatório")
            @Positive(message = "numeroFuncionarios deve ser maior que zero") Integer numeroFuncionarios,
            @NotBlank(message = "tipoDeRocadaDeAtuacao é obrigatório (manual ou mecanizada)") String tipoDeRocadaDeAtuacao) {
    }

    public record Response(
            Long id,
            String nomeEquipe,
            Integer numeroFuncionarios,
            String tipoDeRocadaDeAtuacao) {
    }
}
