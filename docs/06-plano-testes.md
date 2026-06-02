# Fase 5 — Plano de Testes

**Projeto:** ImobFiscal
**PI:** 2º Semestre DSM — FATEC Indaiatuba
**Data:** 2026-06-02

---

## 1. Objetivo

Descrever a estratégia de testes adotada no PI 2, identificar o escopo coberto nesta fase
e documentar os casos de teste implementados para `MotorTributario` e `ImovelDao`.

---

## 2. Escopo dos Testes

### Dentro do escopo

- Testes unitários de `MotorTributario` (cálculo fiscal — classe em `model/`).
- Testes unitários de `ImovelDao` (acesso ao banco — classe em `model/dao/`).
- Verificação de soft delete, exceções e cálculo IBS/CBS.
- Validação manual da integração entre frontend React e a API REST.

### Fora do escopo nesta fase

- Testes automatizados de controller (camada HTTP).
- Testes de integração com o banco de dados PostgreSQL real.
- Testes end-to-end (E2E) automatizados.
- Testes de carga ou desempenho.
- Testes automatizados de frontend.

---

## 3. Tipos de Teste Utilizados

| Tipo | Ferramenta | Camada | Aplicação |
| --- | --- | --- | --- |
| Unitário | JUnit 5 + Mockito | Backend — model / dao | Isola `MotorTributario` e `ImovelDao` simulando dependências com mocks |
| Validação manual | Navegador + Postman | Frontend + API | Verifica o fluxo completo CRUD visualmente |

Os testes unitários são executados sem subir o contexto Spring (`@ExtendWith(MockitoExtension.class)`),
garantindo execução rápida e isolada de dependências externas como banco de dados.
`mvn test` executa os 5 testes e todos passam (verde).

---

## 4. Funcionalidades Testadas

> Legenda de status: **Implementado** = código de teste existe em `src/test/` e é executado com `mvn test`. **Planejado** = caso descrito neste documento, ainda sem implementação no repositório.

| Funcionalidade | Tipo | Caso de Teste | Requisito | Status |
| --- | --- | --- | --- | --- |
| Listar imóveis da imobiliária | Unitário | `deveListarImoveisDaImobiliaria` | RF07 | Implementado |
| Buscar imóvel por ID inexistente | Unitário | `deveLancarExcecao_quandoImovelNaoEncontrado` | RF07 | Implementado |
| Excluir imóvel com soft delete | Unitário | `deveSoftDeletarImovel_usandoUpdateNaoDelete` | RF09 | Implementado |
| Motor tributário — cálculo IBS/CBS correto | Unitário | `deveCalcularIbsCbs_comAliquotaVigente` | RF14 | Implementado |
| Motor tributário — alíquota não encontrada | Unitário | `deveLancarExcecao_quandoAliquotaNaoEncontrada` | RF14 | Implementado |
| Motor tributário — alíquota zero (isenção) | Unitário | `deveRetornarIsencao_quandoAliquotaZero` | RF14 | Implementado |
| Login com credenciais válidas — retorna dados | Unitário | `deveRetornarDadosUsuario_quandoCredenciaisValidas` | RF01 | Planejado |
| Login com credenciais inválidas — rejeita | Unitário | `deveLancarExcecao_quandoCredenciaisInvalidas` | RF01 | Planejado |
| Cadastro com e-mail duplicado | Unitário | `deveLancarExcecao_quandoEmailJaCadastrado` | RF01 | Planejado |
| Criar locador PF | Unitário | `deveCriarLocador_quandoDadosValidos` | RF02 | Planejado |
| Soft delete de locador | Unitário | `deveDeletarLocadorComSoftDelete` | RF05 | Planejado |
| Criar contrato — status inicial RASCUNHO | Unitário | `deveCriarContrato_comStatusRascunho` | RF10 | Planejado |
| Atualizar status do contrato via PATCH | Unitário | `deveAtualizarStatusContrato` | RF10 | Planejado |

---

## 5. Casos de Teste — MotorTributario

> Status: **Implementado** — arquivo `MotorTributarioTest.java`.
>
> Os valores abaixo são calculados com base na fórmula: `valorIbs = valorBase × aliquotaIbs`,
> `valorCbs = valorBase × aliquotaCbs`, `valorLiquido = valorBase − valorIbs − valorCbs`,
> arredondado com `RoundingMode.HALF_UP`.
> Mock utilizado: `JdbcTemplate` / método de consulta do `MotorTributario` para `aliquotas_vigentes`.

