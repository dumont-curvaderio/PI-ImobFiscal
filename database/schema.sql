-- ImobFiscal — Schema do Banco de Dados
-- PI 2 · FATEC DSM 2026-2
-- Gerado a partir das entidades JPA em backend/src/main/java/br/fatec/imobfiscal/entity/
-- Executar: psql -U postgres -d imobfiscal -f database/schema.sql

-- Extensão necessária para gen_random_uuid() no PostgreSQL 13+
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ============================================================
-- IMOBILIARIAS
-- Tenant raiz do sistema — criada PRIMEIRO pois todas as
-- demais tabelas referenciam imobiliaria_id
-- Entidade: Imobiliaria.java | @Table(name = "imobiliarias")
-- ============================================================
CREATE TABLE imobiliarias (
    id             UUID          DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Dados cadastrais
    cnpj           VARCHAR(14)   NOT NULL UNIQUE,           -- @Column(unique=true, length=14)
    razao          VARCHAR(255)  NOT NULL,
    nome_fantasia  VARCHAR(255),                            -- @Column(name="nome_fantasia")
    email          VARCHAR(255)  NOT NULL,
    telefone       VARCHAR(255),

    -- Plano de assinatura: BASICO | PROFISSIONAL | ENTERPRISE
    plano          VARCHAR(20)   NOT NULL DEFAULT 'BASICO'
        CHECK (plano IN ('BASICO', 'PROFISSIONAL', 'ENTERPRISE')),

    -- Audit columns herdadas de BaseEntity
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at     TIMESTAMP
);

-- ============================================================
-- USUARIOS
-- Usuários autenticados — cada um pertence a uma imobiliária
-- Entidade: Usuario.java | @Table(name = "usuarios")
-- ============================================================
CREATE TABLE usuarios (
    id              UUID          DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Multi-tenancy: FK para a imobiliária dona deste usuário
    imobiliaria_id  UUID          NOT NULL REFERENCES imobiliarias(id),

    email           VARCHAR(255)  NOT NULL UNIQUE,
    senha           VARCHAR(255)  NOT NULL,   -- hash BCrypt — nunca texto puro

    nome            VARCHAR(255)  NOT NULL,

    -- Perfil: ADMIN | GERENTE | OPERADOR | FINANCEIRO | READONLY
    perfil          VARCHAR(20)   NOT NULL DEFAULT 'OPERADOR'
        CHECK (perfil IN ('ADMIN', 'GERENTE', 'OPERADOR', 'FINANCEIRO', 'READONLY')),

    -- Audit columns herdadas de BaseEntity
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP
);

-- ============================================================
-- LOCADORES
-- Proprietários de imóveis — Pessoa Física ou Jurídica
-- Entidade: Locador.java | @Table(name = "locadores")
-- Obs: imobiliaria_id é @Column puro (UUID raw), não @ManyToOne
-- ============================================================
CREATE TABLE locadores (
    id              UUID          DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Multi-tenancy: armazenado como UUID direto conforme a entidade
    imobiliaria_id  UUID          NOT NULL REFERENCES imobiliarias(id),

    -- Tipo de pessoa: PF | PJ
    tipo_pessoa     VARCHAR(2)    NOT NULL
        CHECK (tipo_pessoa IN ('PF', 'PJ')),             -- @Column(name="tipo_pessoa")

    cpf_cnpj        VARCHAR(14)   NOT NULL,               -- @Column(name="cpf_cnpj", length=14)

    nome            VARCHAR(255)  NOT NULL,
    email           VARCHAR(255),
    telefone        VARCHAR(255),

    -- Audit columns herdadas de BaseEntity
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP
);

-- ============================================================
-- IMOVEIS
-- Imóveis vinculados a um Locador e a uma Imobiliária
-- Entidade: Imovel.java | @Table(name = "imoveis")
-- ============================================================
CREATE TABLE imoveis (
    id              UUID            DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Multi-tenancy: @JoinColumn(name="imobiliaria_id")
    imobiliaria_id  UUID            NOT NULL REFERENCES imobiliarias(id),

    -- Proprietário: @JoinColumn(name="locador_id")
    locador_id      UUID            NOT NULL REFERENCES locadores(id),

    codigo          VARCHAR(255)    NOT NULL,

    -- Tipo: RESIDENCIAL | COMERCIAL | RURAL | MISTO
    -- @Enumerated(EnumType.STRING)
    tipo            VARCHAR(20)     NOT NULL
        CHECK (tipo IN ('RESIDENCIAL', 'COMERCIAL', 'RURAL', 'MISTO')),

    -- Endereço (campos separados — não usar coluna única "endereco")
    cep             CHAR(8)         NOT NULL,             -- @Column(length=8)
    logradouro      VARCHAR(255)    NOT NULL,
    numero          VARCHAR(255)    NOT NULL,
    complemento     VARCHAR(255),
    bairro          VARCHAR(255)    NOT NULL,
    cidade          VARCHAR(255)    NOT NULL,
    uf              CHAR(2)         NOT NULL,             -- @Column(length=2)

    -- Dados físicos
    area_total      NUMERIC(12, 2),                      -- @Column(name="area_total")
    quartos         INTEGER,
    vagas           INTEGER,

    -- Dados fiscais GCAP
    valor_compra    NUMERIC(15, 2),                      -- @Column(name="valor_compra")
    data_compra     DATE,                                -- @Column(name="data_compra")

    -- Audit columns herdadas de BaseEntity
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted_at      TIMESTAMP
);

