package br.fatec.imobfiscal.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroRequest(
        @NotBlank(message = "Nome obrigatório")
        String nome,

        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail obrigatório")
        String email,

        @NotBlank(message = "Senha obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        @NotBlank(message = "ID da imobiliária obrigatório")
        String imobiliariaId
) {}
