-- Seed: alíquotas vigentes 2026
-- Fonte: LC 214/2025, art. 9º e Tabela de Transição IBS/CBS
-- Última atualização: mai/2026
-- Executar: pnpm db:seed

-- Este arquivo é um backup/referência do seed em 20260526_002_aliquotas_vigentes.sql
-- Para aplicar, use: pnpm supabase db reset (dev apenas) ou pnpm db:seed

SELECT * FROM aliquotas_vigentes WHERE ano IN (2026, 2027) ORDER BY ano, tipo_operacao;
