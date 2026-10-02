package br.com.fiap.main;

import br.com.fiap.dao.*;
import br.com.fiap.model.EquipeManutencao;
import br.com.fiap.service.AvaliacaoTrechos;
import br.com.fiap.service.Relatorio;
import br.com.fiap.model.TrechoRodovia;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        AvaliacaoTrechos analise = new AvaliacaoTrechos();

        TrechoRodoviaDAO trechoDAO = new TrechoRodoviaDAO();
        EquipeManutencaoDAO equipeManutencaoDAO = new EquipeManutencaoDAO();
        IntervencaoOperacionalDAO intervencaoDAO = new IntervencaoOperacionalDAO();
        Relatorio relatorio = new Relatorio();

        trechoDAO.atualizandoTrechosBanco();


        List<TrechoRodovia> trechos = trechoDAO.buscarTodos();
        List<EquipeManutencao> equipes = equipeManutencaoDAO.buscarTodos();

        System.out.println("=======================TRECHOS=====================");
        trechoDAO.listarTodos();

        System.out.println("=======================EQUIPES=====================");
        equipeManutencaoDAO.listarTodos();

        System.out.println("===================GERANDO INTERVENÇÕES=================");
        analise.exibindoTrechos(trechos, equipes);

        System.out.println("=====================INTERVENCOES==================");
        intervencaoDAO.listarTodos();

        relatorio.exibirRelatorio();

        System.out.println("================GUARDANDO RELATORIO=============");
        relatorio.salvarRelatorio();

        System.out.println("=======CODIGO FINALIZADO========");

    }
}