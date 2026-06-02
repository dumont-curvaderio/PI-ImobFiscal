# Plano de Desenvolvimento — PI 2 FATEC DSM (ImobFiscal)

**Decisão consolidada — 2026-06-01 · Semana 1 de 15**

---

## 1. Decisão de Escopo

### O que é este projeto para o PI 2

Sistema web de gestão imobiliária com CRUD de Locadores, Imóveis e Contratos de Locação,
autenticação de usuário, cálculo básico de IBS/CBS (Reforma Tributária) e persistência em
PostgreSQL. Apresentado como **"ImobFiscal — Sistema de Gestão Imobiliária"**.

### Stack tecnológica (definida e fixada)

| Camada | Tecnologia |
|---|---|
| Frontend | React + JavaScript (Vite) |
| Backend | Spring Boot + Java |
| Banco de dados | PostgreSQL |
| Autenticação | BCrypt (sem JWT; API aberta) |
| Deploy frontend | Vercel |
| Deploy backend | Railway |

### Estrutura de pastas no repositório

```
imobfiscal/
├── frontend/          ← React + JavaScript (Vite)
├── backend/           ← Spring Boot 3.3 + Java 17 + Maven
├── database/          ← schema.sql, V2__motor_fiscal.sql, seed.sql, README com DER Mermaid
├── docs/              ← documentação técnica do PI
└── README.md          ← template FATEC preenchido
```

### O que está FORA do escopo do PI 2 (não implementar)

- Integração real com SEFAZ / emissão de NF-e
- Boleto bancário real (simular com flag no banco)
- Módulo mobile (Expo)
- Multi-tenancy avançado (RLS)
- Geração de PDF de contrato

> Regra de ouro: se uma tarefa não aparece no checklist da seção 5, **não faça**.

### Entidades (conforme Diagrama de Classes do professor)

| Classe | Atributos principais |
|---|---|
| Locador | id, nome, cpf, cnpj, regime_tributario (PF/PJ/Simples) |
| Imovel | id, endereco, tipo_uso (Residencial/Comercial), valor_venal, locador_id |
| ContratoLocacao | id, valor_aluguel, data_inicio, dia_vencimento, locador_id, locatario_nome |
| NotaFiscal | id, data_emissao, valor_bruto, valor_liquido, aliquota_ibs, aliquota_cbs, contrato_id |

### Decisões técnicas fixas (não mudar no meio do semestre)

- **Spring Boot 3.3 + SQL puro (JdbcTemplate, sem ORM)** — persistência via DAOs com SQL escrito à mão; sem Hibernate/JPA
- **MVC clássico** — pacotes `controller/`, `model/` (POJOs + regras), `model/dao/` (DAOs), `view/` (DTOs), `config/`, `exception/`, `enums/`; sem camada de service
- **Soft delete** (`deleted_at`) em todas as tabelas com histórico
- **API aberta (sem JWT)** — login/cadastro conferem senha via BCrypt (`spring-security-crypto`); o token devolvido é apenas um marcador de sessão, não um JWT
- **Schema criado manualmente** — rodar os scripts `database/schema.sql`, depois `database/V2__motor_fiscal.sql`, depois `database/seed.sql`; não há criação automática por ORM
- **DER em Mermaid** dentro de `database/README.md` (renderiza no GitHub)
- **Railway PostgreSQL** em produção (URL injetada como variável de ambiente no Railway)

> **Nota de mudanças de decisão (orientadas pelo professor de Engenharia de Software):**
>
> 1. **NestJS/Express → Spring Boot / Java 17** (semanas 7–8): o currículo do curso tem maior
>    suporte a Java e a banca avalia melhor a camada backend em Java.
> 2. **Hibernate/JPA → SQL puro com JdbcTemplate** (refatoração posterior): o professor orientou
>    o uso de SQL escrito à mão para que os alunos aprendessem persistência sem abstrações de ORM.
> 3. **MVC clássico sem camada de service**: o professor orientou manter a estrutura MVC direta
>    (controller → model/dao → view), sem introduzir uma camada de service.
> 4. **Remoção de JWT / Spring Security**: o professor orientou simplificar a autenticação —
>    a API ficou aberta; o login usa BCrypt (`spring-security-crypto`) e devolve apenas um marcador
>    de sessão. A variável `JWT_SECRET` foi removida.
>
> Todas as mudanças estão registradas em `docs/07-consideracoes-finais.md`.

---

## 2. Cronograma Semanal (Semanas 1 → 15)

