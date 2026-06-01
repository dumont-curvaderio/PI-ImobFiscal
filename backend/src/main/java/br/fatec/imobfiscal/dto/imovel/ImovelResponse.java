package br.fatec.imobfiscal.dto.imovel;

import br.fatec.imobfiscal.entity.Imovel;
import br.fatec.imobfiscal.enums.TipoImovel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Dados devolvidos pela API — nunca expõe campos internos (deleted_at, etc.)
public record ImovelResponse(
        UUID id,
        UUID locadorId,
        String codigo,
        TipoImovel tipo,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        BigDecimal areaTotal,
        Integer quartos,
        Integer vagas,
        BigDecimal valorCompra,
        LocalDate dataCompra,
        BigDecimal valorVenal
) {
    // Método de fábrica: converte entidade → DTO de resposta
    public static ImovelResponse from(Imovel imovel) {
        return new ImovelResponse(
                imovel.getId(),
                imovel.getLocador() != null ? imovel.getLocador().getId() : null,
                imovel.getCodigo(),
                imovel.getTipo(),
                imovel.getCep(),
                imovel.getLogradouro(),
                imovel.getNumero(),
                imovel.getComplemento(),
                imovel.getBairro(),
                imovel.getCidade(),
                imovel.getUf(),
                imovel.getAreaTotal(),
                imovel.getQuartos(),
                imovel.getVagas(),
                imovel.getValorCompra(),
                imovel.getDataCompra(),
                imovel.getValorVenal()
        );
    }
}
