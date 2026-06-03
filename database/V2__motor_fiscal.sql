-- V2: Motor Tributário + Boletos (MySQL)

-- Regime tributário em locadores
ALTER TABLE locadores
    ADD COLUMN IF NOT EXISTS regime_tributario VARCHAR(30);

-- Valor venal em imoveis
ALTER TABLE imoveis
    ADD COLUMN IF NOT EXISTS valor_venal DECIMAL(15, 2);

-- Tabela aliquotas_vigentes
CREATE TABLE IF NOT EXISTS aliquotas_vigentes (
    id              VARCHAR(36)   DEFAULT (UUID()) PRIMARY KEY,
    regime          VARCHAR(30)   NOT NULL,
    tipo_imovel     VARCHAR(20)   NOT NULL,
    aliquota_ibs    DECIMAL(6, 4) NOT NULL,
    aliquota_cbs    DECIMAL(6, 4) NOT NULL,
    ano_vigencia    INT           NOT NULL,
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_aliquota (regime, tipo_imovel, ano_vigencia)
);

INSERT IGNORE INTO aliquotas_vigentes (regime, tipo_imovel, aliquota_ibs, aliquota_cbs, ano_vigencia) VALUES
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
    ('LUCRO_REAL', 'MISTO',       0.0350, 0.0175, 2026);

-- Tabela boletos
CREATE TABLE IF NOT EXISTS boletos (
    id                  VARCHAR(36)   DEFAULT (UUID()) PRIMARY KEY,
    imobiliaria_id      VARCHAR(36)   NOT NULL,
    contrato_id         VARCHAR(36)   NOT NULL,
    valor_aluguel       DECIMAL(15, 2) NOT NULL,
    aliquota_ibs        DECIMAL(6, 4)  NOT NULL,
    aliquota_cbs        DECIMAL(6, 4)  NOT NULL,
    valor_ibs           DECIMAL(15, 4) NOT NULL,
    valor_cbs           DECIMAL(15, 4) NOT NULL,
    valor_liquido       DECIMAL(15, 2) NOT NULL,
    data_vencimento     DATE           NOT NULL,
    status              VARCHAR(20)    NOT NULL DEFAULT 'GERADO',
    regime_tributario   VARCHAR(30)    NOT NULL,
    tipo_imovel         VARCHAR(20)    NOT NULL,
    created_at          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          DATETIME,
    FOREIGN KEY (imobiliaria_id) REFERENCES imobiliarias(id),
    FOREIGN KEY (contrato_id) REFERENCES contratos_locacao(id)
);

CREATE INDEX IF NOT EXISTS idx_boletos_imobiliaria_id ON boletos(imobiliaria_id);
CREATE INDEX IF NOT EXISTS idx_boletos_contrato_id ON boletos(contrato_id);
CREATE INDEX IF NOT EXISTS idx_boletos_status ON boletos(status);
CREATE INDEX IF NOT EXISTS idx_boletos_data_vencimento ON boletos(data_vencimento);
