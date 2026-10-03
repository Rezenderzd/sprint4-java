package br.com.motiva.servico;

import br.com.motiva.model.EquipeManutencao;
import br.com.motiva.model.TrechoRodovia;

public class Pulverizacao extends ServicoIntervencao {

    @Override
    public String tipoRocadaExigida() {
        return "manual";
    }

    @Override
    public String tipoServico() {
        return "PULVERIZACAO";
    }

    @Override
    public String descreverServico(TrechoRodovia trecho, EquipeManutencao equipe) {
        return String.format("O km %d ao km %d do trecho %s precisa de serviço manual, "
                        + "a equipe %s foi destinada para essa tarefa. A grama está com %.2f cm",
                trecho.getQuilometroInicial(), trecho.getQuilometroFinal(), trecho.getNomeTrecho(),
                equipe.getNomeEquipe(), trecho.getNivelVegetacaoEmCm());
    }
}
