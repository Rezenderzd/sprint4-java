package br.com.fiap.model;

public abstract class IntervencaoOperacional {
    protected Long id;

    public void setId(Long id) {
        this.id = id;
    }
    public abstract String executarServico(IntervencaoOperacional intervencao,TrechoRodovia trecho, EquipeManutencao equipe);
}
