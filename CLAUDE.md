# ImobFiscal — Contexto para Claude Code
> Este arquivo é lido automaticamente pelo Claude Code em toda sessão.
> É a fonte primária de verdade do projeto. Mantenha sempre atualizado.

---

## O QUE É ESTE PROJETO

SaaS imobiliário brasileiro com módulo fiscal completo, conforme a Reforma Tributária (LC 214/2025).

**Público-alvo MVP:** Imobiliárias pequenas e médias (10–200 imóveis na carteira).  
**Proposta de valor:** Único sistema que gerencia o imóvel do contrato à NF-e, do documento ao GED, com análise fiscal automática conforme as novas leis.

**Documentação de negócio (LEIA ANTES de qualquer task fiscal):**
- `docs/knowledge-base-fiscal.md` — Leis e regras de negócio (fonte primária fiscal)
- `docs/domain-model.md` — Entidades, atributos e máquinas de estado
- `docs/reforma-tributaria-calculos.md` — Algoritmos de cálculo com cenários
- `docs/modulo-ged-documentos.md` — GED+IA e geração de documentos
- `docs/arquitetura-evolucao.md` — Microserviços, MCP, migração de dados

---

## STACK TÉCNICA

```
Frontend web:  Next.js 16 + React + TypeScript + Tailwind CSS 4 + shadcn/ui
Backend API:   NestJS + TypeScript + Prisma (ORM) + Zod (validação)
Banco:         PostgreSQL via Supabase (RLS multi-tenant)
Jobs:          Supabase Edge Functions (Deno/TypeScript)
Mobile:        Expo (React Native) + TypeScript + Expo Router
Monorepo:      Turborepo + pnpm workspaces
Testes:        Vitest (unit) + Supertest (API) + Playwright (e2e)
CI/CD:         GitHub Actions → Vercel (web) + Railway (api) + EAS (mobile)
Fiscal:        Focus NFe (NF-e/NFS-e) | Asaas (boleto+PIX) | Clicksign (assinatura)
```

**Por que esta stack → `docs/decisao-stack.md`**

---

## ESTRUTURA DO PROJETO

```
imobfiscal/
├── apps/
│   ├── web/          → Next.js (frontend)
│   ├── api/          → NestJS (backend REST + Swagger em /api/docs)
│   └── mobile/       → Expo
├── packages/
│   ├── core/         → lógica fiscal PURA (sem framework, sem side effects)
│   ├── ui/           → componentes React compartilhados
│   ├── config/       → ESLint, TypeScript, Prettier configs
│   └── mcp-dev/      → MCP interno para desenvolvimento com Claude Code
├── supabase/
│   ├── migrations/   → SQL versionado (nunca editar migrations já aplicadas)
│   ├── functions/    → Edge Functions
│   └── seed/         → dados iniciais (alíquotas, municípios, cenários de teste)
└── docs/             → knowledge base completo do projeto
```

---

## REGRAS ABSOLUTAS DE DESENVOLVIMENTO

### Fiscal — as mais críticas

1. **NUNCA hardcodar alíquotas** — sempre buscar de `aliquotas_vigentes` no banco, filtrado por `(ano, tipo_operacao, uf, municipio)`. Alíquotas mudam a cada ano da transição 2026–2033.

2. **TODO cálculo fiscal em `packages/core/fiscal/`** — funções puras, sem efeitos colaterais, sem acesso a banco. Entrada e saída explícitas. Fácil de testar isoladamente.

3. **TODO cálculo fiscal TEM teste unitário** obrigatório cobrindo:
   - Caso base (fluxo normal)
   - Isenção (cada hipótese: R$2.500, único imóvel, 180 dias, pré-1969)
   - Edge cases: valor exato no limite (R$2.500,00 = isento? R$2.500,01 = tributado)
   - Cenário PF contribuinte vs. não-contribuinte (RN-001)

