---
name: supabase-migration
description: Use ao criar ou modificar o schema do banco de dados.
             Triggers: "criar tabela", "adicionar coluna", "nova migration",
             "alterar schema", "novo índice", "RLS policy", "foreign key".
version: 1.0
owner: AI Architect de Plataforma
last_updated: 2026-05-01
---

Migrations são irreversíveis em ambientes com dados reais. Uma migration
errada pode corromper dados de dezenas de imobiliárias. Todo schema change
é revisado pelo AI Architect de Plataforma antes de ir para develop.
Nunca editar migration já aplicada — sempre criar nova.

## Processo

1. Criar `supabase/migrations/YYYYMMDD_descricao_curta.sql`
2. Incluir `BEGIN; ... COMMIT;` envolvendo toda a migration
3. Toda tabela nova: `id UUID DEFAULT gen_random_uuid() PRIMARY KEY`
4. Dados fiscais: `deleted_at TIMESTAMPTZ NULL` obrigatório
5. Multi-tenant: `imobiliaria_id UUID NOT NULL REFERENCES imobiliarias(id)`
6. Criar índice em `imobiliaria_id` e em `deleted_at`
7. Habilitar RLS e criar policy de isolamento de tenant
8. Testar localmente: `pnpm supabase migration up`
9. Rodar testes: `pnpm test` — nenhum teste pode quebrar

## Template

```sql
-- Migration: YYYYMMDD_nome_descritivo.sql
-- Description: [o que faz e por quê]

BEGIN;

CREATE TABLE IF NOT EXISTS [nome] (
  id              UUID        DEFAULT gen_random_uuid() PRIMARY KEY,
  imobiliaria_id  UUID        NOT NULL REFERENCES imobiliarias(id),
  -- campos específicos
  created_at      TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  updated_at      TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  deleted_at      TIMESTAMPTZ NULL
);

CREATE INDEX idx_[nome]_imobiliaria ON [nome](imobiliaria_id);
CREATE INDEX idx_[nome]_ativo ON [nome](deleted_at) WHERE deleted_at IS NULL;

ALTER TABLE [nome] ENABLE ROW LEVEL SECURITY;

CREATE POLICY "[nome]_tenant" ON [nome]
  USING (imobiliaria_id::text = auth.jwt() ->> 'tenant_id');

CREATE TRIGGER set_[nome]_updated_at
  BEFORE UPDATE ON [nome]
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

COMMIT;
```

## Regras absolutas

- NUNCA editar migration já aplicada — criar nova
- NUNCA DROP COLUMN sem sprint de deprecação
- NUNCA remover RLS de tabela existente
- TODA tabela com dados fiscais: `deleted_at` obrigatório
- TODA tabela multi-tenant: índice em `imobiliaria_id` obrigatório

## Testes obrigatórios

- [ ] `pnpm supabase migration up` sem erro
- [ ] RLS: usuário de outro tenant não vê dados
- [ ] Soft delete: `deleted_at IS NOT NULL` some das queries padrão
- [ ] `pnpm test` sem regressões
