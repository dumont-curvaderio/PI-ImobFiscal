-- V2: Motor Tributário + Boletos

-- Regime tributário em locadores
ALTER TABLE locadores
    ADD COLUMN IF NOT EXISTS regime_tributario VARCHAR(30)
        CHECK (regime_tributario IN ('PF', 'SIMPLES_NACIONAL', 'LUCRO_PRESUMIDO', 'LUCRO_REAL'));

-- Valor venal em imoveis
ALTER TABLE imoveis
    ADD COLUMN IF NOT EXISTS valor_venal NUMERIC(15, 2);

-- Tabela aliquotas_vigentes
CREATE TABLE IF NOT EXISTS aliquotas_vigentes (
    id              UUID            DEFAULT gen_random_uuid() PRIMARY KEY,
    regime          VARCHAR(30)     NOT NULL
        CHECK (regime IN ('PF', 'SIMPLES_NACIONAL', 'LUCRO_PRESUMIDO', 'LUCRO_REAL')),
    tipo_imovel     VARCHAR(20)     NOT NULL
        CHECK (tipo_imovel IN ('RESIDENCIAL', 'COMERCIAL', 'RURAL', 'MISTO')),
    aliquota_ibs    NUMERIC(6, 4)   NOT NULL,
    aliquota_cbs    NUMERIC(6, 4)   NOT NULL,
    ano_vigencia    INTEGER         NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    UNIQUE (regime, tipo_imovel, ano_vigencia)
);

INSERT INTO aliquotas_vigentes (regime, tipo_imovel, aliquota_ibs, aliquota_cbs, ano_vigencia)
VALUES
    ('PF', 'RESIDENCIAL', 0.0145, 0.0076, 2026),
    ('PF', 'COMERCIAL',   0.0290, 0.0153, 2026),
    ('PF', 'RURAL',       0.0100, 0.0050, 2026),
    ('PF', 'MISTO',       0.0200, 0.0100, 2026),

    ('SIMPLES_NACIONAL', 'RESIDENCIAL', 0.0145, 0.0076, 2026),
    ('SIMPLES_NACIONAL', 'COMERCIAL',   0.0290, 0.0153, 2026),
    ('SIMPLES_NACIONAL', 'RURAL',       0.0100, 0.0050, 2026),
    ('SIMPLES_NACIONAL', 'MISTO',       0.0200, 0.0100, 2026),

    ('LUCRO_PRESUMIDO', 'RESIDENCIAL', 0.0200, 0.0100, 2026),
    ('LUCRO_PRESUMIDO', 'COMERCIAL',   0.0400, 0.0200, 2026),
    ('LUCRO_PRESUMIDO', 'RURAL',       0.0150, 0.0075, 2026),
    ('LUCRO_PRESUMIDO', 'MISTO',       0.0300, 0.0150, 2026),

    ('LUCRO_REAL', 'RESIDENCIAL', 0.0250, 0.0125, 2026),
    ('LUCRO_REAL', 'COMERCIAL',   0.0500, 0.0250, 2026),
    ('LUCRO_REAL', 'RURAL',       0.0200, 0.0100, 2026),
    ('LUCRO_REAL', 'MISTO',       0.0350, 0.0175, 2026)
ON CONFLICT (regime, tipo_imovel, ano_vigencia) DO NOTHING;

-- Tabela boletos
CREATE TABLE IF NOT EXISTS boletos (
    id                  UUID            DEFAULT gen_random_uuid() PRIMARY KEY,
    imobiliaria_id      UUID            NOT NULL REFERENCES imobiliarias(id),
    contrato_id         UUID            NOT NULL REFERENCES contratos_locacao(id),
    valor_aluguel       NUMERIC(15, 2)  NOT NULL,
    aliquota_ibs        NUMERIC(6, 4)   NOT NULL,
    aliquota_cbs        NUMERIC(6, 4)   NOT NULL,
    valor_ibs           NUMERIC(15, 4)  NOT NULL,
    valor_cbs           NUMERIC(15, 4)  NOT NULL,
    valor_liquido       NUMERIC(15, 2)  NOT NULL,
    data_vencimento     DATE            NOT NULL,
    status              VARCHAR(20)     NOT NULL DEFAULT 'GERADO'
        CHECK (status IN ('GERADO', 'PAGO', 'VENCIDO', 'CANCELADO')),
    regime_tributario   VARCHAR(30)     NOT NULL,
    tipo_imovel         VARCHAR(20)     NOT NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_boletos_imobiliaria_id ON boletos(imobiliaria_id);
CREATE INDEX IF NOT EXISTS idx_boletos_contrato_id ON boletos(contrato_id);
CREATE INDEX IF NOT EXISTS idx_boletos_status ON boletos(status);
CREATE INDEX IF NOT EXISTS idx_boletos_data_vencimento ON boletos(data_vencimento);
