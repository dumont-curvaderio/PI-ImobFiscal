CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Tabela imobiliarias
CREATE TABLE imobiliarias (
    id             UUID          DEFAULT gen_random_uuid() PRIMARY KEY,
    cnpj           VARCHAR(14)   NOT NULL UNIQUE,
    razao          VARCHAR(255)  NOT NULL,
    nome_fantasia  VARCHAR(255),
    email          VARCHAR(255)  NOT NULL,
    telefone       VARCHAR(255),
    plano          VARCHAR(20)   NOT NULL DEFAULT 'BASICO'
        CHECK (plano IN ('BASICO', 'PROFISSIONAL', 'ENTERPRISE')),
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at     TIMESTAMP
);

-- Tabela usuarios
CREATE TABLE usuarios (
    id              UUID          DEFAULT gen_random_uuid() PRIMARY KEY,
    imobiliaria_id  UUID          NOT NULL REFERENCES imobiliarias(id),
    email           VARCHAR(255)  NOT NULL UNIQUE,
    senha           VARCHAR(255)  NOT NULL,
    nome            VARCHAR(255)  NOT NULL,
    perfil          VARCHAR(20)   NOT NULL DEFAULT 'OPERADOR'
        CHECK (perfil IN ('ADMIN', 'GERENTE', 'OPERADOR', 'FINANCEIRO', 'READONLY')),
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP
);

-- Tabela locadores
CREATE TABLE locadores (
    id              UUID          DEFAULT gen_random_uuid() PRIMARY KEY,
    imobiliaria_id  UUID          NOT NULL REFERENCES imobiliarias(id),
    tipo_pessoa     VARCHAR(2)    NOT NULL
        CHECK (tipo_pessoa IN ('PF', 'PJ')),
    cpf_cnpj        VARCHAR(14)   NOT NULL,
    nome            VARCHAR(255)  NOT NULL,
    email           VARCHAR(255),
    telefone        VARCHAR(255),
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP
);

-- Tabela imoveis
CREATE TABLE imoveis (
    id              UUID            DEFAULT gen_random_uuid() PRIMARY KEY,
    imobiliaria_id  UUID            NOT NULL REFERENCES imobiliarias(id),
    locador_id      UUID            NOT NULL REFERENCES locadores(id),
    codigo          VARCHAR(255)    NOT NULL,
    tipo            VARCHAR(20)     NOT NULL
        CHECK (tipo IN ('RESIDENCIAL', 'COMERCIAL', 'RURAL', 'MISTO')),
    cep             CHAR(8)         NOT NULL,
    logradouro      VARCHAR(255)    NOT NULL,
    numero          VARCHAR(255)    NOT NULL,
    complemento     VARCHAR(255),
    bairro          VARCHAR(255)    NOT NULL,
    cidade          VARCHAR(255)    NOT NULL,
    uf              CHAR(2)         NOT NULL,
    area_total      NUMERIC(12, 2),
    quartos         INTEGER,
    vagas           INTEGER,
    valor_compra    NUMERIC(15, 2),
    data_compra     DATE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP
);

-- Tabela contratos_locacao
CREATE TABLE contratos_locacao (
    id                   UUID            DEFAULT gen_random_uuid() PRIMARY KEY,
    imobiliaria_id       UUID            NOT NULL REFERENCES imobiliarias(id),
    imovel_id            UUID            NOT NULL REFERENCES imoveis(id),
    tipo_locacao         VARCHAR(20)     NOT NULL
        CHECK (tipo_locacao IN ('RESIDENCIAL_LONGA', 'COMERCIAL', 'SHORT_STAY', 'RURAL')),
    status               VARCHAR(20)     NOT NULL DEFAULT 'RASCUNHO'
        CHECK (status IN ('RASCUNHO', 'ATIVO', 'RESCINDIDO', 'ENCERRADO')),
    locatario_tipo       VARCHAR(2)      NOT NULL
        CHECK (locatario_tipo IN ('PF', 'PJ')),
    locatario_cpf_cnpj   VARCHAR(14)     NOT NULL,
    locatario_nome       VARCHAR(255)    NOT NULL,
    valor_aluguel        NUMERIC(15, 2)  NOT NULL,
    dia_vencimento       INTEGER         NOT NULL
        CHECK (dia_vencimento BETWEEN 1 AND 31),
    data_inicio          DATE            NOT NULL,
    data_fim             DATE,
    prazo_meses          INTEGER,
    created_at           TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted_at           TIMESTAMP
);

-- Tabela notas_fiscais
CREATE TABLE notas_fiscais (
    id                       UUID            DEFAULT gen_random_uuid() PRIMARY KEY,
    imobiliaria_id           UUID            NOT NULL REFERENCES imobiliarias(id),
    contrato_id              UUID            NOT NULL REFERENCES contratos_locacao(id),
    numero                   VARCHAR(255),
    serie                    VARCHAR(255),
    chave_acesso             VARCHAR(44)     UNIQUE,
    status                   VARCHAR(20)     NOT NULL DEFAULT 'AGUARDANDO'
        CHECK (status IN ('AGUARDANDO', 'PROCESSANDO', 'AUTORIZADA', 'REJEITADA', 'CANCELADA')),
    valor_servico            NUMERIC(15, 2)  NOT NULL,
    valor_ibs                NUMERIC(15, 4)  NOT NULL DEFAULT 0,
    valor_cbs                NUMERIC(15, 4)  NOT NULL DEFAULT 0,
    recolhimento_obrigatorio BOOLEAN         NOT NULL DEFAULT FALSE,
    tentativas               INTEGER         NOT NULL DEFAULT 0,
    erro_sefaz               TEXT,
    created_at               TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted_at               TIMESTAMP
);