| ID | Nome do método | Arrange | Act | Assert esperado |
| --- | --- | --- | --- | --- |
| TC-04 | `deveCalcularIbsCbs_comAliquotaVigente` | Mock retorna `AliquotaVigente` com `aliquotaIbs = 0.0145` e `aliquotaCbs = 0.0076` | Chama `motorTributario.calcular(valorBase=2000.00, regime="PF", tipo="RESIDENCIAL")` | `valorIbs = 29.00`, `valorCbs = 15.20`, `valorLiquido = 1955.80` |
| TC-05 | `deveLancarExcecao_quandoAliquotaNaoEncontrada` | Mock retorna vazio para regime/tipo/ano sem correspondência | Chama `motorTributario.calcular(request)` com regime e tipo sem alíquota cadastrada | Lança `RuntimeException` com mensagem contendo "Alíquota não encontrada para" |
| TC-06 | `deveRetornarIsencao_quandoAliquotaZero` | Mock retorna `AliquotaVigente` com `aliquotaIbs = 0.0` e `aliquotaCbs = 0.0` | Chama `motorTributario.calcular(valorBase=2000.00, ...)` | `valorIbs = 0.00`, `valorCbs = 0.00`, `valorLiquido = 2000.00` (isenção total) |

---

## 6. Casos de Teste — ImovelDao

> Status: **Implementado** — arquivo `ImovelDaoTest.java`.
> Mock utilizado: `JdbcTemplate` (injetado via Mockito no `ImovelDao`).

| ID | Nome do método | Arrange | Act | Assert esperado |
| --- | --- | --- | --- | --- |
| TC-01 | `deveListarImoveisDaImobiliaria` | Mock de `JdbcTemplate.query(...)` retorna lista com dois imóveis | Chama `imovelDao.listar(imobiliariaId)` | Retorna lista com os dois imóveis; `query()` invocado exatamente uma vez |
| TC-02 | `deveLancarExcecao_quandoImovelNaoEncontrado` | Mock de `JdbcTemplate.queryForObject(...)` retorna vazio / lança `EmptyResultDataAccessException` | Chama `imovelDao.buscarPorId(imobiliariaId, idInexistente)` | Lança exceção com mensagem "Imóvel não encontrado" |
| TC-03 | `deveSoftDeletarImovel_usandoUpdateNaoDelete` | Mock de `JdbcTemplate.update(...)` configurado | Chama `imovelDao.softDelete(imobiliariaId, imovelId)` | `JdbcTemplate.update()` é invocado com SQL contendo `SET deleted_at`; nenhum `DELETE` físico é executado |

---

## 7. Critérios de Aceite

**TC-01** — aprovado quando `listar()` retorna exatamente os imóveis fornecidos pelo mock
e o método de consulta no `JdbcTemplate` é chamado o número de vezes esperado.

**TC-02** — aprovado quando `buscarPorId()` lança a exceção mapeada pelo projeto para
recurso não encontrado, com mensagem "Imóvel não encontrado".

**TC-03** — aprovado quando, após chamar `softDelete()`:

- `JdbcTemplate.update()` é chamado com instrução SQL de UPDATE (contendo `deleted_at`);
- nenhuma instrução de remoção física (`DELETE`) é executada.

**TC-04** — aprovado quando `calcular()` retorna `valorIbs = 29.00`, `valorCbs = 15.20`
e `valorLiquido = 1955.80` para `valorBase = 2000.00` com as alíquotas do mock.

**TC-05** — aprovado quando `calcular()` lança `RuntimeException` contendo a mensagem
"Alíquota não encontrada para" ao receber regime/tipo sem registro em `aliquotas_vigentes`.

**TC-06** — aprovado quando `calcular()` retorna `valorLiquido = 2000.00` (valor integral,
isenção total) para alíquotas IBS = 0.0 e CBS = 0.0.

---

## 8. Casos de Teste Planejados — AuthController

> Status: **Planejado** — não implementados. Requerem mock de `UsuarioDao` e `BCryptPasswordEncoder`.

| ID | Nome do método | Pré-condição | Arrange | Act | Assert esperado | Requisito |
| --- | --- | --- | --- | --- | --- | --- |
| TC-07 | `deveRetornarDadosUsuario_quandoCredenciaisValidas` | Usuário cadastrado no mock | Mock de `UsuarioDao.findByEmail()` retorna o usuário; `BCryptPasswordEncoder.matches()` retorna `true` | Chama `authController.login(new LoginRequest("user@email.com", "senha123"))` | Retorna HTTP 200 com `email` e `perfil` preenchidos; nenhum token JWT gerado | RF01 |
| TC-08 | `deveLancarExcecao_quandoCredenciaisInvalidas` | Credenciais incorretas | Mock de `BCryptPasswordEncoder.matches()` retorna `false` | Chama `authController.login(new LoginRequest("user@email.com", "senha-errada"))` | Retorna HTTP 401; nenhum dado de usuário exposto | RF01 |
| TC-09 | `deveLancarExcecao_quandoEmailJaCadastrado` | E-mail já existe no banco | Mock de `UsuarioDao.existsByEmail("existente@email.com")` retorna `true` | Chama `authController.cadastrar(new CadastroRequest("existente@email.com", ...))` | Retorna HTTP 409; `UsuarioDao.save()` nunca chamado | RF01 |

