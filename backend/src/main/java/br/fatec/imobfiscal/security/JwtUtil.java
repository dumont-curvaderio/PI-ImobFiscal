package br.fatec.imobfiscal.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

// Responsável por gerar e validar tokens JWT
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    // @Value lê os valores do application.properties
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        // Decodifica a chave Base64 e cria a chave HMAC-SHA256
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    // Gera um token JWT com o e-mail do usuário como "subject"
    public String gerarToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    // Extrai o e-mail (subject) de um token válido
    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    // Verifica se o token é válido e não expirou
    public boolean isTokenValido(String token, String email) {
        String emailDoToken = extrairEmail(token);
        return emailDoToken.equals(email) && !isTokenExpirado(token);
    }

    private boolean isTokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());
    }

    // Decodifica o token e retorna todos os campos (claims)
    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