4. **Em 2026**, `valor_ibs` e `valor_cbs` na NF-e são informativos — `recolhimento_obrigatorio = false`. A partir de 2027, `recolhimento_obrigatorio = true`. Isso é controlado pela tabela `aliquotas_vigentes.recolhimento_obrigatorio` e pela flag `FLAG_IBS_CBS_RECOLHIMENTO` no `.env`.

5. **Referências fiscais:** toda regra de negócio implementada deve referenciar o código `RN-XXX` do knowledge base no comentário do código.

### Banco de dados

6. **NUNCA hard delete** de nenhum dado com histórico fiscal — usar soft delete (`deleted_at`). Prazo de guarda fiscal: 5 anos mínimo.

7. **Multi-tenancy:** TODA query no banco deve filtrar por `imobiliaria_id`. Usar Row Level Security (RLS) do Supabase como segunda linha de defesa. Policy em cada tabela: `USING (imobiliaria_id = auth.jwt() ->> 'tenant_id')`.

8. **Migrations:** nunca editar uma migration já aplicada em qualquer ambiente. Criar nova migration. Nome: `YYYYMMDD_descricao_curta.sql`.

### Segurança

9. **Certificados digitais (PFX):** NUNCA em variáveis de ambiente nem em código. Armazenar no Supabase Vault. Senha do certificado também no Vault, separada.

10. **NF-e:** NUNCA transmitir de forma síncrona na requisição HTTP. Sempre via fila assíncrona (Edge Function). Máximo 5 tentativas com backoff exponencial: 1min, 5min, 15min, 1h, 4h.

11. **Zod obrigatório** em todos os Route Handlers e Controllers. Nenhum `req.body` sem validação.

### Ambientes

12. **NUNCA misturar ambientes SEFAZ.** `SEFAZ_AMBIENTE=homologacao` no `.env.development` e `.env.beta`. `SEFAZ_AMBIENTE=producao` só no `.env.production`. O sistema deve checar e recusar operação se a variável estiver ausente.

13. **Ambiente de desenvolvimento:** banco local Docker. Comando: `pnpm dev:local`.  
    **Ambiente beta:** dados reais das imobiliárias parceiras, SEFAZ homologação.  
    **Produção:** só via CI/CD após merge em `main`, nunca deploy manual.

---

## CONVENÇÕES DE CÓDIGO

### Nomenclatura
- **Entidades de domínio:** português (ex: `Imovel`, `ContratoLocacao`, `ApuracaoFiscal`)
- **Infraestrutura:** inglês (ex: `DatabaseModule`, `QueueService`, `AuthGuard`)
- **Variáveis e funções:** camelCase inglês (ex: `calculateGCAP`, `tenantId`)
- **Enums:** SCREAMING_SNAKE_CASE português (ex: `RESIDENCIAL_LONGA`, `NAO_CONTRIBUINTE`)
- **Arquivos:** kebab-case (ex: `carne-leao.calculator.ts`, `imovel.service.ts`)

### Estrutura de módulo NestJS (template obrigatório)
```
modules/[nome]/
├── [nome].module.ts         ← imports, providers, exports
├── [nome].controller.ts     ← endpoints, Swagger decorators
├── [nome].service.ts        ← lógica de negócio
├── [nome].repository.ts     ← acesso ao banco (Prisma) — só este módulo acessa seus dados
├── dto/
│   ├── create-[nome].dto.ts
│   ├── update-[nome].dto.ts
│   └── [nome]-response.dto.ts
└── __tests__/
    ├── [nome].service.spec.ts
    └── [nome].controller.spec.ts
```

**Módulo A não importa diretamente o repository do Módulo B.** Se B precisar de dados de A, A expõe um método no seu Service com interface bem definida.

### Commits (Conventional Commits — inglês)
```
feat(fiscal): add IBS/CBS calculator with 70% reduction rule (RN-003)
fix(nfe): retry logic on SEFAZ timeout with exponential backoff
fix(gcap): correct reinvestment 180-day exemption (RN-008)
chore(aliquotas): update rates table for 2027 transition
test(fiscal): edge cases for PF locadora threshold RN-001
docs(domain): add benfeitoria evidence level decision
```

