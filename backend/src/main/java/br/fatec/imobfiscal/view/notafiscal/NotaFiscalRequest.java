package br.fatec.imobfiscal.view.notafiscal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

// DTO para solicitar a emissão de uma nota fiscal para um contrato.
public record NotaFiscalRequest(

        @NotNull
        UUID contratoId,

        // Valor do serviço = valor do aluguel do período
        @NotNull
        @DecimalMin("0.01")
        BigDecimal valorServico
) {}
