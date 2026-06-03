package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.RegimeTributario;
import br.fatec.imobfiscal.enums.TipoPessoa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LocadorRequest(

        @NotNull
        TipoPessoa tipoPessoa,

        @NotBlank
        @Size(min = 11, max = 14)
        String cpfCnpj,

        @NotBlank
        String nome,

        String email,

        String telefone,

        RegimeTributario regimeTributario
) {}
