package br.com.motiva.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trechos")
public class TrechoRodovia {

    public static final double ALTURA_POS_CORTE = 5.0;

    @Id
    @SequenceGenerator(name = "seqTrechos", sequenceName = "seq_trechos", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqTrechos")
    private Long id;

    @Column(name = "nome")
    private String nomeTrecho;

    @Column(name = "quilometroInicial")
    private Integer quilometroInicial;

    @Column(name = "quilometroFinal")
    private Integer quilometroFinal;

    @Column(name = "nivelVegetacaoEmCm")
    private Double nivelVegetacaoEmCm;

    @Column(name = "tipoClima")
    private String tipoClima;

    @Column(name = "trechoComSenor")
    private Boolean comSensor;

    public TrechoRodovia() {
    }

    public void registrarCrescimento(double taxa) {
        if (taxa <= 0) {
            throw new IllegalArgumentException("A taxa de crescimento deve ser maior que zero");
        }
        this.nivelVegetacaoEmCm = this.nivelVegetacaoEmCm + taxa;
    }

    public void realizarCorteVegetacao() {
        this.nivelVegetacaoEmCm = ALTURA_POS_CORTE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeTrecho() { return nomeTrecho; }
    public void setNomeTrecho(String nomeTrecho) { this.nomeTrecho = nomeTrecho; }
    public Integer getQuilometroInicial() { return quilometroInicial; }
    public void setQuilometroInicial(Integer quilometroInicial) { this.quilometroInicial = quilometroInicial; }
    public Integer getQuilometroFinal() { return quilometroFinal; }
    public void setQuilometroFinal(Integer quilometroFinal) { this.quilometroFinal = quilometroFinal; }
    public Double getNivelVegetacaoEmCm() { return nivelVegetacaoEmCm; }
    public void setNivelVegetacaoEmCm(Double nivelVegetacaoEmCm) { this.nivelVegetacaoEmCm = nivelVegetacaoEmCm; }
    public String getTipoClima() { return tipoClima; }
    public void setTipoClima(String tipoClima) { this.tipoClima = tipoClima; }
    public Boolean getComSensor() { return comSensor; }
    public void setComSensor(Boolean comSensor) { this.comSensor = comSensor; }
}
