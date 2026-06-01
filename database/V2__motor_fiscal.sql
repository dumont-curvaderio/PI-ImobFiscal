-- ImobFiscal — Migração V2: Motor Tributário + Boletos
-- Reforma Tributária LC 214/2025 — IBS/CBS/Split Payment
-- Executar: psql -U postgres -d imobfiscal -f database/V2__motor_fiscal.sql

-- ============================================================
-- 1. Adiciona regime_tributario em locadores
-- Necessário para o MotorTributarioService calcular as alíquotas corretas
-- ============================================================
ALTER TABLE locadores
    ADD COLUMN IF NOT EXISTS regime_tributario VARCHAR(30)
        CHECK (regime_tributario IN ('PF', 'SIMPLES_NACIONAL', 'LUCRO_PRESUMIDO', 'LUCRO_REAL'));

-- ============================================================
-- 2. Adiciona valor_venal em imoveis
-- Valor de referência municipal — diferente do valor de compra
-- ============================================================
ALTER TABLE imoveis
    ADD COLUMN IF NOT EXISTS valor_venal NUMERIC(15, 2);

-- ============================================================
-- 3. Tabela de alíquotas vigentes
-- REGRA CRÍTICA: NUNCA hardcodar alíquotas no código.
-- Toda consulta fiscal usa esta tabela filtrada por ano_vigencia.
-- Alíquotas mudam a cada ano da transição 2026–2032.
-- ============================================================
CREATE TABLE IF NOT EXISTS aliquotas_vigentes (
    id              UUID            DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Regime tributário do locador
    regime          VARCHAR(30)     NOT NULL
        CHECK (regime IN ('PF', 'SIMPLES_NACIONAL', 'LUCRO_PRESUMIDO', 'LUCRO_REAL')),

    -- Tipo de imóvel (RESIDENCIAL ou COMERCIAL — base para a alíquota)
    tipo_imovel     VARCHAR(20)     NOT NULL
        CHECK (tipo_imovel IN ('RESIDENCIAL', 'COMERCIAL', 'RURAL', 'MISTO')),

    -- IBS: Imposto sobre Bens e Serviços (estadual/municipal)
    aliquota_ibs    NUMERIC(6, 4)   NOT NULL,

    -- CBS: Contribuição sobre Bens e Serviços (federal)
    aliquota_cbs    NUMERIC(6, 4)   NOT NULL,

    -- Ano de vigência (permite múltiplos anos na mesma tabela)
    ano_vigencia    INTEGER         NOT NULL,

    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),

    UNIQUE (regime, tipo_imovel, ano_vigencia)
);

-- Seed: alíquotas ilustrativas 2026 (fase de testes da Reforma Tributária)
-- Fonte: LC 214/2025 — valores aproximados para fins didáticos do PI
INSERT INTO aliquotas_vigentes (regime, tipo_imovel, aliquota_ibs, aliquota_cbs, ano_vigencia)
VALUES
    -- Pessoa Física
    ('PF', 'RESIDENCIAL', 0.0145, 0.0076, 2026),
    ('PF', 'COMERCIAL',   0.0290, 0.0153, 2026),
    ('PF', 'RURAL',       0.0100, 0.0050, 2026),
    ('PF', 'MISTO',       0.0200, 0.0100, 2026),

    -- Simples Nacional
    ('SIMPLES_NACIONAL', 'RESIDENCIAL', 0.0145, 0.0076, 2026),
    ('SIMPLES_NACIONAL', 'COMERCIAL',   0.0290, 0.0153, 2026),
    ('SIMPLES_NACIONAL', 'RURAL',       0.0100, 0.0050, 2026),
    ('SIMPLES_NACIONAL', 'MISTO',       0.0200, 0.0100, 2026),

    -- Lucro Presumido
    ('LUCRO_PRESUMIDO', 'RESIDENCIAL', 0.0200, 0.0100, 2026),
    ('LUCRO_PRESUMIDO', 'COMERCIAL',   0.0400, 0.0200, 2026),
    ('LUCRO_PRESUMIDO', 'RURAL',       0.0150, 0.0075, 2026),
    ('LUCRO_PRESUMIDO', 'MISTO',       0.0300, 0.0150, 2026),

    -- Lucro Real
    ('LUCRO_REAL', 'RESIDENCIAL', 0.0250, 0.0125, 2026),
    ('LUCRO_REAL', 'COMERCIAL',   0.0500, 0.0250, 2026),
    ('LUCRO_REAL', 'RURAL',       0.0200, 0.0100, 2026),
    ('LUCRO_REAL', 'MISTO',       0.0350, 0.0175, 2026)
ON CONFLICT (regime, tipo_imovel, ano_vigencia) DO NOTHING;

-- ============================================================
-- 4. Tabela de boletos (simulado — sem gateway real no PI)
-- Representa UC-003: Gerar Boleto de Aluguel
-- Armazena o detalhamento fiscal (IBS/CBS/Split Payment)
-- ============================================================
CREATE TABLE IF NOT EXISTS boletos (
    id                  UUID            DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Multi-tenancy
    imobiliaria_id      UUID            NOT NULL REFERENCES imobiliarias(id),

    -- Contrato de origem
    contrato_id         UUID            NOT NULL REFERENCES contratos_locacao(id),

    -- Valor base do aluguel neste boleto
    valor_aluguel       NUMERIC(15, 2)  NOT NULL,

    -- Alíquotas aplicadas (registradas no momento da geração — imutável)
    aliquota_ibs        NUMERIC(6, 4)   NOT NULL,
    aliquota_cbs        NUMERIC(6, 4)   NOT NULL,

    -- Valores calculados pelo MotorTributario (Split Payment)
    valor_ibs           NUMERIC(15, 4)  NOT NULL,
    valor_cbs           NUMERIC(15, 4)  NOT NULL,

    -- Valor líquido que o locador recebe após retenção
    valor_liquido       NUMERIC(15, 2)  NOT NULL,

    -- Vencimento do boleto
    data_vencimento     DATE            NOT NULL,

    -- Status: GERADO → PAGO ou VENCIDO (fluxo simplificado)
    status              VARCHAR(20)     NOT NULL DEFAULT 'GERADO'
        CHECK (status IN ('GERADO', 'PAGO', 'VENCIDO', 'CANCELADO')),

    -- Contexto fiscal registrado no momento da geração
    regime_tributario   VARCHAR(30)     NOT NULL,
    tipo_imovel         VARCHAR(20)     NOT NULL,

    -- Audit columns
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMP
);

-- Índices para consultas frequentes
CREATE INDEX IF NOT EXISTS idx_boletos_imobiliaria_id ON boletos(imobiliaria_id);
CREATE INDEX IF NOT EXISTS idx_boletos_contrato_id ON boletos(contrato_id);
CREATE INDEX IF NOT EXISTS idx_boletos_status ON boletos(status);
CREATE INDEX IF NOT EXISTS idx_boletos_data_vencimento ON boletos(data_vencimento);
