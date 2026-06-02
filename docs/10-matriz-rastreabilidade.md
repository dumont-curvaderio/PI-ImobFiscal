# Fase 6 — Matriz de Rastreabilidade de Requisitos

**Projeto:** ImobFiscal
**PI:** 2º Semestre DSM — FATEC Indaiatuba
**Data:** 2026-06-02

---

## 1. O que é rastreabilidade e por que ela importa

Rastreabilidade de requisitos é a capacidade de seguir um requisito desde sua origem até
sua realização completa: do documento de requisitos, passando pelo projeto (casos de uso e
modelo de classes), passando pela implementação (endpoint ou DAO do backend), até a
verificação (caso de teste automatizado ou validação manual).

Sem rastreabilidade, perguntas como "esse requisito foi implementado?" ou "se eu alterar
essa regra de negócio, quais testes precisam ser reexecutados?" exigem varredura manual
de todo o código. Com a matriz, a resposta está em uma tabela.

No contexto do PI 2, a matriz cumpre três papéis:

1. Comprova à banca que todo requisito levantado foi projetado e entregue.
2. Torna visíveis as lacunas de cobertura de testes — onde há apenas validação manual.
3. Serve de referência quando uma regra fiscal mudar (ex: alíquotas de 2027), pois
   indica exatamente quais artefatos são afetados.

---

## 2. Matriz de Rastreabilidade — Requisitos Funcionais

A tabela abaixo cruza cada Requisito Funcional com:

- o(s) Caso(s) de Uso que o realiza no modelo UML,
- o componente do backend que o implementa (Controller + DAO, ou classe de modelo), e
- o(s) Caso(s) de Teste que verificam o comportamento (automatizado ou manual).

> **Legenda de verificação:**
>
> - **TC-xx** = teste unitário automatizado (JUnit 5 + Mockito)
> - **Manual** = validação realizada via navegador / Postman durante o desenvolvimento
> - **–** = não aplicável para este requisito

| RF | Descrição resumida | Caso(s) de Uso | Implementação (backend) | Verificação |
| --- | --- | --- | --- | --- |
| **RF01** | Login com e-mail e senha | — | `POST /api/auth/login` → `AuthController`; senha verificada via BCrypt; API aberta (sem JWT) | Manual (Postman + tela de login) |
| **RF02** | Cadastrar Locador (nome, CPF/CNPJ, regime tributário) | UC04 — Cadastrar Dados do Cliente | `POST /api/imobiliarias/{id}/locadores` → `LocadorController` → `LocadorDao` | Manual |
| **RF03** | Listar Locadores | UC04 | `GET /api/imobiliarias/{id}/locadores` → `LocadorController` → `LocadorDao` | Manual |
| **RF04** | Editar Locador | UC04 | `PUT /api/imobiliarias/{id}/locadores/{locId}` → `LocadorController` → `LocadorDao` | Manual |
| **RF05** | Excluir Locador (soft delete) | UC04 | `DELETE /api/imobiliarias/{id}/locadores/{locId}` → `LocadorDao` (UPDATE com `deletedAt`) | Manual |
| **RF06** | Cadastrar Imóvel (endereço, tipo de uso, valor venal, locador) | UC01 — Cadastrar Imóvel | `POST /api/imobiliarias/{id}/imoveis` → `ImovelController` → `ImovelDao` | Manual |
| **RF07** | Listar Imóveis (filtro por locador) | UC01, UC02 | `GET /api/imobiliarias/{id}/imoveis` → `ImovelController` → `ImovelDao.listar()` | TC-01, TC-02 |
| **RF08** | Editar Imóvel | UC01 | `PUT /api/imobiliarias/{id}/imoveis/{imId}` → `ImovelController` → `ImovelDao` | Manual |
| **RF09** | Excluir Imóvel (soft delete) | UC01 | `DELETE /api/imobiliarias/{id}/imoveis/{imId}` → `ImovelDao.softDelete()` | TC-03 |
| **RF10** | Criar Contrato de Locação | UC05, UC07 — Gerar Contrato | `POST /api/imobiliarias/{id}/contratos` → `ContratoController` → `ContratoDao` | Manual |
| **RF11** | Listar Contratos ativos | UC07 | `GET /api/imobiliarias/{id}/contratos` → `ContratoController` → `ContratoDao` | Manual |
| **RF12** | Editar Contrato | UC07 | `PATCH /api/imobiliarias/{id}/contratos/{cId}/status` → `ContratoController` → `ContratoDao` | Manual |
| **RF13** | Encerrar Contrato (soft delete) | UC07 | `DELETE /api/imobiliarias/{id}/contratos/{cId}` → `ContratoDao` (UPDATE com `deletedAt`) | Manual |
| **RF14** | Calcular IBS e CBS automaticamente | UC09 — Calcular IBS/CBS | `POST /api/motor-tributario/calcular` → `MotorTributarioController` → `model/MotorTributario` → `aliquotas_vigentes` | TC-04, TC-05, TC-06 |
| **RF15** | Exibir resumo tributário vinculado ao Contrato | UC09, UC03 — Gerar Boleto | `POST /api/imobiliarias/{id}/boletos/gerar` → `BoletoController` → `model/GeradorBoleto` → `MotorTributario` | Manual (tela de boleto) |

