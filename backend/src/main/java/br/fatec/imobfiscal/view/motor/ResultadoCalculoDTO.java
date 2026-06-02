package br.fatec.imobfiscal.view.motor;

import java.math.BigDecimal;

// Resultado do Motor Tributário — detalhamento fiscal IBS/CBS para Split Payment.
public record ResultadoCalculoDTO(
        String regime,
        String tipoImovel,
        BigDecimal valorBase,
        BigDecimal aliquotaIbs,      // Ex: 0.0145 = 1,45%
        BigDecimal aliquotaCbs,      // Ex: 0.0076 = 0,76%
        BigDecimal valorIbs,         // valorBase × aliquotaIbs
        BigDecimal valorCbs,         // valorBase × aliquotaCbs
        BigDecimal valorLiquido,     // valorBase - valorIbs - valorCbs
        boolean splitPaymentRequerido
) {}
