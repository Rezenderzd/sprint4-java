package br.com.fiap.service;

import br.com.fiap.dao.RelatorioPrioridadeDAO;
import br.com.fiap.model.EquipesRanking;
import br.com.fiap.model.TrechoRanking;

import java.util.List;
import java.util.Map;

public class Relatorio {

    public void gerandoRankingEquipes(){
        RelatorioPrioridadeDAO dao = new RelatorioPrioridadeDAO();
        System.out.println("\n=== RANKING DE EQUIPES POR INTERVENÇÃO ===");
        List<EquipesRanking> rankingEquipes = dao.obterRankingEquipes();
        if (rankingEquipes.size() == 0){
            System.out.println("Não há nenhuma equipe que realizou uma intervenção até o momento");
        }
        for (int i = 0; i < rankingEquipes.size(); i++) {
            EquipesRanking equipe = rankingEquipes.get(i);
            int posicao = i + 1;

            System.out.println(posicao + "º " + equipe.nomeEquipe() + " - " + equipe.totalIntervencoes() + " intervenções");
        }
    }

    public void gerandoRankingTrechos(){
        RelatorioPrioridadeDAO dao = new RelatorioPrioridadeDAO();
        System.out.println("\n=== RANKING DE TRECHOS POR INTERVENÇÃO ===");
        List<TrechoRanking> rankingTrechos = dao.obterRankingTrechosComMaisIntervencoes();
        if(rankingTrechos.size() == 0){
            System.out.println("Não há nenhum trecho com intervenção até o momento");
        }
        for (int i = 0; i < rankingTrechos.size(); i++) {
            TrechoRanking trecho = rankingTrechos.get(i);
            int posicao = i + 1;
            System.out.println(posicao + "º " + trecho.nomeTrecho() +
                    " (KM " + trecho.kmInicial() + " até KM " + trecho.kmFinal() + ")" +
                    " - " + trecho.totalIntervencoes() + " aparições");
        }
    }

    public void exibirRelatorio(){
        RelatorioPrioridadeDAO dao = new RelatorioPrioridadeDAO();

        int totalEquipes = dao.contarEquipes();
        int totalTrechos = dao.contarTrechos();

        Map<String, Integer> contagemSensores = dao.contarTrechosComESemSensor();
        int trechosComSensor = contagemSensores.getOrDefault("comSensor", 0);
        int trechosSemSensor = contagemSensores.getOrDefault("semSensor", 0);

        // Exibição no console
        System.out.println("=== RELATÓRIO DE DADOS ===");
        System.out.println("Quantidade equipes de manutenção: " + totalEquipes);
        System.out.println("Quantidade de trechos: " + totalTrechos);
        System.out.println("Trechos com sensor: " + trechosComSensor);
        System.out.println("Trechos sem sensor: " + trechosSemSensor);

        gerandoRankingEquipes();
        gerandoRankingTrechos();
    }

    public void salvarRelatorio() {
        RelatorioPrioridadeDAO dao = new RelatorioPrioridadeDAO();

        int totalEquipes = dao.contarEquipes();
        int totalTrechos = dao.contarTrechos();

        Map<String, Integer> contagemSensores =
                dao.contarTrechosComESemSensor();

        int trechosComSensor =
                contagemSensores.getOrDefault("comSensor", 0);

        int trechosSemSensor =
                contagemSensores.getOrDefault("semSensor", 0);

        List<EquipesRanking> rankingEquipes =
                dao.obterRankingEquipes();

        List<TrechoRanking> rankingTrechos =
                dao.obterRankingTrechosComMaisIntervencoes();

        dao.salvarRelatorio(
                totalEquipes,
                totalTrechos,
                trechosComSensor,
                trechosSemSensor,
                rankingEquipes,
                rankingTrechos
        );
    }
}
