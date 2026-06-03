package br.fatec.imobfiscal.view.contrato;

import br.fatec.imobfiscal.enums.StatusContrato;
import br.fatec.imobfiscal.enums.TipoLocacao;
import br.fatec.imobfiscal.enums.TipoPessoa;
import br.fatec.imobfiscal.model.ContratoLocacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ContratoResponse(
        UUID id,
        UUID imovelId,
        TipoLocacao tipoLocacao,
        StatusContrato status,
        TipoPessoa locatarioTipo,
        String locatarioCpfCnpj,
        String locatarioNome,
        BigDecimal valorAluguel,
        Integer diaVencimento,
        LocalDate dataInicio,
        LocalDate dataFim,
        Integer prazoMeses
) {
    public static ContratoResponse from(ContratoLocacao contrato) {
        return new ContratoResponse(
                contrato.getId(),
                contrato.getImovelId(),
                contrato.getTipoLocacao(),
                contrato.getStatus(),
                contrato.getLocatarioTipo(),
                contrato.getLocatarioCpfCnpj(),
                contrato.getLocatarioNome(),
                contrato.getValorAluguel(),
                contrato.getDiaVencimento(),
                contrato.getDataInicio(),
                contrato.getDataFim(),
                contrato.getPrazoMeses()
        );
    }
}
