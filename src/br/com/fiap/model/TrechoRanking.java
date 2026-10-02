package br.com.fiap.model;

public record TrechoRanking(
        String nomeTrecho,
        double kmInicial,
        double kmFinal,
        int totalIntervencoes
) {}