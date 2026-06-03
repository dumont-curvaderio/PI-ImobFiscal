package br.fatec.imobfiscal.view.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail obrigatório")
        String email,

        @NotBlank(message = "Senha obrigatória")
        String senha
) {}
