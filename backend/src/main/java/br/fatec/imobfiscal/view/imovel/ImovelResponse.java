package br.fatec.imobfiscal.view.imovel;

import br.fatec.imobfiscal.enums.TipoImovel;
import br.fatec.imobfiscal.model.Imovel;

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
    // Método de fábrica: converte o model → DTO de resposta.
    // Agora o model já tem o locadorId como UUID direto (não objeto aninhado).
    public static ImovelResponse from(Imovel imovel) {
        return new ImovelResponse(
                imovel.getId(),
                imovel.getLocadorId(),
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
