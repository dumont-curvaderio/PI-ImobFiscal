# Plano de Desenvolvimento — PI 2 FATEC DSM (ImobFiscal)

**Decisão consolidada — 2026-06-01 · Semana 1 de 15**

---

## 1. Decisão de Escopo

### O que é este projeto para o PI 2
Sistema web de gestão imobiliária com CRUD completo de Imóveis e Proprietários, autenticação de usuário e persistência em PostgreSQL. Apresentado como **"Sistema de Gestão Imobiliária"**.

### O que está FORA do escopo do PI 2 (não implementar)
- Cálculos fiscais (IBS/CBS, NF-e, SEFAZ, carnê-leão, GCAP)
- Multi-tenancy avançado (RLS, `@CurrentTenant` decorator)
- Módulos: contratos, financeiro, notas fiscais
- Integrações externas (Focus NFe, Asaas, Clicksign)

> Regra de ouro: se uma tarefa não aparece no checklist da seção 5, **não faça**.

### Decisões técnicas fixas (não mudar no meio do semestre)
- **Service chama Prisma direto** — sem repository pattern, sem CQRS
- **JWT Auth Guard** nas rotas (sem decorator customizado de tenant)
- **`lib/api.ts`** com toggle mock/real para desacoplar frontend do backend
- **DER em Mermaid** dentro de `database/README.md` (renderiza no GitHub)
- **`schema.prisma`** é a fonte de verdade única — `schema.sql` é derivado dele

---

## 2. Cronograma Semanal (Semanas 1 → 15)

| Sem | Foco | Entregáveis concretos |
|-----|------|-----------------------|
| **1** ✅ | Fundação do repositório | Repo GitHub público criado + push inicial + README.md raiz |
| **2** | Escopo | `docs/01-escopo.md` — o que o sistema faz, quem usa, fora de escopo |
| **3** | Requisitos | `docs/02-requisitos.md` — tabela de RF e RNF |
| **4** | UML | `docs/03-casos-de-uso.md` (Mermaid) + `docs/04-diagrama-classes.md` (Mermaid) |
| **5** | Banco — scripts | `database/schema.sql` (gerado do Prisma) + `database/seed.sql` |
| **6** | Banco — docs | `docs/05-dicionario-dados.md` + `database/README.md` com DER Mermaid. **Marco: /database 100% completo** |
| **7** | Backend Imóvel pt.1 | JWT Auth Guard + `CreateImovelDto` + `UpdateImovelDto` + `POST /imoveis` |
| **8** | Backend Imóvel pt.2 | `PUT /imoveis/:id` + `DELETE /imoveis/:id` (soft delete) + GETs filtram `deleted_at IS NULL`. **Marco: CRUD Imóvel completo na API** |
| **9** | Frontend — base + login | 6 componentes base + `lib/api.ts` (mock) + página de login |
| **10** | Frontend — Imóvel | Páginas: listagem → criar → detalhes (virando mock para real) |
| **11** | Frontend Imóvel pt.2 + Backend Proprietário | Páginas: editar + exclusão. CRUD de Proprietário no backend. **Marco: CRUD Imóvel 100% ponta a ponta** |
| **12** | Proprietário frontend + Testes | Páginas de Proprietário + `docs/06-plano-testes.md` + 3 testes unitários |
| **13** | 🛡️ Buffer | Corrigir bugs, ajustar UI, verificar mock virado para real, `pnpm test` verde |
| **14** | Entrega | README final + `docs/07-consideracoes-finais.md` + conferir checklist completo |
| **15** | Pitch | Demo ao vivo + diagramas + slide "o que aprendi" |

> A semana 13 é folga proposital — se algo atrasar, ela é o colchão. Não preencher com features novas.

---

## 3. Ordem de Implementação Técnica (sem bloqueios)

