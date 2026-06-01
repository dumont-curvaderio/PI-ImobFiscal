package br.fatec.imobfiscal.dto.boleto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

// Dados necessários para gerar um boleto a partir de um contrato ativo
public record BoletoRequest(
        @NotNull UUID contratoId,
        @NotNull @Future LocalDate dataVencimento
) {}
