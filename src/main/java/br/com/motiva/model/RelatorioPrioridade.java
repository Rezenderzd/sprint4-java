package br.com.motiva.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "relatoriosPrioridade")
public class RelatorioPrioridade {

    @Id
    @SequenceGenerator(name = "seqRelatorios", sequenceName = "seq_relatorios", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqRelatorios")
    private Long id;

    @Column(name = "dataGeracao")
    private LocalDateTime dataGeracao;

    @Column(name = "totalEquipes")
    private Integer totalEquipes;

    @Column(name = "totalTrechos")
    private Integer totalTrechos;

    @Column(name = "trechosComSensor")
    private Integer trechosComSensor;

    @Column(name = "trechosSemSensor")
    private Integer trechosSemSensor;

    @Column(name = "trechosUrgente")
    private Integer trechosUrgente;

    @Column(name = "trechosCritico")
    private Integer trechosCritico;

    @Column(name = "trechosAtencao")
    private Integer trechosAtencao;

    @Column(name = "trechosNormal")
    private Integer trechosNormal;

    public RelatorioPrioridade() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }
    public Integer getTotalEquipes() { return totalEquipes; }
    public void setTotalEquipes(Integer totalEquipes) { this.totalEquipes = totalEquipes; }
    public Integer getTotalTrechos() { return totalTrechos; }
    public void setTotalTrechos(Integer totalTrechos) { this.totalTrechos = totalTrechos; }
    public Integer getTrechosComSensor() { return trechosComSensor; }
    public void setTrechosComSensor(Integer trechosComSensor) { this.trechosComSensor = trechosComSensor; }
    public Integer getTrechosSemSensor() { return trechosSemSensor; }
    public void setTrechosSemSensor(Integer trechosSemSensor) { this.trechosSemSensor = trechosSemSensor; }
    public Integer getTrechosUrgente() { return trechosUrgente; }
    public void setTrechosUrgente(Integer trechosUrgente) { this.trechosUrgente = trechosUrgente; }
    public Integer getTrechosCritico() { return trechosCritico; }
    public void setTrechosCritico(Integer trechosCritico) { this.trechosCritico = trechosCritico; }
    public Integer getTrechosAtencao() { return trechosAtencao; }
    public void setTrechosAtencao(Integer trechosAtencao) { this.trechosAtencao = trechosAtencao; }
    public Integer getTrechosNormal() { return trechosNormal; }
    public void setTrechosNormal(Integer trechosNormal) { this.trechosNormal = trechosNormal; }
}
