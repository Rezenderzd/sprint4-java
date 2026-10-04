package br.com.motiva.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rankingTrechos")
public class RankingTrecho {

    @Id
    @SequenceGenerator(name = "seqRankingTrechos", sequenceName = "seq_ranking_trechos", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqRankingTrechos")
    private Long id;

    @Column(name = "relatorioId")
    private Long relatorioId;

    @Column(name = "nome")
    private String nome;

    @Column(name = "quilometroInicial")
    private Integer quilometroInicial;

    @Column(name = "quilometroFinal")
    private Integer quilometroFinal;

    @Column(name = "totalIntervencoes")
    private Long totalIntervencoes;

    public RankingTrecho() {
    }

    public RankingTrecho(Long relatorioId, String nome, Integer quilometroInicial,
                         Integer quilometroFinal, Long totalIntervencoes) {
        this.relatorioId = relatorioId;
        this.nome = nome;
        this.quilometroInicial = quilometroInicial;
        this.quilometroFinal = quilometroFinal;
        this.totalIntervencoes = totalIntervencoes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRelatorioId() { return relatorioId; }
    public void setRelatorioId(Long relatorioId) { this.relatorioId = relatorioId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getQuilometroInicial() { return quilometroInicial; }
    public void setQuilometroInicial(Integer quilometroInicial) { this.quilometroInicial = quilometroInicial; }
    public Integer getQuilometroFinal() { return quilometroFinal; }
    public void setQuilometroFinal(Integer quilometroFinal) { this.quilometroFinal = quilometroFinal; }
    public Long getTotalIntervencoes() { return totalIntervencoes; }
    public void setTotalIntervencoes(Long totalIntervencoes) { this.totalIntervencoes = totalIntervencoes; }
}
