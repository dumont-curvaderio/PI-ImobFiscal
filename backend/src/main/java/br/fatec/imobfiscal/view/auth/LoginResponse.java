package br.fatec.imobfiscal.view.auth;

// Resposta do endpoint de login.
// "token" NÃO é um JWT — é apenas um marcador de sessão para o frontend saber
// que o login deu certo (a API não valida esse valor). Inclui também os dados
// básicos do usuário, sem a senha.
public record LoginResponse(
        String token,
        String email,
        String nome,
        String perfil
) {}