---

## 3. Rastreabilidade de Requisitos Não Funcionais

| RNF | Descrição | Como é atendido na arquitetura/código | Como é verificado |
| --- | --- | --- | --- |
| **RNF01** | PostgreSQL como banco de dados relacional | Driver JDBC PostgreSQL declarado em `pom.xml`; `application.properties` aponta para o Railway PostgreSQL | Aplicação sobe e persiste dados (manual / deploy Railway) |
| **RNF02** | API REST com respostas em JSON | Todos os controllers usam `@RestController`; Spring serializa retornos com Jackson | Manual (Postman — Content-Type: application/json em toda resposta) |
| **RNF03** | Senhas com hash BCrypt | `AuthController` chama `BCryptPasswordEncoder.encode()` (via `spring-security-crypto`) antes de persistir; nenhum Spring Security Filter envolvido | Manual (verificar coluna `senha` no banco — valor começa com `$2a$`) |
| **RNF04** | API aberta — login inválido retorna 401 | Sem JWT nem filtro de segurança; `AuthController` verifica BCrypt e retorna 401 explicitamente se credenciais incorretas; e-mail duplicado retorna 409 | Manual (Postman — credenciais erradas retornam 401; e-mail duplicado retorna 409) |
| **RNF05** | Frontend funciona em Chrome, Firefox e Edge | React/Vite gera bundle compatível com ES2015+; sem uso de APIs exclusivas de um browser | Manual (teste visual nos três browsers) |
| **RNF06** | Backend com Spring Boot e Java | `pom.xml` usa `spring-boot-starter-parent 3.3.0`, Java 17 (`<java.version>17</java.version>`) | Build Maven local + deploy Railway |
| **RNF07** | Frontend com React, JavaScript e Vite | `package.json` declara `react`, `react-dom` e `vite`; projeto em JavaScript puro (sem TypeScript) | Build Vite local + deploy Vercel |

---

## 4. Rastreabilidade de Regras de Negócio

