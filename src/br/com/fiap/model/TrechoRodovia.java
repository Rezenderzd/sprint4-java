package br.com.fiap.model;

public class TrechoRodovia {

    private Long id;
    private String nomeTrecho;
    private int quilometroInicial;
    private int quilometroFinal;
    private double nivelVegetacaoEmCm;
    private ValidacoesTrechoRodovia validacoesTrechoRodovia = new ValidacoesTrechoRodovia();
    static final double ALTURA_POS_CORTE = 5.0;
    private String tipoClima;

    public TrechoRodovia(String nomeTrecho, int quilometroInicial, int quilometroFinal, double nivelVegetacaoEmCm, String tipoClima){
        if (!validacoesTrechoRodovia.validacaoGeral(nomeTrecho, quilometroInicial, quilometroFinal, nivelVegetacaoEmCm, tipoClima)){
            System.out.println("Não foi possível criar o Trecho, os dados não são válidos");
            return;
        }
        this.nomeTrecho = nomeTrecho;
        this.quilometroInicial = quilometroInicial;
        this.quilometroFinal = quilometroFinal;
        this.setNivelVegetacaoEmCm(nivelVegetacaoEmCm);
        this.tipoClima = tipoClima;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    protected void setNivelVegetacaoEmCm(double nivelVegetacaoEmCm) {
        this.nivelVegetacaoEmCm = nivelVegetacaoEmCm;
    }

    public String getNomeTrecho() {
        return nomeTrecho;
    }

    public int getQuilometroInicial() {
        return quilometroInicial;
    }

    public int getQuilometroFinal() {
        return quilometroFinal;
    }

    public double getNivelVegetacaoEmCm() {
        return nivelVegetacaoEmCm;
    }

    public String getTipoClima() {
        return tipoClima;
    }


    public void registrarCrescimento(double taxa){
        if(getNomeTrecho() == null){
            System.out.println("Não é possível atualizar o crescimento de um trecho inexistente.");
            return;
        }
        if(taxa<=0){
            System.out.println("Digite uma taxa de crescimento válida.");
            return;
        }
        setNivelVegetacaoEmCm(getNivelVegetacaoEmCm()+taxa);
    }

    public void realizarCorteVegetacao() {
        this.nivelVegetacaoEmCm = ALTURA_POS_CORTE;
    }

    public void simularCrescimento(){
        System.out.println("Não é possível simular crescimento em trechos sem Iot");
    }
}