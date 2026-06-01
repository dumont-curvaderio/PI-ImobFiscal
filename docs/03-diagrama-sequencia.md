# Diagrama de Sequência

**Sistema:** ImobFiscal — Gestão de Locação de Imóveis com Cálculo Tributário (IBS/CBS)
**Versão:** 1.0 — PI 2 / 2026

---

## Fluxo representado

**"Geração de Boleto com Cálculo Tributário IBS/CBS"**

Este é o fluxo principal do sistema: o ClienteLocatário solicita o boleto do mês,
o sistema calcula os impostos devidos (IBS e CBS) conforme a Reforma Tributária,
emite a Nota Fiscal e entrega o boleto pronto para pagamento.

---

## Participantes

| Participante | Tipo | Papel |
| ------------ | ---- | ----- |
| ClienteLocatário | Ator | Solicita o boleto |
| Sistema | Controlador | Orquestra o fluxo |
| ContratoLocacao | Entidade | Armazena dados do aluguel |
| CalculadoraReformaTributaria | Serviço | Calcula IBS e CBS |
| NotaFiscal | Entidade | Registro fiscal da transação |
| IntegracaoBancaria | Serviço | Gera o boleto bancário |

---

## Diagrama

```mermaid
sequenceDiagram
    actor Locatario as ClienteLocatário
    participant S as Sistema
    participant C as ContratoLocacao
    participant Calc as CalculadoraReformaTributaria
    participant NF as NotaFiscal
    participant Banco as IntegracaoBancaria

    %% 1. Locatário solicita o boleto
    Locatario->>S: solicitarBoleto(contratoId)

    %% 2. Sistema busca o contrato no banco de dados
    S->>C: buscarContrato(contratoId)
    C-->>S: { valorAluguel, diaVencimento, locadorId, tipoImovel }

    %% 3. Sistema aciona o motor tributário
    S->>Calc: calcular(valorAluguel, tipoImovel, ano=2026)

    %% 4. Calculadora aplica as regras da Reforma Tributária
    Note over Calc: RN-003: fator de redução 30% (residencial)<br/>RN-002: isento se aluguel ≤ R$2.500
    Calc->>Calc: calcularIBS(valorAluguel, aliquota=0.001)
    Calc->>Calc: calcularCBS(valorAluguel, aliquota=0.009)

    %% 5. Retorna o detalhamento tributário
    Calc-->>S: { valorIBS, valorCBS, valorLiquido, recolhimentoObrigatorio=false }

    %% 6. Sistema emite a Nota Fiscal com os valores fiscais
    S->>NF: emitir(valorBruto, aliquotaIBS, aliquotaCBS, dataEmissao)
    NF-->>S: notaFiscal { id, dataEmissao, valorBruto, valorLiquido }

    %% 7. Sistema gera o boleto bancário
    S->>Banco: gerarBoleto(valorLiquido, diaVencimento, splitPayment=false)
    Banco-->>S: { codigoBoleto, linhaDigitavel, qrCodePix }

    %% 8. Sistema retorna tudo ao locatário
    S-->>Locatario: { boleto, qrCodePix, notaFiscal }
```

---

## Notas sobre o fluxo

### Por que `recolhimentoObrigatorio=false` em 2026?

Em 2026, o IBS e CBS estão na fase de transição. Os valores são **calculados e informados**
na Nota Fiscal, mas o recolhimento ao governo ainda não é obrigatório. A partir de 2027,
`recolhimentoObrigatorio` passa a `true` e o Split Payment entra em vigor.

### O que é Split Payment?

É um mecanismo da Reforma Tributária onde, no momento do pagamento do boleto/PIX, o banco
separa automaticamente a parte do imposto (IBS/CBS) e repassa ao governo. O locador recebe
apenas o valor líquido. Em 2026, isso ainda não está ativo (`splitPayment=false`).

### Fluxo de exceção — Aluguel isento (RN-002)

```mermaid
sequenceDiagram
    actor Locatario as ClienteLocatário
    participant S as Sistema
    participant Calc as CalculadoraReformaTributaria

    Locatario->>S: solicitarBoleto(contratoId)
    S->>Calc: calcular(valorAluguel=2000, tipoImovel=RESIDENCIAL, ano=2026)

    Note over Calc: valorAluguel ≤ R$2.500 → RN-002 aplica isenção
    Calc-->>S: { isento=true, motivoIsencao="RN-002: aluguel residencial ≤ R$2.500",<br/>valorIBS=0, valorCBS=0 }

    S-->>Locatario: Boleto gerado sem tributos (isenção RN-002)
```
