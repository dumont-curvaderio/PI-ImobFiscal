package br.fatec.imobfiscal.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record NotaFiscalRequest(

        @NotNull
        UUID contratoId,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal valorServico
) {}
