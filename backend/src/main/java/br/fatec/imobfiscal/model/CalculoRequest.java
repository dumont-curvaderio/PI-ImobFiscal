package br.fatec.imobfiscal.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CalculoRequest(
        @NotNull @Positive BigDecimal valorBase,
        @NotBlank String regime,
        @NotBlank String tipoImovel
) {}
