// packages/core/fiscal/ibs-cbs.calculator.ts
// Lógica PURA — sem imports de framework, banco ou serviços externos.
// Referência: docs/knowledge-base-fiscal-imobiliario.md

export type TipoLocacao = 'RESIDENCIAL_LONGA' | 'COMERCIAL' | 'SHORT_STAY' | 'RURAL';

export interface AliquotaVigente {
  aliquota_ibs: number;
  aliquota_cbs: number;
  fator_reducao: number;
  redutor_social?: number;
  recolhimento_obrigatorio: boolean;
}

export interface InputIBSCBS {
  valorAluguel: number;
  tipoLocacao: TipoLocacao;
  aliquota: AliquotaVigente;
  locadorEhContribuinte: boolean; // RN-001
}

export interface ResultadoIBSCBS {
  baseCalculo: number;
  aliquotaIBSEfetiva: number;
  aliquotaCBSEfetiva: number;
  valorIBS: number;
  valorCBS: number;
  valorTotal: number;
  isento: boolean;
  motivoIsencao?: string;
  recolhimentoObrigatorio: boolean;
  memoriaCalculo: string[];
}

export function calcularIBSCBS(input: InputIBSCBS): ResultadoIBSCBS {
  const mem: string[] = [];

  // RN-001: PF não-contribuinte → isento (locador com ≤3 imóveis OU ≤R$240k/ano)
  if (!input.locadorEhContribuinte) {
    mem.push('RN-001: locador não é contribuinte IBS/CBS → isento');
    return isento('RN-001: locador não é contribuinte (≤3 imóveis ou ≤R$240k/ano)', mem, input.aliquota.recolhimento_obrigatorio);
  }

  // RN-002: isenção residencial longa permanência ≤ R$2.500/mês
  if (input.tipoLocacao === 'RESIDENCIAL_LONGA' && input.valorAluguel <= 2500) {
    mem.push(`RN-002: residencial ≤ R$2.500 (valor=${input.valorAluguel}) → isento`);
    return isento('RN-002: aluguel residencial ≤ R$2.500/mês', mem, input.aliquota.recolhimento_obrigatorio);
  }

  // RN-004: Short stay ≤ 90 dias usa alíquota diferente (40% da referência via fator_reducao)
  // O fator_reducao correto (0.30 ou 0.40) já vem de aliquotas_vigentes conforme o tipo
  const { aliquota_ibs, aliquota_cbs, fator_reducao, redutor_social = 0, recolhimento_obrigatorio } = input.aliquota;

  mem.push(`Alíquota referência: IBS=${aliquota_ibs} CBS=${aliquota_cbs}`);
  mem.push(`Fator redução: ${fator_reducao} (${input.tipoLocacao})`); // RN-003/RN-004

  // RN-003: alíquota efetiva = referência × fator_reducao
  const ibsEfetivo = aliquota_ibs * fator_reducao;
  const cbsEfetivo = aliquota_cbs * fator_reducao;
  mem.push(`Alíquota efetiva: IBS=${ibsEfetivo.toFixed(6)} CBS=${cbsEfetivo.toFixed(6)}`);

  // Redutor social (residencial acima do limite)
  const baseCalculo = Math.max(0, input.valorAluguel - redutor_social);
  if (redutor_social > 0) {
    mem.push(`Redutor social: R$${redutor_social} → base=${baseCalculo}`);
  }

  const valorIBS = baseCalculo * ibsEfetivo;
  const valorCBS = baseCalculo * cbsEfetivo;
  const valorTotal = valorIBS + valorCBS;

  mem.push(`Resultado: IBS=${valorIBS.toFixed(4)} CBS=${valorCBS.toFixed(4)} Total=${valorTotal.toFixed(4)}`);

  return {
    baseCalculo,
    aliquotaIBSEfetiva: ibsEfetivo,
    aliquotaCBSEfetiva: cbsEfetivo,
    valorIBS,
    valorCBS,
    valorTotal,
    isento: false,
    recolhimentoObrigatorio: recolhimento_obrigatorio,
    memoriaCalculo: mem,
  };
}

function isento(motivo: string, mem: string[], recolhimentoObrigatorio: boolean): ResultadoIBSCBS {
  return {
    baseCalculo: 0,
    aliquotaIBSEfetiva: 0,
    aliquotaCBSEfetiva: 0,
    valorIBS: 0,
    valorCBS: 0,
    valorTotal: 0,
    isento: true,
    motivoIsencao: motivo,
    recolhimentoObrigatorio,
    memoriaCalculo: mem,
  };
}
