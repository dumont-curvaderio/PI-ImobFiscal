package br.fatec.imobfiscal.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BoletoResponse(
        UUID id,
        UUID contratoId,
        UUID imobiliariaId,
        BigDecimal valorAluguel,
        BigDecimal aliquotaIbs,
        BigDecimal aliquotaCbs,
        BigDecimal valorIbs,
        BigDecimal valorCbs,
        BigDecimal valorLiquido,
        LocalDate dataVencimento,
        String status,
        String regimeTributario,
        String tipoImovel
) {
    public static BoletoResponse from(Boleto boleto) {
        return new BoletoResponse(
                boleto.getId(),
                boleto.getContratoId(),
                boleto.getImobiliariaId(),
                boleto.getValorAluguel(),
                boleto.getAliquotaIbs(),
                boleto.getAliquotaCbs(),
                boleto.getValorIbs(),
                boleto.getValorCbs(),
                boleto.getValorLiquido(),
                boleto.getDataVencimento(),
                boleto.getStatus(),
                boleto.getRegimeTributario(),
                boleto.getTipoImovel()
        );
    }
}
