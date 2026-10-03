package br.com.fiap.service;

import br.com.fiap.dao.IntervencaoOperacionalDAO;
import br.com.fiap.dao.TrechoRodoviaDAO;
import br.com.fiap.model.*;

import java.util.List;
import java.util.Random;

public class AvaliacaoTrechos {
    static final double LIMITE_VEGETACAO_CM = 30.0;
    public void exibindoTrechos(List<TrechoRodovia> trechos, List<EquipeManutencao> equipes){
        IntervencaoOperacionalDAO intervencaoDao = new IntervencaoOperacionalDAO();
        TrechoRodoviaDAO trechoDAO = new TrechoRodoviaDAO();
        boolean acionou = false;
        for(TrechoRodovia trecho: trechos){
            if(trecho.getNivelVegetacaoEmCm()>=LIMITE_VEGETACAO_CM){
                avaliandoEquipeIdeal(trecho, equipes, intervencaoDao, trechoDAO);
                acionou = true;
            }
        }
        if(!acionou){
            System.out.println("Não houve a necessidade de nenhuma intervenção.");
        }
    }

    public IntervencaoOperacional avaliandoEquipeIdeal(TrechoRodovia trecho, List<EquipeManutencao> equipes, IntervencaoOperacionalDAO dao, TrechoRodoviaDAO trechoDAO) {
        Random random = new Random();
        String tipoDeRocada;
        EquipeManutencao equipeSelecionada;
        IntervencaoOperacional intervencao = null;

        if (trecho.getTipoClima().equalsIgnoreCase("umido")) {
            do {
                int equipeSorteada = random.nextInt(equipes.size());
                tipoDeRocada = equipes.get(equipeSorteada).getTipoDeRocadaDeAtuacao();
                equipeSelecionada = equipes.get(equipeSorteada);
            } while (!tipoDeRocada.equalsIgnoreCase("mecanizada"));

            intervencao = new RocadaMecanizada();

            System.out.printf("%s\n", intervencao.executarServico(intervencao, trecho, equipeSelecionada));

            trecho.realizarCorteVegetacao();
            trechoDAO.atualizar(trecho);
            return intervencao;
        }

        if (trecho.getTipoClima().equalsIgnoreCase("seco")) {
            do {
                int equipeSorteada = random.nextInt(equipes.size());
                tipoDeRocada = equipes.get(equipeSorteada).getTipoDeRocadaDeAtuacao();
                equipeSelecionada = equipes.get(equipeSorteada);
            } while (!tipoDeRocada.equalsIgnoreCase("manual"));

            intervencao = new Pulverizacao();

            System.out.printf("%s\n", intervencao.executarServico(intervencao, trecho, equipeSelecionada));
            trecho.realizarCorteVegetacao();
            trechoDAO.atualizar(trecho);
            return intervencao;
        }

        System.out.println("Tipo de clima inválido, não tem como saber qual serviço utilizar.");
        return null;
    }
}
