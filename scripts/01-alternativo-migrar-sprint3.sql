ALTER TABLE trechos MODIFY id DROP IDENTITY;
ALTER TABLE equipesManutencao MODIFY id DROP IDENTITY;
ALTER TABLE intervencoesOperacionais MODIFY id DROP IDENTITY;
ALTER TABLE relatoriosPrioridade MODIFY id DROP IDENTITY;
ALTER TABLE rankingEquipes MODIFY id DROP IDENTITY;
ALTER TABLE rankingTrechos MODIFY id DROP IDENTITY;

DECLARE
    PROCEDURE criar_sequence(p_sequence VARCHAR2, p_tabela VARCHAR2) IS
        v_proximo NUMBER;
    BEGIN
        EXECUTE IMMEDIATE 'SELECT NVL(MAX(id), 0) + 1 FROM ' || p_tabela INTO v_proximo;
        EXECUTE IMMEDIATE 'CREATE SEQUENCE ' || p_sequence
                || ' START WITH ' || v_proximo || ' INCREMENT BY 1 NOCACHE';
    END;
BEGIN
    criar_sequence('seq_trechos', 'trechos');
    criar_sequence('seq_equipes', 'equipesManutencao');
    criar_sequence('seq_intervencoes', 'intervencoesOperacionais');
    criar_sequence('seq_relatorios', 'relatoriosPrioridade');
    criar_sequence('seq_ranking_equipes', 'rankingEquipes');
    criar_sequence('seq_ranking_trechos', 'rankingTrechos');
END;
/

ALTER TABLE intervencoesOperacionais ADD (trechoId NUMBER, equipeId NUMBER, nivelVegetacaoAntesCm NUMBER);
ALTER TABLE relatoriosPrioridade ADD (trechosUrgente NUMBER, trechosCritico NUMBER, trechosAtencao NUMBER, trechosNormal NUMBER);

UPDATE intervencoesOperacionais i
   SET trechoId = (SELECT MIN(t.id) FROM trechos t
                    WHERE t.nome = i.nome
                      AND t.quilometroInicial = i.quilometroInicial
                      AND t.quilometroFinal = i.quilometroFinal)
 WHERE trechoId IS NULL;

UPDATE intervencoesOperacionais i
   SET equipeId = (SELECT MIN(e.id) FROM equipesManutencao e WHERE e.nomeEquipe = i.nomeEquipe)
 WHERE equipeId IS NULL;

COMMIT;

SELECT id, nome, nomeEquipe FROM intervencoesOperacionais WHERE trechoId IS NULL OR equipeId IS NULL;

ALTER TABLE intervencoesOperacionais MODIFY (trechoId NOT NULL, equipeId NOT NULL);
ALTER TABLE intervencoesOperacionais ADD CONSTRAINT fk_intervencoes_trecho
    FOREIGN KEY (trechoId) REFERENCES trechos(id);
ALTER TABLE intervencoesOperacionais ADD CONSTRAINT fk_intervencoes_equipe
    FOREIGN KEY (equipeId) REFERENCES equipesManutencao(id);
