-- Schema MySQL — ImobFiscal

CREATE TABLE IF NOT EXISTS imobiliarias (
    id             VARCHAR(36)   DEFAULT (UUID()) PRIMARY KEY,
    cnpj           VARCHAR(14)   NOT NULL UNIQUE,
    razao          VARCHAR(255)  NOT NULL,
    nome_fantasia  VARCHAR(255),
    email          VARCHAR(255)  NOT NULL,
    telefone       VARCHAR(255),
    plano          VARCHAR(20)   NOT NULL DEFAULT 'BASICO',
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at     DATETIME
);

CREATE TABLE IF NOT EXISTS usuarios (
    id              VARCHAR(36)   DEFAULT (UUID()) PRIMARY KEY,
    imobiliaria_id  VARCHAR(36)   NOT NULL,
    email           VARCHAR(255)  NOT NULL UNIQUE,
    senha           VARCHAR(255)  NOT NULL,
    nome            VARCHAR(255)  NOT NULL,
    perfil          VARCHAR(20)   NOT NULL DEFAULT 'OPERADOR',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME,
    FOREIGN KEY (imobiliaria_id) REFERENCES imobiliarias(id)
);

CREATE TABLE IF NOT EXISTS locadores (
    id              VARCHAR(36)   DEFAULT (UUID()) PRIMARY KEY,
    imobiliaria_id  VARCHAR(36)   NOT NULL,
    tipo_pessoa     VARCHAR(2)    NOT NULL,
    cpf_cnpj        VARCHAR(14)   NOT NULL,
    nome            VARCHAR(255)  NOT NULL,
    email           VARCHAR(255),
    telefone        VARCHAR(255),
    regime_tributario VARCHAR(30),
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME,
    FOREIGN KEY (imobiliaria_id) REFERENCES imobiliarias(id)
);

CREATE TABLE IF NOT EXISTS imoveis (
    id              VARCHAR(36)   DEFAULT (UUID()) PRIMARY KEY,
    imobiliaria_id  VARCHAR(36)   NOT NULL,
    locador_id      VARCHAR(36),
    codigo          VARCHAR(255)  NOT NULL UNIQUE,
    tipo            VARCHAR(20)   NOT NULL,
    cep             CHAR(8)       NOT NULL,
    logradouro      VARCHAR(255)  NOT NULL,
    numero          VARCHAR(255)  NOT NULL,
    complemento     VARCHAR(255),
    bairro          VARCHAR(255)  NOT NULL,
    cidade          VARCHAR(255)  NOT NULL,
    uf              CHAR(2)       NOT NULL,
    area_total      DECIMAL(12, 2),
    quartos         INT,
    vagas           INT,
    valor_compra    DECIMAL(15, 2),
    data_compra     DATE,
    valor_venal     DECIMAL(15, 2),
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME,
    FOREIGN KEY (imobiliaria_id) REFERENCES imobiliarias(id),
    FOREIGN KEY (locador_id) REFERENCES locadores(id)
);

CREATE TABLE IF NOT EXISTS contratos_locacao (
    id                   VARCHAR(36)   DEFAULT (UUID()) PRIMARY KEY,
    imobiliaria_id       VARCHAR(36)   NOT NULL,
    imovel_id            VARCHAR(36)   NOT NULL,
    tipo_locacao         VARCHAR(20)   NOT NULL,
    status               VARCHAR(20)   NOT NULL DEFAULT 'RASCUNHO',
    locatario_tipo       VARCHAR(2)    NOT NULL,
    locatario_cpf_cnpj   VARCHAR(14)   NOT NULL,
    locatario_nome       VARCHAR(255)  NOT NULL,
    valor_aluguel        DECIMAL(15, 2) NOT NULL,
    dia_vencimento       INT           NOT NULL,
    data_inicio          DATE          NOT NULL,
    data_fim             DATE,
    prazo_meses          INT,
    created_at           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at           DATETIME,
    FOREIGN KEY (imobiliaria_id) REFERENCES imobiliarias(id),
    FOREIGN KEY (imovel_id) REFERENCES imoveis(id)
);

CREATE TABLE IF NOT EXISTS notas_fiscais (
    id                       VARCHAR(36)   DEFAULT (UUID()) PRIMARY KEY,
    imobiliaria_id           VARCHAR(36)   NOT NULL,
    contrato_id              VARCHAR(36)   NOT NULL,
    numero                   VARCHAR(255),
    serie                    VARCHAR(255),
    chave_acesso             VARCHAR(44)   UNIQUE,
    status                   VARCHAR(20)   NOT NULL DEFAULT 'AGUARDANDO',
    valor_servico            DECIMAL(15, 2) NOT NULL,
    valor_ibs                DECIMAL(15, 4) NOT NULL DEFAULT 0,
    valor_cbs                DECIMAL(15, 4) NOT NULL DEFAULT 0,
    recolhimento_obrigatorio TINYINT(1)    NOT NULL DEFAULT 0,
    tentativas               INT           NOT NULL DEFAULT 0,
    erro_sefaz               TEXT,
    created_at               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at               DATETIME,
    FOREIGN KEY (imobiliaria_id) REFERENCES imobiliarias(id),
    FOREIGN KEY (contrato_id) REFERENCES contratos_locacao(id)
);

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
