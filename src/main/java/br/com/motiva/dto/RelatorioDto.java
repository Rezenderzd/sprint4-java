package br.com.motiva.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RelatorioDto {

    private RelatorioDto() {
    }

    public record RankingEquipeItem(String nomeEquipe, Long totalIntervencoes) {
    }

    public record RankingTrechoItem(String nome, Integer quilometroInicial, Integer quilometroFinal,
                                    Long totalIntervencoes) {
    }

    public record Response(
            Long id,
            LocalDateTime dataGeracao,
            Integer totalEquipes,
            Integer totalTrechos,
            Integer trechosComSensor,
            Integer trechosSemSensor,
            Integer trechosUrgente,
            Integer trechosCritico,
            Integer trechosAtencao,
            Integer trechosNormal,
            List<RankingEquipeItem> rankingEquipes,
            List<RankingTrechoItem> rankingTrechos) {
    }
}
