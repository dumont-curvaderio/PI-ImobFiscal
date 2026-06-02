package br.fatec.imobfiscal.view.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO (View) = Data Transfer Object: objeto que trafega dados entre o cliente e a API.
// Separado do model para não expor campos sensíveis (ex: senha hashed).
public record LoginRequest(
        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail obrigatório")
        String email,

        @NotBlank(message = "Senha obrigatória")
        String senha
) {}
