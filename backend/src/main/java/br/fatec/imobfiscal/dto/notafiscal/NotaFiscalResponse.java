package br.fatec.imobfiscal.dto.notafiscal;

import br.fatec.imobfiscal.entity.NotaFiscal;
import br.fatec.imobfiscal.enums.StatusNFe;

import java.math.BigDecimal;
import java.util.UUID;

public record NotaFiscalResponse(
        UUID id,
        UUID imobiliariaId,
        UUID contratoId,
        String numero,
        String serie,
        String chaveAcesso,
        StatusNFe status,
        BigDecimal valorServico,
        BigDecimal valorIbs,
        BigDecimal valorCbs,
        Boolean recolhimentoObrigatorio,
        Integer tentativas,
        String erroSefaz
) {
    public static NotaFiscalResponse from(NotaFiscal nf) {
        return new NotaFiscalResponse(
                nf.getId(),
                nf.getImobiliariaId(),
                nf.getContrato().getId(),
                nf.getNumero(),
                nf.getSerie(),
                nf.getChaveAcesso(),
                nf.getStatus(),
                nf.getValorServico(),
                nf.getValorIbs(),
                nf.getValorCbs(),
                nf.getRecolhimentoObrigatorio(),
                nf.getTentativas(),
                nf.getErroSefaz()
        );
    }
}
