# Considerações Finais — PI 2

**Projeto:** ImobFiscal  
**PI:** 2º Semestre DSM — FATEC Indaiatuba  
**Data:** 2026-06-01

---

## 1. Resumo do que foi desenvolvido

O ImobFiscal é um sistema web de gestão imobiliária com persistência de dados em banco
relacional. O sistema permite que uma imobiliária cadastre imóveis, registre locadores e
gerencie contratos de locação, com cálculo automático dos tributos IBS e CBS previstos na
Reforma Tributária brasileira (LC 214/2025).

Ao longo do semestre foram desenvolvidos e entregues:

| Fase | O que foi feito |
|---|---|
| 1 | Definição da ideia, escopo e abertura do repositório público no GitHub |
| 2 | Levantamento de requisitos funcionais e não funcionais, casos de uso, diagrama de classes e diagrama de sequência |
| 3 | Modelagem do banco de dados: `schema.sql`, `seed.sql`, DER e dicionário de dados |
| 4 | Desenvolvimento completo — backend Spring Boot em MVC clássico, com SQL puro (JdbcTemplate) e API aberta, e frontend React (login, cadastro, listagem, edição e detalhe) |
| 5 | Plano de testes e 5 testes unitários com JUnit 5 + Mockito cobrindo `MotorTributario` e `ImovelDao` |
| 6 | Revisão e finalização do README, considerações finais e preparação da apresentação |

---

## 2. Tecnologias utilizadas e o que foi aprendido em cada uma

### React + Vite (Frontend)

O frontend foi construído com React e JavaScript usando o empacotador Vite. O aprendizado
principal foi a criação de componentes reutilizáveis, o gerenciamento de estado com
`useState`, o consumo de API REST com `fetch` e o controle de rotas com React Router.

A tela de listagem de imóveis, por exemplo, combina todos esses conceitos: ao carregar, ela
chama a API, armazena os dados no estado e renderiza um cartão por imóvel.

### Spring Boot + Java (Backend)

O backend foi migrado de Node.js/NestJS para Spring Boot durante o desenvolvimento, o que
representou um aprendizado significativo. Posteriormente, por orientação do professor, o
backend ainda passou por uma refatoração importante: saiu o ORM (JPA/Hibernate) e entrou
**SQL puro com JdbcTemplate**, o código foi reorganizado em **MVC clássico** (`model` /
`view` / `controller`, sem camada de service) e a autenticação por **JWT foi removida**
(a API ficou aberta). Os conceitos trabalhados foram:

- **Injeção de dependência** com `@RestController`, `@Component` e `@Repository`
- **SQL puro com JdbcTemplate** — escrever os `SELECT`/`INSERT`/`UPDATE` na mão, com
  `RowMapper` mapeando colunas para objetos (em vez de deixar o Hibernate gerar o SQL)
- **MVC clássico** — POJOs de domínio no `model`, DAOs em `model/dao`, DTOs no `view`
- **BCrypt** para guardar a senha em hash (sem Spring Security; a API é aberta)
- **Soft delete** com o campo `deletedAt` (um `UPDATE`, nunca `DELETE`) para cumprir a
  exigência de guarda fiscal de 5 anos

### PostgreSQL (Banco de Dados)

O banco foi modelado com chaves primárias UUID, chaves estrangeiras com restrições de
integridade, colunas `created_at` e `updated_at` com `DEFAULT NOW()` e índices nas
colunas mais consultadas. O script `schema.sql` representa o estado final do banco e
pode ser usado para recriar todo o ambiente do zero.

### JUnit 5 + Mockito (Testes)

Os cinco testes unitários (`MotorTributarioTest` e `ImovelDaoTest`) foram escritos com a
abordagem Arrange–Act–Assert. O Mockito foi usado para simular o `JdbcTemplate` e o DAO,
permitindo testar a lógica sem depender de um banco real. Destaques: o `MotorTributarioTest`
confere o cálculo de IBS/CBS (R$ 2.000,00 → IBS R$ 29,00, CBS R$ 15,20, líquido R$ 1.955,80),
o caso de alíquota inexistente e o caso de isenção (alíquota zero). O `ImovelDaoTest` verifica
que o soft delete executa um `UPDATE` e **nunca** um `DELETE` físico — garantindo a regra de
guarda fiscal.

