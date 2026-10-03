package br.com.motiva.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "intervencoesOperacionais")
public class IntervencaoOperacional {

    @Id
    @SequenceGenerator(name = "seqIntervencoes", sequenceName = "seq_intervencoes", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqIntervencoes")
    private Long id;

    @Column(name = "trechoId")
    private Long trechoId;

    @Column(name = "equipeId")
    private Long equipeId;

    @Column(name = "nome")
    private String nome;

    @Column(name = "quilometroInicial")
    private Integer quilometroInicial;

    @Column(name = "quilometroFinal")
    private Integer quilometroFinal;

    @Column(name = "tipoClima")
    private String tipoClima;

    @Column(name = "nomeEquipe")
    private String nomeEquipe;

    @Column(name = "nivelVegetacaoAntesCm")
    private Double nivelVegetacaoAntesCm;

    @Column(name = "dataGeracao")
    private LocalDateTime dataGeracao;

    public IntervencaoOperacional() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTrechoId() { return trechoId; }
    public void setTrechoId(Long trechoId) { this.trechoId = trechoId; }
    public Long getEquipeId() { return equipeId; }
    public void setEquipeId(Long equipeId) { this.equipeId = equipeId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getQuilometroInicial() { return quilometroInicial; }
    public void setQuilometroInicial(Integer quilometroInicial) { this.quilometroInicial = quilometroInicial; }
    public Integer getQuilometroFinal() { return quilometroFinal; }
    public void setQuilometroFinal(Integer quilometroFinal) { this.quilometroFinal = quilometroFinal; }
    public String getTipoClima() { return tipoClima; }
    public void setTipoClima(String tipoClima) { this.tipoClima = tipoClima; }
    public String getNomeEquipe() { return nomeEquipe; }
    public void setNomeEquipe(String nomeEquipe) { this.nomeEquipe = nomeEquipe; }
    public Double getNivelVegetacaoAntesCm() { return nivelVegetacaoAntesCm; }
    public void setNivelVegetacaoAntesCm(Double nivelVegetacaoAntesCm) { this.nivelVegetacaoAntesCm = nivelVegetacaoAntesCm; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }
}