---

## REGRAS DE NEGÓCIO — REFERÊNCIA RÁPIDA

> Para detalhes completos, ver `docs/knowledge-base-fiscal.md`

| Código | Regra | Impacto |
|---|---|---|
| RN-001 | PF contribuinte IBS/CBS: >3 imóveis E >R$240k/ano (cumulativo) | NF-e, DARF |
| RN-002 | Isenção residencial ≤R$2.500/mês (aluguel) | NF-e isenta |
| RN-003 | Alíquota efetiva locação: 30% da referência (70% redução) | Cálculo IBS/CBS |
| RN-004 | Short stay ≤90 dias: regime hotelaria (60% da referência) | Alíquota diferente |
| RN-005 | Contrato pré-16/01/2025 pode optar 3,65% flat (art. 487) | Sem créditos |
| RN-006 | Locatário PJ → retenção IRRF 11% na fonte (DARF 3208) | Financeiro |
| RN-007 | Carnê-leão PF: locatário é PF ou exterior, base ≥ tabela progressiva | DARF mensal |
| RN-008 | GCAP: 4 isenções (ganho ≤35k, único imóvel ≤440k, reinvestimento 180d, pré-1969) | IR venda |

**Limite R$240.000 é corrigido pelo IPCA desde jan/2025.** Buscar valor vigente da tabela, não hardcodar.

---

## INTEGRAÇÕES EXTERNAS

| Serviço | Uso | Env var | Sandbox URL |
|---|---|---|---|
| SEFAZ Nacional (SVAN) | NF-e modelo 55 | `SEFAZ_*` | `hom.nfe.fazenda.gov.br` |
| Portal NFS-e (gov) | Nota de serviço | `NFSE_*` | Portal NFS-e homologação |
| Focus NFe | SDK NF-e | `FOCUSNFE_TOKEN` | token sandbox da Focus |
| Supabase | Banco + Storage + Auth | `SUPABASE_*` | projeto `-dev` |
| Asaas | Boleto + PIX | `ASAAS_TOKEN` | sandbox.asaas.com |
| Clicksign | Assinatura eletrônica | `CLICKSIGN_*` | sandbox.clicksign.com |
| Resend | E-mail transacional | `RESEND_API_KEY` | — |
| ViaCEP | CEP → endereço | nenhuma | api.viacep.com.br |
| BACEN | Índices IGPM/IPCA | nenhuma | api.bcb.gov.br |

---

## TESTES — OBRIGAÇÕES

### Unitários (Vitest) — obrigatório para:
- Todo cálculo fiscal em `packages/core/fiscal/`
- Toda função de validação (CPF, CNPJ, datas)
- Toda regra de negócio crítica (enquadramento IBS/CBS, isenções GCAP)

### Integração (Supertest) — obrigatório para:
- Todo endpoint da API que muda estado (POST, PUT, DELETE)
- Webhooks recebidos (SEFAZ, Clicksign, Asaas)
- Jobs fiscais (carnê-leão mensal, alertas)

### Cobertura mínima:
- `packages/core/fiscal/`: 95%
- `apps/api/src/modules/fiscal/`: 80%
- Demais módulos: 60%

### Rodar testes fiscais antes de todo commit:
```bash
pnpm turbo test --filter=@imobfiscal/core
```

---

## COMANDOS FREQUENTES

```bash
# Setup inicial
pnpm install
pnpm supabase start           # banco local Docker
pnpm db:migrate               # aplicar migrations pendentes
pnpm db:seed                  # dados iniciais (alíquotas, municípios)

# Desenvolvimento
pnpm dev                      # todos os apps em paralelo
pnpm dev --filter=web         # só o Next.js
pnpm dev --filter=api         # só o NestJS
pnpm dev --filter=mobile      # só o Expo

# Testes
pnpm test                     # todos (affected only por padrão)
pnpm test:fiscal              # só os cálculos fiscais (crítico)
pnpm test:watch               # modo watch

# Banco
pnpm db:migrate               # rodar migrations
pnpm db:generate              # gerar tipos Prisma após mudar schema
pnpm db:reset                 # reset completo (dev apenas!)
pnpm supabase types            # gerar tipos TypeScript do Supabase

# Qualidade
pnpm lint                     # ESLint
pnpm format                   # Prettier
pnpm typecheck                # TypeScript strict

# Build
pnpm build                    # build completo (Turborepo cached)
pnpm build --filter=api       # só o backend
```

