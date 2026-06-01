package br.fatec.imobfiscal.dto.boleto;

import br.fatec.imobfiscal.entity.Boleto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Resposta completa com detalhamento fiscal para exibição no frontend
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
                boleto.getContrato() != null ? boleto.getContrato().getId() : null,
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
