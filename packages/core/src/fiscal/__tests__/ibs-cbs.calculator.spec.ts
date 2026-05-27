import { describe, it, expect } from 'vitest';
import { calcularIBSCBS, type InputIBSCBS, type AliquotaVigente } from '../ibs-cbs.calculator';

// Alíquotas 2026 — fase de testes, recolhimento dispensado
const ALIQUOTA_2026_RESIDENCIAL: AliquotaVigente = {
  aliquota_ibs: 0.001,   // 0,1%
  aliquota_cbs: 0.009,   // 0,9%
  fator_reducao: 0.30,   // RN-003: 70% redução
  redutor_social: 600,   // residencial
  recolhimento_obrigatorio: false,
};

const ALIQUOTA_2026_CURTA_ESTADIA: AliquotaVigente = {
  aliquota_ibs: 0.001,
  aliquota_cbs: 0.009,
  fator_reducao: 0.40,   // RN-004: short stay = 40% da referência
  recolhimento_obrigatorio: false,
};

const ALIQUOTA_2026_COMERCIAL: AliquotaVigente = {
  aliquota_ibs: 0.001,
  aliquota_cbs: 0.009,
  fator_reducao: 0.30,
  recolhimento_obrigatorio: false,
};

describe('calcularIBSCBS — 2026', () => {
  // ─── RN-001: não-contribuinte ──────────────────────────────────────────────

  it('RN-001: locador não-contribuinte deve ser isento', () => {
    const input: InputIBSCBS = {
      valorAluguel: 5000,
      tipoLocacao: 'RESIDENCIAL_LONGA',
      aliquota: ALIQUOTA_2026_RESIDENCIAL,
      locadorEhContribuinte: false,
    };
    const r = calcularIBSCBS(input);
    expect(r.isento).toBe(true);
    expect(r.motivoIsencao).toContain('RN-001');
    expect(r.valorTotal).toBe(0);
    expect(r.memoriaCalculo).not.toHaveLength(0);
  });

  // ─── RN-002: isenção residencial ≤ R$2.500 ────────────────────────────────

  it('RN-002: residencial R$2.500,00 exato → isento', () => {
    const input: InputIBSCBS = {
      valorAluguel: 2500,
      tipoLocacao: 'RESIDENCIAL_LONGA',
      aliquota: ALIQUOTA_2026_RESIDENCIAL,
      locadorEhContribuinte: true,
    };
    const r = calcularIBSCBS(input);
    expect(r.isento).toBe(true);
    expect(r.motivoIsencao).toContain('RN-002');
    expect(r.valorTotal).toBe(0);
  });

  it('RN-002: residencial R$2.500,01 → tributado (limite exato)', () => {
    const input: InputIBSCBS = {
      valorAluguel: 2500.01,
      tipoLocacao: 'RESIDENCIAL_LONGA',
      aliquota: ALIQUOTA_2026_RESIDENCIAL,
      locadorEhContribuinte: true,
    };
    const r = calcularIBSCBS(input);
    expect(r.isento).toBe(false);
    expect(r.valorTotal).toBeGreaterThan(0);
  });

  // ─── RN-003: alíquota efetiva residencial ─────────────────────────────────

  it('RN-003: residencial R$3.500 contribuinte — cálculo correto', () => {
    // Base = 3500 - 600 (redutor) = 2900
    // IBS efetivo = 0.001 × 0.30 = 0.0003
    // CBS efetivo = 0.009 × 0.30 = 0.0027
    // IBS = 2900 × 0.0003 = 0.87
    // CBS = 2900 × 0.0027 = 7.83
    const input: InputIBSCBS = {
      valorAluguel: 3500,
      tipoLocacao: 'RESIDENCIAL_LONGA',
      aliquota: ALIQUOTA_2026_RESIDENCIAL,
      locadorEhContribuinte: true,
    };
    const r = calcularIBSCBS(input);
    expect(r.isento).toBe(false);
    expect(r.baseCalculo).toBe(2900);
    expect(r.aliquotaIBSEfetiva).toBeCloseTo(0.0003);
    expect(r.aliquotaCBSEfetiva).toBeCloseTo(0.0027);
    expect(r.valorIBS).toBeCloseTo(0.87);
    expect(r.valorCBS).toBeCloseTo(7.83);
    expect(r.valorTotal).toBeCloseTo(8.70);
  });

  // ─── RN-004: short stay ───────────────────────────────────────────────────

  it('RN-004: short stay usa fator 0.40 (não 0.30)', () => {
    const input: InputIBSCBS = {
      valorAluguel: 4000,
      tipoLocacao: 'SHORT_STAY',
      aliquota: ALIQUOTA_2026_CURTA_ESTADIA,
      locadorEhContribuinte: true,
    };
    const r = calcularIBSCBS(input);
    expect(r.isento).toBe(false);
    // fator = 0.40, sem redutor social
    expect(r.aliquotaIBSEfetiva).toBeCloseTo(0.0004); // 0.001 × 0.40
    expect(r.aliquotaCBSEfetiva).toBeCloseTo(0.0036); // 0.009 × 0.40
    expect(r.valorTotal).toBeCloseTo(4000 * (0.0004 + 0.0036));
  });

  // ─── Comercial ────────────────────────────────────────────────────────────

  it('comercial contribuinte: tributado normalmente', () => {
    const input: InputIBSCBS = {
      valorAluguel: 10000,
      tipoLocacao: 'COMERCIAL',
      aliquota: ALIQUOTA_2026_COMERCIAL,
      locadorEhContribuinte: true,
    };
    const r = calcularIBSCBS(input);
    expect(r.isento).toBe(false);
    expect(r.baseCalculo).toBe(10000); // sem redutor social em comercial
    expect(r.valorTotal).toBeCloseTo(10000 * (0.001 + 0.009) * 0.30);
  });

  // ─── 2026: recolhimento dispensado ────────────────────────────────────────

  it('2026: recolhimento_obrigatorio deve ser false', () => {
    const input: InputIBSCBS = {
      valorAluguel: 5000,
      tipoLocacao: 'COMERCIAL',
      aliquota: ALIQUOTA_2026_COMERCIAL,
      locadorEhContribuinte: true,
    };
    const r = calcularIBSCBS(input);
    expect(r.recolhimentoObrigatorio).toBe(false);
  });

  // ─── Memoria de cálculo sempre presente ───────────────────────────────────

  it('memoria de cálculo nunca vazia em qualquer cenário', () => {
    const cenarios: InputIBSCBS[] = [
      { valorAluguel: 1000, tipoLocacao: 'RESIDENCIAL_LONGA', aliquota: ALIQUOTA_2026_RESIDENCIAL, locadorEhContribuinte: false },
      { valorAluguel: 2500, tipoLocacao: 'RESIDENCIAL_LONGA', aliquota: ALIQUOTA_2026_RESIDENCIAL, locadorEhContribuinte: true },
      { valorAluguel: 3000, tipoLocacao: 'RESIDENCIAL_LONGA', aliquota: ALIQUOTA_2026_RESIDENCIAL, locadorEhContribuinte: true },
      { valorAluguel: 5000, tipoLocacao: 'COMERCIAL', aliquota: ALIQUOTA_2026_COMERCIAL, locadorEhContribuinte: true },
    ];
    for (const input of cenarios) {
      const r = calcularIBSCBS(input);
      expect(r.memoriaCalculo.length).toBeGreaterThan(0);
    }
  });
});
