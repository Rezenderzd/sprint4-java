package br.com.motiva.servico;

import br.com.motiva.model.EquipeManutencao;
import br.com.motiva.model.TrechoRodovia;

public abstract class ServicoIntervencao {

    public abstract String tipoRocadaExigida();

    public abstract String tipoServico();

    public abstract String descreverServico(TrechoRodovia trecho, EquipeManutencao equipe);
}
