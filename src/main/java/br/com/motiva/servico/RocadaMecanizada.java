package br.com.motiva.servico;

import br.com.motiva.model.EquipeManutencao;
import br.com.motiva.model.TrechoRodovia;

public class RocadaMecanizada extends ServicoIntervencao {

    @Override
    public String tipoRocadaExigida() {
        return "mecanizada";
    }

    @Override
    public String tipoServico() {
        return "ROCADA_MECANIZADA";
    }

    @Override
    public String descreverServico(TrechoRodovia trecho, EquipeManutencao equipe) {
        return String.format("O km %d ao km %d do trecho %s precisa de serviço de tratores, "
                        + "a equipe %s foi destinada para essa tarefa. A grama está com %.2f cm",
                trecho.getQuilometroInicial(), trecho.getQuilometroFinal(), trecho.getNomeTrecho(),
                equipe.getNomeEquipe(), trecho.getNivelVegetacaoEmCm());
    }
}
