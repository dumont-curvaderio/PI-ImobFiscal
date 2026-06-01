-- ImobFiscal — Schema do Banco de Dados
-- PI 2 · FATEC DSM 2026-2
-- Executar: psql -U postgres -d imobfiscal -f database/schema.sql

-- ============================================================
-- USUARIOS
-- Usuários autenticados no sistema (AdmImobiliaria)
-- ============================================================
CREATE TABLE usuarios (
    id        BIGSERIAL    PRIMARY KEY,
    email     VARCHAR(150) NOT NULL UNIQUE,
    senha     VARCHAR(255) NOT NULL,       -- hash BCrypt
    criado_em TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
-- LOCADORES
-- Proprietários de imóveis — Pessoa Física ou Jurídica
-- ============================================================
CREATE TABLE locadores (
    id                BIGSERIAL   PRIMARY KEY,
    nome              VARCHAR(150) NOT NULL,
    cpf               VARCHAR(14),                -- somente para PF
    cnpj              VARCHAR(18),                -- somente para PJ / Simples
    regime_tributario VARCHAR(20) NOT NULL
        CHECK (regime_tributario IN ('PF', 'PJ', 'SIMPLES')),
    criado_em         TIMESTAMP   NOT NULL DEFAULT NOW(),
    deleted_at        TIMESTAMP                   -- soft delete
);

-- ============================================================
-- IMOVEIS
-- Imóveis vinculados a um Locador
-- ============================================================
CREATE TABLE imoveis (
    id          BIGSERIAL    PRIMARY KEY,
    endereco    VARCHAR(255) NOT NULL,
    tipo_uso    VARCHAR(20)  NOT NULL
        CHECK (tipo_uso IN ('RESIDENCIAL', 'COMERCIAL')),
    valor_venal NUMERIC(15, 2) NOT NULL,
    locador_id  BIGINT       NOT NULL REFERENCES locadores(id),
    criado_em   TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_at  TIMESTAMP
);

-- ============================================================
-- CONTRATOS_LOCACAO
-- Contrato entre Locador e Locatário para um Imóvel
-- ============================================================
CREATE TABLE contratos_locacao (
    id             BIGSERIAL     PRIMARY KEY,
    locatario_nome VARCHAR(150)  NOT NULL,
    valor_aluguel  NUMERIC(15,2) NOT NULL,
    data_inicio    DATE          NOT NULL,
    dia_vencimento SMALLINT      NOT NULL CHECK (dia_vencimento BETWEEN 1 AND 31),
    locador_id     BIGINT        NOT NULL REFERENCES locadores(id),
    imovel_id      BIGINT        NOT NULL REFERENCES imoveis(id),
    criado_em      TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at     TIMESTAMP
);

-- ============================================================
-- NOTAS_FISCAIS
-- Registro tributário calculado para um Contrato
-- ============================================================
CREATE TABLE notas_fiscais (
    id            BIGSERIAL     PRIMARY KEY,
    data_emissao  DATE          NOT NULL,
    valor_bruto   NUMERIC(15,2) NOT NULL,
    valor_liquido NUMERIC(15,2) NOT NULL,
    aliquota_ibs  NUMERIC(5,4)  NOT NULL,   -- ex: 0.0875 = 8,75%
    aliquota_cbs  NUMERIC(5,4)  NOT NULL,   -- ex: 0.0875 = 8,75%
    contrato_id   BIGINT        NOT NULL REFERENCES contratos_locacao(id),
    criado_em     TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at    TIMESTAMP
);
