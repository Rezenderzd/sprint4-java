package br.com.motiva.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rankingEquipes")
public class RankingEquipe {

    @Id
    @SequenceGenerator(name = "seqRankingEquipes", sequenceName = "seq_ranking_equipes", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqRankingEquipes")
    private Long id;

    @Column(name = "relatorioId")
    private Long relatorioId;

    @Column(name = "nomeEquipe")
    private String nomeEquipe;

    @Column(name = "totalIntervencoes")
    private Long totalIntervencoes;

    public RankingEquipe() {
    }

    public RankingEquipe(Long relatorioId, String nomeEquipe, Long totalIntervencoes) {
        this.relatorioId = relatorioId;
        this.nomeEquipe = nomeEquipe;
        this.totalIntervencoes = totalIntervencoes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRelatorioId() { return relatorioId; }
    public void setRelatorioId(Long relatorioId) { this.relatorioId = relatorioId; }
    public String getNomeEquipe() { return nomeEquipe; }
    public void setNomeEquipe(String nomeEquipe) { this.nomeEquipe = nomeEquipe; }
    public Long getTotalIntervencoes() { return totalIntervencoes; }
    public void setTotalIntervencoes(Long totalIntervencoes) { this.totalIntervencoes = totalIntervencoes; }
}
