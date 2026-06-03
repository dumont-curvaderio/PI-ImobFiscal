package br.fatec.imobfiscal.view.auth;

public record LoginResponse(
        String token,
        String email,
        String nome,
        String perfil
) {}
