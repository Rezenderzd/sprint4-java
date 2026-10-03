DROP TABLE rankingEquipes;
DROP TABLE rankingTrechos;
DROP TABLE relatoriosPrioridade;
DROP TABLE intervencoesOperacionais;
DROP TABLE equipesManutencao;
DROP TABLE trechos;

DROP SEQUENCE seq_trechos;
DROP SEQUENCE seq_equipes;
DROP SEQUENCE seq_intervencoes;
DROP SEQUENCE seq_relatorios;
DROP SEQUENCE seq_ranking_equipes;
DROP SEQUENCE seq_ranking_trechos;

CREATE SEQUENCE seq_trechos          START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_equipes          START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_intervencoes     START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_relatorios       START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_ranking_equipes  START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_ranking_trechos  START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE TABLE trechos (
    id                  NUMBER PRIMARY KEY,
    nome                VARCHAR2(100) NOT NULL,
    quilometroInicial   NUMBER NOT NULL,
    quilometroFinal     NUMBER NOT NULL,
    nivelVegetacaoEmCm  NUMBER NOT NULL,
    tipoClima           VARCHAR2(100) NOT NULL,
    trechoComSenor      NUMBER(1) NOT NULL,
    dataGeracao         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE equipesManutencao (
    id                      NUMBER PRIMARY KEY,
    nomeEquipe              VARCHAR2(100) NOT NULL,
    quantidadeFuncionarios  NUMBER NOT NULL,
    rocadaDeAtuacao         VARCHAR2(100) NOT NULL,
    dataGeracao             TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE intervencoesOperacionais (
    id                  NUMBER PRIMARY KEY,
    trechoId            NUMBER NOT NULL,
    equipeId            NUMBER NOT NULL,
    nome                VARCHAR2(100) NOT NULL,
    quilometroInicial   NUMBER NOT NULL,
    quilometroFinal     NUMBER NOT NULL,
    tipoClima           VARCHAR2(100) NOT NULL,
    nomeEquipe          VARCHAR2(100) NOT NULL,
    nivelVegetacaoAntesCm NUMBER,
    dataGeracao         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_intervencoes_trecho
        FOREIGN KEY (trechoId) REFERENCES trechos(id),
    CONSTRAINT fk_intervencoes_equipe
        FOREIGN KEY (equipeId) REFERENCES equipesManutencao(id)
);

CREATE TABLE relatoriosPrioridade (
    id                  NUMBER PRIMARY KEY,
    dataGeracao         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    totalEquipes        NUMBER,
    totalTrechos        NUMBER,
    trechosComSensor    NUMBER,
    trechosSemSensor    NUMBER,
    trechosUrgente      NUMBER,
    trechosCritico      NUMBER,
    trechosAtencao      NUMBER,
    trechosNormal       NUMBER
);

CREATE TABLE rankingEquipes (
    id                  NUMBER PRIMARY KEY,
    relatorioId         NUMBER NOT NULL,
    nomeEquipe          VARCHAR2(100),
    totalIntervencoes   NUMBER,
    CONSTRAINT fk_ranking_equipes_relatorio
        FOREIGN KEY (relatorioId) REFERENCES relatoriosPrioridade(id)
);

CREATE TABLE rankingTrechos (
    id                  NUMBER PRIMARY KEY,
    relatorioId         NUMBER NOT NULL,
    nome                VARCHAR2(100),
    quilometroInicial   NUMBER,
    quilometroFinal     NUMBER,
    totalIntervencoes   NUMBER,
    CONSTRAINT fk_ranking_trechos_relatorio
        FOREIGN KEY (relatorioId) REFERENCES relatoriosPrioridade(id)
);