| Sem | Foco | Entregáveis concretos |
|-----|------|-----------------------|
| **1** ✅ | Fundação | Repo GitHub público + estrutura de pastas + README.md |
| **2** | Escopo | `docs/01-escopo.md` |
| **4** | Requisitos + UML | `docs/02-requisitos.md` + `docs/03-casos-de-uso.md` + `docs/04-diagrama-classes.md` |
| **6** | Banco completo | `database/schema.sql` + `database/seed.sql` + `database/README.md` (DER) + `docs/05-dicionario-dados.md` |
| **7** | Backend pt.1 | Setup Spring Boot + JdbcTemplate + conexão PostgreSQL + CRUD Locador |
| **8** | Backend pt.2 | CRUD Imovel + ContratoLocacao + Auth BCrypt (sem JWT) |
| **9** | Frontend base | Setup Vite/React + componentes base + login |
| **10** | Frontend Locador + Imovel | Telas: listagem, criar, editar, excluir |
| **11** | Frontend Contrato + integração | Telas de contrato + lib/api.js integrada ao backend |
| **12** | Testes + documentação | `docs/06-plano-testes.md` + 3 testes funcionais documentados |
| **13** | 🛡️ Buffer | Corrigir bugs, garantir CRUD ponta-a-ponta funcionando |
| **14** | Entrega final | README completo + `docs/07-consideracoes-finais.md` + checklist |
| **15** | Pitch | Demo ao vivo + diagramas + slide "o que aprendi" |

---

## 3. Ordem de Implementação Técnica (sem bloqueios)

```
PASSO 1 — Banco (sem 6):
  1.1  Definir schema no PostgreSQL (locadores, imoveis, contratos, notas_fiscais)
  1.2  Escrever database/schema.sql com comentários
  1.3  Escrever database/seed.sql com dados fictícios coerentes

PASSO 2 — Backend Spring Boot (sem 7-8):
  2.1  Setup: Spring Web + spring-boot-starter-jdbc + PostgreSQL Driver + spring-security-crypto
  2.2  Configurar application.properties (datasource)
  2.3  POJOs de modelo: Locador, Imovel, ContratoLocacao, NotaFiscal
  2.4  DAOs (model/dao/): SQL escrito à mão com JdbcTemplate
  2.5  Auth: POST /auth/login → confere BCrypt, devolve marcador de sessão (não-JWT)
  2.6  API aberta — sem JwtFilter, sem Spring Security em rotas
  2.7  CRUD Locador: GET/POST/PUT/DELETE /locadores
  2.8  CRUD Imovel: GET/POST/PUT/DELETE /imoveis
  2.9  CRUD Contrato: GET/POST/PUT/DELETE /contratos

PASSO 3 — Frontend React/Vite (sem 9-11):
  3.1  Setup Vite + React + estrutura de pastas
  3.2  lib/api.js — funções fetch para cada recurso
  3.3  Componentes base: Button, Input, Table, Modal
  3.4  Telas: login, dashboard, locadores, imóveis, contratos
  3.5  Conectar ao backend real

PASSO 4 — Testes e ajustes (sem 12-13)
```

---

## 4. Riscos e Mitigações

### 🔴 Risco 1 — Soft delete esquecido nos GETs
Registros excluídos aparecem na listagem porque o `WHERE deleted_at IS NULL` foi esquecido.
- **Mitigação:** toda query de listagem é escrita junto com o filtro desde o início.

### 🔴 Risco 2 — Gold-plating fiscal
Tentar implementar NF-e real, boleto bancário, SEFAZ — zero valor no PI 2.
- **Mitigação:** cálculo IBS/CBS fica em Python/Flask como endpoint separado; o restante é simulado.

### 🟡 Risco 3 — Schema.sql diverge do banco em uso
Alterar a tabela sem atualizar o arquivo SQL deixa a entrega inconsistente.
- **Mitigação:** qualquer ALTER TABLE → atualizar schema.sql no mesmo commit.

---

## 5. Checklist de Done (PI 2)

### Repositório
- [ ] GitHub público em `github.com/dumont-curvaderio/PI-ImobFiscal`
- [ ] `README.md` raiz com objetivo, stack e instruções de execução

### /docs (obrigatório pelo manual)
- [ ] `01-escopo.md`
- [ ] `02-requisitos.md` com RF e RNF em tabela
- [ ] `03-casos-de-uso.md` com diagrama Mermaid ✅ (existe)
- [ ] `03-diagrama-sequencia.md` ✅ (existe)
- [ ] `04-diagrama-classes.md` com diagrama UML Mermaid
- [ ] `05-dicionario-dados.md` com tabela de colunas
- [ ] `06-plano-testes.md`
- [ ] `07-consideracoes-finais.md`

### /database (obrigatório pelo manual)
- [ ] `schema.sql` com DDL comentado
- [ ] `seed.sql` com dados fictícios coerentes
- [ ] `README.md` com DER em Mermaid

### Backend — CRUD completo
- [ ] POST/GET/PUT/DELETE `/locadores`
- [ ] POST/GET/PUT/DELETE `/imoveis`
- [ ] POST/GET/PUT/DELETE `/contratos`
- [ ] Login funcional com verificação BCrypt (API aberta, sem JWT)
- [ ] Soft delete em todas as tabelas (filtra `deleted_at IS NULL`)

### Frontend — integração
- [ ] Tela de login funcional
- [ ] CRUD de Locadores completo na UI
- [ ] CRUD de Imóveis completo na UI
- [ ] CRUD de Contratos completo na UI
- [ ] Deploy no Vercel funcionando

### Testes
- [ ] Pelo menos 3 testes funcionais documentados em `docs/06-plano-testes.md`
- [ ] Prints ou evidências dos testes

### Pitch (semana 15)
- [ ] Demo CRUD ao vivo
- [ ] Diagramas exibidos
- [ ] Slide "o que aprendi neste semestre"
