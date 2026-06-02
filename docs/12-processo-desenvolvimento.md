# Fase 6 — Modelo de Processo de Desenvolvimento

**Projeto:** ImobFiscal
**PI:** 2º Semestre DSM — FATEC Indaiatuba
**Data:** 2026-06-02

---

## 1. Modelo adotado: Iterativo e Incremental

O ImobFiscal foi desenvolvido seguindo um modelo de processo **iterativo e incremental**,
organizado em 6 fases definidas pelo manual do Projeto Integrador da FATEC DSM.

**Iterativo** significa que o projeto passa por múltiplos ciclos de planejamento, execução
e revisão — cada fase é uma iteração que revisa e complementa o que foi feito na anterior.
Se um requisito se mostra impreciso após a modelagem, a revisão acontece na próxima iteração,
não ao final do projeto.

**Incremental** significa que o sistema cresce em partes funcionais entregáveis. A Fase 3
entrega o banco de dados; a Fase 4 entrega o backend e o frontend integrados. Cada incremento
é verificável independentemente.

### Por que esse modelo se adequa a um projeto acadêmico

| Característica | Por que importa no contexto do PI |
|---|---|
| Entregas por fase | O professor avalia cada fase separadamente — o modelo garante que há artefato concreto a cada avaliação |
| Feedback a cada iteração | Comentários do professor na Fase 2 (requisitos) podem ser incorporados antes de implementar na Fase 4 |
| Escopo fixo por semestre | O PI tem 15 semanas; o modelo iterativo permite ajustar o que entra em cada incremento sem comprometer a entrega final |
| Rastreabilidade natural | A sequência Escopo → Requisitos → Modelagem cria trilha de decisões documentada, facilitando a defesa para a banca |

---

## 2. As 6 Fases do PI 2

A tabela abaixo mapeia cada fase ao seu conteúdo, aos artefatos produzidos e ao marco de entrega,
com base no cronograma de 15 semanas registrado em `docs/00-plano-pi2.md`.

| Fase | Semanas | Atividades realizadas | Artefatos produzidos | Marco / Entrega |
|---|---|---|---|---|
| **1 — Escopo** | 1–2 | Definição do problema, proposta do sistema, atores, funcionalidades no escopo, tecnologias fixadas | [`docs/01-escopo.md`](01-escopo.md) + `README.md` raiz + repositório GitHub público | Repositório público com estrutura de pastas |
| **2 — Requisitos e Modelagem** | 3–4 | Levantamento de 15 RFs, 7 RNFs, 5 RNs; diagrama de casos de uso (UC01–UC10); diagrama de classes (7 classes); diagrama de sequência (login e geração de boleto) | [`docs/02-requisitos.md`](02-requisitos.md), [`docs/03-casos-de-uso.md`](03-casos-de-uso.md), [`docs/04-diagrama-classes.md`](04-diagrama-classes.md), [`docs/03-diagrama-sequencia.md`](03-diagrama-sequencia.md) | Documentação UML completa para revisão do professor |
| **3 — Banco de Dados** | 5–6 | Modelagem do DER (8 tabelas, 1:N, sem N:N); script DDL com constraints e índices; seed de demonstração; dicionário de dados | `database/schema.sql` (V1 — 6 tabelas), `database/V2__motor_fiscal.sql` (2 tabelas + Motor Tributário), `database/seed.sql`, [`docs/05-dicionario-dados.md`](05-dicionario-dados.md) | Banco criado e funcional no Railway PostgreSQL |
| **4 — Implementação** | 7–11 | Backend Spring Boot: MVC clássico (controller/model/view sem service), CRUD completo (Locador, Imóvel, Contrato, NotaFiscal, Boleto), Motor Tributário, API aberta (sem JWT); Frontend React/Vite: login, listagem, cadastro, edição, soft delete | Código em `backend/` e `frontend/`; API disponível no Railway; frontend disponível no Vercel | Sistema CRUD ponta-a-ponta funcionando em produção |
| **5 — Testes** | 12–13 | Escrita de 5 testes unitários (JUnit 5 + Mockito) para `MotorTributario` e `ImovelDao`; validação manual via Postman e navegador | [`docs/06-plano-testes.md`](06-plano-testes.md), `backend/src/test/java/…/MotorTributarioTest.java`, `backend/src/test/java/…/ImovelDaoTest.java` | Testes unitários passando (`mvn test`); evidências de validação manual |
| **6 — Considerações Finais** | 14–15 | Revisão de todos os documentos; produção dos documentos finais (Matriz de Rastreabilidade, Diagramas de Atividades, Processo de Desenvolvimento); preparação da apresentação | [`docs/07-consideracoes-finais.md`](07-consideracoes-finais.md), [`docs/10-matriz-rastreabilidade.md`](10-matriz-rastreabilidade.md), [`docs/11-diagrama-atividades.md`](11-diagrama-atividades.md), este documento | Defesa para a banca (Semana 15) |