-- ============================================================
-- CONTRATOS_LOCACAO
-- Contrato entre Locador (via imóvel) e Locatário
-- Entidade: ContratoLocacao.java | @Table(name = "contratos_locacao")
-- ============================================================
CREATE TABLE contratos_locacao (
    id                   UUID            DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Multi-tenancy: @JoinColumn(name="imobiliaria_id")
    imobiliaria_id       UUID            NOT NULL REFERENCES imobiliarias(id),

    -- Imóvel objeto do contrato: @JoinColumn(name="imovel_id")
    imovel_id            UUID            NOT NULL REFERENCES imoveis(id),

    -- Tipo de locação: RESIDENCIAL_LONGA | COMERCIAL | SHORT_STAY | RURAL
    tipo_locacao         VARCHAR(20)     NOT NULL
        CHECK (tipo_locacao IN ('RESIDENCIAL_LONGA', 'COMERCIAL', 'SHORT_STAY', 'RURAL')),

    -- Status: RASCUNHO | ATIVO | RESCINDIDO | ENCERRADO
    status               VARCHAR(20)     NOT NULL DEFAULT 'RASCUNHO'
        CHECK (status IN ('RASCUNHO', 'ATIVO', 'RESCINDIDO', 'ENCERRADO')),

    -- Dados do locatário (desnormalizados para histórico fiscal)
    locatario_tipo       VARCHAR(2)      NOT NULL
        CHECK (locatario_tipo IN ('PF', 'PJ')),          -- @Column(name="locatario_tipo")
    locatario_cpf_cnpj   VARCHAR(14)     NOT NULL,        -- @Column(name="locatario_cpf_cnpj")
    locatario_nome       VARCHAR(255)    NOT NULL,        -- @Column(name="locatario_nome")

    -- Dados financeiros
    valor_aluguel        NUMERIC(15, 2)  NOT NULL,        -- @Column(name="valor_aluguel")
    dia_vencimento       INTEGER         NOT NULL
        CHECK (dia_vencimento BETWEEN 1 AND 31),         -- @Column(name="dia_vencimento")

    -- Vigência
    data_inicio          DATE            NOT NULL,        -- @Column(name="data_inicio")
    data_fim             DATE,                            -- @Column(name="data_fim")
    prazo_meses          INTEGER,                         -- @Column(name="prazo_meses")

    -- Audit columns herdadas de BaseEntity
    created_at           TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted_at           TIMESTAMP
);

-- ============================================================
-- NOTAS_FISCAIS
-- Registro fiscal gerado para cada competência de um contrato
-- Entidade: NotaFiscal.java | @Table(name = "notas_fiscais")
-- Obs: imobiliaria_id é @Column puro (UUID raw), não @ManyToOne
-- ============================================================
CREATE TABLE notas_fiscais (
    id                       UUID            DEFAULT gen_random_uuid() PRIMARY KEY,

    -- Multi-tenancy: armazenado como UUID direto conforme a entidade
    imobiliaria_id           UUID            NOT NULL REFERENCES imobiliarias(id),

    -- Contrato de origem: @JoinColumn(name="contrato_id")
    contrato_id              UUID            NOT NULL REFERENCES contratos_locacao(id),

    -- Identificação na SEFAZ
    numero                   VARCHAR(255),
    serie                    VARCHAR(255),
    chave_acesso             VARCHAR(44)     UNIQUE,      -- @Column(name="chave_acesso")

    -- Status: AGUARDANDO | PROCESSANDO | AUTORIZADA | REJEITADA | CANCELADA
    status                   VARCHAR(20)     NOT NULL DEFAULT 'AGUARDANDO'
        CHECK (status IN ('AGUARDANDO', 'PROCESSANDO', 'AUTORIZADA', 'REJEITADA', 'CANCELADA')),

    -- Valores fiscais
    valor_servico            NUMERIC(15, 2)  NOT NULL,    -- @Column(name="valor_servico")

    -- IBS/CBS: informativos em 2026, obrigatórios a partir de 2027 (RN-003/RN-004)
    valor_ibs                NUMERIC(15, 4)  NOT NULL DEFAULT 0, -- @Column(name="valor_ibs")
    valor_cbs                NUMERIC(15, 4)  NOT NULL DEFAULT 0, -- @Column(name="valor_cbs")

    -- false em 2026 (fase de testes), true a partir de 2027
    recolhimento_obrigatorio BOOLEAN         NOT NULL DEFAULT FALSE,

    -- Controle de retry SEFAZ (max 5 tentativas com backoff exponencial)
    tentativas               INTEGER         NOT NULL DEFAULT 0,
    erro_sefaz               TEXT,                        -- @Column(name="erro_sefaz")

    -- Audit columns herdadas de BaseEntity
    created_at               TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP       NOT NULL DEFAULT NOW(),
    deleted_at               TIMESTAMP
);
