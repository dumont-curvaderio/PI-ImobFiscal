# Fase 5 — Plano de Testes

**Projeto:** ImobFiscal  
**PI:** 2º Semestre DSM — FATEC Indaiatuba  
**Data:** 2026-06-01

---

## 1. Objetivo

Descrever a estratégia de testes adotada no PI 2, identificar o escopo coberto nesta fase
e documentar os casos de teste implementados para a camada de serviço da entidade `Imovel`.

---

## 2. Escopo dos Testes

### Dentro do escopo

- Testes unitários da classe `ImovelService` (camada de negócio do backend Spring Boot).
- Verificação do comportamento de listagem, busca, lançamento de exceção e soft delete.
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
|---|---|---|---|
| Unitário | JUnit 5 + Mockito | Backend — Service | Isola `ImovelService` simulando o repositório com mocks |
| Validação manual | Navegador + Postman | Frontend + API | Verifica o fluxo completo CRUD visualmente |

Os testes unitários são executados sem subir o contexto Spring (`@ExtendWith(MockitoExtension.class)`),
garantindo execução rápida e isolada de dependências externas como banco de dados.

---

## 4. Funcionalidades Testadas

| Funcionalidade | Tipo | Caso de Teste | Requisito Relacionado |
|---|---|---|---|
| Listar imóveis da imobiliária | Unitário | `deveListarImoveisDaImobiliaria` | RF07 |
| Buscar imóvel por ID | Unitário | `deveBuscarImovelPorId_quandoExiste` | RF07 |
| Buscar imóvel por ID inexistente | Unitário | `deveLancarExcecao_quandoImovelNaoEncontrado` | RF07 |
| Excluir imóvel com soft delete | Unitário | `deveDeletarImovelComSoftDelete` | RF09 |

---

## 5. Casos de Teste — ImovelService

| ID | Nome do método | Arrange | Act | Assert esperado |
|---|---|---|---|---|
| TC-01 | `deveListarImoveisDaImobiliaria` | Mock de `ImovelRepository.findAll()` retorna lista com dois imóveis | Chama `imovelService.listar()` | Retorna lista com os dois imóveis; `findAll()` é invocado exatamente uma vez |
| TC-02 | `deveBuscarImovelPorId_quandoExiste` | Mock de `ImovelRepository.findById(1L)` retorna `Optional` com um imóvel preenchido | Chama `imovelService.buscarPorId(1L)` | Retorna o imóvel correspondente sem lançar exceção |
| TC-03 | `deveLancarExcecao_quandoImovelNaoEncontrado` | Mock de `ImovelRepository.findById(99L)` retorna `Optional.empty()` | Chama `imovelService.buscarPorId(99L)` | Lança `ResourceNotFoundException` (ou equivalente do projeto) com mensagem indicando ID não encontrado |
| TC-04 | `deveDeletarImovelComSoftDelete` | Mock de `ImovelRepository.findById(1L)` retorna imóvel com `deletedAt` nulo; mock de `save()` configurado | Chama `imovelService.deletar(1L)` | O campo `deletedAt` do imóvel é preenchido com data/hora atual; `ImovelRepository.save()` é invocado com o objeto atualizado; nenhum `delete()` do repositório é chamado |

---

## 6. Critérios de Aceite

- **TC-01** — aprovado quando `listar()` retorna exatamente os imóveis fornecidos pelo mock
  e o método de busca no repositório é chamado o número de vezes esperado.

- **TC-02** — aprovado quando `buscarPorId()` retorna o objeto correto para um ID existente,
  sem lançar nenhuma exceção.

- **TC-03** — aprovado quando `buscarPorId()` lança a exceção mapeada pelo projeto para
  recurso não encontrado, com ID 99 como entrada.

- **TC-04** — aprovado quando, após chamar `deletar()`:
  - o campo `deletedAt` do imóvel **não é nulo**;
  - `ImovelRepository.save()` é chamado com o objeto modificado;
  - nenhum método de remoção física (`delete`, `deleteById`) é invocado no repositório.

---

## 7. O que NÃO é testado nesta fase

| Item fora do escopo | Justificativa |
|---|---|
| Testes de controller (`ImovelController`) | Exigem contexto HTTP; escopo desta fase cobre apenas a camada de serviço |
| Testes de integração com banco real | Requerem configuração de banco em memória ou container; previsto para fases futuras |
| Testes E2E automatizados | Ferramentas como Selenium ou Playwright não estão no escopo do PI 2 |
| Testes de desempenho e carga | Fora da proposta acadêmica deste semestre |
| Testes automatizados de frontend React | Validação do frontend é realizada manualmente nesta fase |
| Soft delete de `usuarios` | A entidade `usuarios` não implementa exclusão lógica; não há caso de teste aplicável |

---

_Verificado contra: Spring Boot 3.3 / Java 17 / JUnit 5 / Mockito — PI 2 FATEC DSM 2026-2_