---

## Claude CODE — FLUXO DE TRABALHO

### Para features fiscais (alta criticidade):
1. **Ler** os docs relevantes em `docs/` antes de começar
2. **Escrever os testes primeiro** (TDD) — criar `*.spec.ts` com casos de isenção
3. **Implementar** até os testes passarem
4. **Verificar** que nenhum hardcode de alíquota foi inserido
5. **Comentar** as regras com referência `// RN-XXX`

### Para features de UI:
1. Usar componentes de `packages/ui/` ou `shadcn/ui` sempre que possível
2. Seguir os padrões do `apps/web/CLAUDE.md`
3. Validação Zod obrigatória em todos os formulários

### Agent Teams (para features complexas):
- **Líder (Opus 4.6):** lê docs, decompõe, coordena
- **Backend (Sonnet 4.6):** migrations + API + jobs
- **Frontend (Sonnet 4.6):** UI + formulários + páginas
- **Testes (Sonnet 4.6):** spec files + e2e
- **Fiscal (Sonnet 4.6):** calculators + validators

### Não fazer sem aprovação explícita:
- Alterar tabela `aliquotas_vigentes` diretamente
- Desabilitar RLS em qualquer tabela
- Criar endpoint sem autenticação (exceto `/api/health` e `/api/docs`)
- Apontar para SEFAZ produção em ambiente de desenvolvimento
- Hard delete de qualquer registro fiscal

---

## VARIÁVEIS DE AMBIENTE OBRIGATÓRIAS

```env
# Identidade do ambiente
APP_ENV=development|staging|beta|production

# SEFAZ — CRÍTICO: nunca misturar ambientes
SEFAZ_AMBIENTE=homologacao|producao
SEFAZ_URL=https://hom.nfe.fazenda.gov.br

# Feature flags fiscais 2026
FLAG_IBS_CBS_RECOLHIMENTO=false      # true a partir de 2027
FLAG_DIMOB_TRANSMISSAO=false         # true quando lançar em produção

# Supabase
SUPABASE_URL=
SUPABASE_ANON_KEY=
SUPABASE_SERVICE_ROLE_KEY=           # só no backend, nunca no frontend

# APIs externas
FOCUSNFE_TOKEN=
ASAAS_TOKEN=
CLICKSIGN_API_KEY=
RESEND_API_KEY=

# IA (análise de documentos GED)
ANTHROPIC_API_KEY=                   # para Claude Haiku (análise de docs)
GOOGLE_AI_API_KEY=                   # para Gemini Flash (OCR)
```

---

## CONTEXTO DO PROJETO

**Reforma Tributária (mai/2026):**
- IBS: 0,1% | CBS: 0,9% → total 1% (fase de testes, recolhimento dispensado)
- A partir de ago/2026: preenchimento CBS obrigatório nas NF-e
- A partir de jan/2027: CBS plena (8,8%), extinção PIS/COFINS

**Ambiente beta (imobiliárias parceiras):**
- SEFAZ homologação — NF-e sem valor fiscal
- Dados reais das imobiliárias, pagamentos em sandbox
- Objetivo: validar cálculos fiscais e edge cases antes do lançamento

**Stack de um único repo:**
- Monorepo Turborepo com pnpm workspaces
- Uma mudança fiscal → 1 commit atômico (web + mobile + MCP)
- Repos satélite separados: `imobfiscal-infra` (Terraform) e `imobfiscal-mcp-client` (npm)
