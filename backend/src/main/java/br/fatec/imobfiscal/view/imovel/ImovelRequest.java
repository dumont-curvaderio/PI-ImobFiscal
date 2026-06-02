package br.fatec.imobfiscal.view.imovel;

import br.fatec.imobfiscal.enums.TipoImovel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Dados enviados pelo cliente para criar ou atualizar um imóvel.
public record ImovelRequest(
        // Locador é opcional — o imóvel pode ser cadastrado antes de associar o proprietário
        UUID locadorId,

        @NotBlank(message = "Código obrigatório")
        String codigo,

        @NotNull(message = "Tipo obrigatório")
        TipoImovel tipo,

        @NotBlank
        @Size(min = 8, max = 8, message = "CEP deve ter 8 dígitos")
        String cep,

        @NotBlank String logradouro,
        @NotBlank String numero,
        String complemento,
        @NotBlank String bairro,
        @NotBlank String cidade,

        @NotBlank
        @Size(min = 2, max = 2, message = "UF deve ter 2 letras")
        String uf,

        BigDecimal areaTotal,
        Integer quartos,
        Integer vagas,
        BigDecimal valorCompra,
        LocalDate dataCompra,
        BigDecimal valorVenal
) {}
