# MOTIVA — Sprint 4: Spring Boot + JPA + API REST

API REST que controla o crescimento de vegetação em trechos de rodovia, aciona equipes de manutenção,
registra intervenções e gera relatórios de prioridade. É a evolução da Sprint 3 (console + JDBC puro):
o sistema continua usando o **Oracle FIAP** e as **mesmas tabelas e colunas**, mas agora com
**Spring Boot + Spring Data JPA**.

## Tecnologias
Java 17 · Spring Boot 4.1.x · Spring Web · Spring Data JPA (Hibernate) · Bean Validation · Oracle (`ojdbc17` via Maven) · JUnit 5 + Mockito + MockMvc

## Como executar

1. **Banco:** no Oracle SQL Developer rode (F5 / *Run Script*), nesta ordem:
   1. `scripts/01-criar-tabelas.sql` — recria as tabelas da Sprint 3 (mesmos nomes) e cria as **sequences**. Os `DROP` podem dar erro na primeira vez; é normal. **Apaga os dados.**
      *Alternativa que reaproveita as tabelas e os dados da Sprint 3:* `scripts/01-alternativo-migrar-sprint3.sql` (remove o `IDENTITY`, cria as sequences a partir do maior id, adiciona as colunas novas e as FKs). Use um **ou** outro.
      O script alternativo não foi executado contra o Oracle da FIAP: faça backup/export das tabelas antes. A consulta de
      conferência perto do fim do script (`... WHERE trechoId IS NULL OR equipeId IS NULL`) **precisa voltar vazia** antes de
      criar as chaves estrangeiras; se listar linhas, corrija ou apague essas intervenções (o trecho ou a equipe delas não existe mais).
   2. `scripts/02-dados.sql` — massa de dados de teste.
2. **Credenciais** (nunca no código; o `application.properties` lê `${DB_USER}` e `${DB_PASSWORD}` **sem valor padrão**, então sem as variáveis a aplicação não sobe):
   ```bash
   # Linux / Mac
   export DB_USER=RM000000
   export DB_PASSWORD=sua_senha
   ```
   ```powershell
   # Windows PowerShell
   $env:DB_USER="RM000000"
   $env:DB_PASSWORD="sua_senha"
   ```
3. **Subir a API:** `mvn spring-boot:run` (ou *Run* na classe `MotivaApplication` pela IDE) → http://localhost:8080
4. **Testes:** `mvn test` (não precisam de Oracle nem sobem o Spring).

## Endpoints

### Trechos — `/api/trechos`
| Método | Rota | Ação | Sucesso / Erro |
|---|---|---|---|
| GET | `/api/trechos` | Listar todos (com a prioridade calculada) | 200 |
| GET | `/api/trechos/{id}` | Buscar por ID | 200 / 404 |
| GET | `/api/trechos/vegetacao?minimo=30` | Derived query: vegetação ≥ mínimo | 200 |
| GET | `/api/trechos/clima/{clima}` | Derived query: por clima (`umido`/`seco`) | 200 |
| POST | `/api/trechos` | Criar (header `Location` aponta para o novo recurso) | 201 / 400 |
| POST | `/api/trechos/simular-crescimento` | Ação: faz a grama crescer em todos os trechos | 200 |
| PUT | `/api/trechos/{id}` | Atualizar | 200 / 400 / 404 |
| DELETE | `/api/trechos/{id}` | Remover (400 se o trecho já tem intervenções) | 204 / 400 / 404 |

Exemplos cURL:

```bash
# Listar trechos (200)
curl http://localhost:8080/api/trechos

# Criar trecho (201)
curl -X POST http://localhost:8080/api/trechos -H "Content-Type: application/json" \
  -d '{"nomeTrecho":"Rodo Norte","quilometroInicial":40,"quilometroFinal":55,"nivelVegetacaoEmCm":35.0,"tipoClima":"umido","comSensor":true}'

# Buscar inexistente (404)
curl -i http://localhost:8080/api/trechos/9999

# Atualizar (200)
curl -X PUT http://localhost:8080/api/trechos/1 -H "Content-Type: application/json" \
  -d '{"nomeTrecho":"Br","quilometroInicial":10,"quilometroFinal":15,"nivelVegetacaoEmCm":82.5,"tipoClima":"umido","comSensor":false}'

# Dados inválidos: km final menor que o inicial (400)
curl -i -X POST http://localhost:8080/api/trechos -H "Content-Type: application/json" \
  -d '{"nomeTrecho":"X","quilometroInicial":30,"quilometroFinal":10,"nivelVegetacaoEmCm":5,"tipoClima":"seco"}'

# Deletar (204)
curl -i -X DELETE http://localhost:8080/api/trechos/5

# Derived query: trechos com pelo menos 30 cm
curl "http://localhost:8080/api/trechos/vegetacao?minimo=30"
```

### Equipes — `/api/equipes`
| Método | Rota | Ação | Sucesso / Erro |
|---|---|---|---|
| GET | `/api/equipes` | Listar todas | 200 |
| GET | `/api/equipes/{id}` | Buscar por ID | 200 / 404 |
| GET | `/api/equipes/rocada/{tipo}` | Derived query: `manual` ou `mecanizada` | 200 |
| POST | `/api/equipes` | Criar (com `Location`) | 201 / 400 |
| PUT | `/api/equipes/{id}` | Atualizar | 200 / 400 / 404 |
| DELETE | `/api/equipes/{id}` | Remover (400 se a equipe já tem intervenções) | 204 / 400 / 404 |

