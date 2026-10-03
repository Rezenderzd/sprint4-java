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

## Integrantes
| RM | Nome |
|---|---|
| 563415 | Fernando Caires Silva |
| 563567 | Raphael Mischiatti de Souza |
| 563500 | Guilherme Martins Rezende |
| 565434 | Giovanna Fernandes Pereira |
| 565323 | João Pedro de Moura Albino |
| 561675 | Kauê Silva Matheus |
