package br.fatec.imobfiscal.dto.locador;

import br.fatec.imobfiscal.enums.TipoPessoa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO de entrada para criar ou atualizar um locador
// record = classe imutável com getters automáticos (Java 16+)
public record LocadorRequest(

        @NotNull
        TipoPessoa tipoPessoa,

        // CPF tem 11 dígitos, CNPJ tem 14
        @NotBlank
        @Size(min = 11, max = 14)
        String cpfCnpj,

        @NotBlank
        String nome,

        String email,

        String telefone
) {}