Exemplos cURL:

```bash
# Criar equipe (201)
curl -X POST http://localhost:8080/api/equipes -H "Content-Type: application/json" \
  -d '{"nomeEquipe":"Equipe Alfa","numeroFuncionarios":6,"tipoDeRocadaDeAtuacao":"mecanizada"}'

# Derived query: equipes manuais
curl http://localhost:8080/api/equipes/rocada/manual
```

### Intervenções — `/api/intervencoes`
| Método | Rota | Ação | Sucesso / Erro |
|---|---|---|---|
| GET | `/api/intervencoes` | Listar histórico | 200 |
| GET | `/api/intervencoes/{id}` | Buscar por ID | 200 / 404 |
| GET | `/api/intervencoes/equipe/{nome}` | Por nome da equipe | 200 |
| POST | `/api/intervencoes` | Criar (`trechoId` + `equipeId`), com `Location` | 201 / 400 / 404 |
| POST | `/api/intervencoes/gerar` | Ação: motor da Sprint 3, gera para trechos ≥ 30 cm | 200 |
| PUT | `/api/intervencoes/{id}` | Trocar trecho/equipe (ver regra abaixo) | 200 / 400 / 404 |
| DELETE | `/api/intervencoes/{id}` | Remover | 204 / 404 |

O `PUT` de intervenção mantém o estado coerente: se só a equipe muda, atualiza a equipe (conferindo a compatibilidade) e mantém data e vegetação; se o **trecho** muda, o novo trecho é cortado (volta a 5 cm), a altura anterior ao corte do trecho antigo é restaurada (somente se ele ainda estiver em 5 cm) e a `dataGeracao` é renovada.

Exemplos cURL:

```bash
# Motor: gerar intervenções (200)
curl -X POST http://localhost:8080/api/intervencoes/gerar

# Intervenção com equipe incompatível (400): trecho úmido exige equipe mecanizada
curl -i -X POST http://localhost:8080/api/intervencoes -H "Content-Type: application/json" \
  -d '{"trechoId":1,"equipeId":1}'
```

### Relatórios — `/api/relatorios`
| Método | Rota | Ação | Sucesso / Erro |
|---|---|---|---|
| POST | `/api/relatorios` | Gera o relatório com os trechos atuais e **persiste** no histórico (com `Location`) | 201 |
| GET | `/api/relatorios` | Lista o histórico | 200 |
| GET | `/api/relatorios/periodo?inicio=2026-09-01&fim=2026-09-30` | Consulta por período (datas `AAAA-MM-DD`) | 200 / 400 |
| GET | `/api/relatorios/{id}` | Busca um relatório | 200 / 404 |

Exemplos cURL:

```bash
# Gerar relatório (201), histórico (200) e consulta por período (200)
curl -X POST http://localhost:8080/api/relatorios
curl http://localhost:8080/api/relatorios
curl "http://localhost:8080/api/relatorios/periodo?inicio=2026-09-01&fim=2026-12-31"
```

### Regras de negócio (todas no Service)
- **Motor de prioridade** (`MotorPrioridadeService`): `URGENTE` ≥ 80 cm · `CRITICO` ≥ 50 cm · `ATENCAO` ≥ 25 cm · `NORMAL` < 25 cm.
- **Motor de intervenções**: trechos com vegetação ≥ 30 cm (regra da Sprint 3). Clima **úmido** → `RocadaMecanizada` (equipe `mecanizada`); clima **seco** → `Pulverizacao` (equipe `manual`). Depois do serviço o trecho volta a 5 cm.
- **Validações da Sprint 1**: nome obrigatório, clima `umido`/`seco`, `kmFinal >= kmInicial`, `nivelVegetacao >= 0`.
- **PUT de intervenção:** se só a equipe muda, atualiza a equipe (conferindo a compatibilidade) e mantém data e vegetação. Se o **trecho** muda, o novo trecho é cortado (volta a 5 cm), a altura anterior ao corte do trecho antigo é restaurada (somente se ele ainda estiver em 5 cm, para não apagar crescimento posterior) e a `dataGeracao` é renovada. A altura anterior ao corte fica na coluna `nivelVegetacaoAntesCm`.
- **Erros** sempre no mesmo formato JSON: `{"status":404,"erro":"Not Found","mensagem":"...","timestamp":"..."}`. Além de 400/404, o `GlobalExceptionHandler` converte `DataIntegrityViolationException` (o banco recusou por FK/UNIQUE) em **400** e qualquer erro inesperado em **500**, sem expor stack trace nem mensagens `ORA-` (checklist da Aula 13: 404, 400, 500).

## Integrantes
| RM | Nome |
|---|---|
| 563415 | Fernando Caires Silva |
| 563567 | Raphael Mischiatti de Souza |
| 563500 | Guilherme Martins Rezende |
| 565434 | Giovanna Fernandes Pereira |
| 565323 | João Pedro de Moura Albino |
| 561675 | Kauê Silva Matheus |
