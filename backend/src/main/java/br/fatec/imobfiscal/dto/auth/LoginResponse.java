package br.fatec.imobfiscal.dto.auth;

// Resposta do endpoint de login
// Retorna o token JWT e dados básicos do usuário (sem senha)
public record LoginResponse(
        String token,
        String email,
        String nome,
        String perfil
) {}