---

## 3. Dificuldades encontradas e como foram superadas

**Migração de backend (NestJS → Spring Boot)**

A maior dificuldade do semestre foi a decisão de trocar o backend de NestJS (Node.js)
para Spring Boot (Java) a partir da Fase 4. O NestJS já tinha sido iniciado mas
apresentou problemas de compatibilidade com as bibliotecas de segurança escolhidas.
A solução foi refazer o backend em Spring Boot, aproveitando os conceitos de injeção
de dependência e camada de serviço que já eram familiares do NestJS.

**Configuração do CORS**

A integração entre o frontend rodando na porta 5173 e o backend na porta 8080 exigiu
configuração explícita de CORS no Spring Boot (`@CrossOrigin` e `CorsConfigurationSource`).
Sem isso, o navegador bloqueava todas as requisições do frontend para a API.

**Refatorar para SQL puro e remover o JWT**

Durante o projeto chegamos a implementar JPA/Hibernate e autenticação JWT com Spring
Security. Depois, por orientação do professor, refizemos essa parte: trocamos o ORM por
SQL escrito à mão (JdbcTemplate) e removemos o JWT, deixando a API aberta. O aprendizado
foi entender o que cada camada realmente faz — ao escrever o SQL na mão (com `WHERE
imobiliaria_id = ? AND deleted_at IS NULL`), ficou muito mais claro como o multi-tenancy
e o soft delete acontecem, algo que o Hibernate antes escondia.

---

## 4. O que o sistema faz (funcionalidades entregues)

- **Login e cadastro** de usuário com verificação de senha por BCrypt (sem JWT; a API é aberta)
- **Listagem de imóveis** com filtro por imobiliária e paginação visual
- **Cadastro de novo imóvel** com formulário validado no frontend e backend
- **Edição de imóvel** com carregamento dos dados existentes
- **Exclusão lógica** (soft delete) — o imóvel some da listagem mas fica no banco
- **Cálculo de IBS/CBS** exibido na tela de detalhe do imóvel
- **Banco relacional** com schema versionado e dados de exemplo

---

## 5. Limitações e o que ficou de fora do escopo

Por ser um trabalho acadêmico de um único semestre, algumas funcionalidades foram
definidas como fora do escopo:

- Geração de PDF da Nota Fiscal — previsto como evolução futura
- Integração real com a SEFAZ para emissão de NF-e
- Testes automatizados de interface (E2E)
- Deploy em produção com domínio público

Essas limitações estão documentadas no [Plano de Testes](06-plano-testes.md) e no
[Escopo do Sistema](01-escopo.md).

---

## 6. Relação com as disciplinas do semestre

| Disciplina | Como foi aplicada no PI |
|---|---|
| **Engenharia de Software II** | Levantamento de requisitos, diagramas UML, plano de testes, documentação técnica |
| **Desenvolvimento Web II** | Frontend React com componentes, estado, rotas e consumo de API |
| **Banco de Dados Relacional** | Modelagem ERD, DDL, DML, constraints, índices e relacionamentos |

---

## 7. Reflexão pessoal

Este projeto foi a primeira experiência completa de desenvolvimento full-stack: desde a
concepção da ideia até um sistema funcionando com banco de dados, backend e frontend
integrados. O maior aprendizado não foi uma tecnologia específica, mas a percepção de
como as camadas se conectam — um formulário no React faz uma chamada HTTP, o Spring Boot
executa a regra de negócio e persiste no PostgreSQL (com SQL escrito à mão), e a resposta
JSON volta para atualizar a tela.

A migração forçada de backend no meio do semestre foi estressante, mas ensinou algo
importante: entender os conceitos (injeção de dependência, repositório, serviço) é mais
valioso do que dominar uma ferramenta específica, porque o mesmo padrão apareceu no
NestJS e no Spring Boot.

---

_FATEC Indaiatuba — Dr. Archimedes Lammoglia · 2026_
