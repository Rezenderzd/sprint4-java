INSERT INTO trechos (id, nome, quilometroInicial, quilometroFinal, nivelVegetacaoEmCm, tipoClima, trechoComSenor)
VALUES (seq_trechos.NEXTVAL, 'Br', 10, 15, 20.0, 'umido', 0);

INSERT INTO trechos (id, nome, quilometroInicial, quilometroFinal, nivelVegetacaoEmCm, tipoClima, trechoComSenor)
VALUES (seq_trechos.NEXTVAL, 'Rodo Anel', 20, 30, 19.0, 'seco', 0);

INSERT INTO trechos (id, nome, quilometroInicial, quilometroFinal, nivelVegetacaoEmCm, tipoClima, trechoComSenor)
VALUES (seq_trechos.NEXTVAL, 'Motiva Sorocabana', 15, 20, 18.0, 'umido', 1);

INSERT INTO trechos (id, nome, quilometroInicial, quilometroFinal, nivelVegetacaoEmCm, tipoClima, trechoComSenor)
VALUES (seq_trechos.NEXTVAL, 'Rodovia Presidente Dutra', 30, 40, 22.0, 'seco', 1);

INSERT INTO equipesManutencao (id, nomeEquipe, quantidadeFuncionarios, rocadaDeAtuacao)
VALUES (seq_equipes.NEXTVAL, 'Equipe Alpha', 5, 'manual');

INSERT INTO equipesManutencao (id, nomeEquipe, quantidadeFuncionarios, rocadaDeAtuacao)
VALUES (seq_equipes.NEXTVAL, 'Equipe Delta', 7, 'mecanizada');

INSERT INTO equipesManutencao (id, nomeEquipe, quantidadeFuncionarios, rocadaDeAtuacao)
VALUES (seq_equipes.NEXTVAL, 'Equipe Gama', 8, 'manual');

INSERT INTO equipesManutencao (id, nomeEquipe, quantidadeFuncionarios, rocadaDeAtuacao)
VALUES (seq_equipes.NEXTVAL, 'Equipe Zeta', 7, 'mecanizada');

COMMIT;
