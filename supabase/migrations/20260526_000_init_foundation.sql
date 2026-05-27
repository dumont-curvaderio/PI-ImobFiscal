-- Migration: 20260526_000_init_foundation.sql
-- Description: Tabelas base — helper function, imobiliarias, usuarios, RLS base

BEGIN;

-- Função auxiliar para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ─── IMOBILIÁRIAS (tenant root) ──────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS imobiliarias (
  id            UUID        DEFAULT gen_random_uuid() PRIMARY KEY,
  cnpj          VARCHAR(14) NOT NULL UNIQUE,
  razao         TEXT        NOT NULL,
  nome_fantasia TEXT,
  email         TEXT        NOT NULL,
  telefone      TEXT,
  plano         TEXT        NOT NULL DEFAULT 'BASICO'
                            CHECK (plano IN ('BASICO', 'PROFISSIONAL', 'ENTERPRISE')),
  created_at    TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  updated_at    TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  deleted_at    TIMESTAMPTZ NULL
);

CREATE INDEX idx_imobiliarias_ativo ON imobiliarias(deleted_at) WHERE deleted_at IS NULL;

ALTER TABLE imobiliarias ENABLE ROW LEVEL SECURITY;

CREATE TRIGGER set_imobiliarias_updated_at
  BEFORE UPDATE ON imobiliarias
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ─── USUÁRIOS ─────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS usuarios (
  id              UUID        DEFAULT gen_random_uuid() PRIMARY KEY,
  imobiliaria_id  UUID        NOT NULL REFERENCES imobiliarias(id),
  supabase_uid    TEXT        NOT NULL UNIQUE,
  email           TEXT        NOT NULL,
  nome            TEXT        NOT NULL,
  perfil          TEXT        NOT NULL DEFAULT 'OPERADOR'
                              CHECK (perfil IN ('ADMIN', 'GERENTE', 'OPERADOR', 'FINANCEIRO', 'READONLY')),
  created_at      TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  updated_at      TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  deleted_at      TIMESTAMPTZ NULL
);

CREATE INDEX idx_usuarios_imobiliaria ON usuarios(imobiliaria_id);
CREATE INDEX idx_usuarios_supabase_uid ON usuarios(supabase_uid);
CREATE INDEX idx_usuarios_ativo ON usuarios(deleted_at) WHERE deleted_at IS NULL;

ALTER TABLE usuarios ENABLE ROW LEVEL SECURITY;

-- Policy: usuário só vê dados da própria imobiliária
CREATE POLICY "usuarios_tenant" ON usuarios
  USING (imobiliaria_id::text = auth.jwt() ->> 'tenant_id');

CREATE TRIGGER set_usuarios_updated_at
  BEFORE UPDATE ON usuarios
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

COMMIT;
