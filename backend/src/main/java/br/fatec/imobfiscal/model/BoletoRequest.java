package br.fatec.imobfiscal.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record BoletoRequest(
        @NotNull UUID contratoId,
        @NotNull @Future LocalDate dataVencimento
) {}
