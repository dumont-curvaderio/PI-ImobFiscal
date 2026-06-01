package br.fatec.imobfiscal.dto.motor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

// Dados de entrada para o Motor Tributário
public record CalculoRequest(
        @NotNull @Positive BigDecimal valorBase,
        @NotBlank String regime,       // ex: "PF", "SIMPLES_NACIONAL"
        @NotBlank String tipoImovel    // ex: "RESIDENCIAL", "COMERCIAL"
) {}