---

## 9. Casos de Teste Planejados — LocadorDao

> Status: **Planejado** — não implementados. Requerem mock de `JdbcTemplate`.

| ID | Nome do método | Pré-condição | Arrange | Act | Assert esperado | Requisito |
| --- | --- | --- | --- | --- | --- | --- |
| TC-10 | `deveCriarLocador_quandoDadosValidos` | Nenhum | Mock de `JdbcTemplate.update()` configurado; `LocadorRequest` com `tipoPessoa = PF`, CPF válido, nome, e-mail, telefone e `regimeTributario` | Chama `locadorDao.criar(imobiliariaId, request)` | `update()` invocado uma vez com INSERT; retorna `LocadorResponse` com `deletedAt` nulo | RF02 |
| TC-11 | `deveDeletarLocadorComSoftDelete` | Locador ativo existe no mock | Mock de `JdbcTemplate.update()` configurado para UPDATE | Chama `locadorDao.softDelete(imobiliariaId, id)` | SQL de UPDATE com `SET deleted_at` executado; nenhum DELETE físico | RF05 |

---

## 10. Casos de Teste Planejados — ContratoDao

> Status: **Planejado** — não implementados. Requerem mock de `JdbcTemplate`.

| ID | Nome do método | Pré-condição | Arrange | Act | Assert esperado | Requisito |
| --- | --- | --- | --- | --- | --- | --- |
| TC-12 | `deveCriarContrato_comStatusRascunho` | Imobiliária e imóvel existem nos mocks | Mock de `JdbcTemplate.update()` configurado; `ContratoRequest` com `valorAluguel = 2000.00`, datas e demais campos | Chama `contratoDao.criar(imobiliariaId, request)` | INSERT executado; `ContratoResponse` retornado com `status = RASCUNHO` | RF10 |
| TC-13 | `deveAtualizarStatusContrato` | Contrato em `RASCUNHO` existe no mock | Mock de `JdbcTemplate.update()` configurado para UPDATE de status | Chama `contratoDao.atualizarStatus(imobiliariaId, id, StatusContrato.ATIVO)` | UPDATE executado; `ContratoResponse` com `status = ATIVO` | RF10 |

---

## 11. O que NÃO é testado nesta fase

| Item fora do escopo | Justificativa |
| --- | --- |
| Testes de controller (`ImovelController`, `AuthController`, etc.) | Exigem contexto HTTP (`MockMvc`); escopo desta fase cobre apenas model e dao |
| Testes de integração com banco real | Requerem configuração de banco em memória ou container; previsto para fases futuras |
| Testes E2E automatizados | Ferramentas como Selenium ou Playwright não estão no escopo do PI 2 |
| Testes de desempenho e carga | Fora da proposta acadêmica deste semestre |
| Testes automatizados de frontend React | Validação do frontend é realizada manualmente nesta fase |
| Soft delete de `usuarios` | A entidade `usuarios` não implementa exclusão lógica; não há caso de teste aplicável |
| `GeradorBoleto` | Depende de integração entre múltiplas classes; testes unitários isolados exigiriam mocks encadeados de complexidade elevada; candidato para fase seguinte |

---

## 12. Rastreamento Requisito × Teste

| Requisito | Testes implementados | Testes planejados |
| --- | --- | --- |
| RF01 — Login / Cadastro | — | TC-07, TC-08, TC-09 |
| RF02 — Cadastrar Locador | — | TC-10 |
| RF05 — Soft delete Locador | — | TC-11 |
| RF07 — Listar / Buscar Imóvel | TC-01, TC-02 | — |
| RF09 — Soft delete Imóvel | TC-03 | — |
| RF10 — Criar / Atualizar Contrato | — | TC-12, TC-13 |
| RF14 — Cálculo IBS/CBS | TC-04, TC-05, TC-06 | — |

---

_Verificado contra: Spring Boot 3.3 / Java 17 / JUnit 5 / Mockito — PI 2 FATEC DSM 2026-2_
_Última atualização: 2026-06-02._
