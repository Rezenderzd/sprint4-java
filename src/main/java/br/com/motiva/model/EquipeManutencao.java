package br.com.motiva.model;

import jakarta.persistence.*;

@Entity
@Table(name = "equipesManutencao")
public class EquipeManutencao {

    @Id
    @SequenceGenerator(name = "seqEquipes", sequenceName = "seq_equipes", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqEquipes")
    private Long id;

    @Column(name = "nomeEquipe")
    private String nomeEquipe;

    @Column(name = "quantidadeFuncionarios")
    private Integer numeroFuncionarios;

    @Column(name = "rocadaDeAtuacao")
    private String tipoDeRocadaDeAtuacao;

    public EquipeManutencao() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeEquipe() { return nomeEquipe; }
    public void setNomeEquipe(String nomeEquipe) { this.nomeEquipe = nomeEquipe; }
    public Integer getNumeroFuncionarios() { return numeroFuncionarios; }
    public void setNumeroFuncionarios(Integer numeroFuncionarios) { this.numeroFuncionarios = numeroFuncionarios; }
    public String getTipoDeRocadaDeAtuacao() { return tipoDeRocadaDeAtuacao; }
    public void setTipoDeRocadaDeAtuacao(String tipoDeRocadaDeAtuacao) { this.tipoDeRocadaDeAtuacao = tipoDeRocadaDeAtuacao; }
}
