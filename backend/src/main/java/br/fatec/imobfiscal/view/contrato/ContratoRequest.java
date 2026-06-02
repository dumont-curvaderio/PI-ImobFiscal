package br.fatec.imobfiscal.view.contrato;

import br.fatec.imobfiscal.enums.TipoLocacao;
import br.fatec.imobfiscal.enums.TipoPessoa;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ContratoRequest(
        @NotNull(message = "Imóvel obrigatório")
        UUID imovelId,

        @NotNull(message = "Tipo de locação obrigatório")
        TipoLocacao tipoLocacao,

        @NotNull TipoPessoa locatarioTipo,

        @NotBlank
        @Size(min = 11, max = 14, message = "CPF ou CNPJ inválido")
        String locatarioCpfCnpj,

        @NotBlank String locatarioNome,

        @NotNull
        @DecimalMin(value = "0.01", message = "Valor do aluguel deve ser positivo")
        BigDecimal valorAluguel,

        @NotNull
        @Min(value = 1) @Max(value = 31)
        Integer diaVencimento,

        @NotNull LocalDate dataInicio,
        LocalDate dataFim,
        Integer prazoMeses
) {}
