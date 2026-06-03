package br.fatec.imobfiscal.model;

public record LoginResponse(
        String token,
        String email,
        String nome,
        String perfil
) {}