| RN | Descrição | Onde está implementada |
| --- | --- | --- |
| **RN01** | Imóvel só pode ser cadastrado vinculado a um Locador existente | `ImovelDao.criar()`: busca o locador por ID via SQL; se não encontrado, lança exceção "Locador não encontrado". Reforçada pela FK `imoveis.locador_id REFERENCES locadores(id)` no banco |
| **RN02** | Contrato só pode ser criado vinculado a Locador e Imóvel existentes | `ContratoDao.criar()`: valida `imovel_id` existente via SQL; locador é obtido transitivamente via `imovel → locador`. FK `contratos_locacao.imovel_id REFERENCES imoveis(id)` no banco |
| **RN03** | Registros excluídos não aparecem nas listagens | Todos os métodos de listagem nos DAOs aplicam `WHERE deleted_at IS NULL` no SQL. Verificado explicitamente pelo TC-03 (`ImovelDaoTest.softDelete`) |
| **RN04** | Cálculo de IBS e CBS sobre o valor do aluguel do Contrato | `model/MotorTributario.calcular()`: recebe `valorBase` = `valorAluguel` do contrato; busca alíquota em `aliquotas_vigentes` por regime + tipo + ano via SQL; aplica `valorIbs = valorBase × aliquotaIbs` e `valorCbs = valorBase × aliquotaCbs` com `HALF_UP`. Verificado pelos TC-04, TC-05, TC-06 |
| **RN05** | O resultado tributário é simulação — não emite NF real | `NotaFiscal` persiste os valores calculados mas o campo `recolhimento_obrigatorio = false` em 2026; não há integração com SEFAZ — documentado em `01-escopo.md` como item fora do escopo |

---

## 5. Análise de Cobertura

### Cobertura por tipo de verificação

| Categoria | Requisitos cobertos | Forma de verificação |
| --- | --- | --- |
| Teste automatizado (JUnit 5 + Mockito) | RF07 (listar/buscar), RF09 (soft delete), RF14 (cálculo fiscal) | TC-01 a TC-06 |
| Validação manual (Postman + navegador) | RF01 a RF06, RF08, RF10 a RF13, RF15 | Execução manual durante desenvolvimento |

### O que TC-01 a TC-06 cobrem exatamente

| Caso de Teste | Arquivo | Método testado | Requisito verificado |
| --- | --- | --- | --- |
| TC-01 | `ImovelDaoTest` | `ImovelDao.listar()` | RF07 — listagem retorna apenas ativos (RN03) |
| TC-02 | `ImovelDaoTest` | `ImovelDao.buscarPorId()` — ID inexistente | RF07 — lança exceção "Imóvel não encontrado" |
| TC-03 | `ImovelDaoTest` | `ImovelDao.softDelete()` | RF09 + RN03 — usa UPDATE (nunca DELETE físico) |
| TC-04 | `MotorTributarioTest` | `MotorTributario.calcular()` — caminho feliz | RF14 + RN04 — IBS R$ 29,00 / CBS R$ 15,20 / líquido R$ 1.955,80 sobre R$ 2.000,00 |
| TC-05 | `MotorTributarioTest` | `MotorTributario.calcular()` — alíquota ausente | RF14 — lança exceção quando alíquota não encontrada |
| TC-06 | `MotorTributarioTest` | `MotorTributario.calcular()` — alíquota zero | RF14 — isenção total; líquido igual ao valor bruto |

### Lacunas conhecidas de cobertura automatizada

As lacunas abaixo são conhecidas e documentadas — não são surpresas na entrega:

| Área sem teste automatizado | Justificativa registrada em `06-plano-testes.md` |
| --- | --- |
| `LocadorDao`, `ContratoDao` | Fora do escopo desta fase; padrão idêntico ao `ImovelDao` |
| `AuthController` / BCrypt (RF01, RNF03) | Verificação manual via Postman; testes unitários de auth não implementados no PI 2 |
| Camada de Controller (todos os RFs) | Exige contexto HTTP (`@SpringBootTest` ou `MockMvc`); fora do escopo desta fase |
| Frontend React (todos os RFs) | Ferramentas E2E (Selenium, Playwright) fora do escopo do PI 2 |

---

_Verificado contra: `docs/02-requisitos.md`, `docs/03-casos-de-uso.md`, `docs/06-plano-testes.md` e código em `backend/` — Spring Boot 3.3.0, Java 17, JdbcTemplate (sem Hibernate/JPA, sem JWT)._
_Última atualização: 2026-06-02._