---

## 3. Papéis da Equipe

O ImobFiscal foi desenvolvido por um grupo reduzido (contexto acadêmico). Os papéis abaixo
descrevem as responsabilidades, que podem ser exercidas pela mesma pessoa em projetos de equipe
pequena:

| Papel | Responsabilidades no PI |
|---|---|
| **Analista / Documentador** | Levantamento de requisitos, casos de uso, modelagem UML, dicionário de dados, documentação de cada fase |
| **Arquiteto / Designer de BD** | Decisões de stack (Spring Boot, PostgreSQL, SQL puro), modelagem das 8 tabelas, padrões de projeto (soft delete, UUID, scripts SQL versionados em `database/`) |
| **Desenvolvedor Backend** | Implementação dos controllers, DAOs (SQL puro com JdbcTemplate), Motor Tributário, GeradorBoleto, DTOs; API aberta sem JWT |
| **Desenvolvedor Frontend** | Componentes React, consumo de API com `fetch`, roteamento, integração com backend, deploy no Vercel |
| **Testador** | Escrita dos casos de teste unitários com JUnit 5 + Mockito, validação manual via Postman, registro de evidências |

---

## 4. Ferramentas e Práticas Adotadas

### Controle de versão — Git e GitHub

O repositório Git é a fonte de verdade do projeto. Todo artefato — código, scripts SQL,
documentação — está versionado no mesmo repositório público no GitHub.

**Conventional Commits** foi adotado como convenção de mensagens:

```
feat(backend): adicionar CRUD de Locador com soft delete
fix(auth): corrigir validação de token expirado
docs(requisitos): atualizar RF14 com detalhamento LC 214/2025
chore(deps): atualizar jjwt para 0.12.5
```

Tipos usados: `feat`, `fix`, `docs`, `chore`, `refactor`, `test`. O formato facilita
gerar changelogs e identificar a natureza de cada mudança no histórico.

### Revisão de código

A revisão de código é feita antes de integrar mudanças significativas ao branch principal.
Em equipes pequenas, isso ocorre como auto-revisão disciplinada: releitura do diff antes do
commit, verificação de que o soft delete filtra `deleted_at IS NULL`, e que nenhuma senha
é commitada em texto puro.

### Testes unitários

JUnit 5 com Mockito, executados via `mvn test`. Os 5 testes cobrem `MotorTributario`
(cálculo IBS/CBS, alíquota ausente, alíquota zero/isenção) e `ImovelDao` (soft delete via
UPDATE e exceção para imóvel não encontrado). A ausência de teste físico contra o banco é
uma limitação documentada (ver `docs/06-plano-testes.md`): os testes são unitários puros,
usando mocks do JdbcTemplate / DAO.

### Deploy

| Componente | Plataforma | Como é feito |
|---|---|---|
| Frontend | Vercel | Push para o branch principal aciona re-build automático pelo Vercel |
| Backend | Railway | Railway detecta `railway.toml`, executa `mvn clean package -DskipTests` e reinicia o container |
| Banco de dados | Railway PostgreSQL | Instância persistente; schema criado manualmente executando os scripts SQL em `database/` — o backend usa JdbcTemplate (SQL puro), sem ORM |

**Nota importante:** o deploy é acionado automaticamente por push no GitHub (via integração
Vercel/Railway), mas **não há pipeline de CI/CD com gates** — os testes não rodam antes do
deploy. O flag `-DskipTests` no build Railway pula os testes unitários para agilizar o deploy.
Em um projeto de produção, os testes deveriam rodar no pipeline antes do deploy chegar ao
ambiente de destino.

---

## 5. Gestão de Configuração e Mudança de Schema

### Schema em runtime: scripts SQL manuais

O schema do banco é criado e mantido **exclusivamente por execução manual dos scripts SQL**
em `database/`. O backend usa `JdbcTemplate` (SQL escrito à mão) — não há Hibernate, JPA
nem `ddl-auto`. Nenhuma ferramenta (Flyway, Liquibase) aplica migrações automaticamente.

Ao provisionar o banco no Railway, o operador executa os scripts na ordem:

1. `database/schema.sql` — cria as 6 tabelas originais.
2. `database/V2__motor_fiscal.sql` — adiciona colunas e cria `aliquotas_vigentes` e `boletos`.
3. `database/seed.sql` — insere dados de demonstração.

O projeto **não usa Flyway** nem nenhuma outra ferramenta de migration automática.

### Scripts SQL versionados em `database/`

Os arquivos SQL da pasta `database/` seguem a convenção de nome estilo Flyway (prefixo
`V2__`, por exemplo), mas servem a dois propósitos distintos: documentar o schema e
permitir a aplicação manual.
**Eles não são aplicados automaticamente** — o operador os executa uma vez ao configurar o banco.

