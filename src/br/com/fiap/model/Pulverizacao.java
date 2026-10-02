package br.com.fiap.model;

import br.com.fiap.dao.IntervencaoOperacionalDAO;

public class Pulverizacao extends IntervencaoOperacional {
    @Override
    public String executarServico(IntervencaoOperacional intervencao, TrechoRodovia trecho, EquipeManutencao equipe) {
        IntervencaoOperacionalDAO dao = new IntervencaoOperacionalDAO();
        dao.inserir(intervencao, trecho, equipe);
        String frase = String.format("O km %d ao km %d do trecho %s precisa de serviço manual, a equipe %s foi destinada para essa tarefa. A grama está com %.2f cm", trecho.getQuilometroInicial(), trecho.getQuilometroFinal(), trecho.getNomeTrecho(), equipe.getNomeEquipe(), trecho.getNivelVegetacaoEmCm());
        return frase;
    }
}