```
PASSO 1 — Banco (sem 5-6):
  1.1  Confirmar schema.prisma (já 100%)
  1.2  Gerar database/schema.sql a partir do Prisma
  1.3  Criar database/seed.sql com dados fictícios

PASSO 2 — Backend Imóvel (sem 7-8), uma operação de cada vez:
  2.1  JWT Auth Guard nas rotas             ← POST sem guard = vulnerável
  2.2  CreateImovelDto + UpdateImovelDto    ← validar antes de aceitar dados
  2.3  POST /imoveis                        ← criar
  2.4  PUT  /imoveis/:id                   ← editar
  2.5  DELETE /imoveis/:id (soft delete)   ← excluir
  2.6  Atualizar GET e GET/:id             ← filtrar WHERE deleted_at IS NULL

PASSO 3 — Frontend (sem 9-11), contra MOCK primeiro:
  3.1  6 componentes base (Button, Input+Label, Card, PageHeader, Spinner, ConfirmDialog)
  3.2  lib/api.ts em modo MOCK
  3.3  Todas as páginas funcionando contra mock
  3.4  Virar para REAL endpoint por endpoint, na ordem em que o backend fica pronto

PASSO 4 — Proprietário (sem 11-12), mesmo molde do Imóvel

PASSO 5 — Testes (sem 12), depois que CRUD funciona
```

**Regra anti-bloqueio:** frontend nunca espera o backend (começa no mock). As duas frentes só se encontram no passo 3.4.

---

## 4. Riscos e Mitigações

### 🔴 Risco 1 — Soft delete esquecido nos GETs existentes
Adicionar `DELETE` com `deleted_at` mas o `GET /imoveis` continuar mostrando registros excluídos.
- **Mitigação:** o passo 2.6 está amarrado ao 2.5. Não fechar a semana 8 sem o GET filtrando `deleted_at IS NULL`. Um dos 3 testes unitários verifica exatamente isto.

### 🔴 Risco 2 — Gold-plating com features fiscais fora do escopo
IBS/CBS, NFe, SEFAZ, RLS avançado — nada disso vale nota no PI 2 e consome semanas.
- **Mitigação:** seção 1 descarta explicitamente. Se sentir vontade de "adicionar o módulo fiscal", releia esta linha e volte ao checklist da seção 5.

### 🟡 Risco 3 — Divergência entre schema.prisma e schema.sql/DER entregues
Mudar o Prisma depois de gerar o SQL deixa a entrega /database desatualizada.
- **Mitigação:** `schema.prisma` é fonte de verdade única. Qualquer mudança no Prisma → regenerar `schema.sql` e DER Mermaid no mesmo dia.

---

## 5. Checklist de Done (PI 2)

Responda sim/não. Se algum for "não" na semana 14, não está pronto.

### Repositório
- [ ] GitHub público em `github.com/dumont-curvaderio/PI-ImobFiscal`
- [ ] `README.md` raiz com objetivo, stack e instruções de execução

### /docs (obrigatório pelo manual)
- [ ] `01-escopo.md`
- [ ] `02-requisitos.md` com RF e RNF em tabela
- [ ] `03-casos-de-uso.md` com diagrama de Caso de Uso
- [ ] `03-diagrama-sequencia.md` (já existe ✅)
- [ ] `04-diagrama-classes.md` com diagrama UML de Classes
- [ ] `05-dicionario-dados.md` com tabela de colunas
- [ ] `06-plano-testes.md`
- [ ] `07-consideracoes-finais.md`

### /database (obrigatório pelo manual)
- [ ] `schema.sql` derivado do Prisma, com comentários
- [ ] `seed.sql` com dados fictícios coerentes
- [ ] `README.md` com DER em Mermaid (renderiza no GitHub)
- [ ] `schema.sql` bate com o `schema.prisma` atual

### Backend — CRUD completo
- [ ] Rotas protegidas por JWT Auth Guard
- [ ] `POST /imoveis` cria e persiste no PostgreSQL
- [ ] `GET /imoveis` lista (filtra `deleted_at IS NULL`)
- [ ] `GET /imoveis/:id` retorna um (filtra `deleted_at IS NULL`)
- [ ] `PUT /imoveis/:id` edita
- [ ] `DELETE /imoveis/:id` faz soft delete
- [ ] CRUD de Proprietário no mesmo padrão

### Frontend — integração front-back
- [ ] Página de login funcional contra backend real
- [ ] Listagem de imóveis com dados reais do banco
- [ ] Criar imóvel pela tela persiste no banco
- [ ] Página de detalhes do imóvel
- [ ] Editar imóvel pela tela
- [ ] Excluir imóvel com confirmação
- [ ] `lib/api.ts` 100% em modo "real" (zero mock na demo)

### Testes
- [ ] Pelo menos 3 testes unitários
- [ ] `pnpm test` roda verde

### Pitch (semana 15)
- [ ] Demo do CRUD funcionando ao vivo
- [ ] Diagramas exibidos
- [ ] Slide "o que aprendi neste semestre"