| Arquivo | Versão | O que documenta / faz |
|---|---|---|
| `database/schema.sql` | V1 (referência) | Cria as 6 tabelas originais: `imobiliarias`, `usuarios`, `locadores`, `imoveis`, `contratos_locacao`, `notas_fiscais` |
| `database/V2__motor_fiscal.sql` | V2 | Adiciona colunas a `locadores` e `imoveis`; cria `aliquotas_vigentes` (16 linhas de alíquotas 2026) e `boletos` (com 4 índices explícitos) |

**Boa prática mesmo sem Flyway:** nunca edite um script já aplicado manualmente em produção.
Se uma estrutura precisar mudar, crie um novo script (`V3__corrigir_xyz.sql`) e aplique
manualmente — isso mantém o histórico rastreável. Para um produto de produção real, o
recomendado seria adotar Flyway ou Liquibase com `ddl-auto=validate`, garantindo que o
schema em produção só muda por migrações revisadas e versionadas.

### Mudanças de decisão documentadas

Quatro decisões técnicas relevantes foram alteradas durante o semestre, todas por orientação
do professor (orientador da disciplina de Engenharia de Software). Cada mudança foi tratada
como requisito redefinido pelo orientador — não como retrabalho.

#### Mudança 1 — NestJS/Express → Spring Boot

O `docs/00-plano-pi2.md` (planejamento inicial) registrava o backend como **Node.js +
Express com SQL puro**. Durante a implementação, as bibliotecas de segurança para JWT
geraram incompatibilidades.

A decisão foi migrar para **Spring Boot + Java**. Os motivos registrados em
`docs/07-consideracoes-finais.md`:

1. O NestJS já estava iniciado mas as bibliotecas de segurança geravam incompatibilidades.
2. Os conceitos de injeção de dependência — já conhecidos do NestJS — mapearam diretamente
   para `@RestController` e `@Component` do Spring Boot, tornando a migração mais rápida
   do que parecia.
3. O Spring Boot entrega BCrypt, CORS e validação de DTO como funcionalidades nativas
   (starters), reduzindo o código de infraestrutura.

**Impacto:** a pasta `backend/` foi recriada com a estrutura Maven. Os scripts SQL do banco
não foram alterados. O frontend React não sofreu mudança: o contrato da API REST
(endpoints, verbos HTTP, formato JSON) foi mantido.

#### Mudança 2 — Hibernate/JPA → JdbcTemplate (SQL puro)

Por orientação do professor, a camada de persistência foi refatorada para usar
**`JdbcTemplate` com SQL escrito à mão**. O Hibernate e o Spring Data JPA foram removidos
do `pom.xml`. O objetivo didático é que o código SQL fique explícito e auditável.

Consequências diretas:

- Não há entidades `@Entity` nem repositórios `JpaRepository`.
- Soft delete e multi-tenancy são implementados diretamente nos WHERE/UPDATE das queries SQL.
- O schema não é mais sincronizado automaticamente: passa a ser criado executando os scripts
  de `database/` manualmente (ver seção acima).

#### Mudança 3 — Arquitetura em camadas → MVC clássico sem service

Por orientação do professor, a estrutura de pacotes foi reorganizada para **MVC clássico**:
`controller/` (REST), `model/` (POJOs e lógica de negócio — `MotorTributario`, `GeradorBoleto`),
`model/dao/` (acesso ao banco), `view/` (DTOs de resposta). A camada `service/` foi removida.
Infraestrutura transversal fica em `config/`, `exception/` e `enums/`.

Essa estrutura aproxima o código do padrão MVC estudado em aula, tornando a defesa mais
direta.

#### Mudança 4 — JWT/Spring Security → API aberta com BCrypt

Por orientação do professor, o Spring Security e o JWT foram removidos do projeto. A API
passou a ser **aberta** (sem filtro de autenticação em cada requisição). O cadastro e o
login continuam funcionando:

- Senha armazenada com hash BCrypt (`spring-security-crypto`, que permanece no `pom.xml`).
- Login válido retorna dados do usuário (HTTP 200); inválido retorna HTTP 401.
- E-mail duplicado no cadastro retorna HTTP 409.
- As dependências removidas do `pom.xml`: `spring-boot-starter-security`, `jjwt-*`,
  `spring-security-test`; adicionadas: `spring-boot-starter-jdbc`,
  `spring-security-crypto`.

---

_Verificado contra: `docs/00-plano-pi2.md`, `docs/07-consideracoes-finais.md`, `docs/06-plano-testes.md`, `database/`, `backend/pom.xml` e `backend/src/` — Spring Boot 3.3.0, Java 17, JdbcTemplate (sem Hibernate/JPA, sem JWT, sem Spring Security)._
_Última atualização: 2026-06-02._
