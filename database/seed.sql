-- ImobFiscal — Dados de Exemplo (Seed)
-- PI 2 · FATEC DSM 2026-2
-- Executar após schema.sql: psql -U postgres -d imobfiscal -f database/seed.sql
--
-- UUID da imobiliária demo é FIXO ('11111111-...') — referenciado pelo frontend

-- ============================================================
-- IMOBILIARIAS
-- UUID fixo: '11111111-1111-1111-1111-111111111111'
-- Usado como tenant padrão em todos os registros abaixo
-- ============================================================
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

-- ============================================================
-- USUARIOS
-- Usuário administrador da imobiliária demo
-- Senha: admin123 (hash BCrypt gerado pelo sistema)
-- ============================================================
INSERT INTO usuarios (id, imobiliaria_id, email, senha, nome, perfil)
VALUES (
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    '11111111-1111-1111-1111-111111111111',
    'admin@imobfiscal.com.br',
    '$2a$10$7QJ8xKZ1mNpL3vRwT5uYeO9bHcXdFgIjKlMnOpQrStUvWxYzAaBb',
    'Administrador Demo',
    'ADMIN'
);

-- ============================================================
-- LOCADORES
-- Dois proprietários: uma PF e uma PJ
-- ============================================================
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

-- ============================================================
-- IMOVEIS
-- Três imóveis: dois do locador PF, um do locador PJ
-- ============================================================
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

-- ============================================================
-- CONTRATOS_LOCACAO
-- Um contrato ativo por imóvel residencial
-- ============================================================
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

-- ============================================================
-- NOTAS_FISCAIS
-- Uma NF autorizada para o contrato acima
-- Alíquotas 2026: IBS 0,1% | CBS 0,9% (fase informativa — RN-003)
-- recolhimento_obrigatorio = false até 2027
-- ============================================================
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
    1.80,   -- IBS: 0,1% de 1800,00
    16.20,  -- CBS: 0,9% de 1800,00
    FALSE,
    1
);
