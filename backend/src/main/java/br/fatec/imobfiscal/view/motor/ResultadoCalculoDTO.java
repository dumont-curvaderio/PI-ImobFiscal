package br.fatec.imobfiscal.view.motor;

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
