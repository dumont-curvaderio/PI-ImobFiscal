---
name: fiscal-calculator
description: Use ao implementar qualquer função de cálculo fiscal: IBS/CBS,
             carnê-leão, GCAP, ITBI, retenções IRRF. Triggers: "calcular
             imposto", "apurar IBS", "calcular GCAP", "carnê-leão", qualquer
             menção a RN-001 a RN-011, "isenção fiscal", "alíquota".
version: 1.0
owner: AI Architect Fiscal
last_updated: 2026-05-01
---

Cálculos fiscais têm consequência legal direta. Um bug de R$0,01 pode
resultar em multa real ao cliente. Toda calculadora fiscal é uma função
PURA em packages/core/fiscal/ — sem banco, sem framework, sem efeitos
colaterais. Leia docs/knowledge-base-fiscal.md antes de começar.

## Pré-condições

- [ ] Li a RN correspondente no knowledge-base-fiscal.md
- [ ] Confirmo: a alíquota virá de parâmetro ou `aliquotas_vigentes` (nunca hardcoded)
- [ ] Confirmo: arquivo vai em packages/core/fiscal/ (não em apps/)

## Processo

1. Criar `packages/core/fiscal/[nome].calculator.ts`
2. Definir interfaces de input e output com tipos explícitos (sem `any`)
3. Escrever testes ANTES do código (TDD obrigatório)
4. Cobrir: caso base, cada isenção possível, valor exato no limite
5. Implementar a função pura
6. Adicionar comentário `// RN-XXX` em cada regra implementada
7. Exportar via `packages/core/fiscal/index.ts`
8. Confirmar coverage ≥ 95% no arquivo

## Regras absolutas

- NUNCA hardcodar alíquotas — sempre parâmetro externo
- NUNCA importar Prisma, Supabase, NestJS ou Express
- NUNCA arredondar internamente — retornar precisão total
- TODO cálculo fiscal TEM teste. PR sem testes fiscais não faz merge.
- SEMPRE incluir `memoriaCalculo: string[]` no output para auditoria

## Exemplo correto

```typescript
// packages/core/fiscal/ibs-cbs.calculator.ts
// RN-003: Alíquota efetiva = 30% da referência (70% redução)

export interface InputIBSCBS {
  valorAluguel: number
  tipoLocacao: 'RESIDENCIAL' | 'COMERCIAL' | 'SHORT_STAY'
  aliquotaReferencia: number    // vem de aliquotas_vigentes
  locadorContribuinte: boolean  // RN-001
}

export interface ResultadoIBSCBS {
  baseCalculo: number
  aliquotaEfetiva: number
  valorIBS: number
  valorCBS: number
  isento: boolean
  motivoIsencao?: string
  memoriaCalculo: string[]
}

export function calcularIBSCBS(input: InputIBSCBS): ResultadoIBSCBS {
  const mem: string[] = []
  // RN-002: isenção residencial ≤ R$2.500
  if (input.valorAluguel <= 2500 && input.tipoLocacao === 'RESIDENCIAL') {
    mem.push(`Isento RN-002: ${input.valorAluguel} ≤ 2500`)
    return { baseCalculo: 0, aliquotaEfetiva: 0, valorIBS: 0,
             valorCBS: 0, isento: true,
             motivoIsencao: 'RN-002: aluguel residencial ≤ R$2.500',
             memoriaCalculo: mem }
  }
  // RN-003: alíquota efetiva = 30% da referência
  const fatorReducao = input.tipoLocacao === 'SHORT_STAY' ? 0.40 : 0.30 // RN-004
  mem.push(`Fator redução: ${fatorReducao * 100}% (${input.tipoLocacao})`)
  const aliquotaEfetiva = input.aliquotaReferencia * fatorReducao
  // ... resto da implementação
}
```

## Exemplo incorreto

```typescript
// ❌ NUNCA FAZER
export function calcularIBS(valor: number) {
  const ALIQUOTA_IBS = 0.001  // ← HARDCODE PROIBIDO
  const ALIQUOTA_CBS = 0.009  // ← HARDCODE PROIBIDO
  return valor * (ALIQUOTA_IBS + ALIQUOTA_CBS)
  // ❌ Sem memória de cálculo
  // ❌ Sem isenção RN-002
  // ❌ Sem tipos de entrada/saída
}
```

## Testes obrigatórios

- [ ] Caso base: residencial R$3.500, PF contribuinte
- [ ] Isenção RN-002: R$2.500,00 exato → isento
- [ ] Limite RN-002: R$2.500,01 → tributado
- [ ] Short stay: fator 40% (RN-004)
- [ ] PF não-contribuinte (RN-001): retorna isento com motivo correto
- [ ] Memória de cálculo: não vazia, referencia a RN aplicada
