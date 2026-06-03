-- Imobiliaria demo
INSERT INTO imobiliarias (id, cnpj, razao, nome_fantasia, email, telefone, plano)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    '12345678000195',
    'ImobFiscal Gestão Imobiliária Ltda',
    'ImobFiscal Demo',
    'contato@imobfiscal.com.br',
    '(11) 4002-8922',
    'PROFISSIONAL'
);

-- Usuario admin
INSERT INTO usuarios (id, imobiliaria_id, email, senha, nome, perfil)
VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '11111111-1111-1111-1111-111111111111',
    'admin@imobfiscal.com.br',
    '$2b$10$5fPNnosb1mXAd.ubBAo5p.jHLEEnIJXObiw0No52.s0x.VwdL1dQS',
    'Administrador Demo',
    'ADMIN'
);

-- Locadores
INSERT INTO locadores (id, imobiliaria_id, tipo_pessoa, cpf_cnpj, nome, email, telefone)
VALUES
(
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'PF',
    '12345678900',
    'Carlos Eduardo Pereira',
    'carlos.pereira@email.com',
    '(11) 98765-4321'
),
(
    '33333333-3333-3333-3333-333333333333',
    '11111111-1111-1111-1111-111111111111',
    'PJ',
    '12345678000195',
    'Imóveis Centro Ltda',
    'contato@imoveiscentro.com.br',
    '(11) 3333-4444'
);

-- Imoveis
INSERT INTO imoveis (
    id, imobiliaria_id, locador_id, codigo, tipo,
    cep, logradouro, numero, complemento, bairro, cidade, uf,
    area_total, quartos, vagas,
    valor_compra, data_compra
)
VALUES
(
    '44444444-4444-4444-4444-444444444444',
    '11111111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222',
    'IDA-001',
    'RESIDENCIAL',
    '13330000', 'Rua das Flores', '100', 'Apto 12', 'Centro', 'Indaiatuba', 'SP',
    65.50, 2, 1,
    250000.00, '2020-03-15'
),
(
    '55555555-5555-5555-5555-555555555555',
    '11111111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222',
    'CAM-002',
    'RESIDENCIAL',
    '13010000', 'Rua das Acácias', '42', NULL, 'Jardim Guanabara', 'Campinas', 'SP',
    80.00, 3, 2,
    320000.00, '2019-07-20'
),
(
    '66666666-6666-6666-6666-666666666666',
    '11111111-1111-1111-1111-111111111111',
    '33333333-3333-3333-3333-333333333333',
    'SAO-003',
    'COMERCIAL',
    '01310100', 'Av. Paulista', '500', 'Sala 301', 'Bela Vista', 'São Paulo', 'SP',
    120.00, NULL, 2,
    800000.00, '2018-11-10'
);

-- Contratos
INSERT INTO contratos_locacao (
    id, imobiliaria_id, imovel_id,
    tipo_locacao, status,
    locatario_tipo, locatario_cpf_cnpj, locatario_nome,
    valor_aluguel, dia_vencimento,
    data_inicio, data_fim, prazo_meses
)
VALUES
(
    '77777777-7777-7777-7777-777777777777',
    '11111111-1111-1111-1111-111111111111',
    '44444444-4444-4444-4444-444444444444',
    'RESIDENCIAL_LONGA',
    'ATIVO',
    'PF', '98765432100', 'Roberto Alves',
    1800.00, 5,
    '2026-01-01', NULL, 30
);

-- Notas fiscais
INSERT INTO notas_fiscais (
    id, imobiliaria_id, contrato_id,
    numero, serie, chave_acesso,
    status,
    valor_servico, valor_ibs, valor_cbs,
    recolhimento_obrigatorio, tentativas
)
VALUES
(
    '88888888-8888-8888-8888-888888888888',
    '11111111-1111-1111-1111-111111111111',
    '77777777-7777-7777-7777-777777777777',
    '000000001', '1',
    '35260112345678000195550010000000011000000014',
    'AUTORIZADA',
    1800.00,
    1.80,
    16.20,
    FALSE,
    1
);
