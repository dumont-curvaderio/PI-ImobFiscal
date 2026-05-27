-- Migration: 20260526_001_imoveis.sql
-- Description: Módulo M1 — imóveis, proprietários, benfeitorias

BEGIN;

CREATE TABLE IF NOT EXISTS imoveis (
  id              UUID        DEFAULT gen_random_uuid() PRIMARY KEY,
  imobiliaria_id  UUID        NOT NULL REFERENCES imobiliarias(id),
  codigo          TEXT        NOT NULL,
  tipo            TEXT        NOT NULL CHECK (tipo IN ('RESIDENCIAL', 'COMERCIAL', 'RURAL', 'MISTO')),
  finalidade      TEXT        NOT NULL DEFAULT 'LOCACAO'
                              CHECK (finalidade IN ('LOCACAO', 'VENDA', 'VENDA_E_LOCACAO')),
  -- Endereço
  cep             VARCHAR(8)  NOT NULL,
  logradouro      TEXT        NOT NULL,
  numero          TEXT        NOT NULL,
  complemento     TEXT,
  bairro          TEXT        NOT NULL,
  cidade          TEXT        NOT NULL,
  uf              CHAR(2)     NOT NULL,
  municipio_ibge  VARCHAR(7),
  -- Dados físicos
  area_total      NUMERIC(12,2),
  area_privativa  NUMERIC(12,2),
  quartos         INTEGER,
  vagas           INTEGER,
  -- Dados fiscais GCAP
  valor_compra    NUMERIC(15,2),
  data_compra     DATE,
  -- Timestamps
  created_at      TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  updated_at      TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  deleted_at      TIMESTAMPTZ NULL,
  UNIQUE(imobiliaria_id, codigo)
);

CREATE INDEX idx_imoveis_imobiliaria ON imoveis(imobiliaria_id);
CREATE INDEX idx_imoveis_ativo ON imoveis(deleted_at) WHERE deleted_at IS NULL;

ALTER TABLE imoveis ENABLE ROW LEVEL SECURITY;
CREATE POLICY "imoveis_tenant" ON imoveis
  USING (imobiliaria_id::text = auth.jwt() ->> 'tenant_id');

CREATE TRIGGER set_imoveis_updated_at
  BEFORE UPDATE ON imoveis
  FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ─── PROPRIETÁRIOS ───────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS proprietarios (
  id              UUID        DEFAULT gen_random_uuid() PRIMARY KEY,
  imobiliaria_id  UUID        NOT NULL REFERENCES imobiliarias(id),
  tipo_pessoa     TEXT        NOT NULL CHECK (tipo_pessoa IN ('PF', 'PJ')),
  cpf_cnpj        VARCHAR(14) NOT NULL,
  nome            TEXT        NOT NULL,
  email           TEXT,
  telefone        TEXT,
  created_at      TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  updated_at      TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  deleted_at      TIMESTAMPTZ NULL
);

CREATE INDEX idx_proprietarios_imobiliaria ON proprietarios(imobiliaria_id);
ALTER TABLE proprietarios ENABLE ROW LEVEL SECURITY;
CREATE POLICY "proprietarios_tenant" ON proprietarios
  USING (imobiliaria_id::text = auth.jwt() ->> 'tenant_id');

CREATE TABLE IF NOT EXISTS imoveis_proprietarios (
  imovel_id       UUID        NOT NULL REFERENCES imoveis(id),
  proprietario_id UUID        NOT NULL REFERENCES proprietarios(id),
  percentual      NUMERIC(5,2) NOT NULL DEFAULT 100,
  PRIMARY KEY (imovel_id, proprietario_id)
);

-- ─── BENFEITORIAS ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS benfeitorias (
  id               UUID        DEFAULT gen_random_uuid() PRIMARY KEY,
  imovel_id        UUID        NOT NULL REFERENCES imoveis(id),
  descricao        TEXT        NOT NULL,
  valor            NUMERIC(15,2) NOT NULL,
  data             DATE        NOT NULL,
  nivel_evidencia  TEXT        NOT NULL
                   CHECK (nivel_evidencia IN ('COMPROVADO', 'RECIBO', 'DECLARADO')),
  created_at       TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  updated_at       TIMESTAMPTZ DEFAULT NOW() NOT NULL,
  deleted_at       TIMESTAMPTZ NULL
);

CREATE INDEX idx_benfeitorias_imovel ON benfeitorias(imovel_id);
ALTER TABLE benfeitorias ENABLE ROW LEVEL SECURITY;

-- RLS via imovel: só acessa se tem acesso ao imóvel
CREATE POLICY "benfeitorias_tenant" ON benfeitorias
  USING (
    imovel_id IN (
      SELECT id FROM imoveis WHERE imobiliaria_id::text = auth.jwt() ->> 'tenant_id'
    )
  );

COMMIT;
