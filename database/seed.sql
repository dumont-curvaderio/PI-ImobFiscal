-- ImobFiscal — Dados de Exemplo (Seed)
-- PI 2 · FATEC DSM 2026-2
-- Executar após schema.sql: psql -U postgres -d imobfiscal -f database/seed.sql

-- ============================================================
-- USUARIOS
-- ============================================================
INSERT INTO usuarios (email, senha) VALUES
-- senha: admin123 (hash BCrypt gerado pelo sistema)
('admin@imobfiscal.com', '$2a$10$7QJ8xKZ1mNpL3vRwT5uYeO9bHcXdFgIjKlMnOpQrStUvWxYzAaBb');

-- ============================================================
-- LOCADORES
-- ============================================================
INSERT INTO locadores (nome, cpf, cnpj, regime_tributario) VALUES
('Carlos Eduardo Pereira',  '123.456.789-00', NULL,                  'PF'),
('Imóveis Centro Ltda',      NULL,             '12.345.678/0001-99', 'PJ'),
('Maria Aparecida dos Santos','987.654.321-00', NULL,                 'PF');

-- ============================================================
-- IMOVEIS
-- ============================================================
INSERT INTO imoveis (endereco, tipo_uso, valor_venal, locador_id) VALUES
('Rua das Flores, 100 — Indaiatuba/SP',       'RESIDENCIAL', 250000.00, 1),
('Av. Paulista, 500 — São Paulo/SP',           'COMERCIAL',   800000.00, 2),
('Rua das Acácias, 42 — Campinas/SP',         'RESIDENCIAL', 320000.00, 1),
('Rua Comércio, 88 — Indaiatuba/SP',          'COMERCIAL',   450000.00, 2),
('Rua das Palmeiras, 7 — Salto/SP',           'RESIDENCIAL', 180000.00, 3);

-- ============================================================
-- CONTRATOS_LOCACAO
-- ============================================================
INSERT INTO contratos_locacao (locatario_nome, valor_aluguel, data_inicio, dia_vencimento, locador_id, imovel_id) VALUES
('Roberto Alves',       1800.00, '2026-01-01', 5,  1, 1),
('Tech Solutions Ltda', 5500.00, '2026-02-01', 10, 2, 2),
('Ana Paula Silva',     2200.00, '2026-03-01', 15, 1, 3);

-- ============================================================
-- NOTAS_FISCAIS
-- Alíquotas ilustrativas conforme LC 214/2025
-- IBS: 8,75% | CBS: 8,75%
-- ============================================================
INSERT INTO notas_fiscais (data_emissao, valor_bruto, valor_liquido, aliquota_ibs, aliquota_cbs, contrato_id) VALUES
('2026-01-05', 1800.00, 1485.00, 0.0875, 0.0875, 1),
('2026-02-10', 5500.00, 4537.50, 0.0875, 0.0875, 2),
('2026-03-15', 2200.00, 1815.00, 0.0875, 0.0875, 3);
