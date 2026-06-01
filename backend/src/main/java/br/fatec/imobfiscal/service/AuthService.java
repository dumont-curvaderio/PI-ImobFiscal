package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.dto.auth.CadastroRequest;
import br.fatec.imobfiscal.dto.auth.LoginRequest;
import br.fatec.imobfiscal.dto.auth.LoginResponse;
import br.fatec.imobfiscal.entity.Imobiliaria;
import br.fatec.imobfiscal.entity.Usuario;
import br.fatec.imobfiscal.repository.ImobiliariaRepository;
import br.fatec.imobfiscal.repository.UsuarioRepository;
import br.fatec.imobfiscal.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ImobiliariaRepository imobiliariaRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;

    // Autentica o usuário e retorna um token JWT
    public LoginResponse login(LoginRequest request) {
        // Delega para o Spring Security verificar email + senha
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        // Se chegou aqui, autenticação foi bem-sucedida
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtUtil.gerarToken(usuario.getEmail());

        return new LoginResponse(token, usuario.getEmail(), usuario.getNome(),
                usuario.getPerfil().name());
    }

    // Cadastra um novo usuário
    public void cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        Imobiliaria imobiliaria = imobiliariaRepository
                .findById(UUID.fromString(request.imobiliariaId()))
                .orElseThrow(() -> new IllegalArgumentException("Imobiliária não encontrada"));

        Usuario usuario = Usuario.builder()
                .imobiliaria(imobiliaria)
                .email(request.email())
                // Sempre armazena a senha como hash BCrypt — nunca em texto puro
                .senha(passwordEncoder.encode(request.senha()))
                .nome(request.nome())
                .build();

        usuarioRepository.save(usuario);
    }
}
