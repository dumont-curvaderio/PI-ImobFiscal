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
| Backend principal | Node.js + Express + JavaScript |
| Calculadora tributária (bônus) | Python + Flask — chamado pelo backend Node |
| Banco de dados | PostgreSQL |
| Deploy frontend | Vercel |
| Deploy backend | Railway |

> **Python:** disciplina paralela — o módulo `CalculadoraImposto` do Diagrama de Classes
> será implementado em Python/Flask, chamado via HTTP pelo backend Node. Se não for exigido
> na entrega do PI, entra como diferencial.

### Estrutura de pastas no repositório

```
imobfiscal/
├── frontend/          ← React + JavaScript (Vite)
├── backend/           ← Node.js + Express
├── database/          ← schema.sql, seed.sql, README com DER Mermaid
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

- **Express direto com pg** — sem ORM, SQL puro para o professor ver o banco
- **Soft delete** (`deleted_at`) em todas as tabelas com histórico
- **JWT** para autenticação (jsonwebtoken + middleware simples)
- **DER em Mermaid** dentro de `database/README.md` (renderiza no GitHub)

---

## 2. Cronograma Semanal (Semanas 1 → 15)

| Sem | Foco | Entregáveis concretos |
|-----|------|-----------------------|
| **1** ✅ | Fundação | Repo GitHub público + estrutura de pastas + README.md |
| **2** | Escopo | `docs/01-escopo.md` |
| **4** | Requisitos + UML | `docs/02-requisitos.md` + `docs/03-casos-de-uso.md` + `docs/04-diagrama-classes.md` |
| **6** | Banco completo | `database/schema.sql` + `database/seed.sql` + `database/README.md` (DER) + `docs/05-dicionario-dados.md` |
| **7** | Backend pt.1 | Setup Express + conexão PostgreSQL + CRUD Locador |
| **8** | Backend pt.2 | CRUD Imovel + ContratoLocacao + JWT Auth |
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

PASSO 2 — Backend Node/Express (sem 7-8):
  2.1  Setup: express + pg + cors + dotenv + jsonwebtoken
  2.2  Conexão com PostgreSQL (pool)
  2.3  Rotas de auth: POST /auth/login
  2.4  Middleware JWT (verificar token em rotas protegidas)
  2.5  CRUD Locador: GET/POST/PUT/DELETE /locadores
  2.6  CRUD Imovel: GET/POST/PUT/DELETE /imoveis
  2.7  CRUD Contrato: GET/POST/PUT/DELETE /contratos

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
- [ ] Rotas protegidas por JWT
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
