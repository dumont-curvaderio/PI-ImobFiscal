package br.fatec.imobfiscal.model;

import java.math.BigDecimal;

public record ResultadoCalculoDTO(
        String regime,
        String tipoImovel,
        BigDecimal valorBase,
        BigDecimal aliquotaIbs,
        BigDecimal aliquotaCbs,
        BigDecimal valorIbs,
        BigDecimal valorCbs,
        BigDecimal valorLiquido,
        boolean splitPaymentRequerido
) {}
