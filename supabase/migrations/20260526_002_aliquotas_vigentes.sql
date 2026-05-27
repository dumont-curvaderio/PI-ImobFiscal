-- Migration: 20260526_002_aliquotas_vigentes.sql
-- Description: Seed de alíquotas IBS/CBS 2026-2033 (tabela de referência)
-- NUNCA hardcodar alíquotas no código — sempre consultar esta tabela (CLAUDE.md regra #1)

BEGIN;

CREATE TABLE IF NOT EXISTS aliquotas_vigentes (
  id                       UUID        DEFAULT gen_random_uuid() PRIMARY KEY,
  ano                      INTEGER     NOT NULL,
  tipo_operacao            VARCHAR(50) NOT NULL,
  uf                       CHAR(2),
  municipio_ibge           VARCHAR(7),
  aliquota_ibs             NUMERIC(8,6) NOT NULL,
  aliquota_cbs             NUMERIC(8,6) NOT NULL,
  fator_reducao            NUMERIC(5,4) NOT NULL,
  redutor_social           NUMERIC(10,2),
  recolhimento_obrigatorio BOOLEAN     NOT NULL DEFAULT FALSE,
  vigencia_inicio          DATE        NOT NULL,
  vigencia_fim             DATE,
  UNIQUE(ano, tipo_operacao, uf, municipio_ibge)
);

CREATE INDEX idx_aliquotas_ano ON aliquotas_vigentes(ano);

-- ─── SEED: Alíquotas 2026 (fase de transição — recolhimento dispensado) ──────

INSERT INTO aliquotas_vigentes
  (ano, tipo_operacao, aliquota_ibs, aliquota_cbs, fator_reducao, redutor_social, recolhimento_obrigatorio, vigencia_inicio)
VALUES
  -- Locação residencial longa permanência (RN-003: fator 0.30)
  (2026, 'RESIDENCIAL_LONGA', 0.001000, 0.009000, 0.30, 600.00, false, '2026-01-01'),
  -- Locação comercial (RN-003: fator 0.30, sem redutor social)
  (2026, 'COMERCIAL',         0.001000, 0.009000, 0.30, NULL,   false, '2026-01-01'),
  -- Short stay / hotelaria ≤90 dias (RN-004: fator 0.40)
  (2026, 'SHORT_STAY',        0.001000, 0.009000, 0.40, NULL,   false, '2026-01-01'),
  -- Rural
  (2026, 'RURAL',             0.001000, 0.009000, 0.30, NULL,   false, '2026-01-01')
ON CONFLICT (ano, tipo_operacao, uf, municipio_ibge) DO NOTHING;

-- ─── SEED: Alíquotas 2027 (CBS plena — PIS/COFINS extintos) ──────────────────

INSERT INTO aliquotas_vigentes
  (ano, tipo_operacao, aliquota_ibs, aliquota_cbs, fator_reducao, redutor_social, recolhimento_obrigatorio, vigencia_inicio)
VALUES
  (2027, 'RESIDENCIAL_LONGA', 0.001000, 0.088000, 0.30, 600.00, true, '2027-01-01'),
  (2027, 'COMERCIAL',         0.001000, 0.088000, 0.30, NULL,   true, '2027-01-01'),
  (2027, 'SHORT_STAY',        0.001000, 0.088000, 0.40, NULL,   true, '2027-01-01'),
  (2027, 'RURAL',             0.001000, 0.088000, 0.30, NULL,   true, '2027-01-01')
ON CONFLICT (ano, tipo_operacao, uf, municipio_ibge) DO NOTHING;

COMMIT;
