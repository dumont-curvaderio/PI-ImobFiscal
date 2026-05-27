// MCP interno para desenvolvimento com Claude Code
// Ferramentas disponíveis para os agentes durante o desenvolvimento

import { calcularIBSCBS } from '@imobfiscal/core/fiscal';

export const MCP_TOOLS = {
  /**
   * Simula cálculo fiscal para testes durante o desenvolvimento.
   * Não acessa banco — usa alíquotas passadas como parâmetro.
   */
  simular_calculo_ibs_cbs: calcularIBSCBS,
};

// TODO: implementar servidor MCP completo com:
// - simular_calculo_fiscal(tipo, params)
// - consultar_aliquota_vigente(ano, tipo, municipio) — acessa banco local
// - gerar_nfe_homologacao(contrato_id)
// - importar_excel_preview(filepath)
// - seed_test_data(scenario)

console.log('MCP-dev tools carregados:', Object.keys(MCP_TOOLS));
